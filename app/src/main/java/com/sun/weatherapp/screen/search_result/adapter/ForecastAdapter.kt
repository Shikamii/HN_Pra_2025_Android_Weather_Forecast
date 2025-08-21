package com.sun.weatherapp.screen.search_result.adapter

import android.content.Context
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sun.weatherapp.data.model.DailyWeather
import com.sun.weatherapp.databinding.ItemForecastDayBinding
import com.sun.weatherapp.utils.toCelsius
import com.sun.weatherapp.utils.WeatherIconLoader
import java.util.*

class ForecastAdapter :
    ListAdapter<DailyWeather, ForecastAdapter.ForecastViewHolder>(ForecastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val binding = ItemForecastDayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class ForecastViewHolder(
        private val binding: ItemForecastDayBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dailyWeather: DailyWeather, position: Int) {
            binding.apply {
                val context = binding.root.context
                val dayName = when (position) {
                    0 -> context.getString(com.sun.weatherapp.R.string.today)
                    1 -> context.getString(com.sun.weatherapp.R.string.tomorrow)
                    2 -> context.getString(com.sun.weatherapp.R.string.day_after_tomorrow)
                    else -> {
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = dailyWeather.dt * 1000
                        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                        getVietnameseDayName(dayOfWeek, context)
                    }
                }
                tvDayName.text = dayName

                val tempCelsius = dailyWeather.temp.day.toCelsius()
                tvTemperature.text = "${tempCelsius}°"

                val weather = dailyWeather.weather.firstOrNull()
                val iconCode = weather?.icon ?: "01d"
                WeatherIconLoader.loadWeatherIcon(iconCode, ivWeatherIcon)

                ivWeatherIcon.setColorFilter(
                    android.graphics.Color.WHITE,
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        }

        private fun getVietnameseDayName(dayOfWeek: Int, context: Context): String {
            return when (dayOfWeek) {
                Calendar.SUNDAY -> context.getString(com.sun.weatherapp.R.string.sunday)
                Calendar.MONDAY -> context.getString(com.sun.weatherapp.R.string.monday)
                Calendar.TUESDAY -> context.getString(com.sun.weatherapp.R.string.tuesday)
                Calendar.WEDNESDAY -> context.getString(com.sun.weatherapp.R.string.wednesday)
                Calendar.THURSDAY -> context.getString(com.sun.weatherapp.R.string.thursday)
                Calendar.FRIDAY -> context.getString(com.sun.weatherapp.R.string.friday)
                Calendar.SATURDAY -> context.getString(com.sun.weatherapp.R.string.saturday)
                else -> context.getString(com.sun.weatherapp.R.string.unknown_day)
            }
        }
    }

    private class ForecastDiffCallback : DiffUtil.ItemCallback<DailyWeather>() {
        override fun areItemsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
            return oldItem == newItem
        }
    }
}
