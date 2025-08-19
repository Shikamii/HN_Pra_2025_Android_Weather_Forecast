package com.sun.weatherapp.screen.broadcast

import android.location.Location
import com.sun.weatherapp.data.model.DailyWeather
import com.sun.weatherapp.data.model.DailyWeatherType
import com.sun.weatherapp.data.model.WeatherDetailResponse
import com.sun.weatherapp.data.model.validateAndGetDailyWeather
import com.sun.weatherapp.data.reposiroty.LocationRepository
import com.sun.weatherapp.data.reposiroty.WeatherRepository
import com.sun.weatherapp.data.reposiroty.source.remote.OnResultListener
import com.sun.weatherapp.screen.base.BasePresenter
import kotlinx.coroutines.launch

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
                                data.validateAndGetDailyWeather(tabType)
                            getView()?.showBroadcasts(listDailyWeather)
                            if (tabType == DailyWeatherType.WEEK) {
                                if (listDailyWeather.size < 7) {
                                    getView()?.showError("Only ${listDailyWeather.size}/7 days available")
                                }
                            }
                        } catch (e: IllegalStateException) {
                            // Show specific message for validation errors
                            getView()?.showError(e.message ?: "Invalid weather data")
                        } catch (e: Exception) {
                            // Show generic error message for other exceptions
                            getView()?.showError("Error processing weather data: ${e.message}")
                        }
                    }

                    override fun onError(exception: java.lang.Exception?) {
                        getView()?.hideLoading()
                        val errorMessage =
                            exception?.message ?: "Unable to load weather data from server"
                        getView()?.showError(errorMessage)
                    }
                })
            }

            override fun onError(exception: Exception?) {
                getView()?.hideLoading()
                val errorMessage = exception?.message ?: "Unable to determine current location"
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
        presenterScope.launch {
            try {
                getView()?.hideLoading()
                getView()?.showWeatherInfo("Ha Noi, Viet Nam", "3°C")
            } catch (e: Exception) {
                getView()?.hideLoading()
                getView()?.showError(e.message ?: "Unable to load weather information")
            }
        }
    }

}
