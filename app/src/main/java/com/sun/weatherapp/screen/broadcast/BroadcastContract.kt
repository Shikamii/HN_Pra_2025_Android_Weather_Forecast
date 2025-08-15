package com.sun.weatherapp.screen.broadcast

import com.sun.weatherapp.data.model.DailyWeatherType
import com.sun.weatherapp.data.model.WeatherResponse
import com.sun.weatherapp.screen.base.BaseContract

interface BroadcastContract : BaseContract<BroadcastContract.View, BroadcastContract.Presenter> {

    interface View : BaseContract.View {
        fun showBroadcasts(broadcasts: List<WeatherResponse>)
        fun updateSelectedTab(tabType: DailyWeatherType)
        fun showWeatherInfo(location: String, temperature: String)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun loadBroadcasts(tabType: DailyWeatherType)
        fun onTabSelected(tabType: DailyWeatherType)
        fun loadWeatherInfo()
    }
}
