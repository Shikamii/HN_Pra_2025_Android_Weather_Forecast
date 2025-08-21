package com.sun.weatherapp.screen.search_result

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sun.weatherapp.R
import com.sun.weatherapp.data.model.City
import com.sun.weatherapp.data.model.WeatherDetailResponse
import com.sun.weatherapp.data.reposiroty.WeatherRepository
import com.sun.weatherapp.data.reposiroty.source.local.WeatherLocalDataSource
import com.sun.weatherapp.data.reposiroty.source.remote.WeatherRemoteDataSource
import com.sun.weatherapp.databinding.FragmentSearchResultBinding
import com.sun.weatherapp.screen.base.BaseFragment
import com.sun.weatherapp.screen.search_result.adapter.ForecastAdapter
import com.sun.weatherapp.utils.toCelsius
import com.sun.weatherapp.utils.toKmPerHour

class SearchResultFragment : BaseFragment<FragmentSearchResultBinding, SearchResultPresenter>(),
    SearchResultContract.View {

    private lateinit var forecastAdapter: ForecastAdapter
    private var selectedCity: City? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedCity = arguments?.getParcelable("city")
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchResultBinding {
        return FragmentSearchResultBinding.inflate(inflater, container, false)
    }

    override fun initializePresenter() {
        val weatherRepository = WeatherRepository.getInstance(
            WeatherRemoteDataSource.getInstance(),
            WeatherLocalDataSource.getInstance()
        )
        presenter = SearchResultPresenter(weatherRepository)
        presenter?.attachView(this)
    }

    override fun setupViews() {
        setupRecyclerView()
        setupCityInfo()

        selectedCity?.let { city ->
            presenter?.loadWeatherData(city.lat, city.lon)
        }
    }

    override fun setupListeners() {
        binding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun showWeatherData(weatherData: WeatherDetailResponse) {
        binding.apply {
            val tempCelsius = weatherData.current.temp.toCelsius()
            tvMainTemperature.text = "${tempCelsius}°"

            val weatherDescription =
                weatherData.current.weather.firstOrNull()?.main ?: getString(R.string.clear_weather)
            tvWeatherDescription.text = weatherDescription

            val feelsLikeCelsius = weatherData.current.feels_like.toCelsius()
            tvFeelsLike.text =
                getString(R.string.feels_like_temp, feelsLikeCelsius.toString())

            tvHumidity.text = "${weatherData.current.humidity}%"
            tvWind.text =
                "${weatherData.current.wind_speed.toKmPerHour()} ${getString(R.string.kmh)}"

            val dayTemp =
                weatherData.daily.firstOrNull()?.temp?.day?.toCelsius() ?: 0
            val nightTemp =
                weatherData.daily.firstOrNull()?.temp?.night?.toCelsius() ?: 0
            tvSunrise.text = "${dayTemp}°"
            tvSunset.text = "${nightTemp}°"

            val forecastData = weatherData.daily.take(3)
            if (forecastData.isEmpty()) {
                rvForecast.visibility = View.GONE
                tvEmptyForecast.visibility = View.VISIBLE
            } else {
                rvForecast.visibility = View.VISIBLE
                tvEmptyForecast.visibility = View.GONE
                forecastAdapter.submitList(forecastData)
            }
        }
    }

    override fun showError(message: String) {}

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun setupRecyclerView() {
        forecastAdapter = ForecastAdapter()
        binding.rvForecast.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = forecastAdapter
        }
    }

    private fun setupCityInfo() {
        selectedCity?.let { city ->
            binding.tvCityName.text = city.name
        }
    }
}
