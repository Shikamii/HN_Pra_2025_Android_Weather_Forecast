package com.sun.weatherapp.screen.broadcast

import com.sun.weatherapp.data.model.DailyWeatherType
import com.sun.weatherapp.screen.base.BasePresenter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BroadcastPresenter : BasePresenter<BroadcastContract.View>(), BroadcastContract.Presenter {

    private var currentTab = DailyWeatherType.TODAY
    private var isInitialLoad = true

    override fun loadBroadcasts(tabType: DailyWeatherType) {
        currentTab = tabType

        presenterScope.launch {
            try {
                if (isInitialLoad) {
                    getView()?.showLoading()
                }

                getView()?.updateSelectedTab(tabType)
                // Simulate loading data
                delay(500)
                val listDailyWeather = when (tabType) {
                    DailyWeatherType.TODAY -> {
                        listOf(sampleWeatherList[0])
                    }

                    DailyWeatherType.TOMORROW -> {
                        listOf(sampleWeatherList[1])
                    }

                    DailyWeatherType.WEEK -> {
                        sampleWeatherList
                    }
                }
                getView()?.showBroadcasts(listDailyWeather)

                getView()?.hideLoading()
                isInitialLoad = false

            } catch (e: Exception) {
                getView()?.hideLoading()
                getView()?.showError("Không thể tải dữ liệu phát thanh: ${e.message}")
                isInitialLoad = false
            }
        }
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
