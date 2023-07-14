package com.android.batya.tictactoe.domain.model

import java.util.UUID

data class Room(
    var id: String = "",
    //var id: String = UUID.randomUUID().toString(),
    val isRunning: Boolean = false,
    val isPrivate: Boolean = false,
    val connections: Map<String, User> = mapOf()

)