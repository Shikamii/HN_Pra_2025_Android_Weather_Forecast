package com.sun.mvp.data.repository.source.remote.fetchjson

import android.util.Log
import com.sun.weatherapp.data.model.City
import com.sun.weatherapp.data.model.WeatherEntry
import com.sun.weatherapp.utils.notNull
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ParseDataWithJson {
    fun parseJsonToData(jsonObject: JSONObject?, keyEntity: String): Any? {
        try {
            jsonObject?.notNull {
                return when (keyEntity) {
                    WeatherEntry.WEATHER -> ParseJson().parseWeatherJson(it)
                    WeatherEntry.WEATHER_DETAIL -> ParseJson().parseWeatherDetailJson(it)
                    else -> null
                }
            }
        } catch (e: JSONException) {
            Log.e("ParseDataWithJson", "parseJsonToData: ", e)
        }
        return null
    }

    fun parseJsonArrayToData(jsonArray: JSONArray?, keyEntity: String): Any? {
        try {
            jsonArray?.let { array ->
                return when (keyEntity) {
                    WeatherEntry.WEATHER -> {
                        val list = mutableListOf<City>()
                        for (i in 0 until array.length()) {
                            val item = array.getJSONObject(i)
                            val city = ParseJson().parseCityJson(item)
                            list.add(city)
                        }
                        list
                    }
                    else -> null
                }
            }
        } catch (e: JSONException) {
            Log.e("ParseDataWithJson", "parseJsonArrayToData: ", e)
        }
        return null
    }
}
