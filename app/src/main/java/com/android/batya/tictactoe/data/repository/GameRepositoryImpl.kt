package com.android.batya.tictactoe.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.batya.tictactoe.domain.model.Turn

import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.model.User
import com.android.batya.tictactoe.domain.repository.GameRepository
import com.android.batya.tictactoe.util.Constants
import com.android.batya.tictactoe.util.Constants.TURNS_REF
import com.android.batya.tictactoe.util.Constants.CURRENT_PLAYER_REF
import com.android.batya.tictactoe.util.Constants.CURRENT_TURN_START_TIME_REF
import com.android.batya.tictactoe.util.Constants.FIRST_TURN_PLAYER_REF
import com.android.batya.tictactoe.util.Constants.LAST_TURN_REF
import com.android.batya.tictactoe.util.Constants.MATCH_START_TIME_REF
import com.android.batya.tictactoe.util.Constants.TIMESTAMP_REF
import com.android.batya.tictactoe.util.Constants.WINNER_REF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class GameRepositoryImpl(
    private val auth: FirebaseAuth,
    private val gamesReference: DatabaseReference
) : GameRepository {
    private val userId = auth.currentUser!!.uid

    override fun disconnect(roomId: String) {
        gamesReference.child(roomId).child(Constants.CONNECTIONS_REF).child(userId).removeValue()

    }

    override fun removeRoom(roomId: String) {
        gamesReference.child(roomId).removeValue()

    }

    override fun getUsers(roomId: String): MutableLiveData<Result<List<User>>> {
        val connectionsLiveData: MutableLiveData<Result<List<User>>> = MutableLiveData()

        gamesReference.child(roomId).child(Constants.CONNECTIONS_REF).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<User>()
                for (s in snapshot.children) {
                    val user = s.getValue(User::class.java)
                    if (user != null) {
                        users.add(user)
                    }
                }
                Log.d("TAG", "Get users(connections): ${users}")
                connectionsLiveData.value = Result.Success(users)
            }
            override fun onCancelled(error: DatabaseError) {
                connectionsLiveData.value = Result.Failure(error.message)
                Log.w("TAG", "Failed to read connectionsLiveData.", error.toException())
            }
        })
        return connectionsLiveData
    }


    override fun setCell(roomId: String, turn: Turn) {
        gamesReference.child(roomId).child(TURNS_REF).push().setValue(turn)
    }

    override fun getTurns(roomId: String): MutableLiveData<Result<List<Turn>>> {
        val turnsLiveData: MutableLiveData<Result<List<Turn>>> = MutableLiveData()

        gamesReference.child(roomId).child(TURNS_REF).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val turns = mutableListOf<Turn>()
                for (s in snapshot.children) {
                    val turn = s.getValue(Turn::class.java)
                    if (turn != null) {
                        turns.add(turn)
                    }
                }
                Log.d("TAG", "Get turns: ${turns.toList()}")
                turnsLiveData.value = Result.Success(turns.toList())
            }
            override fun onCancelled(error: DatabaseError) {
                turnsLiveData.value = Result.Failure(error.message)
                Log.w("TAG", "Failed to read turnsLiveData.", error.toException())
            }
        })
        return turnsLiveData
    }

    override fun setCurrentPlayer(roomId: String, playerId: String) {
        gamesReference.child(roomId).child(CURRENT_PLAYER_REF).setValue(playerId)
    }

    override fun getCurrentPlayer(roomId: String): MutableLiveData<Result<String>> {
        val currentPlayerLiveData: MutableLiveData<Result<String>> = MutableLiveData()

        gamesReference.child(roomId).child(CURRENT_PLAYER_REF).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val playerId = snapshot.getValue<String>()
                if (playerId != null) {
                    currentPlayerLiveData.value = Result.Success(playerId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                currentPlayerLiveData.value = Result.Failure(error.message)
                Log.w("TAG", "Failed to read currentPlayerLiveData.", error.toException())
            }

        })

        return currentPlayerLiveData
    }

    override fun setLastTurn(roomId: String, turn: Turn) {
        gamesReference.child(roomId).child(LAST_TURN_REF).setValue(turn)

    }
    override fun getLastTurn(roomId: String): MutableLiveData<Result<Turn>> {
        val lastTurnLiveData: MutableLiveData<Result<Turn>> = MutableLiveData()

        gamesReference.child(roomId).child(LAST_TURN_REF).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val turn = snapshot.getValue<Turn>()
                if (turn != null) {
                    lastTurnLiveData.value = Result.Success(turn)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                lastTurnLiveData.value = Result.Failure(error.message)
                Log.w("TAG", "Failed to read lastTurnLiveData.", error.toException())
            }

        })

        return lastTurnLiveData
    }

    override fun setWinner(roomId: String, playerId: String) {
        gamesReference.child(roomId).child(WINNER_REF).setValue(playerId)
    }

    override fun getWinner(roomId: String): MutableLiveData<Result<String>> {
        val winnerLiveData: MutableLiveData<Result<String>> = MutableLiveData()

        gamesReference.child(roomId).child(WINNER_REF).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val winner = snapshot.getValue<String>()
                if (winner != null) {
                    winnerLiveData.value = Result.Success(winner)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                winnerLiveData.value = Result.Failure(error.message)
                Log.w("TAG", "Failed to read winnerLiveData.", error.toException())
            }

        })

        return winnerLiveData
    }


    override fun setFirstTurnPlayer(roomId: String, playerId: String) {
        gamesReference.child(roomId).child(FIRST_TURN_PLAYER_REF).setValue(playerId)
    }

    override fun getFirstTurnPlayer(
        roomId: String
    ): MutableLiveData<Result<String>> {
        val firstTurnLiveData: MutableLiveData<Result<String>> = MutableLiveData()

        gamesReference.child(roomId).child(FIRST_TURN_PLAYER_REF).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val firstTurn = snapshot.getValue<String>()
                if (firstTurn != null) {
                    firstTurnLiveData.value = Result.Success(firstTurn)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                firstTurnLiveData.value = Result.Failure(error.message)
                Log.w("TAG", "Failed to read firstTurnLiveData.", error.toException())
            }

        })

        return firstTurnLiveData
    }

    override fun setMatchStartTime(roomId: String) {
        gamesReference.child(roomId).child(MATCH_START_TIME_REF).setValue(ServerValue.TIMESTAMP)
    }

    override fun getMatchStartTime(roomId: String): MutableLiveData<Result<Long>> {
        val matchStartLiveData: MutableLiveData<Result<Long>> = MutableLiveData()

        gamesReference.child(roomId).child(MATCH_START_TIME_REF).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val time = snapshot.getValue<Long>()
                if (time != null) {
                    matchStartLiveData.value = Result.Success(time)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                matchStartLiveData.value = Result.Failure(error.message)
                Log.w("TAG", "Failed to read matchStartLiveData.", error.toException())
            }
        })
        return matchStartLiveData
    }

    override fun setTimestamp(roomId: String) {
        gamesReference.child(roomId).child(TIMESTAMP_REF).setValue(ServerValue.TIMESTAMP)
    }

    override fun getTimestamp(roomId: String): MutableLiveData<Result<Long>> {
        val timestampLiveData: MutableLiveData<Result<Long>> = MutableLiveData()

        gamesReference.child(roomId).child(TIMESTAMP_REF).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val time = snapshot.getValue<Long>()
                if (time != null) {
                    timestampLiveData.value = Result.Success(time)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                timestampLiveData.value = Result.Failure(error.message)
                Log.w("TAG", "Failed to read timestampLiveData.", error.toException())
            }
        })
        return timestampLiveData
    }

    override fun setCurrentTurnStartTime(roomId: String) {
        gamesReference.child(roomId).child(CURRENT_TURN_START_TIME_REF).setValue(ServerValue.TIMESTAMP)
    }

    override fun getCurrentTurnStartTime(roomId: String): MutableLiveData<Result<Long>> {
        val currentTurnStartLiveData: MutableLiveData<Result<Long>> = MutableLiveData()

        gamesReference.child(roomId).child(CURRENT_TURN_START_TIME_REF).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val time = snapshot.getValue<Long>()
                if (time != null) {
                    currentTurnStartLiveData.value = Result.Success(time)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                currentTurnStartLiveData.value = Result.Failure(error.message)
                Log.w("TAG", "Failed to read currentTurnStartLiveData.", error.toException())
            }
        })
        return currentTurnStartLiveData
    }

}