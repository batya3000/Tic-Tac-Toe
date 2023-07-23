package com.android.batya.tictactoe.presentation.friends.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.android.batya.tictactoe.databinding.ItemFriendBinding
import com.android.batya.tictactoe.domain.model.User
import com.android.batya.tictactoe.util.getStatusColor

class FriendAdapter(private val onFriendClicked: (User) -> Unit): RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    var items: List<User> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding, onFriendClicked)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class FriendViewHolder(
        private val binding: ItemFriendBinding,
        private val onFriendClicked: (User) -> Unit,
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) = with(binding) {

            tvNickname.text = user.name
            tvId.text = "ID: ${user.id.take(6)}"
            tvCrowns.text = user.points.toString()
            ivStatus.setImageResource(getStatusColor(user.status))

            cvItem.setOnClickListener {
                onFriendClicked(user)
            }

            if (user.photoUri != null) {
                ivPhoto.load(user.photoUri)
            }
        }
    }
}