package com.sun.weatherapp.screen.search_weather

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sun.weatherapp.R
import com.sun.weatherapp.data.model.City
import com.sun.weatherapp.data.reposiroty.WeatherRepository
import com.sun.weatherapp.data.reposiroty.source.local.WeatherLocalDataSource
import com.sun.weatherapp.data.reposiroty.source.remote.WeatherRemoteDataSource
import com.sun.weatherapp.databinding.FragmentSearchWeatherBinding
import com.sun.weatherapp.screen.base.BaseFragment
import com.sun.weatherapp.screen.search_weather.adapter.SearchWeatherAdapter

class SearchWeatherFragment : BaseFragment<FragmentSearchWeatherBinding, SearchWeatherPresenter>(), SearchWeatherContract.View {

    private lateinit var searchWeatherAdapter: SearchWeatherAdapter

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchWeatherBinding {
        return FragmentSearchWeatherBinding.inflate(inflater, container, false)
    }

    override fun initializePresenter() {
        val weatherRepository= WeatherRepository.getInstance(
            WeatherRemoteDataSource.getInstance(),
            WeatherLocalDataSource.getInstance()
        )
        presenter = SearchWeatherPresenter(
            weatherRepository
        )
        presenter?.attachView(this)
    }

    override fun setupViews() {
        setupRecyclerViews()

        binding.apply {
            // Initialize views here if needed
        }
    }

    override fun setupListeners() {
        binding.apply {
            etSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                    val query = etSearch.text.toString().trim()
                    if (query.isNotEmpty()) {
                        presenter?.onSearchQuery(query)
                    }
                    true
                } else {
                    false
                }
            }

            val parentLayout = root as android.widget.LinearLayout
            if (parentLayout.childCount >= 3) {
                val currentLocationLayout =
                    parentLayout.getChildAt(2) as? android.widget.LinearLayout
                currentLocationLayout?.setOnClickListener {
                    navigateToCurrentLocation()
                }
            }
        }
    }

    override fun showSearchResults(listCity: List<City>) {
        searchWeatherAdapter.submitList(listCity)
    }

    private fun setupRecyclerViews() {
        searchWeatherAdapter = SearchWeatherAdapter { city ->
            navigateToSearchResult(city)
        }

        binding.rvWeather.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchWeatherAdapter
        }
    }

    private fun navigateToCurrentLocation() {
        // Create a City object for current location using the displayed data
        binding.apply {
            val currentCity = City(
                name = tvCityCurrent.text.toString(),
                lat = 0.0, // Default values - will be handled by the destination fragment
                lon = 0.0, // Default values - will be handled by the destination fragment
                country = tvCountryCurrent.text.toString()
            )
            navigateToSearchResult(currentCity)
        }
    }
    private fun navigateToSearchResult(city: City) {
        val bundle = Bundle().apply {
            putParcelable("city", city)
        }
        findNavController().navigate(
            R.id.action_search_weather_fragment_to_search_result_fragment,
            bundle
        )
    }
}
