package com.android.batya.tictactoe.presentation.friends

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.batya.tictactoe.R
import com.android.batya.tictactoe.databinding.FragmentFriendInvitationsBinding
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.model.User
import com.android.batya.tictactoe.presentation.friends.adapter.FriendInvitationsAdapter
import com.android.batya.tictactoe.presentation.friends.viewmodel.FriendInvitationsViewModel
import com.android.batya.tictactoe.presentation.friends.viewmodel.SearchViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel


class FriendInvitationsFragment : Fragment(R.layout.fragment_friend_invitations) {
    private var _binding: FragmentFriendInvitationsBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel by viewModel<SearchViewModel>()
    private val invitesViewModel by viewModel<FriendInvitationsViewModel>()

    private var myId: String = ""
    private var user: User? = null

    private lateinit var friendInvitationsAdapter: FriendInvitationsAdapter
    private lateinit var itemTouchHelper: InvitationItemTouchHelper


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendInvitationsBinding.inflate(inflater, container, false)
        myId = Firebase.auth.currentUser!!.uid

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        searchViewModel.getUser(myId)
        observeUser()

    }

    private fun setupAdapter() {
        friendInvitationsAdapter = FriendInvitationsAdapter(
            onInvitationAcceptClicked = {
                if (user != null) {
                    invitesViewModel.acceptInvitation(it)
                } else {
                    Toast.makeText(context, "Не удается принять запрос...", Toast.LENGTH_SHORT).show()
                }
            }
        )
        val itemTouchHelper = ItemTouchHelper(InvitationItemTouchHelper(friendInvitationsAdapter))
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)



        binding.recyclerView.apply {
            adapter = friendInvitationsAdapter

            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

            //itemAnimator = DefaultItemAnimator()
        }

    }

    private fun observeUser() {
        searchViewModel.userLiveData.observe(viewLifecycleOwner) { user ->
            when (user) {
                is Result.Success -> {
                    //Log.d("TAG", "success: userId=${user.data.id}")

                    invitesViewModel.getIncomingInvitations(user.data.id)
                    observeInvitations()
                    this@FriendInvitationsFragment.user = user.data
                }
                is Result.Failure -> {
                    Log.d("TAG", "failure log in: ${user.error}")
                }
            }
        }
    }

    private fun observeInvitations() {
        invitesViewModel.invitesLiveData.observe(viewLifecycleOwner) { invitations ->
            when (invitations) {
                is Result.Success -> {
                    val myInvitations = invitations.data.filter { it.toId == myId }

                    friendInvitationsAdapter.items = myInvitations
//                    binding.tvTitleCount.text = myInvitations.size.toString()
                }
                is Result.Failure -> {
                    Toast.makeText(context, "Error observeFriends", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}