package com.android.batya.tictactoe.domain.repository

import com.android.batya.tictactoe.domain.model.PushNotification


interface NotificationRepository {

    suspend fun sendNotification(pushNotification: PushNotification)

}