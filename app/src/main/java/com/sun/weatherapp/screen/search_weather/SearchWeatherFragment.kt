package com.sun.weatherapp.screen.search_weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
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
        }
    }

    override fun showSearchResults(listCity: List<City>) {
        searchWeatherAdapter.submitList(listCity)
    }

    private fun setupRecyclerViews() {
        searchWeatherAdapter = SearchWeatherAdapter { city ->
            presenter?.onSearchQuery(city.name)
        }

        binding.rvWeather.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchWeatherAdapter
        }
    }
}