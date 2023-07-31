package com.batya.tictactoe.domain.model

data class BattleInvitation(
    val roomId: String = "",
    val fromName: String = "",
    val fromId: String = "",
    val toId: String = "",
    val toToken: String = ""
)
