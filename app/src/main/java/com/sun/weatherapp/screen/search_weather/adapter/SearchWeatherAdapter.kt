package com.sun.weatherapp.screen.search_weather.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sun.weatherapp.data.model.City
import com.sun.weatherapp.databinding.ItemSearchWeatherBinding

class SearchWeatherAdapter (
    private val onItemClick: (City) -> Unit
) : ListAdapter<City, SearchWeatherAdapter.SearchWeatherViewHolder>(SearchWeatherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchWeatherViewHolder {
        val binding = ItemSearchWeatherBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchWeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchWeatherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SearchWeatherViewHolder(
        private val binding: ItemSearchWeatherBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(city: City) {
            binding.apply {
                tvCityCurrent.text = city.name
                tvCountryCurrent.text = city.country
                root.setOnClickListener {
                    onItemClick(city)
                }
            }
        }
    }

    private class SearchWeatherDiffCallback : DiffUtil.ItemCallback<City>() {
        override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem == newItem
        }
    }
}
