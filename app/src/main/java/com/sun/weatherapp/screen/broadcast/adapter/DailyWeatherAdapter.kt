package com.sun.weatherapp.screen.broadcast.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sun.weatherapp.R
import com.sun.weatherapp.data.model.DailyWeather
import com.sun.weatherapp.data.model.HourlyWeather
import com.sun.weatherapp.data.model.WeatherDetailResponse
import com.sun.weatherapp.databinding.ItemDailyWeatherBinding
import com.sun.weatherapp.utils.formatToDate
import com.sun.weatherapp.utils.formatToTime
import com.sun.weatherapp.utils.toCelsius
import java.util.Calendar

class DailyWeatherAdapter :
    ListAdapter<DailyWeather, DailyWeatherAdapter.DailyWeatherViewHolder>(DailyWeatherDiffCallback()) {

    private var weatherDetailResponse: WeatherDetailResponse? = null
    private lateinit var hourlyRainAdapter: HourlyRainProgressAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        val binding = ItemDailyWeatherBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DailyWeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }


    fun submitListWithHourlyData(
        broadcasts: List<DailyWeather>,
        weatherDetailResponse: WeatherDetailResponse?
    ) {
        this.weatherDetailResponse = weatherDetailResponse
        submitList(broadcasts)
    }

    inner class DailyWeatherViewHolder(
        private val binding: ItemDailyWeatherBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dailyWeather: DailyWeather, position: Int) {
            binding.apply {
                layoutDailyWeatherSecondInfo.visibility = View.GONE
                ivExpand.setImageResource(R.drawable.ic_expand_close)

                tvDay.text = dailyWeather.dt.formatToDate()
                tvStatus.text = dailyWeather.weather[0].description

                ivExpand.setOnClickListener {
                    if (layoutDailyWeatherSecondInfo.isVisible) {
                        layoutDailyWeatherSecondInfo.visibility = View.GONE
                        ivExpand.setImageResource(R.drawable.ic_expand_close)
                    } else {
                        layoutDailyWeatherSecondInfo.visibility = View.VISIBLE
                        ivExpand.setImageResource(R.drawable.ic_expand_open)
                        setupChartsAndHourlyData(dailyWeather, position)
                    }
                }

                tvWindSpeedValue.text = dailyWeather.wind_speed.toString() + " km/h"
                tvHumidityValue.text = dailyWeather.humidity.toString() + " %"
                tvPressureValue.text = dailyWeather.pressure.toString() + " hPa"
                tvUvIndexValue.text = dailyWeather.uvi.toString() + " %"
                tvSunriseValue.text = dailyWeather.sunrise.formatToTime()
                tvSunsetValue.text = dailyWeather.sunset.formatToTime()
                tvMoonriseValue.text = (dailyWeather.moonrise ?: 0L).formatToTime()
                tvMoonsetValue.text = (dailyWeather.moonset ?: 0L).formatToTime()
            }
        }

        private fun setupChartsAndHourlyData(dailyWeather: DailyWeather, dayIndex: Int) {
            val weatherData = weatherDetailResponse ?: return

            binding.apply {
                setupTemperatureChart(weatherData, dailyWeather, dayIndex)
                setupHourlyRainRecyclerView(weatherData, dailyWeather, dayIndex)
            }
        }

        private fun setupTemperatureChart(
            weatherData: WeatherDetailResponse,
            dailyWeather: DailyWeather,
            dayIndex: Int
        ) {
            val startOfDay = Calendar.getInstance().apply {
                timeInMillis = dailyWeather.dt * 1000
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis / 1000

            val endOfDay = startOfDay + (24 * 60 * 60)

            val dayHourlyWeather = if (dayIndex == 0) {
                weatherData.hourly.take(24)
            } else {
                weatherData.hourly.filter { hourly ->
                    hourly.dt in startOfDay..<endOfDay
                }
            }

            if (dayHourlyWeather.isNotEmpty()) {
                val hours = dayHourlyWeather.mapIndexed { index, hourly ->
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = hourly.dt * 1000
                    calendar.get(Calendar.HOUR_OF_DAY).toFloat()
                }

                val temperatures = dayHourlyWeather.map { it.temp.toCelsius().toFloat() }

                binding.tempChart.setData(
                    xValues = hours,
                    yValues = temperatures,
                    highlightIndex = if (dayIndex == 0) 0 else -1,
                    maximumNumberOfDisplayPointInXAxis = 12,
                    title = binding.root.context.getString(R.string.temperature_24h),
                    xT = binding.root.context.getString(R.string.hour_unit)
                )
            }
        }

        private fun setupHourlyRainRecyclerView(
            weatherData: WeatherDetailResponse,
            dailyWeather: DailyWeather,
            dayIndex: Int
        ) {
            val dayHourlyWeather = if (dayIndex == 0) {
                weatherData.hourly.take(6)
            } else {
                generateLimitedHourlyDataFromDaily(dailyWeather)
            }

            hourlyRainAdapter = HourlyRainProgressAdapter()
            binding.rvHourlyRainProbability.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = hourlyRainAdapter
            }

            hourlyRainAdapter.submitList(dayHourlyWeather)
        }

        private fun generateLimitedHourlyDataFromDaily(dailyWeather: DailyWeather): List<HourlyWeather> {
            val baseTimestamp = dailyWeather.dt
            val hourlyList = mutableListOf<HourlyWeather>()

            for (i in 0..5) {
                val timestamp = baseTimestamp + (i * 4 * 60 * 60)
                val tempVariation = when (i) {
                    0 -> dailyWeather.temp.morn
                    1, 2 -> dailyWeather.temp.day
                    3, 4 -> dailyWeather.temp.eve
                    else -> dailyWeather.temp.night
                }

                val hourlyWeather = HourlyWeather(
                    dt = timestamp,
                    temp = tempVariation,
                    feels_like = tempVariation - 1.0,
                    pressure = dailyWeather.pressure,
                    humidity = dailyWeather.humidity,
                    dew_point = dailyWeather.dew_point,
                    uvi = dailyWeather.uvi,
                    clouds = dailyWeather.clouds,
                    visibility = 10000,
                    wind_speed = dailyWeather.wind_speed,
                    wind_deg = dailyWeather.wind_deg,
                    wind_gust = dailyWeather.wind_gust,
                    weather = dailyWeather.weather,
                    pop = dailyWeather.pop,
                    rain = null
                )
                hourlyList.add(hourlyWeather)
            }

            return hourlyList
        }
    }

    private class DailyWeatherDiffCallback : DiffUtil.ItemCallback<DailyWeather>() {
        override fun areItemsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
            return oldItem.weather[0].id == newItem.weather[0].id
        }

        override fun areContentsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
            return oldItem == newItem
        }
    }
}
