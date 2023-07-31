package com.batya.tictactoe.presentation.waiting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieDrawable
import com.batya.tictactoe.R
import com.batya.tictactoe.databinding.FragmentWaitingBinding
import com.batya.tictactoe.domain.model.Result
import com.batya.tictactoe.domain.model.Room
import com.batya.tictactoe.domain.model.User
import com.batya.tictactoe.domain.model.UserStatus
import com.batya.tictactoe.presentation.BattleInvitationsViewModel
import com.batya.tictactoe.presentation.menu.UserViewModel
import com.batya.tictactoe.util.Constants
import com.batya.tictactoe.util.gone
import com.batya.tictactoe.util.isNetworkAvailable
import com.batya.tictactoe.util.visible
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.UUID
import kotlin.math.abs


class WaitingFragment : Fragment(R.layout.fragment_waiting) {
    private var _binding: FragmentWaitingBinding? = null
    private val binding get() = _binding!!

    private lateinit var myId: String
    private var user: User? = null

    private var roomIdArg: String? = null
    private var started: Boolean = false

    private val roomViewModel by viewModel<RoomViewModel>()
    private val userViewModel by viewModel<UserViewModel>()
    private val invitationsViewModel by viewModel<BattleInvitationsViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWaitingBinding.inflate(inflater, container, false)

        myId = Firebase.auth.currentUser!!.uid

        roomIdArg = arguments?.getString(Constants.ARG_ROOM_ID)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isNetworkAvailable(requireContext())) {
            userViewModel.getUser(myId)

            observeRoomConnected()

            if (roomIdArg == null) {
                roomViewModel.getWaitingPool()
                observeWaitingPool()
                waitingUI()
            } else {
                invitationsViewModel.getOutgoingInvitations(myId)
                observeBattleInvitations()
                waitingInvitedUI()
            }
        } else {
            noInternetUI()
        }

        binding.bnMainMenu.setOnClickListener {
            findNavController().navigate(R.id.action_waitingFragment_to_menuFragment)
        }
    }

    private fun observeRoomConnected() {
        userViewModel.userLiveData.observe(viewLifecycleOwner) { user ->
            when (user) {
                is Result.Success -> {
                    this.user = user.data

                    connectToRoom(user.data)
                }
                is Result.Failure -> {}
            }
        }
    }

    private fun connectToRoom(user: User) {
        val roomId = user.roomConnected

        if (roomId != null) {
            if (findNavController().currentDestination?.id == R.id.waitingFragment) {
                Log.d("TAG", "starting match $roomId")

                roomViewModel.connect(roomId, myId)

                if (roomIdArg != null) {
                    findNavController().navigate(
                        R.id.action_waitingFragment_to_onlineFragment,
                        bundleOf(Constants.ARG_ROOM_ID to roomId, Constants.ARG_ROOM_PRIVATE to true)
                    )
                } else {
                    findNavController().navigate(
                        R.id.action_waitingFragment_to_onlineFragment,
                        bundleOf(Constants.ARG_ROOM_ID to roomId)
                    )
                }
            }
        }
    }

    private fun observeBattleInvitations() {
        invitationsViewModel.invitesLiveData.observe(viewLifecycleOwner) { invitations ->
            when (invitations) {
                is Result.Success -> {
                    if (invitations.data.isEmpty()) {
                        invitationDeclinedUI()
                    }
                }
                is Result.Failure -> {
                    Log.d("TAG", "failure load me: ${invitations.error}")
                }
            }
        }
    }

    private fun observeWaitingPool() {
        roomViewModel.waitingPoolLiveData.observe(viewLifecycleOwner) { waitingUsers ->
            when (waitingUsers) {
                is Result.Success -> {
                    //Log.d("TAG", "observeWaitingPool: waitingUsers=${waitingUsers.data}")

                    if (waitingUsers.data.isEmpty()) {
                        userViewModel.updateStatus(myId, UserStatus.WAITING)
                    } else {
                        foundEnemy(waitingUsers.data)
                    }
                }
                is Result.Failure -> {
                    Log.d("TAG", "Не могу получить данные...")
                }
            }
        }
    }

    private fun foundEnemy(usersPool: List<User>) {
        val users = usersPool.filter { it.id != myId }
        if (users.isNotEmpty() && user != null) {
            val sortedUsers = users.sortedBy { abs(it.points - user!!.points) }
            //Log.d("TAG", "users=${users.map { it.points }}")
            //Log.d("TAG", "sortedUsers=${sortedUsers.map { it.points }}")

            val enemy = sortedUsers.firstOrNull()
            Log.d("TAG", "enemy = ${enemy?.points}")

            if (enemy != null && enemy.roomConnected == null) {
                createRoom(myId, enemy.id)
            }
            //}
        }
    }

    private fun createRoom(myId: String, enemyId: String) {
        if (!started) {
            val roomId = UUID.randomUUID().toString()

            Log.d("TAG", "connectToRoom: roomId=$roomId")
            userViewModel.updateRoomConnected(myId, roomId)
            userViewModel.updateRoomConnected(enemyId, roomId)

            roomViewModel.createRoom(room = Room(id = roomId))
            started = true
        }
    }

    private fun noInternetUI() {
        binding.lottie.gone()
        binding.imageView.visible()
        binding.imageView.setBackgroundResource(R.drawable.ic_no_internet)
        binding.tvTitle.text = getString(R.string.waiting_no_internet_title)
        binding.tvSubtitle.text = getString(R.string.wating_no_internet_subtitle)
        binding.tvMainMenu.text = getString(R.string.game_button_main_menu)
        stopTimer()
    }

    private fun invitationDeclinedUI() {
        binding.lottie.gone()
        binding.imageView.visible()
        binding.imageView.setBackgroundResource(R.drawable.ic_decline_battle_invitation)
        binding.tvTitle.text = getString(R.string.waiting_invite_reqected_title)
        binding.tvSubtitle.text = getString(R.string.waiting_invite_declined_subtitle)
        binding.tvMainMenu.text = getString(R.string.game_button_main_menu)
        stopTimer()
    }

    private fun waitingUI() {
        binding.lottie.visible()
        binding.lottie.apply {
            repeatCount = LottieDrawable.INFINITE
            repeatMode = LottieDrawable.RESTART
            playAnimation()
        }
        binding.tvTitle.text = getString(R.string.waiting_in_search)
        binding.tvSubtitle.gone()
        binding.tvMainMenu.text = getString(R.string.waiting_button_cancel_search)
        startTimer()
    }

    private fun startTimer() {
        binding.cvTime.visible()
        binding.chronometer.start()
    }
    private fun stopTimer() {
        binding.cvTime.gone()
        binding.chronometer.stop()
    }
    private fun waitingInvitedUI() {
        binding.lottie.visible()
        binding.lottie.apply {
            repeatCount = LottieDrawable.INFINITE
            repeatMode = LottieDrawable.RESTART
            playAnimation()
        }
        binding.tvTitle.text = getString(R.string.waiting_waiting_for_player_title)
        binding.tvSubtitle.text = getString(R.string.waiting_waiting_for_player_subtitle)
        binding.tvMainMenu.text = getString(R.string.waiting_button_cancel_game)

        startTimer()
    }

    override fun onPause() {
        super.onPause()
        if (user != null) {
            userViewModel.updateStatus(user!!.id, UserStatus.OFFLINE)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (roomIdArg != null) {
            invitationsViewModel.removeInvitation(roomIdArg!!)
        }
    }
}
