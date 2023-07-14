package com.android.batya.tictactoe.presentation.online.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.repository.GameRepository

class PlayerViewModel(private val gameRepository: GameRepository) : ViewModel() {
    private var _currentPlayerLiveData: MutableLiveData<Result<String>> = MutableLiveData()
    val currentPlayerLiveData: LiveData<Result<String>> get() = _currentPlayerLiveData

    private var _firstPlayerLiveData: MutableLiveData<Result<String>> = MutableLiveData()
    val firstPlayerLiveData: LiveData<Result<String>> get() = _firstPlayerLiveData

    fun setCurrentPlayer(roomId: String, playerId: String) {
        gameRepository.setCurrentPlayer(roomId, playerId)
    }

    fun getCurrentPlayer(roomId: String) {
        _currentPlayerLiveData = gameRepository.getCurrentPlayer(roomId)
    }

    fun setFirstTurnPlayer(roomId: String, playerId: String) {
        gameRepository.setFirstTurnPlayer(roomId, playerId)
        Log.d("TAG", "setFirstTurnPlayer: $roomId, $playerId")
    }

    fun getFirstTurnPlayer(roomId: String) {
        _firstPlayerLiveData = gameRepository.getFirstTurnPlayer(roomId)
    }
}