package com.batya.tictactoe.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.batya.tictactoe.domain.model.Result
import com.batya.tictactoe.domain.model.User
import com.batya.tictactoe.domain.repository.AuthRepository
import com.batya.tictactoe.util.Constants
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlin.concurrent.thread

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val usersReference: DatabaseReference
) : AuthRepository {

    override fun isAuthenticated(): Boolean {
        return auth.currentUser != null
    }
    // Sign in using google
    override fun firebaseSignInWithGoogle(googleAuthCredential: AuthCredential): MutableLiveData<Result<String>> {
        val authenticatedUserMutableLiveData: MutableLiveData<Result<String>> =
            MutableLiveData()

        auth.signInWithCredential(googleAuthCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firebaseUser: FirebaseUser? = auth.currentUser

                if (firebaseUser != null) {

                    if (task.result.additionalUserInfo?.isNewUser == true) {
                        val uid = firebaseUser.uid
                        var name = firebaseUser.displayName
                        name = when {
                            name == null -> "User_${uid.take(4)}"
                            name.length > 11 -> name.take(12)
                            else -> name
                        }
                        val user = User(
                            id = uid,
                            name = name,
                            photoUri = firebaseUser.photoUrl.toString(),
                            points = 1000,
                            isAnonymousAccount = false
                        )
                        storeUser(user)
                    }

                    authenticatedUserMutableLiveData.value = Result.Success(firebaseUser.uid)

                }
            } else {
                authenticatedUserMutableLiveData.value = task.exception?.message?.let {
                    Result.Failure(it)
                }

            }


        }
        return authenticatedUserMutableLiveData
    }

    override fun signInAnonymously(): MutableLiveData<Result<String>> {
        val authenticatedUserMutableLiveData: MutableLiveData<Result<String>> = MutableLiveData()

        auth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firebaseUser: FirebaseUser? = auth.currentUser
                if (firebaseUser != null) {
                    val uid = firebaseUser.uid
                    val name = "Quest_${firebaseUser.uid.take(4)}"
                    val user = User(id = uid, name = name, isAnonymousAccount = true)

                    storeUser(user)

                    authenticatedUserMutableLiveData.value = Result.Success(uid)
                    Log.d("TAG", "signInAnonymously:success (${auth.currentUser?.uid})")

                }
            } else {
                authenticatedUserMutableLiveData.value = Result.Failure(error = task.exception?.message!!)
                Log.w("TAG", "signInAnonymously:failure", task.exception)
            }
        }
        return authenticatedUserMutableLiveData
    }

    override fun linkAnonWithGoogle(googleAuthCredential: AuthCredential) {

    }

    override fun storeUser(user: User) {
        usersReference.child(user.id).setValue(user)
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

    override fun updatePhoto(photoUri: String?) {
        usersReference.child(auth.currentUser!!.uid).updateChildren(mapOf(Constants.USER_PHOTO_REF to photoUri))
    }
}