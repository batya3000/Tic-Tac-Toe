package com.android.batya.tictactoe.util

object Constants {
    const val ARG_USER_ID = "user_id"
    const val ARG_ROOM_ID = "room_id"
    const val ARG_ROOM_PRIVATE = "room_private"

    const val USERS_REF = "users"
    const val ROOMS_REF = "rooms"
    const val INVITATIONS_REF = "invitations"
    const val FRIEND_INVITATIONS_REF = "friends"
    const val BATTLE_INVITATIONS_REF = "battles"

    const val ROOMS_RUNNING_REF = "running"
    const val ROOMS_WAITING_REF = "waiting"
    const val ROOMS_CONNECTIONS_REF = "connections"

    const val CONNECTIONS_REF = "connections"
    const val TURNS_REF = "turns"
    const val LAST_TURN_REF = "last_turn"
    const val CURRENT_PLAYER_REF = "current_player"
    const val WINNER_REF = "winner"
    const val FIRST_TURN_PLAYER_REF = "first_turn"

    const val MATCH_START_TIME_REF = "match_start_time"
    const val TIMESTAMP_REF = "timestamp"
    const val CURRENT_TURN_START_TIME_REF = "current_turn_start_time"



    const val USER_NAME_REF = "name"
    const val USER_POINTS_REF = "points"
    const val USER_PHOTO_REF = "photoUri"
    const val USER_GAMES_REF = "games"
    const val USER_FRIENDS_REF = "friends"
    const val USER_ACCOUNT_TYPE_REF = "anonymousAccount"
    const val USER_STATUS_REF = "status"
    const val USER_TOKEN_REF = "token"

    const val USER_ROOM_CONNECTED_REF = "roomConnected"

    const val BASE_URL = "https://fcm.googleapis.com"
    const val CONTENT_TYPE = "application/json"


    const val SHARED_PREFS_SETTINGS = "shared_prefs_settings"

    const val SHARED_PREFS_ARE_CROSSES_FIRST = "cross_first"
    const val SHARED_PREFS_IS_LIGHT_MODE = "light"
    const val SHARED_PREFS_IS_VIBRATION_ON = "vibration"

    const val CHANNEL_ID = "my_channel"
    const val CHANNEL_NAME = "com.android.batya.tictactoe"

    const val TELEGRAM = "tictactoe_infinity"


    const val WIN_SEQUENCE_LENGTH = 5

    const val MINIMAX_DEPTH_PROPERTY = "minimax_depth"
    const val DEFAULT_MINIMAX_DEPTH_LIMIT = 3
    const val WIN_STRATEGY_SCORE = 100
    const val LOSE_STRATEGY_SCORE = -100
    const val DRAW_STRATEGY_SCORE = 0
    const val AVAILABILITY_RADIUS = 2

}