package com.batya.tictactoe.data.repository

import android.util.Log
import com.batya.tictactoe.data.api.NotificationAPI
import com.batya.tictactoe.domain.model.PushNotification
import com.batya.tictactoe.domain.repository.NotificationRepository
import com.google.gson.Gson

class NotificationRepositoryImpl(
    private val notificationAPI: com.batya.tictactoe.data.api.NotificationAPI,
) : NotificationRepository {


    override suspend fun sendNotification(pushNotification: PushNotification) {
        try {
            val response = notificationAPI.postNotification(pushNotification)
            if (response.isSuccessful) {
                Log.d("TAG", "Response: ${Gson().toJson(response.body())}")
            } else {
                Log.e("TAG", response.errorBody().toString())

            }
        } catch (e: Exception) {
            Log.e("TAG", e.toString())
        }
    }
}