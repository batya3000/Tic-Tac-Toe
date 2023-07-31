package com.batya.tictactoe.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.batya.tictactoe.domain.model.BattleInvitation
import com.batya.tictactoe.domain.model.FriendInvitation
import com.batya.tictactoe.domain.model.Result
import com.batya.tictactoe.domain.repository.InvitationRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class InvitationRepositoryImpl(
    private val friendInvitationsReference: DatabaseReference,
    private val battleInvitationsReference: DatabaseReference
): InvitationRepository {
    override fun getFriendIncomingInvitations(userId: String): MutableLiveData<Result<List<FriendInvitation>>> {
        val invitationsLiveData: MutableLiveData<Result<List<FriendInvitation>>> = MutableLiveData()

        friendInvitationsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val invitations = mutableListOf<FriendInvitation>()
                for (s in snapshot.children) {

                    val invitation = s.getValue(FriendInvitation::class.java)

                    if (invitation != null && invitation.toId == userId) {
                        invitations.add(invitation)
                    }
                }
                invitationsLiveData.value = Result.Success(invitations)
            }
            override fun onCancelled(error: DatabaseError) {
                invitationsLiveData.value = Result.Failure(error.message)
                Log.w("TAG", "Failed to read invitationsLiveData.", error.toException())
            }
        })
        return invitationsLiveData
    }

    override fun getFriendOutgoingInvitations(userId: String): MutableLiveData<Result<List<FriendInvitation>>> {
        val invitationsLiveData: MutableLiveData<Result<List<FriendInvitation>>> = MutableLiveData()

        friendInvitationsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val invitations = mutableListOf<FriendInvitation>()
                for (s in snapshot.children) {

                    val invitation = s.getValue(FriendInvitation::class.java)
                    if (invitation != null && invitation.fromId == userId) {
                        invitations.add(invitation)
                    }
                }
                invitationsLiveData.value = Result.Success(invitations)
            }
            override fun onCancelled(error: DatabaseError) {
                invitationsLiveData.value = Result.Failure(error.message)
                Log.w("TAG", "Failed to read invitationsLiveData.", error.toException())
            }
        })
        return invitationsLiveData
    }

    override fun sendFriendInvitation(friendInvitation: FriendInvitation) {
        friendInvitationsReference.child(friendInvitation.id).setValue(friendInvitation)
    }

    override fun removeFriendInvitation(friendInvitation: FriendInvitation) {
        friendInvitationsReference.child(friendInvitation.id).removeValue()
    }

    override fun sendBattleInvitation(battleInvitation: BattleInvitation) {
        battleInvitationsReference.child(battleInvitation.roomId).setValue(battleInvitation)
    }

    override fun removeBattleInvitation(roomId: String) {
        battleInvitationsReference.child(roomId).removeValue()
    }

    override fun getBattleIncomingInvitations(userId: String): MutableLiveData<Result<List<BattleInvitation>>> {
        val invitationsLiveData: MutableLiveData<Result<List<BattleInvitation>>> = MutableLiveData()

        battleInvitationsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val invitations = mutableListOf<BattleInvitation>()
                for (s in snapshot.children) {

                    val invitation = s.getValue(BattleInvitation::class.java)

                    if (invitation != null && invitation.toId == userId) {
                        invitations.add(invitation)
                    }
                }
                invitationsLiveData.value = Result.Success(invitations)
            }
            override fun onCancelled(error: DatabaseError) {
                invitationsLiveData.value = Result.Failure(error.message)
                Log.w("TAG", "Failed to read invitationsLiveData.", error.toException())
            }
        })
        return invitationsLiveData
    }

    override fun getBattleOutgoingInvitations(userId: String): MutableLiveData<Result<List<BattleInvitation>>> {
        val invitationsLiveData: MutableLiveData<Result<List<BattleInvitation>>> = MutableLiveData()

        battleInvitationsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val invitations = mutableListOf<BattleInvitation>()
                for (s in snapshot.children) {

                    val invitation = s.getValue(BattleInvitation::class.java)

                    if (invitation != null && invitation.fromId == userId) {
                        invitations.add(invitation)
                    }
                }
                invitationsLiveData.value = Result.Success(invitations)
            }
            override fun onCancelled(error: DatabaseError) {
                invitationsLiveData.value = Result.Failure(error.message)
                Log.w("TAG", "Failed to read invitationsLiveData.", error.toException())
            }
        })
        return invitationsLiveData
    }
}