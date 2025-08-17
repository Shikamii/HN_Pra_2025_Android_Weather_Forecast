package com.sun.weatherapp.screen.broadcast

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sun.weatherapp.R
import com.sun.weatherapp.WeatherApplication
import com.sun.weatherapp.data.model.DailyWeather
import com.sun.weatherapp.data.model.DailyWeatherType
import com.sun.weatherapp.data.reposiroty.LocationRepository
import com.sun.weatherapp.data.reposiroty.WeatherRepository
import com.sun.weatherapp.data.reposiroty.source.local.LocationLocalDataSource
import com.sun.weatherapp.data.reposiroty.source.local.WeatherLocalDataSource
import com.sun.weatherapp.data.reposiroty.source.remote.WeatherRemoteDataSource
import com.sun.weatherapp.databinding.FragmentBroadcastBinding
import com.sun.weatherapp.screen.base.BaseFragment
import com.sun.weatherapp.screen.broadcast.adapter.DailyWeatherAdapter

class BroadcastFragment : BaseFragment<FragmentBroadcastBinding, BroadcastPresenter>(), BroadcastContract.View {

    private lateinit var dailyWeatherAdapter: DailyWeatherAdapter

    private var currentTab = DailyWeatherType.TODAY
    private var isInitialized = false

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentBroadcastBinding {
        return FragmentBroadcastBinding.inflate(inflater, container, false)
    }

    override fun initializePresenter() {
        val app = WeatherApplication.getInstance()
        val locationRepository =  LocationRepository.getInstance(
            LocationLocalDataSource.getInstance(app.locationService)
        )
        val weatherRepository= WeatherRepository.getInstance(
            WeatherRemoteDataSource.getInstance(),
            WeatherLocalDataSource.getInstance()
        )
        presenter = BroadcastPresenter(
            locationRepository,
            weatherRepository
        )
        presenter?.attachView(this)
    }

    override fun setupViews() {
        setupRecyclerViews()
        setupTabs()
        isInitialized = true

        // Load initial data
        presenter?.loadWeatherInfo()
        presenter?.loadBroadcasts(DailyWeatherType.TODAY)
    }

    override fun setupListeners() {
        binding.apply {
            tvTabRecommend.setOnClickListener {
                if (currentTab != DailyWeatherType.TODAY) {
                    showLoading()
                    clearAdapterData()
                    presenter?.onTabSelected(DailyWeatherType.TODAY)
                }
            }

            tvTabArtist.setOnClickListener {
                if (currentTab != DailyWeatherType.TOMORROW) {
                    showLoading()
                    clearAdapterData()
                    presenter?.onTabSelected(DailyWeatherType.TOMORROW)
                }
            }

            tvTabAllSongs.setOnClickListener {
                if (currentTab != DailyWeatherType.WEEK) {
                    showLoading()
                    clearAdapterData()
                    presenter?.onTabSelected(DailyWeatherType.WEEK)
                }
            }

            ivSearch.setOnClickListener {
                findNavController().navigate(R.id.search_weather_fragment)
            }
        }
    }

    private fun setupRecyclerViews() {
        dailyWeatherAdapter = DailyWeatherAdapter()

        binding.rvDailyWeather.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dailyWeatherAdapter
        }
    }

    private fun setupTabs() {
        updateTabSelection(DailyWeatherType.TODAY)
    }

    private fun updateTabSelection(selectedTab: DailyWeatherType) {
        if (!isInitialized && currentTab == selectedTab) return

        binding.apply {
            tvTabRecommend.apply {
                setBackgroundResource(R.drawable.bg_tab_unselected)
                setTypeface(null, Typeface.NORMAL)
            }
            tvTabArtist.apply {
                setBackgroundResource(R.drawable.bg_tab_unselected)
                setTypeface(null, Typeface.NORMAL)
            }
            tvTabAllSongs.apply {
                setBackgroundResource(R.drawable.bg_tab_unselected)
                setTypeface(null, Typeface.NORMAL)
            }

            when (selectedTab) {
                DailyWeatherType.TODAY -> {
                    tvTabRecommend.apply {
                        setBackgroundResource(R.drawable.bg_tab_selected)
                        setTypeface(null, Typeface.BOLD)
                    }
                }
                DailyWeatherType.TOMORROW -> {
                    tvTabArtist.apply {
                        setBackgroundResource(R.drawable.bg_tab_selected)
                        setTypeface(null, Typeface.BOLD)
                    }
                }
                DailyWeatherType.WEEK -> {
                    tvTabAllSongs.apply {
                        setBackgroundResource(R.drawable.bg_tab_selected)
                        setTypeface(null, Typeface.BOLD)
                    }
                }
            }
        }
    }

    override fun showBroadcasts(broadcasts: List<DailyWeather>) {
        dailyWeatherAdapter.submitList(broadcasts)
    }

    override fun updateSelectedTab(tabType: DailyWeatherType) {
        if (currentTab == tabType && isInitialized) return
        currentTab = tabType
        updateTabSelection(tabType)
    }

    override fun showWeatherInfo(location: String, temperature: String) {
        binding.apply {
            tvLocation.text = location
            tvTemperature.text = temperature
        }
    }

    private fun clearAdapterData() {
        dailyWeatherAdapter.submitList(emptyList())
    }
}
