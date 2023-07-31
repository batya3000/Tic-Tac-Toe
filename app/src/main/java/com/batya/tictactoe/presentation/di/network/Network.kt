package com.batya.tictactoe.presentation.di.network

import com.batya.tictactoe.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


inline fun <reified T> createRetrofit(): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return retrofit.create(T::class.java)
}