package com.batya.tictactoe.domain.model

import java.util.UUID

data class Room(
//    var id: String = "",
    var id: String = UUID.randomUUID().toString(),
    val connections: Map<String, User> = mapOf()

)