package com.android.batya.tictactoe.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

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
