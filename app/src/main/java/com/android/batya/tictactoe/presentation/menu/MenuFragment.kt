package com.android.batya.tictactoe.presentation.menu

import android.app.ActivityOptions
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.android.batya.tictactoe.R
import com.android.batya.tictactoe.databinding.BottomSheetFragmentBinding
import com.android.batya.tictactoe.databinding.DialogNicknameBinding
import com.android.batya.tictactoe.databinding.FragmentMenuBinding
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.model.User
import com.android.batya.tictactoe.presentation.friends.viewmodel.FriendInvitationsViewModel
import com.android.batya.tictactoe.util.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel

class MenuFragment : Fragment(R.layout.fragment_menu) {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private val userViewModel by viewModel<UserViewModel>()
    private val invitationsViewModel by viewModel<FriendInvitationsViewModel>()

    private var myId: String = ""
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)

        myId = Firebase.auth.currentUser!!.uid

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListeners()

        userViewModel.getUser(myId)
        observeUserLiveData()

    }

    private fun setOnClickListeners() {
        binding.bnSolo.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_offlineFragment)
        }

        binding.bnOnline.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_waitingFragment)
        }

        binding.bnHelp.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_helpFragment)
        }

        binding.bnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_settingsFragment)
        }

        binding.bnProfile.setOnClickListener {
            if (isNetworkAvailable(requireContext())) {
                findNavController().navigate(R.id.action_menuFragment_to_profileFragment)
            } else {
                requireContext().toast("Нет интернета")
            }

        }

        binding.bnFriends.setOnClickListener {
            if (user?.isAnonymousAccount == false) {
                findNavController().navigate(R.id.action_menuFragment_to_friendsFragment)
            } else if (user == null) {
                requireContext().toast("Проблемы с получением пользователя...")
            } else {
                requireContext().toast("Подключите Google к аккаунту!")
            }
        }

        val dialog = BottomSheetDialog(requireContext())
        val bsdBinding = BottomSheetFragmentBinding.inflate(layoutInflater)

        binding.tvGameTitle.setOnClickListener {
            dialog.setCancelable(true)
            dialog.setContentView(bsdBinding.root)
            dialog.show()
        }

    }
    private fun showBottomSheetDialog(nickname: (String) -> Unit) {

        val dialog = BottomSheetDialog(requireContext())
        val bsdBinding = BottomSheetFragmentBinding.inflate(layoutInflater)

        dialog.setCancelable(true)
        dialog.setContentView(bsdBinding.root)
        dialog.show()
    }
    private fun observeUserLiveData() {
        userViewModel.userLiveData.observe(viewLifecycleOwner) { user ->
            when (user) {
                is Result.Success -> {
                    Log.d("TAG", "success: userId=${user.data.id}")
                    this@MenuFragment.user = user.data

                    if (user.data.isAnonymousAccount) binding.tvNickname.text = getString(R.string.quest)
                    else binding.tvNickname.text = user.data.name

                    binding.tvCrowns.text = user.data.points.toString()

                    invitationsViewModel.getIncomingInvitations(myId)
                    observeInvitations()
                }
                is Result.Failure -> {
                    Log.d("TAG", "failure log in: ${user.error}")
                    binding.tvNickname.text = "null"
                    binding.tvCrowns.text = ""
                }
            }
        }
    }
    private fun observeInvitations() {
        invitationsViewModel.invitesLiveData.observe(viewLifecycleOwner) { invitations ->
            when (invitations) {
                is Result.Success -> {
                    if (invitations.data.isNotEmpty()) binding.cvFriendsIndicator.visible()
                    else binding.cvFriendsIndicator.gone()
                }
                is Result.Failure -> {
                    Toast.makeText(context, "Error observeInvitations", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}