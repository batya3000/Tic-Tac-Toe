package com.android.batya.tictactoe.domain.model

data class User(
    var id: String = "",
    val name: String = "",
    val photoUri: String? = null,
    val token: String = "",
    val points: Int = 1000,
    var isAnonymousAccount: Boolean = false,
    var games: Map<String, Game> = mapOf(),
    var friends: Map<String, String> = mapOf(),
    var status: UserStatus = UserStatus.OFFLINE,
    var roomConnected: String? = null
)