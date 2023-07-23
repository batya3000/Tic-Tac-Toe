package com.android.batya.tictactoe.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.batya.tictactoe.domain.model.BattleInvitation
import com.android.batya.tictactoe.domain.model.FriendInvitation
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.model.Room
import com.android.batya.tictactoe.domain.model.User
import com.android.batya.tictactoe.domain.repository.InvitationRepository
import com.android.batya.tictactoe.domain.repository.RoomRepository
import com.android.batya.tictactoe.domain.repository.UserRepository

class BattleInvitationsViewModel(
    private val invitationRepository: InvitationRepository,
    private val roomRepository: RoomRepository
) : ViewModel() {
    private var _invitesLiveData: MutableLiveData<Result<List<BattleInvitation>>> = MutableLiveData()
    val invitesLiveData: LiveData<Result<List<BattleInvitation>>> get() = _invitesLiveData



    fun acceptInvitation(roomId: String) {
        invitationRepository.removeBattleInvitation(roomId)
    }

    fun declineInvitation(roomId: String) {
        invitationRepository.removeBattleInvitation(roomId)
    }

//    fun createPrivateRoom(room: Room, user: User) {
//        roomRepository.createRoom(room, user)
//    }

    fun sendInvitation(battleInvitation: BattleInvitation) {
        invitationRepository.sendBattleInvitation(battleInvitation)
    }


    fun removeInvitation(roomId: String) {
        invitationRepository.removeBattleInvitation(roomId)
    }

    fun getIncomingInvitations(userId: String) {
        _invitesLiveData = invitationRepository.getBattleIncomingInvitations(userId)
    }
    fun getOutgoingInvitations(userId: String) {
        _invitesLiveData = invitationRepository.getBattleOutgoingInvitations(userId)
    }

}