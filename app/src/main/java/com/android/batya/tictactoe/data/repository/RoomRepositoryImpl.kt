package com.android.batya.tictactoe.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.model.Room
import com.android.batya.tictactoe.domain.model.User
import com.android.batya.tictactoe.domain.repository.RoomRepository
import com.android.batya.tictactoe.util.Constants
import com.android.batya.tictactoe.util.Constants.ROOMS_CONNECTIONS_REF
import com.android.batya.tictactoe.util.Constants.ROOMS_RUNNING_REF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue


class RoomRepositoryImpl(
    private val auth: FirebaseAuth,
    private val roomsReference: DatabaseReference
) : RoomRepository {
    private val userId = auth.currentUser!!.uid


    override fun createRoom(room: Room) {
        val pushId = roomsReference.push().key
        roomsReference.child(pushId!!).setValue(room.copy(id = pushId))
    }


    override fun connect(roomId: String, user: User) {
        roomsReference.child(roomId).child(ROOMS_CONNECTIONS_REF).child(userId).setValue(user)
    }

    override fun disconnect(roomId: String, user: User) {
        roomsReference.child(roomId).child(ROOMS_CONNECTIONS_REF).child(userId).removeValue()
    }

    override fun updateIsRunning(roomId: String, isRunning: Boolean) {
        roomsReference.child(roomId).updateChildren(mapOf(ROOMS_RUNNING_REF to isRunning))
    }

    override fun removeRoom(roomId: String) {
        roomsReference.child(roomId).removeValue()
    }

    override fun getRooms(): MutableLiveData<Result<List<Room>>> {
        val roomsLiveData: MutableLiveData<Result<List<Room>>> = MutableLiveData()

        roomsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val rooms = mutableListOf<Room>()
                for (s in snapshot.children) {
                    Log.d("TAG", "getRooms. value: ${s.value}")

                    val room = s.getValue(Room::class.java)
                    if (room != null && !room.isRunning) {
                        rooms.add(room)
                    }
                }
                Log.d("TAG", "Get roomsLiveData: $rooms")

                roomsLiveData.value = Result.Success(rooms)
            }
            override fun onCancelled(error: DatabaseError) {
                roomsLiveData.value = Result.Failure(error.message)
                Log.w("TAG", "Failed to read roomsLiveData.", error.toException())
            }
        })
        return roomsLiveData
    }

}