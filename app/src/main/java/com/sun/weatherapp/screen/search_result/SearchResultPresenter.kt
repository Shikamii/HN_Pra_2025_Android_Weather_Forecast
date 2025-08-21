package com.sun.weatherapp.screen.search_result

import com.sun.weatherapp.data.model.WeatherDetailResponse
import com.sun.weatherapp.data.reposiroty.WeatherRepository
import com.sun.weatherapp.data.reposiroty.source.remote.OnResultListener
import com.sun.weatherapp.screen.base.BasePresenter

class SearchResultPresenter(
    private val weatherRepository: WeatherRepository
) : BasePresenter<SearchResultContract.View>(), SearchResultContract.Presenter {

    override fun loadWeatherData(lat: Double, lon: Double) {
        getView()?.showLoading()

        weatherRepository.getWeatherDetail(
            lat,
            lon,
            object : OnResultListener<WeatherDetailResponse> {
                override fun onSuccess(data: WeatherDetailResponse) {
                    getView()?.hideLoading()
                    getView()?.showWeatherData(data)
                }

            override fun onError(exception: Exception?) {
                getView()?.hideLoading()
                getView()?.showError(exception?.message ?: "Unknown error occurred")
            }
        })
    }
}
