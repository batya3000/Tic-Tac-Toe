package com.android.batya.tictactoe.presentation.friends

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.batya.tictactoe.R
import com.android.batya.tictactoe.databinding.FragmentFriendsBinding
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.model.User
import com.android.batya.tictactoe.presentation.friends.adapter.FriendAdapter
import com.android.batya.tictactoe.presentation.friends.viewmodel.FriendInvitationsViewModel
import com.android.batya.tictactoe.presentation.friends.viewmodel.SearchViewModel
import com.android.batya.tictactoe.util.Constants
import com.android.batya.tictactoe.util.gone
import com.android.batya.tictactoe.util.visible
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel


class FriendsFragment : Fragment(R.layout.fragment_friends) {
    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel by viewModel<SearchViewModel>()
    private val invitesViewModel by viewModel<FriendInvitationsViewModel>()

    private var myId: String = ""
    private var user: User? = null

    private lateinit var friendAdapter: FriendAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)

        myId = Firebase.auth.currentUser!!.uid

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFriendAdapter()
        searchViewModel.getUser(myId)
        observeUser()

        binding.cvSearch.setOnClickListener {
            findNavController().navigate(R.id.action_friendsFragment_to_friendsSearchFragment)
        }
        binding.bnInvitations.setOnClickListener {
            findNavController().navigate(R.id.action_friendsFragment_to_friendsInvitationsFragment)
        }
    }

    private fun setupFriendAdapter() {
        friendAdapter = FriendAdapter {
            Log.d("TAG", "clicked friend ${it.id}")
            findNavController().navigate(R.id.action_friendsFragment_to_profileFragment, bundleOf(Constants.ARG_USER_ID to it.id))
        }

        binding.recyclerView.apply {
            adapter = friendAdapter

            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

            itemAnimator = DefaultItemAnimator()
        }
    }


    private fun observeUsers() {
        searchViewModel.usersLiveData.observe(viewLifecycleOwner) { users ->
            when (users) {
                is Result.Success -> {
                    val friends = users.data.filter { it.id in user!!.friends.values }

                    if (friends.isEmpty()) binding.tvNoFriends.visible()
                    else binding.tvNoFriends.gone()

                    friendAdapter.items = friends
                    Log.d("TAG", "observeUsers: user=$user")
                }
                is Result.Failure -> {
                    Toast.makeText(context, "Error observeFriends", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeUser() {
        searchViewModel.userLiveData.observe(viewLifecycleOwner) { user ->
            when (user) {
                is Result.Success -> {
                    Log.d("TAG", "success: userId=${user.data.id}")

//                    binding.tvTitleCount.text = user.data.friends.size.toString()

                    invitesViewModel.getIncomingInvitations(user.data.id)
                    observeInvitations()
                    searchViewModel.getUsers()

                    observeUsers()

                    this@FriendsFragment.user = user.data
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
                    if (invitations.data.isEmpty()) binding.bnInvitations.gone()
                    else {
                        binding.bnInvitations.visible()
                        binding.tvInvitationsCounter.text = invitations.data.size.toString()
                    }

//                    Log.d("TAG", "observeInvitations: observeInvitations=$user")
                }
                is Result.Failure -> {
                    Toast.makeText(context, "Error observeFriends", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}