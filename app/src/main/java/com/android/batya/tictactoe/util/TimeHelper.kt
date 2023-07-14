package com.android.batya.tictactoe.util

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


fun timeToString(time: Long): String {
    return SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
    //format(DateTimeFormatter.ofPattern("mm:ss"))
}

