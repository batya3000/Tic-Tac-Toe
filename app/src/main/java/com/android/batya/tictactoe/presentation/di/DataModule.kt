package com.android.batya.tictactoe.presentation.di

import com.android.batya.tictactoe.data.api.NotificationAPI
import com.android.batya.tictactoe.data.repository.*
import com.android.batya.tictactoe.data.storage.SettingsStorage
import com.android.batya.tictactoe.data.storage.sharedprefs.SharedPrefSettingsStorage
import com.android.batya.tictactoe.domain.repository.*
import com.android.batya.tictactoe.presentation.di.network.createRetrofit
import com.android.batya.tictactoe.util.Constants
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {

    single { AuthRepositoryImpl(auth = get(), usersReference = get(named(Constants.USERS_REF))) as AuthRepository }
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


    single { createRetrofit<NotificationAPI>() }

    single { SharedPrefSettingsStorage(context = get()) as SettingsStorage }


}