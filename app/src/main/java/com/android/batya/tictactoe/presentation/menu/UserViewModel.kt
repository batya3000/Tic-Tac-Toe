package com.android.batya.tictactoe.presentation.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.model.User
import com.android.batya.tictactoe.domain.model.UserStatus
import com.android.batya.tictactoe.domain.repository.UserRepository

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    private var _userLiveData: MutableLiveData<Result<User>> = MutableLiveData()
    val userLiveData: LiveData<Result<User>> get() = _userLiveData

    fun getUser(userId: String) {
        _userLiveData = userRepository.getUser(userId)

//        thread {
//            Thread.sleep(5000)
//            if (_userLiveData.value == null) {
//                Firebase.auth.signOut()
//            }
//        }
    }
    fun updateStatus(userId: String, userStatus: UserStatus) {
        userRepository.updateStatus(userId, userStatus)
    }

    fun updateToken(userId: String, token: String) {
        userRepository.updateToken(userId, token)
    }

    fun updateRoomConnected(userId: String, roomId: String?) {
        userRepository.updateRoomConnected(userId, roomId)
    }
}