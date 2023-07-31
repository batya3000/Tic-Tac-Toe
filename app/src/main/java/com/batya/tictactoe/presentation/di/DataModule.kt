package com.batya.tictactoe.presentation.di

import com.batya.tictactoe.data.api.NotificationAPI
import com.batya.tictactoe.data.repository.*
import com.batya.tictactoe.data.storage.SettingsStorage
import com.batya.tictactoe.data.storage.sharedprefs.SharedPrefSettingsStorage
import com.batya.tictactoe.domain.repository.*
import com.batya.tictactoe.presentation.di.network.createRetrofit
import com.batya.tictactoe.util.Constants
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {

    single { com.batya.tictactoe.data.repository.AuthRepositoryImpl(
        auth = get(),
        usersReference = get(named(Constants.USERS_REF))
    ) as AuthRepository }
    single { UserRepositoryImpl(usersReference = get(named(Constants.USERS_REF))) as UserRepository }
    single { GameRepositoryImpl(gamesReference = get(named(Constants.ROOMS_REF))) as GameRepository }
    single { RoomRepositoryImpl(roomsReference = get(named(Constants.ROOMS_REF)), usersReference = get(named(Constants.USERS_REF))) as RoomRepository }
    single {
        InvitationRepositoryImpl(
            friendInvitationsReference = get(named(Constants.FRIEND_INVITATIONS_REF)),
            battleInvitationsReference = get(named(Constants.BATTLE_INVITATIONS_REF))
        ) as InvitationRepository
    }
    single { NotificationRepositoryImpl(notificationAPI = get()) as NotificationRepository }
    single { SettingsRepositoryImpl(settingsStorage = get()) as SettingsRepository }


    single { createRetrofit<com.batya.tictactoe.data.api.NotificationAPI>() }

    single { SharedPrefSettingsStorage(context = get()) as SettingsStorage }


}