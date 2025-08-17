package com.sun.weatherapp.screen.search_weather

import com.sun.weatherapp.data.model.City
import com.sun.weatherapp.data.reposiroty.WeatherRepository
import com.sun.weatherapp.data.reposiroty.source.remote.OnResultListener
import com.sun.weatherapp.screen.base.BasePresenter

class SearchWeatherPresenter(
    private val weatherRepository: WeatherRepository,
) : BasePresenter<SearchWeatherContract.View>(), SearchWeatherContract.Presenter {

    override fun onSearchQuery(query: String) {
        weatherRepository.getCurrentWeatherByCityName(query, object : OnResultListener<List<City>> {
            override fun onSuccess(data: List<City>) {
                getView()?.showSearchResults(data)
            }

            override fun onError(exception: Exception?) {
                getView()?.showError(exception?.message ?: "Failed to search for city")
            }
        })
    }

}