package com.sun.weatherapp.screen.search_result

import com.sun.weatherapp.data.model.WeatherDetailResponse
import com.sun.weatherapp.screen.base.BaseContract

interface SearchResultContract {
    interface View : BaseContract.View {
        fun showWeatherData(weatherData: WeatherDetailResponse)
        override fun showError(message: String)
        override fun showLoading()
        override fun hideLoading()
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun loadWeatherData(lat: Double, lon: Double)
    }
}
