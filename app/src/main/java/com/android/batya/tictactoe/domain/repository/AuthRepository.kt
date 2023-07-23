package com.android.batya.tictactoe.domain.repository

import androidx.lifecycle.MutableLiveData
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.model.User
import com.google.firebase.auth.AuthCredential

interface AuthRepository {


    fun isAuthenticated(): Boolean

    // Sign in using google
    fun firebaseSignInWithGoogle(googleAuthCredential: AuthCredential): MutableLiveData<Result<String>>
    fun signInAnonymously(): MutableLiveData<Result<String>>

    fun linkAnonWithGoogle(googleAuthCredential: AuthCredential)

    fun storeUser(user: User)

    fun getUser(userId: String): MutableLiveData<Result<User>>

    fun updatePhoto(photoUri: String?)
}