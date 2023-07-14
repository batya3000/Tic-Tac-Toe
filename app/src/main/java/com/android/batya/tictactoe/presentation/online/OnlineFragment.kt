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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.batya.tictactoe.R
import com.android.batya.tictactoe.databinding.FragmentOnlineBinding
import com.android.batya.tictactoe.domain.model.*
import com.android.batya.tictactoe.presentation.friends.viewmodel.FriendInvitationsViewModel
import com.android.batya.tictactoe.presentation.offline.OnCellClickedListener
import com.android.batya.tictactoe.presentation.online.viewmodel.UsersViewModel
import com.android.batya.tictactoe.presentation.online.viewmodel.PlayerViewModel
import com.android.batya.tictactoe.presentation.online.viewmodel.TimeViewModel
import com.android.batya.tictactoe.presentation.online.viewmodel.TurnsViewModel
import com.android.batya.tictactoe.util.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.math.pow


class OnlineFragment : Fragment(R.layout.fragment_online) {
    private var _binding: FragmentOnlineBinding? = null
    private val binding get() = _binding!!

    private val usersViewModel by viewModel<UsersViewModel>()
    private val turnsViewModel by viewModel<TurnsViewModel>()
    private val playerViewModel by viewModel<PlayerViewModel>()
//    private val userViewModel by viewModel<UserViewModel>()
    private val timeViewModel by viewModel<TimeViewModel>()
    private val invitationsViewModel by viewModel<FriendInvitationsViewModel>()


    private var myId: String = ""
    private var myName: String = ""
    private var myPoints: Int = 0

    private var enemyId: String? = null
    private var enemyPoints: Int = 0

    private var actionListener: OnCellClickedListener? = null
    private var winnerId: String = ""
    private var isPaused = false

    private var firstPlayer: String? = null
    private var currentPlayer: String? = null


    private var isMyTurn: Boolean = true
    private var connections = listOf<String>()
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

        roomId = arguments?.getString(Constants.ROOMS_REF).toString()
        myId = Firebase.auth.currentUser!!.uid

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startMatch()
    }
    private fun startMatch() {
        winnerId = ""
        initCallback()
        initUI()
        createField()
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
        timeViewModel.setTimestamp(roomId)
        val duration = Date(timestamp).time - Date(matchStartTime).time

        binding.tvTime.text = "Матч длится ${timeToString(duration)}"
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
        usersViewModel.getConnections(roomId)
        turnsViewModel.getLastTurn(roomId)
        turnsViewModel.getTurns(roomId)
        turnsViewModel.getWinner(roomId)
        playerViewModel.getFirstTurnPlayer(roomId)
        playerViewModel.getCurrentPlayer(roomId)
//        userViewModel.getUser(myId)


        timeViewModel.getTimestamp(roomId)
        timeViewModel.getMatchStartTime(roomId)
        timeViewModel.getCurrentTurnStartTime(roomId)
    }

    private fun initObservers() {
        observeConnections()
        observeTurns()
        observeLastTurn()
        observeFirstPlayerTurn()
        observeCurrentPlayer()
        observeWinner()

        observeStartTime()
        observeTimestamp()
        observeCurrentTurnStartLiveData()
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
                            turnsViewModel.setWinner(roomId, enemyId!!)
                        } else {
                            turnsViewModel.setWinner(roomId, myId)
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

            bnMainMenu.setOnClickListener {
                if (winnerId == "") {
                    buildAlertDialog("Предупреждение", "Вы действительно хотите выйти в меню?") {
                        usersViewModel.disconnect(roomId)
                        findNavController().navigate(R.id.action_onlineFragment_to_menuFragment)
                    }
                } else {
                    usersViewModel.disconnect(roomId)
                    findNavController().navigate(R.id.action_onlineFragment_to_menuFragment)
                }
            }
            bnSurrender.setOnClickListener {
                buildAlertDialog("Предупреждение", "Вы действительно хотите сдаться?") {
                    turnsViewModel.setWinner(roomId, enemyId!!)
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
                is Result.Failure -> {
                    //
                }
            }
        }
    }

    private fun observeConnections() {
        usersViewModel.usersLiveData.observe(viewLifecycleOwner) { users ->
            when (users) {
                is Result.Success -> {
                    if (users.data.isNotEmpty()) {
                        if (firstPlayer == null) {
                            firstPlayer = users.data.map { it.id }.random()
                            playerViewModel.setFirstTurnPlayer(roomId, firstPlayer!!)
                        }


                        if (users.data.size == 1) {
                            Toast.makeText(context, "Противник покинул матч.", Toast.LENGTH_SHORT).show()
                            if (winnerId == "") turnsViewModel.setWinner(roomId, myId)
                            usersViewModel.removeRoom(roomId)
                        }

                        if (users.data.size == 2) {
                            val enemy = users.data.filter { it.id != myId }[0]
                            enemyId = enemy.id
                            enemyPoints = enemy.points
                            if (enemy.isAnonymousAccount) binding.tvEnemyName.text = getString(R.string.quest)
                            else binding.tvEnemyName.text = enemy.name
                            binding.tvEnemyPoints.text = enemyPoints.toString()
                            Log.d("TAG", "observeConnections: enemyId: $enemyId")

                            val me = users.data.filter { it.id == myId }[0]
                            myName = me.name
                            myPoints = me.points

                            invitationsViewModel.getOutgoingInvitations(myId)
                            observeInvitations(me, enemy)
                        }
                    }
                    this@OnlineFragment.connections = users.data.map { it.id }
                }
                is Result.Failure -> {
                    Toast.makeText(context, "Error observeConnections", Toast.LENGTH_SHORT).show()
                }
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
                        binding.tvTurn.text = "Ваш ход"
                    } else {
                        isMyTurn = false
                        binding.tvTurn.text = "Ход врага"
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
                    endMode(winnerId == myId)
                    removeObservers()
                    usersViewModel.removeRoom(roomId)

                    if (winnerId == myId) {
                        usersViewModel.saveGame(
                            userId = myId,
                            game = Game(winnerId)
                        )
                        usersViewModel.saveGame(
                            userId = enemyId!!,
                            game = Game(winnerId)
                        )
                    }

                    updatePoints(winnerId = winnerId, myPoints, enemyPoints)


                    Log.d("TAG", "endmode: winner=$winnerId")
                }
                is Result.Failure -> {
                    Toast.makeText(context, "Error observeTurns", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updatePoints(winnerId: String, myPoints: Int, enemyPoints: Int) {
        val expectedPoints = 1 / (1.65 + 10.0.pow((enemyPoints - myPoints) / 400.0))
        val points =
            if (winnerId == myId) (24 * (1 - expectedPoints)).toInt()
            else (24 * (0 - expectedPoints)).toInt()

        usersViewModel.updatePoints(myId, myPoints + points)
        if (winnerId == myId) binding.tvEndPoints.text = "+$points"
        else binding.tvEndPoints.text = "$points"


    }
    private fun gameMode(isMyTurn: Boolean) {
        with(binding) {
            isPaused = false

            layoutGameMenu.visible()
            layoutTurn.visible()
            if (isMyTurn) {
                tvTurn.text = "Ваш ход"
            } else {
                tvTurn.text = "Ход врага"
            }


            layoutPauseMenu.gone()
            layoutEndTextViews.gone()
            layoutEndMenu.gone()

            tvTime.text = "Матч длится ..."
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

            binding.ivTime.background = getDrawable(requireContext(), R.drawable.ic_pause)
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

            layoutEndTextViews.visible()
            if (isWinMode) {
                tvWinner.text = "Вы выиграли!"
                tvWinner.setTextColor(resources.getColor(R.color.primary, null))
                clMatchPoints.background = getDrawable(requireContext(), R.drawable.gradient_primary)
            } else {
                tvWinner.text = "Вы проиграли!"
                tvWinner.setTextColor(resources.getColor(R.color.secondary, null))
                clMatchPoints.background = getDrawable(requireContext(), R.drawable.gradient_yellow)
            }

            layoutTurn.gone()
            layoutEndMenu.visible()
        }
    }


    private fun buildAlertDialog(title: String, message: String, onPositiveClicked: () -> Unit) {
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Да") { _, _ ->
            onPositiveClicked()
            //TODO()
        }
        builder.setNegativeButton("Нет") { _, _ -> }
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
                    playerViewModel.setCurrentPlayer(roomId, enemyId!!)

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
                buildAlertDialog("Предупреждение", "Вы действительно хотите завершить игру и выйти в меню?") {
                    usersViewModel.disconnect(roomId)
                    findNavController().navigate(R.id.action_onlineFragment_to_menuFragment)
                }
            }
        }
    }

    private fun removeObservers() {
        usersViewModel.usersLiveData.removeObservers(viewLifecycleOwner)
        turnsViewModel.turnsLiveData.removeObservers(viewLifecycleOwner)
        turnsViewModel.lastTurnLiveData.removeObservers(viewLifecycleOwner)
        playerViewModel.firstPlayerLiveData.removeObservers(viewLifecycleOwner)
        playerViewModel.currentPlayerLiveData.removeObservers(viewLifecycleOwner)
        turnsViewModel.winnerLiveData.removeObservers(viewLifecycleOwner)
        timeViewModel.currentTurnStartTimeLiveData.removeObservers(viewLifecycleOwner)
        timeViewModel.timestampLiveData.removeObservers(viewLifecycleOwner)
        timeViewModel.startTimeLiveData.removeObservers(viewLifecycleOwner)
    }

    override fun onDestroy() {
        super.onDestroy()

        usersViewModel.disconnect(roomId)
    }
}