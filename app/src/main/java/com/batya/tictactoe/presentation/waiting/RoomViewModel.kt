package com.batya.tictactoe.presentation.waiting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.batya.tictactoe.domain.model.Result
import com.batya.tictactoe.domain.model.Room
import com.batya.tictactoe.domain.model.User
import com.batya.tictactoe.domain.repository.RoomRepository

class RoomViewModel(private val roomRepository: RoomRepository) : ViewModel() {
    private var _waitingPoolLiveData: MutableLiveData<Result<List<User>>> = MutableLiveData()
    val waitingPoolLiveData: LiveData<Result<List<User>>> get() = _waitingPoolLiveData

    fun createRoom(room: Room) {
        roomRepository.createRoom(room)
    }

    fun connect(roomId: String, userId: String) {
        roomRepository.connect(roomId, userId)
    }

    fun removeRoom(roomId: String) {
        roomRepository.removeRoom(roomId)
    }

    fun getWaitingPool() {
        _waitingPoolLiveData = roomRepository.getWaitingPool()
    }


}