package com.sun.weatherapp.screen.broadcast.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sun.weatherapp.data.model.HourlyWeather
import com.sun.weatherapp.databinding.ItemHourlyRainProgressBinding
import java.util.*
import kotlin.math.roundToInt

class HourlyRainProgressAdapter :
    ListAdapter<HourlyWeather, HourlyRainProgressAdapter.HourlyRainProgressViewHolder>(
        HourlyWeatherDiffCallback()
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HourlyRainProgressViewHolder {
        val binding = ItemHourlyRainProgressBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HourlyRainProgressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyRainProgressViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HourlyRainProgressViewHolder(
        private val binding: ItemHourlyRainProgressBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(hourlyWeather: HourlyWeather) {
            binding.apply {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = hourlyWeather.dt * 1000
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                tvHour.text = "${hour}h"

                val rainPercentage = (hourlyWeather.pop * 100).roundToInt()
                tvPercentage.text = "$rainPercentage%"

                progressContainer.post {
                    val containerWidth = progressContainer.width
                    val progressWidth = (containerWidth * rainPercentage / 100.0f).toInt()

                    val params = progressView.layoutParams
                    params.width = maxOf(progressWidth, 0)
                    progressView.layoutParams = params
                }
            }
        }
    }

    private class HourlyWeatherDiffCallback : DiffUtil.ItemCallback<HourlyWeather>() {
        override fun areItemsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
            return oldItem == newItem
        }
    }
}
