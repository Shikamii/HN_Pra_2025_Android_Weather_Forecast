package com.sun.weatherapp.screen.broadcast

import android.location.Location
import com.sun.weatherapp.WeatherApplication
import com.sun.weatherapp.data.model.DailyWeather
import com.sun.weatherapp.data.model.DailyWeatherType
import com.sun.weatherapp.data.model.WeatherDetailResponse
import com.sun.weatherapp.data.model.validateAndGetDailyWeather
import com.sun.weatherapp.data.reposiroty.LocationRepository
import com.sun.weatherapp.data.reposiroty.WeatherRepository
import com.sun.weatherapp.data.reposiroty.source.remote.OnResultListener
import com.sun.weatherapp.screen.base.BasePresenter
import com.sun.weatherapp.data.model.WeatherResponse
import com.sun.weatherapp.utils.toCelsius
import com.sun.weatherapp.R

class BroadcastPresenter(
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository,
) : BasePresenter<BroadcastContract.View>(), BroadcastContract.Presenter {

    private var currentTab = DailyWeatherType.TODAY

    override fun loadBroadcasts(tabType: DailyWeatherType) {
        currentTab = tabType

        getView()?.updateSelectedTab(tabType)
        getView()?.showLoading()

        locationRepository.getCurrentLocation(object : OnResultListener<Location> {
            override fun onSuccess(data: Location) {
                weatherRepository.getWeatherDetail(data.latitude, data.longitude, object : OnResultListener<WeatherDetailResponse> {
                    override fun onSuccess(data: WeatherDetailResponse) {
                        getView()?.hideLoading()
                        try {
                            val listDailyWeather: List<DailyWeather> =
                                data.validateAndGetDailyWeather(tabType, WeatherApplication.getInstance())
                            getView()?.showBroadcastsWithHourlyData(listDailyWeather, data)
                        } catch (e: IllegalStateException) {
                            // Show specific message for validation errors
                            getView()?.showError(
                                e.message ?: WeatherApplication.getInstance()
                                    .getString(R.string.error_invalid_weather_data)
                            )
                        } catch (e: Exception) {
                            // Show generic error message for other exceptions
                            getView()?.showError(
                                WeatherApplication.getInstance().getString(
                                    R.string.error_processing_weather_data,
                                    e.message.orEmpty()
                                )
                            )
                        }
                    }

                    override fun onError(exception: java.lang.Exception?) {
                        getView()?.hideLoading()
                        val errorMessage =
                            exception?.message ?: WeatherApplication.getInstance()
                                .getString(R.string.error_unable_to_load_weather_data)
                        getView()?.showError(errorMessage)
                    }
                })
            }

            override fun onError(exception: Exception?) {
                getView()?.hideLoading()
                val errorMessage = exception?.message ?: WeatherApplication.getInstance()
                    .getString(R.string.error_unable_to_determine_location)
                getView()?.showError(errorMessage)
            }
        })
    }

    override fun onTabSelected(tabType: DailyWeatherType) {
        if (currentTab != tabType) {
            loadBroadcasts(tabType)
        }
    }

    override fun loadWeatherInfo() {
        getView()?.showLoading()

        locationRepository.getCurrentLocation(object : OnResultListener<Location> {
            override fun onSuccess(data: Location) {
                weatherRepository.getCurrentWeather(
                    data.latitude,
                    data.longitude,
                    object : OnResultListener<WeatherResponse> {
                        override fun onSuccess(data: WeatherResponse) {
                            getView()?.hideLoading()
                            val locationName = data.name.ifEmpty {
                                WeatherApplication.getInstance()
                                    .getString(R.string.unknown_location)
                            }
                            val temperature = "${data.main.temp.toCelsius()}°C"
                            getView()?.showWeatherInfo(locationName, temperature)
                        }

                        override fun onError(exception: Exception?) {
                            getView()?.hideLoading()
                            val errorMessage =
                                exception?.message ?: WeatherApplication.getInstance()
                                    .getString(R.string.error_unable_to_load_weather_info)
                            getView()?.showError(errorMessage)
                        }
                    })
            }

            override fun onError(exception: Exception?) {
                getView()?.hideLoading()
                val errorMessage = exception?.message ?: WeatherApplication.getInstance()
                    .getString(R.string.error_unable_to_determine_location)
                getView()?.showError(errorMessage)
            }
        })
    }

}
