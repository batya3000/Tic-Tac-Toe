package com.android.batya.tictactoe.presentation.waiting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieDrawable
import com.android.batya.tictactoe.R
import com.android.batya.tictactoe.databinding.FragmentWaitingBinding
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.model.Room
import com.android.batya.tictactoe.domain.model.User
import com.android.batya.tictactoe.domain.model.UserStatus
import com.android.batya.tictactoe.presentation.BattleInvitationsViewModel
import com.android.batya.tictactoe.presentation.menu.UserViewModel
import com.android.batya.tictactoe.util.Constants
import com.android.batya.tictactoe.util.gone
import com.android.batya.tictactoe.util.isNetworkAvailable
import com.android.batya.tictactoe.util.visible
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.UUID
import kotlin.concurrent.thread
import kotlin.math.abs


class WaitingFragmentBackup : Fragment(R.layout.fragment_waiting) {
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
                waitingInvitedUI()
                observeBattleInvitations()
            }
        } else {
            noInternetUI()
        }

        binding.bnMainMenu.setOnClickListener {
            findNavController().navigate(R.id.action_waitingFragment_to_menuFragment)
        }
    }
    private fun observeBattleInvitations() {
        invitationsViewModel.invitesLiveData.removeObservers(viewLifecycleOwner)
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

    private fun observeRoomConnected() {
        userViewModel.userLiveData.observe(viewLifecycleOwner) { user ->
            when (user) {
                is Result.Success -> {
                    this.user = user.data
                    val roomId = user.data.roomConnected

                    if (user.data.status == UserStatus.IN_BATTLE && roomIdArg != null) {
                        userViewModel.updateRoomConnected(myId, roomIdArg!!)
                    }

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
                is Result.Failure -> {}
            }
        }
    }



    private fun observeWaitingPool() {
        roomViewModel.waitingPoolLiveData.observe(viewLifecycleOwner) { waitingUsers ->
            when (waitingUsers) {
                is Result.Success -> {
                    Log.d("TAG", "observeWaitingPool: waitingUsers=${waitingUsers.data}")
                    binding.tvTitle.text = "Поиск игры..."
                    binding.tvSubtitle.text = "..."
                    if (waitingUsers.data.isEmpty()) {
                        userViewModel.updateStatus(myId, UserStatus.WAITING)
                    } else {
                        //thread {
                            //Thread.sleep(1000)

                        val users = waitingUsers.data.filter { it.id != myId }
                        if (users.isNotEmpty() && user != null) {
                            val sortedUsers = users.sortedBy { abs(it.points - user!!.points) }
                            Log.d("TAG", "users=${users.map { it.points }}")
                            Log.d("TAG", "sortedUsers=${sortedUsers.map { it.points }}")

                            val enemy = sortedUsers.firstOrNull()
                            Log.d("TAG", "enemy===${enemy?.points}")

                            if (enemy != null && enemy.roomConnected == null) {
                                createRoom(myId, enemy.id)
                            }
                            //}
                        }
                    }


                }
                is Result.Failure -> {
                    Log.d("TAG", "Не могу получить данные...")
                }
            }
        }
    }

    private fun createRoom(myId: String, enemyId: String) {
        if (!started) {
            val roomId = UUID.randomUUID().toString()

            userViewModel.updateStatus(myId, UserStatus.IN_BATTLE)
            userViewModel.updateStatus(enemyId, UserStatus.IN_BATTLE)

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
        binding.tvTitle.text = "Интернет-соединение отсутствует"
        binding.tvSubtitle.text = "В онлайн-игру сыграть не получится :("
        binding.tvMainMenu.text = "Выйти в меню"
        stopTimer()
    }

    private fun invitationDeclinedUI() {
        binding.lottie.gone()
        binding.imageView.visible()
        binding.imageView.setBackgroundResource(R.drawable.ic_decline_battle_invitation)
        binding.tvTitle.text = "Ваше приглашение отклонили"
        binding.tvSubtitle.text = "Возможно, что такой друг вам не нужен..."
        binding.tvMainMenu.text = "Выйти в меню"
        roomViewModel.removeRoom(roomIdArg!!)
        stopTimer()
    }

    private fun waitingUI() {
        binding.lottie.visible()
        binding.lottie.apply {
            repeatCount = LottieDrawable.INFINITE
            repeatMode = LottieDrawable.RESTART
            playAnimation()
        }
        binding.tvTitle.text = "Поиск игры..."
        binding.tvSubtitle.gone()
        binding.tvMainMenu.text = "Отменить поиск"
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
        binding.tvTitle.text = "Ожидание игрока..."
        binding.tvSubtitle.text = "Приглашение отправлено"
        binding.tvMainMenu.text = "Отменить игру"

        startTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (roomIdArg != null) {
            invitationsViewModel.removeInvitation(roomIdArg!!)
        }
    }
}
