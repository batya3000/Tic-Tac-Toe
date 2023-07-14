package com.android.batya.tictactoe.presentation.waiting

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
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
import com.android.batya.tictactoe.presentation.menu.UserViewModel
import com.android.batya.tictactoe.util.Constants
import com.android.batya.tictactoe.util.isNetworkAvailable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel


class WaitingFragment : Fragment(R.layout.fragment_waiting) {
    private var _binding: FragmentWaitingBinding? = null
    private val binding get() = _binding!!

    private lateinit var myId: String
    private var roomConnected: Room? = null
    private lateinit var user: User
    private var rooms: List<Room> = listOf()
    private val roomViewModel by viewModel<RoomViewModel>()
    private val userViewModel by viewModel<UserViewModel>()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWaitingBinding.inflate(inflater, container, false)

        myId = Firebase.auth.currentUser!!.uid


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isNetworkAvailable(requireContext())) {
            userViewModel.getUser(myId)
            observeUser()
            waitingUI()
        } else {
            noInternetUI()


        }


        binding.bnMainMenu.setOnClickListener {
            if (roomConnected != null && roomConnected?.connections?.size != 2) roomViewModel.removeRoom(roomConnected!!.id)
            findNavController().navigate(R.id.action_waitingFragment_to_menuFragment)
        }


    }

    private fun noInternetUI() {
        binding.lottie.setBackgroundResource(R.drawable.ic_no_internet)
        binding.tvTitle.text = "Интернет-соединение отсутствует"
        binding.tvSubtitle.text = "В онлайн-игру сыграть не получится :("
        binding.tvMainMenu.text = "Выйти в меню"
    }

    private fun waitingUI() {
        binding.lottie.apply {
            repeatCount = LottieDrawable.INFINITE
            repeatMode = LottieDrawable.RESTART
            playAnimation()
        }
        binding.tvMainMenu.text = "Отменить поиск"
    }

    private fun initRoomConnected() {
        with(binding) {
            if (rooms.isNotEmpty()) {

                roomConnected = rooms.filter { !it.isRunning }.random()
                roomViewModel.connect(
                    roomId = roomConnected!!.id,
                    user = user
                )
            } else {
                val room = Room(connections = mapOf(myId to user))
                roomConnected = room
                roomViewModel.createRoom(room)
            }
        }
    }

    private fun observeUser() {
        userViewModel.userLiveData.observe(viewLifecycleOwner) { user ->
            when (user) {
                is Result.Success -> {
                    this@WaitingFragment.user = user.data
                    roomViewModel.getRooms()
                    observeRooms()
                }
                is Result.Failure -> {
                    Log.d("TAG", "failure load me: ${user.error}")
                }
            }
        }
    }

    private fun observeRooms() {
        roomViewModel.roomsLiveData.removeObservers(viewLifecycleOwner)
        roomViewModel.roomsLiveData.observe(viewLifecycleOwner) { rooms ->
            when (rooms) {
                is Result.Success -> {
                    this@WaitingFragment.rooms = rooms.data
                    binding.tvTitle.text = "Поиск игры..."
                    binding.tvSubtitle.text = "..."

                    val temp = rooms.data.filter { it.connections.containsKey(myId) }

                    if (temp.isNotEmpty()) roomConnected = temp[0]

                    if (roomConnected?.connections?.size == 2) {
                        roomViewModel.updateIsRunning(roomConnected!!.id, true)
                        if (findNavController().currentDestination?.id == R.id.waitingFragment) {
                            findNavController().navigate(
                                R.id.action_waitingFragment_to_onlineFragment,
                                bundleOf(Constants.ROOMS_REF to roomConnected!!.id)
                            )
                        }
                    }
                    initRoomConnected()
                }
                is Result.Failure -> {
                    Log.d("TAG", "Не могу получить данные...")
                }
            }
        }
    }


    override fun onPause() {
        super.onPause()

        if (roomConnected != null && roomConnected?.connections?.size != 2) roomViewModel.removeRoom(roomConnected!!.id)

    }
}
