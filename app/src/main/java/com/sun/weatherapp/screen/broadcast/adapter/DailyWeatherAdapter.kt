package com.sun.weatherapp.screen.broadcast.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sun.weatherapp.R
import com.sun.weatherapp.data.model.WeatherResponse
import com.sun.weatherapp.databinding.ItemDailyWeatherBinding

class DailyWeatherAdapter : ListAdapter<WeatherResponse, DailyWeatherAdapter.DailyWeatherViewHolder>(DailyWeatherDiffCallback()) {

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

        fun bind(weatherResponse: WeatherResponse) {
            binding.apply {
                tvDay.text = weatherResponse.dt.toString()
                tvStatus.text = weatherResponse.weather.firstOrNull()?.description ?: "No data"
                ivExpand.setOnClickListener{
                    if (layoutDailyWeatherSecondInfo.visibility == View.VISIBLE) {
                        layoutDailyWeatherSecondInfo.visibility = View.GONE
                        ivExpand.setImageResource(R.drawable.ic_expand_close)
                    } else {
                        layoutDailyWeatherSecondInfo.visibility = View.VISIBLE
                        ivExpand.setImageResource(R.drawable.ic_expand_open)
                    }
                }
            }
        }
    }

    private class DailyWeatherDiffCallback : DiffUtil.ItemCallback<WeatherResponse>() {
        override fun areItemsTheSame(oldItem: WeatherResponse, newItem: WeatherResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WeatherResponse, newItem: WeatherResponse): Boolean {
            return oldItem == newItem
        }
    }
}
