package com.android.batya.tictactoe.domain.repository

import androidx.lifecycle.MutableLiveData
import com.android.batya.tictactoe.domain.model.FriendInvitation
import com.android.batya.tictactoe.domain.model.Result

interface InvitationRepository {

   fun getFriendIncomingInvitations(userId: String): MutableLiveData<Result<List<FriendInvitation>>>
   fun getFriendOutgoingInvitations(userId: String): MutableLiveData<Result<List<FriendInvitation>>>

   fun sendFriendInvitation(friendInvitation: FriendInvitation)
   fun removeFriendInvitation(friendInvitation: FriendInvitation)

   fun sendBattleInvitation(friendInvitation: FriendInvitation)
   fun removeBattleInvitation(friendInvitation: FriendInvitation)
}