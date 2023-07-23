package com.android.batya.tictactoe.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.batya.tictactoe.domain.model.NotificationData
import com.android.batya.tictactoe.domain.model.PushNotification
import com.android.batya.tictactoe.domain.model.User
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.repository.NotificationRepository
import com.android.batya.tictactoe.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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