package com.android.batya.tictactoe.presentation.online.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.batya.tictactoe.domain.model.FriendInvitation
import com.android.batya.tictactoe.domain.model.Game
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.model.User
import com.android.batya.tictactoe.domain.repository.GameRepository
import com.android.batya.tictactoe.domain.repository.InvitationRepository
import com.android.batya.tictactoe.domain.repository.UserRepository

class UsersViewModel(
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository,
    private val invitationRepository: InvitationRepository,

    ) : ViewModel() {
    private var _usersLiveData: MutableLiveData<Result<List<User>>> = MutableLiveData()
    val usersLiveData: LiveData<Result<List<User>>> get() = _usersLiveData


    fun disconnect(roomId: String) {
        gameRepository.disconnect(roomId)
    }

    fun removeRoom(roomId: String) {
        gameRepository.removeRoom(roomId)
    }

    fun saveGame(userId: String, game: Game) {
        userRepository.saveGame(userId, game)
    }

    fun getConnections(roomId: String) {
        _usersLiveData = gameRepository.getUsers(roomId)
    }

    fun updatePoints(userId: String, points: Int) {
        userRepository.updatePoints(userId, points)
    }

    fun sendInvitation(friendInvitation: FriendInvitation) {
        invitationRepository.sendFriendInvitation(friendInvitation)
    }

//    fun addFriend(userId: String, friendId: String) {
//        userRepository.addFriend(userId, friendId)
//    }

}