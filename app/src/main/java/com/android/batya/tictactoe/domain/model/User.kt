package com.android.batya.tictactoe.domain.model

data class User(
    var id: String = "",
    val name: String = "",
    val points: Int = 1000,
    var isAnonymousAccount: Boolean = false,
    var games: Map<String, Game> = mapOf(),
    var friends: Map<String, String> = mapOf()
)