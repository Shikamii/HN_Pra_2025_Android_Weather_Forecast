package com.sun.weatherapp.data.helper

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper private constructor(context: Context) {

    companion object {
        private const val PREF_NAME = "my_shared_pref"
        const val KEY_REMEMBER_ME = "remember_me"
        const val KEY_EMAIL = "email"
        const val KEY_PASSWORD = "password"

        @Volatile
        private var instance: PreferenceHelper? = null

        fun getInstance(context: Context): PreferenceHelper {
            return instance ?: synchronized(this) {
                instance ?: PreferenceHelper(context.applicationContext).also { instance = it }
            }
        }
    }

    private val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}
