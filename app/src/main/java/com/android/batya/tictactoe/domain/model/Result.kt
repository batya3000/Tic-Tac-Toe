package com.android.batya.tictactoe.domain.model

sealed class Result<out T>  {
    class Success<out T>(val data: T) : Result<T>()
    class Failure(val error: String) : Result<Nothing>()

}