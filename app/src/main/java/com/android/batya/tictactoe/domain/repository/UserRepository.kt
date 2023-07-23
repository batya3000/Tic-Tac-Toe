package com.android.batya.tictactoe.domain.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.android.batya.tictactoe.domain.model.Game
import com.android.batya.tictactoe.domain.model.User
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.model.UserStatus

interface UserRepository {

    fun getUser(userId: String): MutableLiveData<Result<User>>
    fun searchUser(query: String): MutableLiveData<Result<List<User>>>

    fun getUsers(): MutableLiveData<Result<List<User>>>

    fun updateUserName(userId: String, name: String)

    fun updatePoints(userId: String, points: Int)

    fun updateAccountType(userId: String, isAnonymousAccount: Boolean)
    fun updateStatus(userId: String, userStatus: UserStatus)
    fun updateToken(userId: String, token: String)

    fun updateRoomConnected(userId: String, roomId: String?)

    fun saveGame(userId: String, game: Game)


    fun addFriend(user1Id: String, user2Id: String)

    fun removeFriend(user1Id: String, user2Id: String)

}