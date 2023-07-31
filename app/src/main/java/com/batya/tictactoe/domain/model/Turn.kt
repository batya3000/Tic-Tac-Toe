package com.batya.tictactoe.domain.model

data class Turn(
    val playerId: String = "",
    val row: Int = -1,
    val column: Int = -1,
)
