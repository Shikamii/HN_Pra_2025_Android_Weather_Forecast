package com.sun.weatherapp.screen.broadcast

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.sun.weatherapp.data.model.DailyWeatherType
import com.sun.weatherapp.data.model.WeatherResponse
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
        presenter = BroadcastPresenter()
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
                setBackgroundResource(com.sun.weatherapp.R.drawable.bg_tab_unselected)
                setTypeface(null, Typeface.NORMAL)
            }
            tvTabArtist.apply {
                setBackgroundResource(com.sun.weatherapp.R.drawable.bg_tab_unselected)
                setTypeface(null, Typeface.NORMAL)
            }
            tvTabAllSongs.apply {
                setBackgroundResource(com.sun.weatherapp.R.drawable.bg_tab_unselected)
                setTypeface(null, Typeface.NORMAL)
            }

            when (selectedTab) {
                DailyWeatherType.TODAY -> {
                    tvTabRecommend.apply {
                        setBackgroundResource(com.sun.weatherapp.R.drawable.bg_tab_selected)
                        setTypeface(null, Typeface.BOLD)
                    }
                }
                DailyWeatherType.TOMORROW -> {
                    tvTabArtist.apply {
                        setBackgroundResource(com.sun.weatherapp.R.drawable.bg_tab_selected)
                        setTypeface(null, Typeface.BOLD)
                    }
                }
                DailyWeatherType.WEEK -> {
                    tvTabAllSongs.apply {
                        setBackgroundResource(com.sun.weatherapp.R.drawable.bg_tab_selected)
                        setTypeface(null, Typeface.BOLD)
                    }
                }
            }
        }
    }

    override fun showBroadcasts(broadcasts: List<WeatherResponse>) {
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
