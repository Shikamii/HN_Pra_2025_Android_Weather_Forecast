package com.sun.weatherapp.screen.search_weather

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sun.weatherapp.databinding.FragmentSearchWeatherBinding
import com.sun.weatherapp.screen.base.BaseFragment

class SearchWeatherFragment : BaseFragment<FragmentSearchWeatherBinding, SearchWeatherPresenter>(), SearchWeatherContract.View {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchWeatherBinding {
        return FragmentSearchWeatherBinding.inflate(inflater, container, false)
    }

    override fun initializePresenter() {
        presenter = SearchWeatherPresenter()
        presenter?.attachView(this)
    }

    override fun setupViews() {
        binding.apply {
            // Initialize views here if needed
        }
    }

    override fun setupListeners() {

    }

    override fun showSearchResults() {

    }

}