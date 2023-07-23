package com.android.batya.tictactoe.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.batya.tictactoe.domain.model.User
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.repository.UserRepository

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    private var _profileLiveData: MutableLiveData<Result<User>> = MutableLiveData()
    val profileLiveData: LiveData<Result<User>> get() = _profileLiveData

    private var _usersLiveData: MutableLiveData<Result<List<User>>> = MutableLiveData()
    val usersLiveData: LiveData<Result<List<User>>> get() = _usersLiveData

    fun getProfile(userId: String) {
        _profileLiveData = userRepository.getUser(userId)

    }
    fun getUsers() {
        _usersLiveData = userRepository.getUsers()

    }
    fun updateAccountType(userId: String, isAnonymousAccount: Boolean) {
        userRepository.updateAccountType(userId, isAnonymousAccount)
    }

    fun removeFriend(userId: String, friendId: String) {
        userRepository.removeFriend(userId, friendId)
    }

    fun updateUserName(userId: String, name: String) {
        userRepository.updateUserName(userId, name)
    }
}