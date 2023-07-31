package com.batya.tictactoe.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batya.tictactoe.domain.model.NotificationData
import com.batya.tictactoe.domain.model.PushNotification
import com.batya.tictactoe.domain.repository.NotificationRepository
import kotlinx.coroutines.launch

class NotificationsViewModel(private val notificationRepository: NotificationRepository) : ViewModel() {
    fun sendNotification(fromName: String, toToken: String) {
        viewModelScope.launch {
            notificationRepository.sendNotification(
                PushNotification(
                    data = NotificationData(
                        title = "Приглашение",
                        message = "$fromName приглашает вас в матч!"
                    ),
                    to = toToken
                )

            )
        }

    }
}