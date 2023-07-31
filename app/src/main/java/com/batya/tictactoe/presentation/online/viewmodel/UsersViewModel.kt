package com.batya.tictactoe.presentation.online.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.batya.tictactoe.domain.model.Game
import com.batya.tictactoe.domain.model.Result
import com.batya.tictactoe.domain.model.User
import com.batya.tictactoe.domain.model.UserStatus
import com.batya.tictactoe.domain.repository.GameRepository
import com.batya.tictactoe.domain.repository.UserRepository

class UsersViewModel(
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private var _usersLiveData: MutableLiveData<Result<List<String>>> = MutableLiveData()
    val usersLiveData: LiveData<Result<List<String>>> get() = _usersLiveData

    private var _meLiveData: MutableLiveData<Result<User>> = MutableLiveData()
    val meLiveData: LiveData<Result<User>> get() = _meLiveData

    private var _enemyLiveData: MutableLiveData<Result<User>> = MutableLiveData()
    val enemyLiveData: LiveData<Result<User>> get() = _enemyLiveData


    fun getMe(myId: String) {
        _meLiveData = userRepository.getUser(myId)
    }
    fun getEnemy(enemyId: String) {
        _enemyLiveData = userRepository.getUser(enemyId)
    }

    fun removeRoom(roomId: String) {
        gameRepository.removeRoom(roomId)
    }

    fun saveGame(userId: String, game: Game) {
        userRepository.saveGame(userId, game)
    }

    fun getConnections(roomId: String) {
        _usersLiveData = gameRepository.getConnections(roomId)
    }

    fun updatePoints(userId: String, points: Int) {
        userRepository.updatePoints(userId, points)
    }
    fun updateStatus(userId: String, userStatus: UserStatus) {
        userRepository.updateStatus(userId, userStatus)
    }

    fun updateRoomConnected(userId: String, roomId: String?) {
        userRepository.updateRoomConnected(userId, roomId)
    }


}