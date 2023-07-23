package com.android.batya.tictactoe.presentation.friends

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.batya.tictactoe.R
import com.android.batya.tictactoe.databinding.FragmentFriendsSearchBinding
import com.android.batya.tictactoe.domain.model.FriendInvitation
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.model.User
import com.android.batya.tictactoe.presentation.friends.adapter.FriendSearchAdapter
import com.android.batya.tictactoe.presentation.friends.viewmodel.FriendInvitationsViewModel
import com.android.batya.tictactoe.presentation.friends.viewmodel.SearchViewModel
import com.android.batya.tictactoe.util.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel


class FriendsSearchFragment : Fragment(R.layout.fragment_friends_search) {
    private var _binding: FragmentFriendsSearchBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel by viewModel<SearchViewModel>()
    private val invitationsViewModel by viewModel<FriendInvitationsViewModel>()

    private var myId: String = ""
    private var user: User? = null

    private lateinit var friendSearchAdapter: FriendSearchAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsSearchBinding.inflate(inflater, container, false)

        myId = Firebase.auth.currentUser!!.uid

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchViewModel.getUser(myId)
        observeUser()

        initEditText()

    }

    private fun setupAdapter() {
        friendSearchAdapter = FriendSearchAdapter {
            Log.d("TAG", "clicked friend ${it.id}")
            if (user != null) {
                invitationsViewModel.sendInvitation(
                    FriendInvitation(
                        fromName = user!!.name,
                        fromId =  user!!.id,
                        fromPoints = user!!.points,
                        fromPhotoUri = user!!.photoUri,
                        toId = it.id
                    )
                )
            } else {
                Toast.makeText(context, "Не удается отправить приглашение...", Toast.LENGTH_SHORT).show()
            }

        }

        binding.recyclerView.apply {
            adapter = friendSearchAdapter

            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

            itemAnimator = DefaultItemAnimator()
        }
//        binding.ivSearch.setOnClickListener {
//            search()
//        }
    }

    private fun observeUser() {
        searchViewModel.userLiveData.observe(viewLifecycleOwner) { user ->
            when (user) {
                is Result.Success -> {
                    Log.d("TAG", "success: userId=${user.data.id}")

//                    binding.tvTitleCount.text = user.data.friends.size.toString()

                    this@FriendsSearchFragment.user = user.data
                }
                is Result.Failure -> {
                    Log.d("TAG", "failure log in: ${user.error}")
                }
            }
        }
    }

    private fun observeQuery() {
        searchViewModel.searchLiveData.removeObservers(viewLifecycleOwner)
        searchViewModel.searchLiveData.observe(viewLifecycleOwner) { users ->
            when (users) {
                is Result.Success -> {
                    Log.d("TAG", "observeQuery: ${users.data}")
                    val searchResult = users.data.filter {
                        it.id != myId &&
                        user?.friends?.containsKey(it.id) == false
                    }
                    Log.d("TAG", "observeQueryAfterSearchResult: $searchResult")

                    invitationsViewModel.getOutgoingInvitations(myId)
                    observeInvitations(searchResult)
                }

                is Result.Failure -> {
                    Log.d("TAG", "failure search: ${users.error}")
                }
            }
        }
    }

    private fun observeInvitations(searchResult: List<User>) {
        invitationsViewModel.invitesLiveData.observe(viewLifecycleOwner) { invitations ->
            when (invitations) {
                is Result.Success -> {

                    val items = searchResult.filter {
                        it.id !in invitations.data.map { it2 -> it2.toId}
                    }
                    setupAdapter()
                    friendSearchAdapter.items = items

                    if (items.isEmpty()) binding.tvPlayersFoundCount.text = "Нет результатов"
                    else {
                        binding.tvPlayersFoundCount.text = "${getWordFoundInCase(items.size)} ${items.size} ${getWordPlayersInCase(items.size)}"
                    }
                }
                is Result.Failure -> {
                    Toast.makeText(context, "Error observeInvitations", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initEditText() {
        binding.editTextSearch.showKeyboard()
        binding.editTextSearch.setOnKeyListener { _, _, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                //Toast.makeText(context, "${binding.editTextSearch.text}", Toast.LENGTH_SHORT).show()
                binding.editTextSearch.hideKeyboard()
                search()

                return@setOnKeyListener true
            } else {
                return@setOnKeyListener false
            }
        }
    }

    private fun search() {
        searchViewModel.searchUser(binding.editTextSearch.text.toString())
        observeQuery()
    }
}