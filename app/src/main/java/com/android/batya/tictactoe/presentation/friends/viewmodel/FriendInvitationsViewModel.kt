package com.android.batya.tictactoe.presentation.friends.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.batya.tictactoe.domain.model.FriendInvitation
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.repository.InvitationRepository
import com.android.batya.tictactoe.domain.repository.UserRepository

class FriendInvitationsViewModel(
    private val invitationRepository: InvitationRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private var _invitesLiveData: MutableLiveData<Result<List<FriendInvitation>>> = MutableLiveData()
    val invitesLiveData: LiveData<Result<List<FriendInvitation>>> get() = _invitesLiveData


    fun getIncomingInvitations(userId: String) {
        _invitesLiveData = invitationRepository.getFriendIncomingInvitations(userId)
    }

    fun getOutgoingInvitations(userId: String) {
        _invitesLiveData = invitationRepository.getFriendOutgoingInvitations(userId)
    }

    fun acceptInvitation(friendInvitation: FriendInvitation) {
        userRepository.addFriend(user1Id = friendInvitation.fromId, user2Id = friendInvitation.toId)
        invitationRepository.removeFriendInvitation(friendInvitation)
    }

    fun declineInvitation(friendInvitation: FriendInvitation) {
        invitationRepository.removeFriendInvitation(friendInvitation)
    }

    fun sendInvitation(friendInvitation: FriendInvitation) {
        invitationRepository.sendFriendInvitation(friendInvitation)
    }

}