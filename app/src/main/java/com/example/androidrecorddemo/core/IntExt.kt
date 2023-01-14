package com.example.androidrecorddemo.core

import java.text.SimpleDateFormat
import java.util.*

fun Int.toSecondsFormatted(): String {
    val simpleDateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
    val date = Calendar.getInstance().apply {
        timeInMillis = this@toSecondsFormatted.toLong()
    }.time
    return simpleDateFormat.format(date)
}