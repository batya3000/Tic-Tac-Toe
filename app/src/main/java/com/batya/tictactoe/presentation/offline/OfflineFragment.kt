package com.batya.tictactoe.presentation.offline

import android.app.AlertDialog
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.batya.tictactoe.R
import com.batya.tictactoe.domain.model.Cell
import com.batya.tictactoe.domain.model.Field
import com.batya.tictactoe.databinding.FragmentOfflineBinding
import com.batya.tictactoe.domain.model.UserStatus
import com.batya.tictactoe.presentation.menu.UserViewModel
import com.batya.tictactoe.presentation.settings.SettingsViewModel
import com.batya.tictactoe.util.gone
import com.batya.tictactoe.util.vibrateDevice
import com.batya.tictactoe.util.visible
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel

class OfflineFragment : Fragment(R.layout.fragment_offline) {
    private var _binding: FragmentOfflineBinding? = null
    private val binding get() = _binding!!
    private var actionListener: OnCellClickedListener? = null
    private var winner: Cell = Cell.EMPTY
    private var timeWhenStopped: Long = 0
    private var isPaused = false
    private var isLightMode: Boolean = true

    private val userViewModel by viewModel<UserViewModel>()
    private val settingsViewModel by viewModel<SettingsViewModel>()
    private var myId: String = ""
    private var isVibrationOn = false
    private var areCrossesFirst = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOfflineBinding.inflate(inflater, container, false)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (isPaused) {
                gameMode()
            } else {
                buildAlertDialog(getString(R.string.dialog_text)) {
                    findNavController().navigateUp()
                }
            }
        }
        myId = Firebase.auth.currentUser!!.uid


        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        createField()
        setBlur()
        initChronometer()
        startTimer()

        settingsViewModel.loadSettings()
        userViewModel.updateStatus(myId, UserStatus.IN_BATTLE)
        observeTheme()
        observeVibrations()
        observeAreCrossesFirst()


    }

    private fun observeTheme() {
        settingsViewModel.isLightMode.observe(viewLifecycleOwner) { isLightMode ->
            this.isLightMode = isLightMode
        }
    }
    private fun observeVibrations() {
        settingsViewModel.isVibrationOn.observe(viewLifecycleOwner) { isVibrationOn ->
            this@OfflineFragment.isVibrationOn = isVibrationOn
        }
    }
    private fun observeAreCrossesFirst() {
        settingsViewModel.areCrossesFirst.observe(viewLifecycleOwner) { areCrossesFirst ->
            this@OfflineFragment.areCrossesFirst = areCrossesFirst
            binding.fieldView.setFirstTurn(areCrossesFirst)
        }
    }

    private fun initUI() {
        with(binding) {
            setActionListener()
            bnPause.setOnClickListener {
                pauseMode()
            }
            bnContinue.setOnClickListener {
                gameMode()
            }
            bnMainMenu.setOnClickListener {
                if (winner != Cell.EMPTY) {
                    findNavController().navigateUp()
                } else {
                    buildAlertDialog(getString(R.string.dialog_text)) {
                        findNavController().navigateUp()

                    }
                }

            }
            bnRerun.setOnClickListener {
                buildAlertDialog(getString(R.string.dialog_new_game_text)) {
                    createField()
                    gameMode()
                    resetTimer()
                    startTimer()
                    winner = Cell.EMPTY
                }
            }
            bnRollback.setOnClickListener {
                binding.fieldView.rollbackLastMove()
                setCurrentTurn()
            }
            cvTime.setOnClickListener {  }
        }
    }

    private fun buildAlertDialog( message: String, onPositiveClicked: () -> Unit) {
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        builder.setTitle(getString(R.string.dialog_title_warning))
        builder.setMessage(message)
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            onPositiveClicked()

        }
        builder.setNegativeButton(getString(R.string.no)) { _, _ -> }
        builder.show()
    }
    private fun initChronometer() {
        binding.chronometer.base = SystemClock.elapsedRealtime()
    }

    private fun startTimer() {
        binding.chronometer.base = SystemClock.elapsedRealtime() + timeWhenStopped
        binding.chronometer.start()
    }

    private fun stopTimer() {
        timeWhenStopped = binding.chronometer.base - SystemClock.elapsedRealtime()
        binding.chronometer.stop()
    }
    private fun resetTimer() {
        timeWhenStopped = 0
        binding.chronometer.base = SystemClock.elapsedRealtime()
    }

    private fun setBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.ivGrad1.setRenderEffect(RenderEffect.createBlurEffect(30f, 30f, Shader.TileMode.CLAMP))
            binding.ivGrad2.setRenderEffect(RenderEffect.createBlurEffect(30f, 30f, Shader.TileMode.CLAMP))
        }
    }

    private fun createField() {
        binding.fieldView.field = Field(30, 30)
        binding.fieldView.actionListener = actionListener
    }

    private fun setActionListener() {
        actionListener = {
                row, column, field ->
            val cell = field.getCell(row, column)
            if (cell == Cell.EMPTY) {
                if (getCurrentTurn() == TURN.PLAYER_1) {
                    field.setCell(row, column, Cell.PLAYER_1)
                    binding.fieldView.lastMovePlayer1 += Pair(row, column)
                    setCurrentTurn(TURN.PLAYER_2)
                } else {
                    field.setCell(row, column, Cell.PLAYER_2)
                    binding.fieldView.lastMovePlayer2 += Pair(row, column)
                    setCurrentTurn(TURN.PLAYER_1)
                }
                if (isVibrationOn) vibrateDevice(requireContext(), 50L)

                winner = field.checkWinnerOffline(row, column)
                if (winner != Cell.EMPTY) {
                    binding.fieldView.drawWinLine = true
                    if (isVibrationOn) vibrateDevice(requireContext(), 250L)
                    winMode()
                }

            }
        }
    }
    private fun setCurrentTurn(turn: TURN = getCurrentTurn()) {
        binding.fieldView.currentTurn = turn
        val turnString = if (turn == TURN.PLAYER_1) {
            if (areCrossesFirst) getString(R.string.turn_crosses)
            else getString(R.string.turn_noughts)
        } else {
            if (areCrossesFirst) getString(R.string.turn_noughts)
            else getString(R.string.turn_crosses)
        }
        binding.tvTurn.text = getString(R.string.turn) + turnString
    }
    private fun getCurrentTurn(): TURN {
        return binding.fieldView.currentTurn
    }
    private fun pauseMode() {
        with(binding) {
            isPaused = true

            layoutGameMenu.gone()
            layoutPauseEndMenu.visible()
            bnNext.gone()
            bnContinue.visible()

            tvPause.visible()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                zoomLayout.setRenderEffect(RenderEffect.createBlurEffect(16f, 16f, Shader.TileMode.DECAL))
            }

            binding.tvTime.text = getString(R.string.game_offline_subtitle_match_is_on)
            stopTimer()

            binding.ivTime.background = getDrawable(requireContext(), R.drawable.ic_pause)
            zoomLayout.setZoomEnabled(false)
            zoomLayout.setFlingEnabled(false)
            zoomLayout.setScrollEnabled(false)
            zoomLayout.setVerticalPanEnabled(false)
            zoomLayout.setHorizontalPanEnabled(false)
            fieldView.clearActionListeners()
        }
    }
    private fun gameMode() {
        with(binding) {
            isPaused = false
            binding.chronometer.start()

            layoutGameMenu.visible()
            layoutPauseEndMenu.gone()
            layoutEndTextViews.gone()
            bnNext.gone()

            tvTime.text = getString(R.string.game_offline_subtitle_match_is_on)
            tvPause.gone()

            startTimer()
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

            tvTurn.visible()

            setCurrentTurn()
        }
    }

    private fun winMode() {
        with(binding) {
            binding.chronometer.stop()
            stopTimer()
            fieldView.clearActionListeners()

            layoutGameMenu.gone()
            bnNext.visible()

            layoutEndTextViews.visible()

            val winnerString = if (winner == Cell.PLAYER_1) {
                if (areCrossesFirst) getString(R.string.turn_crosses)
                else getString(R.string.turn_noughts)
            } else {
                if (areCrossesFirst) getString(R.string.turn_noughts)
                else getString(R.string.turn_crosses)
            }

            tvWinner.text = getString(R.string.won) + winnerString

                //tvPause.text = "Матч окончен"

            tvTime.text = getString(R.string.game_match_duration)
            ivTime.background = getDrawable(requireContext(), R.drawable.ic_clock)




            tvTurn.gone()

            bnNext.setOnClickListener {
                layoutPauseEndMenu.visible()
                bnNext.gone()
                bnContinue.gone()
                zoomLayout.setZoomEnabled(false)
                zoomLayout.setFlingEnabled(false)
                zoomLayout.setScrollEnabled(false)
                zoomLayout.setVerticalPanEnabled(false)
                zoomLayout.setHorizontalPanEnabled(false)
                fieldView.clearActionListeners()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    zoomLayout.setRenderEffect(RenderEffect.createBlurEffect(16f, 16f, Shader.TileMode.DECAL))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        userViewModel.updateStatus(myId, UserStatus.ONLINE)
    }
}