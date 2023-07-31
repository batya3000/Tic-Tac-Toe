package com.batya.tictactoe.domain.repository

import com.batya.tictactoe.domain.model.PushNotification


interface NotificationRepository {

    suspend fun sendNotification(pushNotification: PushNotification)

}