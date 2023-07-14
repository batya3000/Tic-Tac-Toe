package com.android.batya.tictactoe.presentation.waiting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.model.Room
import com.android.batya.tictactoe.domain.model.User
import com.android.batya.tictactoe.domain.repository.RoomRepository

class RoomViewModel(private val roomRepository: RoomRepository) : ViewModel() {
    private var _roomsLiveData: MutableLiveData<Result<List<Room>>> = MutableLiveData()
    val roomsLiveData: LiveData<Result<List<Room>>> get() = _roomsLiveData


    fun createRoom(room: Room) {
        roomRepository.createRoom(room)
    }

    fun connect(roomId: String, user: User) {
        roomRepository.connect(roomId, user)

    }
    fun updateIsRunning(roomId: String, isRunning: Boolean) {
        roomRepository.updateIsRunning(roomId, isRunning)

    }

    fun disconnect(roomId: String, user: User) {
        roomRepository.disconnect(roomId, user)
    }

    fun removeRoom(roomId: String) {
        roomRepository.removeRoom(roomId)
    }

    fun getRooms() {
        _roomsLiveData = roomRepository.getRooms()
    }

}