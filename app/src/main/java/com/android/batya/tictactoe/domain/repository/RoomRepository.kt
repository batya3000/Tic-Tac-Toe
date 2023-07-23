package com.android.batya.tictactoe.domain.repository

import androidx.lifecycle.MutableLiveData
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.model.Room
import com.android.batya.tictactoe.domain.model.User

interface RoomRepository {



    fun createRoom(room: Room)

    fun connect(roomId: String, userId: String)

    fun disconnect(roomId: String, userId: String)

    fun updateIsRunning(roomId: String, isRunning: Boolean)

    fun removeRoom(roomId: String)

    fun getWaitingPool(): MutableLiveData<Result<List<User>>>
//    fun getConnectingPool(): MutableLiveData<Result<List<User>>>


}