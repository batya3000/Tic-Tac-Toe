package com.android.batya.tictactoe.domain.model

import java.util.UUID

data class BattleInvitation(
    val roomId: String = "",
    val fromName: String = "",
    val fromId: String = "",
    val toId: String = "",
    val toToken: String = ""
)
