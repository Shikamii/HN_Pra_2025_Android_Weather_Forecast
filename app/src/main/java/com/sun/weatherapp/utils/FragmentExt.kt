package com.sun.weatherapp.utils

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import com.sun.weatherapp.R

fun Fragment.showProgressDialog(): AlertDialog? {
    return if (context != null) {
        AlertDialog.Builder(context!!, R.style.AFUtilProgressBarStyle)
            .setCancelable(false)
            .setView(R.layout.dialog_loading)
            .show()
    } else null
}

fun Fragment.showErrorDialog(message: String): AlertDialog? {
    return if (context != null) {
        AlertDialog.Builder(context!!)
            .setTitle(R.string.error)
            .setMessage(message)
            .setCancelable(true)
            .show()
    } else null
}
