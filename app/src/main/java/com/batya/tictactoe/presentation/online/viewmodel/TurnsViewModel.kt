package com.batya.tictactoe.presentation.online.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.batya.tictactoe.domain.model.Turn
import com.batya.tictactoe.domain.model.Result
import com.batya.tictactoe.domain.repository.GameRepository

class TurnsViewModel(private val gameRepository: GameRepository) : ViewModel() {
    private var _turnsLiveData: MutableLiveData<Result<List<Turn>>> = MutableLiveData()
    val turnsLiveData: LiveData<Result<List<Turn>>> get() = _turnsLiveData

    private var _lastTurnLiveData: MutableLiveData<Result<Turn>> = MutableLiveData()
    val lastTurnLiveData: LiveData<Result<Turn>> get() = _lastTurnLiveData

    private var _winnerLiveData: MutableLiveData<Result<String>> = MutableLiveData()
    val winnerLiveData: LiveData<Result<String>> get() = _winnerLiveData



    fun setCell(roomId: String, turn: Turn) {
        gameRepository.setCell(roomId, turn)
        setLastTurn(roomId, turn)
    }

    private fun setLastTurn(roomId: String, turn: Turn) {
        gameRepository.setLastTurn(roomId, turn)
    }

    fun getTurns(roomId: String) {
        _turnsLiveData = gameRepository.getTurns(roomId)
    }

    fun getLastTurn(roomId: String) {
        _lastTurnLiveData = gameRepository.getLastTurn(roomId)
    }

    fun setWinner(roomId: String, playerId: String) {
        gameRepository.setWinner(roomId, playerId)
    }

    fun getWinner(roomId: String) {
        _winnerLiveData = gameRepository.getWinner(roomId)
    }
}