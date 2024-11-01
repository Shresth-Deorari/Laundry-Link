package com.example.laundrylink

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun convertEpochToString(epochSeconds: Long): String {
    val date = Date(epochSeconds * 1000)
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(date)
}