package com.sun.weatherapp.screen.search_weather

import com.sun.weatherapp.screen.base.BaseContract

interface SearchWeatherContract : BaseContract<SearchWeatherContract.View, SearchWeatherContract.Presenter> {

    interface View : BaseContract.View {
        fun showSearchResults()
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun onSearchQuery(query: String)
    }
}