package com.android.batya.tictactoe.presentation.friends.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.android.batya.tictactoe.databinding.ItemFriendInviteBinding
import com.android.batya.tictactoe.domain.model.FriendInvitation
import com.android.batya.tictactoe.util.gone

class FriendInvitationsAdapter(
    private val onInvitationAcceptClicked: (FriendInvitation) -> Unit,
    ): RecyclerView.Adapter<FriendInvitationsAdapter.FriendInvitationsViewHolder>() {

    var items: List<FriendInvitation> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendInvitationsViewHolder {
        val binding = ItemFriendInviteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendInvitationsViewHolder(binding, onInvitationAcceptClicked)
    }

    override fun onBindViewHolder(holder: FriendInvitationsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun onSwiped(adapterPosition: Int) {
        val invitation = items[adapterPosition]
        items = items.filter { it != invitation }
        notifyItemChanged(adapterPosition)
    }


    inner class FriendInvitationsViewHolder(
        private val binding: ItemFriendInviteBinding,
        private val onInvitationAcceptClicked: (FriendInvitation) -> Unit,
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(friendInvitation: FriendInvitation) = with(binding) {
            tvNickname.text = friendInvitation.fromName
            tvId.text = "ID: ${friendInvitation.fromId.take(6)}"
            tvPoints.text = friendInvitation.fromPoints.toString()

            bnAccept.setOnClickListener {
                //items = items.filter { it.id != friendInvitation.id }
                onInvitationAcceptClicked(friendInvitation)
            }
//            bnDecline.setOnClickListener {
                //items = items.filter { it.id != friendInvitation.id }
//                onInvitationDeclineClicked(friendInvitation)
//            }

            if (friendInvitation.fromPhotoUri != null) {
                ivPhoto.load(friendInvitation.fromPhotoUri)
            }
        }
    }
}