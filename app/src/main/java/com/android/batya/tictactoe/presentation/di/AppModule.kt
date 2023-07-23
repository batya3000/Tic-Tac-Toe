package com.android.batya.tictactoe.presentation.di

import com.android.batya.tictactoe.presentation.BattleInvitationsViewModel
import com.android.batya.tictactoe.presentation.auth.AuthViewModel
import com.android.batya.tictactoe.presentation.friends.viewmodel.FriendInvitationsViewModel
import com.android.batya.tictactoe.presentation.friends.viewmodel.SearchViewModel
import com.android.batya.tictactoe.presentation.menu.UserViewModel
import com.android.batya.tictactoe.presentation.online.viewmodel.UsersViewModel
import com.android.batya.tictactoe.presentation.online.viewmodel.PlayerViewModel
import com.android.batya.tictactoe.presentation.online.viewmodel.TimeViewModel
import com.android.batya.tictactoe.presentation.online.viewmodel.TurnsViewModel
import com.android.batya.tictactoe.presentation.profile.NotificationsViewModel
import com.android.batya.tictactoe.presentation.profile.ProfileViewModel
import com.android.batya.tictactoe.presentation.settings.SettingsViewModel
import com.android.batya.tictactoe.presentation.waiting.RoomViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel {
        AuthViewModel(
            authRepository = get()
        )
    }
    viewModel {
        UserViewModel(
            userRepository = get()
        )
    }
    viewModel {
        UsersViewModel(
            gameRepository = get(),
            userRepository = get(),
        )
    }
    viewModel {
        TurnsViewModel(
            gameRepository = get()
        )
    }
    viewModel {
        PlayerViewModel(
            gameRepository = get()
        )
    }
    viewModel {
        TimeViewModel(
            gameRepository = get()
        )
    }
    viewModel {
        RoomViewModel(
            roomRepository = get()
        )
    }
    viewModel {
        SearchViewModel(
            userRepository = get()
        )
    }
    viewModel {
        FriendInvitationsViewModel(
            invitationRepository = get(),
            userRepository = get()
        )
    }
    viewModel {
        SettingsViewModel(
            settingsRepository = get()
        )
    }
    viewModel {
        BattleInvitationsViewModel(
            invitationRepository = get(),
        )
    }
    viewModel {
        ProfileViewModel(
            userRepository = get()
        )
    }
    viewModel {
        NotificationsViewModel(
            notificationRepository = get()
        )
    }
}