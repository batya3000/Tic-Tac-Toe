package com.android.batya.tictactoe.domain.repository

import androidx.lifecycle.MutableLiveData
import com.android.batya.tictactoe.domain.model.Turn
import com.android.batya.tictactoe.domain.model.Result

interface GameRepository {
    fun removeRoom(roomId: String)

    fun getConnections(roomId: String): MutableLiveData<Result<List<String>>>

    fun setCell(roomId: String, turn: Turn)

    fun getTurns(roomId: String): MutableLiveData<Result<List<Turn>>>

    fun setCurrentPlayer(roomId: String, playerId: String)
    fun getCurrentPlayer(roomId: String): MutableLiveData<Result<String>>

    fun setLastTurn(roomId: String, turn: Turn)
    fun getLastTurn(roomId: String): MutableLiveData<Result<Turn>>

    fun setWinner(roomId: String, playerId: String)
    fun getWinner(roomId: String): MutableLiveData<Result<String>>

    fun setFirstTurnPlayer(roomId: String, playerId: String)
    fun getFirstTurnPlayer(roomId: String): MutableLiveData<Result<String>>

    fun setMatchStartTime(roomId: String)
    fun getMatchStartTime(roomId: String): MutableLiveData<Result<Long>>

    fun setTimestamp(roomId: String)
    fun getTimestamp(roomId: String): MutableLiveData<Result<Long>>

    fun setCurrentTurnStartTime(roomId: String)
    fun getCurrentTurnStartTime(roomId: String): MutableLiveData<Result<Long>>
}