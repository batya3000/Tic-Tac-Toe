package com.android.batya.tictactoe.presentation.menu

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.batya.tictactoe.domain.model.Game
import com.android.batya.tictactoe.domain.model.User
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.repository.UserRepository
import com.android.batya.tictactoe.presentation.auth.AuthActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.concurrent.thread

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    private var _userLiveData: MutableLiveData<Result<User>> = MutableLiveData()
    val userLiveData: LiveData<Result<User>> get() = _userLiveData


    fun updateUserName(userId: String, name: String) {
        userRepository.updateUserName(userId, name)
    }


    fun getUser(userId: String) {
        _userLiveData = userRepository.getUser(userId)

//        thread {
//            Thread.sleep(5000)
//            if (_userLiveData.value == null) {
//                Firebase.auth.signOut()
//            }
//        }
    }


    fun updatePoints(userId: String, points: Int) {
        userRepository.updatePoints(userId, points)
    }

    fun updateAccountType(userId: String, isAnonymousAccount: Boolean) {
        userRepository.updateAccountType(userId, isAnonymousAccount)
    }

    fun removeFriend(userId: String, friendId: String) {
        userRepository.removeFriend(userId, friendId)
    }
}