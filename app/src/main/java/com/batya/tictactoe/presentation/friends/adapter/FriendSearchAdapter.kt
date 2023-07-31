package com.batya.tictactoe.presentation.friends.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.batya.tictactoe.databinding.ItemFriendSearchBinding
import com.batya.tictactoe.domain.model.User
import com.batya.tictactoe.util.getStatusColor
import com.batya.tictactoe.util.gone

class FriendSearchAdapter(private val onInvitationSendClicked: (User) -> Unit): RecyclerView.Adapter<FriendSearchAdapter.FriendSearchViewHolder>() {

    var items: List<User> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendSearchViewHolder {
        val binding = ItemFriendSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendSearchViewHolder(binding, onInvitationSendClicked)
    }

    override fun onBindViewHolder(holder: FriendSearchViewHolder, position: Int) {
        holder.bind(items[position])

    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class FriendSearchViewHolder(
        private val binding: ItemFriendSearchBinding,
        private val onInvitationSendClicked: (User) -> Unit,
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) = with(binding) {
            tvNickname.text = user.name
            tvPoints.text = user.points.toString()
            ivStatus.setImageResource(getStatusColor(user.status))

            if (user.isAnonymousAccount) {
                //tvId.gone()
                tvId.text = "ID: ${user.id.take(6)}"
                cvSendRequest.gone()
            } else {
                tvId.text = "ID: ${user.id.take(6)}"
                cvSendRequest.setOnClickListener {
                    onInvitationSendClicked(user)
                    cvSendRequest.gone()
                }
            }
            if (user.photoUri != null) {
                ivPhoto.load(user.photoUri)
            }
        }
    }
}