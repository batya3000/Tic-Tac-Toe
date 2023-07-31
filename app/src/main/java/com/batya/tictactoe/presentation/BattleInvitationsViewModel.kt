package com.batya.tictactoe.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.batya.tictactoe.domain.model.BattleInvitation
import com.batya.tictactoe.domain.model.Result
import com.batya.tictactoe.domain.repository.InvitationRepository

class BattleInvitationsViewModel(
    private val invitationRepository: InvitationRepository,
) : ViewModel() {
    private var _invitesLiveData: MutableLiveData<Result<List<BattleInvitation>>> = MutableLiveData()
    val invitesLiveData: LiveData<Result<List<BattleInvitation>>> get() = _invitesLiveData



    fun acceptInvitation(roomId: String) {
        invitationRepository.removeBattleInvitation(roomId)
    }

    fun declineInvitation(roomId: String) {
        invitationRepository.removeBattleInvitation(roomId)
    }

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