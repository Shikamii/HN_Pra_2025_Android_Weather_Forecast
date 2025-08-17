package com.sun.weatherapp.screen.broadcast.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sun.weatherapp.R
import com.sun.weatherapp.data.model.DailyWeather
import com.sun.weatherapp.databinding.ItemDailyWeatherBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyWeatherAdapter : ListAdapter<DailyWeather, DailyWeatherAdapter.DailyWeatherViewHolder>(DailyWeatherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        val binding = ItemDailyWeatherBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DailyWeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DailyWeatherViewHolder(
        private val binding: ItemDailyWeatherBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(dailyWeather: DailyWeather) {
            binding.apply {
                layoutDailyWeatherSecondInfo.visibility = View.GONE
                ivExpand.setImageResource(R.drawable.ic_expand_close)

                tvDay.text = formatDate(dailyWeather.dt)
                tvStatus.text = dailyWeather.weather[0].description
                ivExpand.setOnClickListener{
                    if (layoutDailyWeatherSecondInfo.visibility == View.VISIBLE) {
                        layoutDailyWeatherSecondInfo.visibility = View.GONE
                        ivExpand.setImageResource(R.drawable.ic_expand_close)
                    } else {
                        layoutDailyWeatherSecondInfo.visibility = View.VISIBLE
                        ivExpand.setImageResource(R.drawable.ic_expand_open)
                    }
                }
                tvWindSpeedValue.text = dailyWeather.wind_speed.toString() + " km/h"
                tvHumidityValue.text = dailyWeather.humidity.toString() + " %"
                tvPressureValue.text = dailyWeather.pressure.toString() + " hPa"
                tvUvIndexValue.text = dailyWeather.uvi.toString() + " %"
                tvSunriseValue.text = formatTime(dailyWeather.sunrise)
                tvSunsetValue.text = formatTime(dailyWeather.sunset)
                tvMoonriseValue.text = formatTime(dailyWeather.moonrise ?: 0L)
                tvMoonsetValue.text = formatTime(dailyWeather.moonset ?: 0L)
            }
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

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }
}
