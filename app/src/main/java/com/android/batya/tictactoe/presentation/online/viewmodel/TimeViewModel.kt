package com.android.batya.tictactoe.presentation.online.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.repository.GameRepository

class TimeViewModel(private val gameRepository: GameRepository) : ViewModel() {
    private var _startTimeLiveData: MutableLiveData<Result<Long>> = MutableLiveData()
    val startTimeLiveData: LiveData<Result<Long>> get() = _startTimeLiveData

    private var _timestampLiveData: MutableLiveData<Result<Long>> = MutableLiveData()
    val timestampLiveData: LiveData<Result<Long>> get() = _timestampLiveData

    private var _currentTurnStartTimeLiveData: MutableLiveData<Result<Long>> = MutableLiveData()
    val currentTurnStartTimeLiveData: LiveData<Result<Long>> get() = _currentTurnStartTimeLiveData


    fun setMatchStartTime(roomId: String) {
        gameRepository.setMatchStartTime(roomId)
    }
    fun getMatchStartTime(roomId: String) {
        _startTimeLiveData = gameRepository.getMatchStartTime(roomId)
    }

    fun setTimestamp(roomId: String) {
        gameRepository.setTimestamp(roomId)
    }
    fun getTimestamp(roomId: String) {
        _timestampLiveData = gameRepository.getTimestamp(roomId)
    }

    fun setCurrentTurnStartTime(roomId: String) {
        gameRepository.setCurrentTurnStartTime(roomId)
    }
    fun getCurrentTurnStartTime(roomId: String) {
        _currentTurnStartTimeLiveData = gameRepository.getCurrentTurnStartTime(roomId)
    }

}