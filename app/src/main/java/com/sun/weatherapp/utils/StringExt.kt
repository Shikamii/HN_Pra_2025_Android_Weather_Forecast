package com.sun.weatherapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.formatToDate(): String {
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return sdf.format(Date(this * 1000))
}

fun Long.formatToTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(this * 1000))
}