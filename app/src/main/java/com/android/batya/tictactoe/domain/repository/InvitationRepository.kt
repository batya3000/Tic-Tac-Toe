package com.android.batya.tictactoe.domain.repository

import androidx.lifecycle.MutableLiveData
import com.android.batya.tictactoe.domain.model.BattleInvitation
import com.android.batya.tictactoe.domain.model.FriendInvitation
import com.android.batya.tictactoe.domain.model.Result

interface InvitationRepository {

   fun getFriendIncomingInvitations(userId: String): MutableLiveData<Result<List<FriendInvitation>>>
   fun getFriendOutgoingInvitations(userId: String): MutableLiveData<Result<List<FriendInvitation>>>

   fun sendFriendInvitation(friendInvitation: FriendInvitation)
   fun removeFriendInvitation(friendInvitation: FriendInvitation)

   fun sendBattleInvitation(battleInvitation: BattleInvitation)
   fun removeBattleInvitation(roomId: String)
   fun getBattleIncomingInvitations(userId: String): MutableLiveData<Result<List<BattleInvitation>>>
   fun getBattleOutgoingInvitations(userId: String): MutableLiveData<Result<List<BattleInvitation>>>

}