package com.sun.weatherapp.screen.broadcast

import android.location.Location
import com.sun.weatherapp.data.model.DailyWeather
import com.sun.weatherapp.data.model.DailyWeatherType
import com.sun.weatherapp.data.model.WeatherDetailResponse
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
                        val listDailyWeather: List<DailyWeather> = when (tabType) {
                            DailyWeatherType.TODAY -> listOf(data.daily[0])
                            DailyWeatherType.TOMORROW -> listOf(data.daily[1])
                            DailyWeatherType.WEEK -> data.daily
                        }

                        getView()?.showBroadcasts(listDailyWeather)
                    }


                    override fun onError(exception: java.lang.Exception?) {
                        getView()?.hideLoading()
                        getView()?.showError(exception?.message ?: "Unknown error")
                    }
                })
            }

            override fun onError(exception: Exception?) {
                getView()?.hideLoading()
                getView()?.showError(exception?.message ?: "Failed to get current location")
            }
        })
    }

    override fun onTabSelected(tabType: DailyWeatherType) {
        if (currentTab != tabType) {
            loadBroadcasts(tabType)
        }
    }

    override fun loadWeatherInfo() {
        presenterScope.launch {
            try {
                getView()?.showWeatherInfo("Ha Noi, Viet Nam", "3°C")
            } catch (e: Exception) {
                getView()?.showError("Không thể tải thông tin thời tiết")
            }
        }
    }

}
