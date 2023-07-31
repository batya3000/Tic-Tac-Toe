package com.batya.tictactoe.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.batya.tictactoe.domain.model.Result
import com.batya.tictactoe.domain.model.User
import com.batya.tictactoe.domain.repository.AuthRepository
import com.google.firebase.auth.AuthCredential

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private var _authenticatedUserLiveData: MutableLiveData<Result<String>> = MutableLiveData()
    val authenticatedUserLiveData: LiveData<Result<String>> get() = _authenticatedUserLiveData

    fun isAuthenticated(): Boolean {
        return authRepository.isAuthenticated()
    }
    fun signInWithGoogle(googleAuthCredential: AuthCredential) {
        _authenticatedUserLiveData = authRepository.firebaseSignInWithGoogle(googleAuthCredential)
    }

    fun signInAnonymously() {
        _authenticatedUserLiveData = authRepository.signInAnonymously()
    }

    fun updatePhoto(photoUri: String?) {
        authRepository.updatePhoto(photoUri)
    }

}