package com.android.batya.tictactoe.presentation.di.network

import com.android.batya.tictactoe.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


inline fun <reified T> createRetrofit(): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return retrofit.create(T::class.java)
}