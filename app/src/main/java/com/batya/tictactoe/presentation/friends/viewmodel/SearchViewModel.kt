package com.batya.tictactoe.presentation.friends.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.batya.tictactoe.domain.model.FriendInvitation
import com.batya.tictactoe.domain.model.User
import com.batya.tictactoe.domain.model.Result
import com.batya.tictactoe.domain.repository.InvitationRepository
import com.batya.tictactoe.domain.repository.UserRepository

class SearchViewModel(
    private val userRepository: UserRepository
    ) : ViewModel() {
    private var _userLiveData: MutableLiveData<Result<User>> = MutableLiveData()
    val userLiveData: LiveData<Result<User>> get() = _userLiveData

    private var _searchLiveData: MutableLiveData<Result<List<User>>> = MutableLiveData()
    val searchLiveData: LiveData<Result<List<User>>> get() = _searchLiveData

    private var _usersLiveData: MutableLiveData<Result<List<User>>> = MutableLiveData()
    val usersLiveData: LiveData<Result<List<User>>> get() = _usersLiveData

    fun getUsers() {
        _usersLiveData = userRepository.getUsers()
    }

    fun getUser(userId: String) {
        _userLiveData = userRepository.getUser(userId)
    }

    fun searchUser(query: String) {
        _searchLiveData = userRepository.searchUser(query)
    }

}