package com.batya.tictactoe.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.ContextCompat.getSystemService
import com.batya.tictactoe.R
import com.batya.tictactoe.domain.model.UserStatus

fun getWordPlayersInCase(count: Int): String {
    return if ((count % 10 == 1) && (count % 100 != 11)) "игрок"
    else if ((count % 10 == 2 || count % 10 == 3 || count % 10 == 4)
        && !(count % 100 == 12 || count % 100 == 13 || count % 100 == 14)) {
        "игрока"
    } else "игроков"
}

fun getWordFoundInCase(count: Int): String {
    return if ((count % 10 == 1) && (count % 100 != 11)) "Найден"
    else "Найдено"
}


fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // For 29 api or above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else ->     false
        }
    }
    else {
        if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting) {
            return true
        }
    }
    return false
}

fun vibrateDevice(context: Context, time: Long) {
    val vibrator = getSystemService(context, Vibrator::class.java)
    vibrator?.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE))
}

fun getStatusColor(status: UserStatus): Int {
    return when(status) {
        UserStatus.ONLINE -> R.color.user_status_online
        UserStatus.OFFLINE -> R.color.user_status_offline
        UserStatus.WAITING -> R.color.user_status_waiting
        UserStatus.IN_BATTLE -> R.color.user_status_battle
    }
}