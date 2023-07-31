package com.batya.tictactoe.domain.model

import java.util.UUID

data class FriendInvitation(
    val id: String = UUID.randomUUID().toString(),
    val fromName: String = "",
    val fromId: String = "",
    val fromPoints: Int = 0,
    val fromPhotoUri: String? = null,
    val toId: String = ""
)
