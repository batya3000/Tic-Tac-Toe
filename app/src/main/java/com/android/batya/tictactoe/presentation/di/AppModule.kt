package com.android.batya.tictactoe.presentation.di

import com.android.batya.tictactoe.presentation.auth.AuthViewModel
import com.android.batya.tictactoe.presentation.friends.viewmodel.FriendInvitationsViewModel
import com.android.batya.tictactoe.presentation.friends.viewmodel.SearchViewModel
import com.android.batya.tictactoe.presentation.menu.UserViewModel
import com.android.batya.tictactoe.presentation.online.viewmodel.UsersViewModel
import com.android.batya.tictactoe.presentation.online.viewmodel.PlayerViewModel
import com.android.batya.tictactoe.presentation.online.viewmodel.TimeViewModel
import com.android.batya.tictactoe.presentation.online.viewmodel.TurnsViewModel
import com.android.batya.tictactoe.presentation.settings.SettingsViewModel
import com.android.batya.tictactoe.presentation.waiting.RoomViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel<AuthViewModel> {
        AuthViewModel(
            authRepository = get()
        )
    }
    viewModel<UserViewModel> {
        UserViewModel(
            userRepository = get()
        )
    }
    viewModel<UsersViewModel> {
        UsersViewModel(
            gameRepository = get(),
            userRepository = get(),
            invitationRepository = get()
        )
    }
    viewModel<TurnsViewModel> {
        TurnsViewModel(
            gameRepository = get()
        )
    }
    viewModel<PlayerViewModel> {
        PlayerViewModel(
            gameRepository = get()
        )
    }
    viewModel<TimeViewModel> {
        TimeViewModel(
            gameRepository = get()
        )
    }
    viewModel<RoomViewModel> {
        RoomViewModel(
            roomRepository = get()
        )
    }
    viewModel<SearchViewModel> {
        SearchViewModel(
            userRepository = get()
        )
    }
    viewModel<FriendInvitationsViewModel> {
        FriendInvitationsViewModel(
            invitationRepository = get(),
            userRepository = get()
        )
    }
    viewModel<SettingsViewModel> {
        SettingsViewModel(
            settingsRepository = get()
        )
    }
}