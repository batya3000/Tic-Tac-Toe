package com.android.batya.tictactoe.domain.repository

import androidx.lifecycle.MutableLiveData
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.model.Room
import com.android.batya.tictactoe.domain.model.User

interface RoomRepository {



    fun createRoom(room: Room)

    fun connect(roomId: String, user: User)

    fun disconnect(roomId: String, user: User)

    fun updateIsRunning(roomId: String, isRunning: Boolean)

    fun removeRoom(roomId: String)

    fun getRooms(): MutableLiveData<Result<List<Room>>>
}