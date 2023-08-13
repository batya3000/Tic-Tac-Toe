package com.batya.tictactoe.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.batya.tictactoe.domain.model.Game
import com.batya.tictactoe.domain.model.Result
import com.batya.tictactoe.domain.model.User
import com.batya.tictactoe.domain.model.UserStatus
import com.batya.tictactoe.domain.repository.UserRepository
import com.batya.tictactoe.util.Constants.USER_ACCOUNT_TYPE_REF
import com.batya.tictactoe.util.Constants.USER_FRIENDS_REF
import com.batya.tictactoe.util.Constants.USER_GAMES_REF
import com.batya.tictactoe.util.Constants.USER_NAME_REF
import com.batya.tictactoe.util.Constants.USER_POINTS_REF
import com.batya.tictactoe.util.Constants.USER_ROOM_CONNECTED_REF
import com.batya.tictactoe.util.Constants.USER_STATUS_REF
import com.batya.tictactoe.util.Constants.USER_TOKEN_REF
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class UserRepositoryImpl(
    private val usersReference: DatabaseReference
) : UserRepository {


    override fun updateUserName(userId: String, name: String) {
        usersReference.child(userId).updateChildren(mapOf(USER_NAME_REF to name))
    }


    override fun updatePoints(userId: String, points: Int) {
        usersReference.child(userId).updateChildren(mapOf(USER_POINTS_REF to points))
    }

    override fun updateAccountType(userId: String, isAnonymousAccount: Boolean) {
        usersReference.child(userId).updateChildren(mapOf(USER_ACCOUNT_TYPE_REF to isAnonymousAccount))

    }

    override fun updateStatus(userId: String, userStatus: UserStatus) {
        usersReference.child(userId).updateChildren(mapOf(USER_STATUS_REF to userStatus))
    }

    override fun updateToken(userId: String, token: String) {
        usersReference.child(userId).updateChildren(mapOf(USER_TOKEN_REF to token))

    }

    override fun updateRoomConnected(userId: String, roomId: String?) {
        usersReference.child(userId).updateChildren(mapOf(USER_ROOM_CONNECTED_REF to roomId))

    }

    override fun saveGame(userId: String, game: Game) {
        usersReference.child(userId).child(USER_GAMES_REF).push().setValue(game)
    }

    override fun addFriend(user1Id: String, user2Id: String) {
        usersReference.child(user1Id).child(USER_FRIENDS_REF).child(user2Id).setValue(user2Id)
        usersReference.child(user2Id).child(USER_FRIENDS_REF).child(user1Id).setValue(user1Id)
    }

    override fun removeFriend(user1Id: String, user2Id: String) {
        usersReference.child(user1Id).child(USER_FRIENDS_REF).child(user2Id).removeValue()
        usersReference.child(user2Id).child(USER_FRIENDS_REF).child(user1Id).removeValue()
    }

    override fun removeUser(userId: String) {
        usersReference.child(userId).removeValue()

    }

    override fun getUser(userId: String): MutableLiveData<Result<User>> {
        val userMutableLiveData: MutableLiveData<Result<User>> = MutableLiveData()

        usersReference.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue<User>()
                if (user != null) {
                    userMutableLiveData.value = Result.Success(user)
                    //Log.d("TAG", "getting user: ${user.id}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                userMutableLiveData.value = Result.Failure(error.message)
                Log.w("TAG", "Failed to read value.", error.toException())
            }

        })

        return userMutableLiveData
    }

    override fun searchUser(query: String): MutableLiveData<Result<List<User>>> {
        val usersLiveData: MutableLiveData<Result<List<User>>> = MutableLiveData()

        usersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<User>()
                for (s in snapshot.children) {

                    val user = s.getValue(User::class.java)
                    if (user != null && (query.lowercase() in user.name.lowercase())) {
                    //if (user != null && (query.lowercase() in user.name.lowercase() || query.lowercase() in user.id.lowercase())) {
                        users.add(user)
                    //}
                    }
                }
                usersLiveData.value = Result.Success(users)
            }
            override fun onCancelled(error: DatabaseError) {
                usersLiveData.value = Result.Failure(error.message)
                Log.w("TAG", "Failed to read usersLiveData.", error.toException())
            }
        })
        return usersLiveData
    }


    override fun getUsers(): MutableLiveData<Result<List<User>>> {

        val usersLiveData: MutableLiveData<Result<List<User>>> = MutableLiveData()

        usersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<User>()
                for (s in snapshot.children) {
                    try {
                        val user = s.getValue(User::class.java)
                        if (user != null) {
                            users.add(user)
                        }
                    } catch (e: Exception) {
                        Log.d("TAG", "getUsers: ${e.message}")
                    }
                }
                usersLiveData.value = Result.Success(users)
            }
            override fun onCancelled(error: DatabaseError) {
                usersLiveData.value = Result.Failure(error.message)
                Log.w("TAG", "Failed to read usersLiveData.", error.toException())
            }
        })
        return usersLiveData

    }


}