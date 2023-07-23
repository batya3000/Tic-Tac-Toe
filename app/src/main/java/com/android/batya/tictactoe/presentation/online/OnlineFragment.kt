package com.android.batya.tictactoe.presentation.online

import android.app.AlertDialog
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.android.batya.tictactoe.R
import com.android.batya.tictactoe.databinding.FragmentOnlineBinding
import com.android.batya.tictactoe.domain.model.Cell
import com.android.batya.tictactoe.domain.model.Field
import com.android.batya.tictactoe.domain.model.FriendInvitation
import com.android.batya.tictactoe.domain.model.Game
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.model.Turn
import com.android.batya.tictactoe.domain.model.User
import com.android.batya.tictactoe.domain.model.UserStatus
import com.android.batya.tictactoe.presentation.friends.viewmodel.FriendInvitationsViewModel
import com.android.batya.tictactoe.presentation.offline.OnCellClickedListener
import com.android.batya.tictactoe.presentation.online.viewmodel.PlayerViewModel
import com.android.batya.tictactoe.presentation.online.viewmodel.TimeViewModel
import com.android.batya.tictactoe.presentation.online.viewmodel.TurnsViewModel
import com.android.batya.tictactoe.presentation.online.viewmodel.UsersViewModel
import com.android.batya.tictactoe.presentation.settings.SettingsViewModel
import com.android.batya.tictactoe.util.Constants
import com.android.batya.tictactoe.util.gone
import com.android.batya.tictactoe.util.timeToString
import com.android.batya.tictactoe.util.toast
import com.android.batya.tictactoe.util.vibrateDevice
import com.android.batya.tictactoe.util.visible
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Date
import java.util.Timer
import kotlin.concurrent.thread
import kotlin.math.pow


class OnlineFragment : Fragment(R.layout.fragment_online) {
    private var _binding: FragmentOnlineBinding? = null
    private val binding get() = _binding!!

    private val usersViewModel by viewModel<UsersViewModel>()
    private val turnsViewModel by viewModel<TurnsViewModel>()
    private val playerViewModel by viewModel<PlayerViewModel>()
    private val timeViewModel by viewModel<TimeViewModel>()
    private val invitationsViewModel by viewModel<FriendInvitationsViewModel>()
    private val settingsViewModel by viewModel<SettingsViewModel>()

    private var me: User? = null
    private var enemy: User? = null

    private var myId: String = ""

    private var actionListener: OnCellClickedListener? = null
    private var winnerId: String = ""
    private var isPaused = false

    private var firstPlayer: String? = null
    private var currentPlayer: String? = null


    private var isMyTurn: Boolean = true
    private var isPrivate: Boolean = false
    private var isVibrationOn = false

    private var roomId: String = ""
    private lateinit var timer: Timer

    private var matchStartTime = 0L
    private var timestamp = 0L
    private var currentTurnStartTime = 0L


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnlineBinding.inflate(inflater, container, false)

        roomId = arguments?.getString(Constants.ARG_ROOM_ID).toString()
        isPrivate = arguments?.getBoolean(Constants.ARG_ROOM_PRIVATE) ?: false

        myId = Firebase.auth.currentUser!!.uid

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if (isNetworkAvailable(requireContext())) {
//
//        } else {
//
//        }

        initCallback()
        initUI()

        createField()
        usersViewModel.getConnections(roomId)
        usersViewModel.updateStatus(myId, UserStatus.IN_BATTLE)
        observeConnections()

    }
    private fun startMatch() {
        winnerId = ""

        //createField()
        setBlur()
        initTimer()
        initViewModels()
        initObservers()
        startTimer()
        gameMode(isMyTurn)
    }



    private fun initTimer() {
        timer = Timer()
        timeViewModel.setTimestamp(roomId)
        timeViewModel.setMatchStartTime(roomId)
    }

    private fun timerTask() {
        if (winnerId == "") timeViewModel.setTimestamp(roomId)
        val duration = Date(timestamp).time - Date(matchStartTime).time

        binding.tvTime.text = getString(R.string.game_online_subtitle_match_is_on_short) + timeToString(duration)
        val turnDuration = 60000 - (Date(timestamp).time - Date(currentTurnStartTime).time)

        binding.progressBar.progress = turnDuration.toInt()
        binding.tvTurnTime.text = timeToString(turnDuration)
    }
    private fun startTimer() {
        timer.scheduleAtFixedRate(kotlin.concurrent.timerTask {
            activity?.runOnUiThread {
                timerTask()
            }
        }, 0, 1000)
    }

    private fun stopTimer() {
        timer.cancel()
    }

    private fun initViewModels() {

        turnsViewModel.getLastTurn(roomId)
        turnsViewModel.getTurns(roomId)
        turnsViewModel.getWinner(roomId)
        playerViewModel.getFirstTurnPlayer(roomId)
        playerViewModel.getCurrentPlayer(roomId)

        timeViewModel.getTimestamp(roomId)
        timeViewModel.getMatchStartTime(roomId)
        timeViewModel.getCurrentTurnStartTime(roomId)

        settingsViewModel.loadSettings()
    }

    private fun initObservers() {
        observeTurns()
        observeLastTurn()
        observeFirstPlayerTurn()
        observeCurrentPlayer()
        observeWinner()

        observeStartTime()
        observeTimestamp()
        observeCurrentTurnStartLiveData()

        observeTheme()
        observeVibrations()
    }

    private fun observeTheme() {
        settingsViewModel.isLightMode.observe(viewLifecycleOwner) { isLightMode ->
            binding.fieldView.setTheme(isLightMode)
        }
    }
    private fun observeVibrations() {
        settingsViewModel.isVibrationOn.observe(viewLifecycleOwner) { isVibrationOn ->
            this@OnlineFragment.isVibrationOn = isVibrationOn
        }
    }

    private fun observeStartTime() {
        timeViewModel.startTimeLiveData.observe(viewLifecycleOwner) { timeLong ->
            when (timeLong) {
                is Result.Success -> {
                    matchStartTime = timeLong.data
                    if (currentTurnStartTime == 0L) currentTurnStartTime = matchStartTime
                }
                is Result.Failure -> {
                    Log.d("TAG", "failure load me: ${timeLong.error}")
                }
            }
        }
    }

    private fun observeTimestamp() {
        timeViewModel.timestampLiveData.observe(viewLifecycleOwner) { timeLong ->
            when (timeLong) {
                is Result.Success -> {
                    timestamp = timeLong.data

                    //binding.tvTurnTime.text = timeToString(30 - Date(timestamp - currentTurnStartTime).time)

                    if (timestamp != 0L && currentTurnStartTime != 0L && Date(timestamp - currentTurnStartTime).toInstant().epochSecond >= 60) {
                        if (currentPlayer == myId) {
                            turnsViewModel.setWinner(roomId, enemy!!.id)
                        } else {
                            turnsViewModel.setWinner(roomId, me!!.id)
                        }
                    }
                }
                is Result.Failure -> {
                    Log.d("TAG", "failure load me: ${timeLong.error}")
                }
            }
        }
    }
    private fun observeCurrentTurnStartLiveData() {
        timeViewModel.currentTurnStartTimeLiveData.observe(viewLifecycleOwner) { timeLong ->
            when (timeLong) {
                is Result.Success -> {
                    currentTurnStartTime = timeLong.data

                    binding.tvTurnTime.text = timeToString(60 - Date(timestamp - currentTurnStartTime).toInstant().epochSecond)

                }
                is Result.Failure -> {
                    Log.d("TAG", "failure load me: ${timeLong.error}")
                }
            }
        }
    }

    private fun initUI() {
        with(binding) {

            setActionListener()

            bnPause.setOnClickListener {
                pauseMode()
            }

            bnContinue.setOnClickListener {
                gameMode(isMyTurn)
            }
            cvTime.setOnClickListener {  }
            cvEnemy.setOnClickListener {
//                turnsViewModel.setCell(
//                    roomId = roomId,
//                    turn = Turn(
//                        playerId = myId,
//                        row = 12,
//                        column = 12
//                    )
//                )
            }

            bnMainMenu.setOnClickListener {
                if (winnerId == "") {
                    buildAlertDialog(getString(R.string.dialog_text)) {
                        findNavController().navigate(R.id.action_onlineFragment_to_menuFragment)
                    }
                } else {
                    findNavController().navigate(R.id.action_onlineFragment_to_menuFragment)
                }
            }
            bnSurrender.setOnClickListener {
                buildAlertDialog(getString(R.string.dialog_text_surrender)) {
                    if (winnerId == "" && enemy != null) turnsViewModel.setWinner(roomId, enemy!!.id)
                    if (enemy == null) findNavController().navigate(R.id.action_onlineFragment_to_menuFragment)
                }
            }

            with (binding.progressBar) {
                max = 60000
            }
        }
    }

    private fun observeFirstPlayerTurn() {
        playerViewModel.firstPlayerLiveData.observe(viewLifecycleOwner) { firstTurn ->
            when (firstTurn) {
                is Result.Success -> {
                    firstPlayer = firstTurn.data
                    playerViewModel.setCurrentPlayer(roomId, firstPlayer!!)

                    Log.d("TAG", "observeFirstPlayerTurn: firstPlayer=$firstPlayer")
                }
                is Result.Failure -> {
                    Log.d("TAG", "observeFirstPlayerTurn: error=${firstTurn.error}")

                }
            }
        }
    }

    private fun observeTurns() {
        turnsViewModel.turnsLiveData.observe(viewLifecycleOwner) { turns ->
            when (turns) {
                is Result.Success -> {
                    val field = Field(25, 25)
                    turns.data.forEach { turn ->
                        field.setCell(
                            row = turn.row,
                            column = turn.column,
                            cell = if (turn.playerId == firstPlayer) Cell.PLAYER_1 else Cell.PLAYER_2
                        )
                    }
                    binding.fieldView.field = field
                    if (isVibrationOn) vibrateDevice(requireContext(), 50L)

                    Log.d("TAG", "observeTurns: ${turns.data}")
                    if (field.hasWinner) {
                        binding.fieldView.drawWinLine = true
                    }
                }
                is Result.Failure -> {
                    Toast.makeText(context, "Error observeTurns", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeLastTurn() {
        turnsViewModel.lastTurnLiveData.observe(viewLifecycleOwner) { lastTurn ->
            when (lastTurn) {
                is Result.Success -> {
                    binding.fieldView.lastTurn = Pair(lastTurn.data.row, lastTurn.data.column)
                }
                is Result.Failure -> {}
            }
        }
    }

    private fun observeConnections() {
        usersViewModel.usersLiveData.observe(viewLifecycleOwner) { users ->
            when (users) {
                is Result.Success -> {
                    val connections = users.data
                    if (connections.isNotEmpty()) {
                        if (firstPlayer == null) {
                            playerViewModel.setFirstTurnPlayer(roomId, connections.random())
                        }
                        thread {
                            Thread.sleep(1350)

                            if (firstPlayer == null) {
                                activity?.runOnUiThread {
                                    if (findNavController().currentDestination?.id == R.id.onlineFragment) {
                                        requireContext().toast(getString(R.string.toast_error_connect))
                                        findNavController().navigate(R.id.action_onlineFragment_to_menuFragment)
                                    }
                                }

                            }
                        }
                        if (connections.size == 2) {
                            usersViewModel.getMe(myId)
                            usersViewModel.getEnemy(connections.first { it != myId })

                            observeMe()
                            observeEnemy()
                            startMatch()
                        }
                    }
                }
                is Result.Failure -> {
                    Toast.makeText(context, "Error observeConnections", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun observeMe() {
        usersViewModel.meLiveData.observe(viewLifecycleOwner) { user ->
            when (user) {
                is Result.Success -> {
                    me = user.data

                    observeEnemy()
                }
                is Result.Failure -> {}
            }
        }
    }

    private fun observeEnemy() {
        usersViewModel.enemyLiveData.observe(viewLifecycleOwner) { user ->
            when (user) {
                is Result.Success -> {
                    enemy = user.data



                    if (enemy!!.isAnonymousAccount) binding.tvEnemyName.text = getString(R.string.quest)
                    else binding.tvEnemyName.text = enemy!!.name
                    binding.tvEnemyPoints.text = enemy!!.points.toString()
                    Log.d("TAG", "observeConnections: enemyId: ${enemy!!.id}")
                    if (enemy!!.photoUri != null) {
                        binding.ivPhoto.load(user.data.photoUri)
                    } else {
                        binding.ivPhoto.setImageResource(R.drawable.ic_photo)
                    }


                    invitationsViewModel.getOutgoingInvitations(me!!.id)
                    observeInvitations(me!!, enemy!!)

                }
                is Result.Failure -> {}
            }
        }
    }


    private fun observeInvitations(me: User, enemy: User) {
        invitationsViewModel.invitesLiveData.observe(viewLifecycleOwner) { invitations ->
            when (invitations) {
                is Result.Success -> {
                    initSendInvitationButton(invitations.data, me, enemy)
                }
                is Result.Failure -> {
                    Toast.makeText(context, "Error observeFriends", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initSendInvitationButton(invitations: List<FriendInvitation>, me: User, enemy: User) {
        if (
            !me.isAnonymousAccount &&
            !enemy.isAnonymousAccount &&
            enemy.id !in me.friends &&
            enemy.id !in invitations.map { it.toId }

        ) {
            binding.bnSendInvitation.visible()
            binding.bnSendInvitation.setOnClickListener {
                invitationsViewModel.sendInvitation(
                    FriendInvitation(
                        fromName = me.name,
                        fromId =  me.id,
                        toId = enemy.id
                    )
                )
                //binding.bnSendInvitation.gone()
            }
        } else {
            binding.bnSendInvitation.gone()
        }
    }


    private fun observeCurrentPlayer() {
        playerViewModel.currentPlayerLiveData.observe(viewLifecycleOwner) { player ->
            when (player) {
                is Result.Success -> {
                    currentPlayer = player.data
                    if (currentPlayer == myId) {
                        isMyTurn = true
                        binding.tvTurn.text = getString(R.string.game_your_turn)
                        binding.tvTurn.setTextColor(getColor(requireContext(), R.color.primary2))
                        binding.tvTurnTime.setTextColor(getColor(requireContext(), R.color.primary2))
                        binding.progressBar.setIndicatorColor(getColor(requireContext(), R.color.primary2))
                        binding.progressBar.trackColor = getColor(requireContext(), R.color.trackColor)
                    } else {
                        isMyTurn = false
                        binding.tvTurn.text = getString(R.string.game_enemy_turn)
                        binding.tvTurn.setTextColor(getColor(requireContext(), R.color.secondary))
                        binding.tvTurnTime.setTextColor(getColor(requireContext(), R.color.secondary))
                        binding.progressBar.setIndicatorColor(getColor(requireContext(), R.color.secondary))
                        binding.progressBar.trackColor = getColor(requireContext(), R.color.trackEnemyTurnColor)
                    }
                }
                is Result.Failure -> {
                    Toast.makeText(context, "Error observeCurrentPlayer", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun observeWinner() {
        turnsViewModel.winnerLiveData.observe(viewLifecycleOwner) { winner ->
            when (winner) {
                is Result.Success -> {
                    winnerId = winner.data
                    stopTimer()
                    removeObservers()

                    endMode(winnerId == myId)
                    if (isVibrationOn) vibrateDevice(requireContext(), 250L)

                    if (!isPrivate) {
                        if (winnerId == myId) {
                            usersViewModel.saveGame(
                                userId = myId,
                                game = Game(winnerId)
                            )
                            usersViewModel.saveGame(
                                userId = enemy!!.id,
                                game = Game(winnerId)
                            )
                        }

                        updatePoints(winnerId = winnerId, me!!.points, enemy!!.points)
                    }
                    usersViewModel.removeRoom(roomId)


                    Log.d("TAG", "endmode: winner=$winnerId")
                }
                is Result.Failure -> {
                    Toast.makeText(context, "Error observeTurns", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updatePoints(winnerId: String, myPoints: Int, enemyPoints: Int) {
        val textPoints =
            if (winnerId == myId) {
                val expectedWinnerPoints = 1 / (1.75 + 10.0.pow((enemyPoints - myPoints) / 800.0))
                val expectedLoserPoints = 1 / (1.75 + 10.0.pow((myPoints - enemyPoints) / 800.0))
                val pointsWinner = (24 * (1 - expectedWinnerPoints)).toInt()
                //val pointsLoser = (24 * (0 - expectedLoserPoints)).toInt()

                usersViewModel.updatePoints(myId, myPoints + pointsWinner)
                usersViewModel.updatePoints(enemy!!.id, enemyPoints - pointsWinner)

                pointsWinner
            } else {
                //val expectedWinnerPoints = 1 / (1.75 + 10.0.pow((enemyPoints - myPoints) / 800.0))
                val expectedLoserPoints = 1 / (1.75 + 10.0.pow((myPoints - enemyPoints) / 800.0))
                val pointsLoser = (24 * (1 - expectedLoserPoints)).toInt()

                pointsLoser
            }

        if (winnerId == myId) binding.tvEndPoints.text = "+$textPoints"
        else binding.tvEndPoints.text = "-$textPoints"


    }
    private fun gameMode(isMyTurn: Boolean) {
        with(binding) {
            isPaused = false

            layoutGameMenu.visible()
            layoutTurn.visible()
            if (isMyTurn) {
                tvTurn.text = getString(R.string.game_your_turn)
                tvTurn.setTextColor(getColor(requireContext(), R.color.primary2))
                tvTurnTime.setTextColor(getColor(requireContext(), R.color.primary2))
                progressBar.setIndicatorColor(getColor(requireContext(), R.color.primary2))
                progressBar.trackColor = getColor(requireContext(), R.color.trackColor)

            } else {
                tvTurn.text = getString(R.string.game_enemy_turn)
                tvTurn.setTextColor(getColor(requireContext(), R.color.secondary))
                tvTurnTime.setTextColor(getColor(requireContext(), R.color.secondary))
                progressBar.setIndicatorColor(getColor(requireContext(), R.color.secondary))
                progressBar.trackColor = getColor(requireContext(), R.color.trackEnemyTurnColor)
            }


            layoutPauseMenu.gone()
            layoutEndTextViews.gone()
            layoutEndMenu.gone()

            tvTime.text = getString(R.string.game_online_subtitle_match_is_on)
            tvPause.gone()


            ivTime.background = getDrawable(requireContext(), R.drawable.ic_clock)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                zoomLayout.setRenderEffect(null)
            }
            zoomLayout.setZoomEnabled(true)
            zoomLayout.setFlingEnabled(true)
            zoomLayout.setScrollEnabled(true)
            zoomLayout.setVerticalPanEnabled(true)
            zoomLayout.setHorizontalPanEnabled(true)
            fieldView.actionListener = actionListener

        }
    }

    private fun pauseMode() {
        with(binding) {
            isPaused = true
            layoutGameMenu.gone()
            layoutPauseMenu.visible()

            tvPause.visible()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                zoomLayout.setRenderEffect(RenderEffect.createBlurEffect(16f, 16f, Shader.TileMode.DECAL))
            }

            ivTime.background = getDrawable(requireContext(), R.drawable.ic_pause)
            zoomLayout.setZoomEnabled(false)
            zoomLayout.setFlingEnabled(false)
            zoomLayout.setScrollEnabled(false)
            zoomLayout.setVerticalPanEnabled(false)
            zoomLayout.setHorizontalPanEnabled(false)
            fieldView.clearActionListeners()
        }
    }
    private fun endMode(isWinMode: Boolean) {
        with(binding) {
            isPaused = false

            fieldView.clearActionListeners()
            layoutPauseMenu.gone()
            tvPause.gone()
            layoutGameMenu.gone()

            ivTime.background = getDrawable(requireContext(), R.drawable.ic_clock)

            if (isPrivate) {
                cvMatchPoints.gone()
            } else {
                cvMatchPoints.visible()
            }

            layoutEndTextViews.visible()


            if (isWinMode) {
                tvWinner.text = getString(R.string.game_subtitle_you_won)
                tvWinner.setTextColor(resources.getColor(R.color.primary2, null))
                clMatchPoints.background = getDrawable(requireContext(), R.drawable.gradient_primary)
            } else {
                tvWinner.text = getString(R.string.game_subtitle_you_lose)
                tvWinner.setTextColor(resources.getColor(R.color.secondary, null))
                clMatchPoints.background = getDrawable(requireContext(), R.drawable.gradient_yellow)
            }

            layoutTurn.gone()
            layoutEndMenu.visible()
        }
    }


    private fun buildAlertDialog(message: String, onPositiveClicked: () -> Unit) {
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        builder.setTitle(getString(R.string.dialog_title_warning))
        builder.setMessage(message)
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            onPositiveClicked()
        }
        builder.setNegativeButton(getString(R.string.no)) { _, _ -> }
        builder.show()
    }

    private fun createField() {
        binding.fieldView.field = Field(25, 25)
        binding.fieldView.actionListener = actionListener
    }

    private fun setActionListener() {
        actionListener = { row, column, field ->
            if (currentPlayer == myId) {
                val cellType = field.getCell(row, column)
                if (cellType == Cell.EMPTY) {
                    turnsViewModel.setCell(
                        roomId = roomId,
                        turn = Turn(
                            playerId = myId,
                            row = row,
                            column = column
                        )
                    )


                    timeViewModel.setCurrentTurnStartTime(roomId)

                    val cell = if (firstPlayer == currentPlayer) Cell.PLAYER_1 else Cell.PLAYER_2
                    if (field.checkWinnerOnline(row, column, cell)) {
                        binding.fieldView.drawWinLine = true
                        turnsViewModel.setWinner(roomId, myId)
                    }
                    playerViewModel.setCurrentPlayer(roomId, enemy!!.id)

                }
            }
        }
    }

    private fun setBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.ivGrad1.setRenderEffect(RenderEffect.createBlurEffect(30f, 30f, Shader.TileMode.CLAMP))
            binding.ivGrad2.setRenderEffect(RenderEffect.createBlurEffect(30f, 30f, Shader.TileMode.CLAMP))
        }
    }

    private fun initCallback() {
        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (isPaused) {
                gameMode(isMyTurn)
            } else {
                buildAlertDialog(getString(R.string.dialog_text)) {
                    findNavController().navigate(R.id.action_onlineFragment_to_menuFragment)
                }
            }
        }
    }

    private fun removeObservers() {
        usersViewModel.usersLiveData.removeObservers(viewLifecycleOwner)
        turnsViewModel.turnsLiveData.removeObservers(viewLifecycleOwner)
        turnsViewModel.lastTurnLiveData.removeObservers(viewLifecycleOwner)
        turnsViewModel.winnerLiveData.removeObservers(viewLifecycleOwner)
        playerViewModel.firstPlayerLiveData.removeObservers(viewLifecycleOwner)
        playerViewModel.currentPlayerLiveData.removeObservers(viewLifecycleOwner)
        timeViewModel.currentTurnStartTimeLiveData.removeObservers(viewLifecycleOwner)
        timeViewModel.timestampLiveData.removeObservers(viewLifecycleOwner)
        timeViewModel.startTimeLiveData.removeObservers(viewLifecycleOwner)
    }
    override fun onDestroy() {
        super.onDestroy()
        if (winnerId == "" && enemy != null) {
            turnsViewModel.setWinner(roomId, enemy!!.id)
            usersViewModel.removeRoom(roomId)
        }
        if (enemy == null) usersViewModel.removeRoom(roomId)
        usersViewModel.updateRoomConnected(myId, null)
        usersViewModel.updateStatus(myId, UserStatus.ONLINE)
    }
}