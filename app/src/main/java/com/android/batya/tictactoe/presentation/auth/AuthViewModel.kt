package com.android.batya.tictactoe.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.batya.tictactoe.domain.model.User
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.repository.AuthRepository
import com.google.firebase.auth.AuthCredential

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private var _authenticateUserLiveData: MutableLiveData<Result<String>> = MutableLiveData()
    val authenticateUserLiveData: LiveData<Result<String>> get() = _authenticateUserLiveData

    fun isAuthenticated(): Boolean {
        return authRepository.isAuthenticated()
    }
    fun signInWithGoogle(googleAuthCredential: AuthCredential) {
        _authenticateUserLiveData = authRepository.firebaseSignInWithGoogle(googleAuthCredential)
    }

    fun signInAnonymously() {
        _authenticateUserLiveData = authRepository.signInAnonymously()
    }


}