package com.batya.tictactoe.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.batya.tictactoe.domain.model.Result
import com.batya.tictactoe.domain.model.Room
import com.batya.tictactoe.domain.model.User
import com.batya.tictactoe.domain.model.UserStatus
import com.batya.tictactoe.domain.repository.RoomRepository
import com.batya.tictactoe.util.Constants.ROOMS_CONNECTIONS_REF
import com.batya.tictactoe.util.Constants.ROOMS_RUNNING_REF
import com.google.firebase.database.*


class RoomRepositoryImpl(
    private val roomsReference: DatabaseReference,
    private val usersReference: DatabaseReference

) : RoomRepository {

    override fun createRoom(room: Room) {
        roomsReference.child(room.id).setValue(room)
    }


    override fun connect(roomId: String, userId: String) {
        roomsReference.child(roomId).child(ROOMS_CONNECTIONS_REF).push().setValue(userId)
    }

    override fun disconnect(roomId: String, userId: String) {
        roomsReference.child(roomId).child(ROOMS_CONNECTIONS_REF).child(userId).removeValue()
    }

    override fun updateIsRunning(roomId: String, isRunning: Boolean) {
        roomsReference.child(roomId).updateChildren(mapOf(ROOMS_RUNNING_REF to isRunning))
    }

    override fun removeRoom(roomId: String) {
        roomsReference.child(roomId).removeValue()
    }

    override fun getWaitingPool(): MutableLiveData<Result<List<User>>> {
        val waitingPoolLiveData: MutableLiveData<Result<List<User>>> = MutableLiveData()

        usersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val waitingUsers = mutableListOf<User>()
                for (s in snapshot.children) {
                    val user = s.getValue(User::class.java)

                    if (user != null && user.status == UserStatus.WAITING) {
                        waitingUsers.add(user)
                    }
                }

                waitingPoolLiveData.value = Result.Success(waitingUsers)
            }
            override fun onCancelled(error: DatabaseError) {
                waitingPoolLiveData.value = Result.Failure(error.message)
                Log.w("TAG", "Failed to read waitingPoolLiveData.", error.toException())
            }
        })
        return waitingPoolLiveData
    }
}