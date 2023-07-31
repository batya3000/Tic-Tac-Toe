package com.batya.tictactoe.domain.model

data class User(
    var id: String = "",
    var name: String = "",
    var photoUri: String? = null,
    var token: String = "",
    var points: Int = 1000,
    var isAnonymousAccount: Boolean = false,
    var games: Map<String, Game> = mapOf(),
    var friends: Map<String, String> = mapOf(),
    var status: UserStatus = UserStatus.OFFLINE,
    var roomConnected: String? = null
)