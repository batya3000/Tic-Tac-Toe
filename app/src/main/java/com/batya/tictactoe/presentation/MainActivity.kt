package com.batya.tictactoe.presentation

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.batya.tictactoe.R
import com.batya.tictactoe.databinding.ActivityMainBinding
import com.batya.tictactoe.databinding.BottomSheetFragmentBinding
import com.batya.tictactoe.domain.model.BattleInvitation
import com.batya.tictactoe.domain.model.Result
import com.batya.tictactoe.domain.model.Room
import com.batya.tictactoe.domain.model.User
import com.batya.tictactoe.domain.model.UserStatus
import com.batya.tictactoe.presentation.menu.UserViewModel
import com.batya.tictactoe.presentation.settings.SettingsViewModel
import com.batya.tictactoe.presentation.waiting.RoomViewModel
import com.batya.tictactoe.util.Constants

import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val invitationsViewModel by viewModel<BattleInvitationsViewModel>()
    private val userViewModel by viewModel<UserViewModel>()
    private val roomViewModel by viewModel<RoomViewModel>()
    private val settingsViewModel by viewModel<SettingsViewModel>()

    private lateinit var dialog: BottomSheetDialog

    private var myId: String = ""
    private var user: User? = null
    private var invitations: List<BattleInvitation> = listOf()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme()

        myId = Firebase.auth.currentUser!!.uid
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        userViewModel.getUser(myId)
        observeUserLiveData()

        invitationsViewModel.getIncomingInvitations(myId)

        observeBattleInvitations()

//        showBottomSheetDialog(
//            BattleInvitation(
//                fromName = "Антон"
//            )
//        )


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationsPermission()
        }
    }

    private fun requestNotificationsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.POST_NOTIFICATIONS)) {
            AlertDialog.Builder(this)
                .setTitle("Разрешение")
                .setMessage("Для корректной работы приглашений нам необходимо разрешение на уведомления")
                .setPositiveButton("OK") { _, _ ->
                    pushNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
                .create()
                .show()
        } else {
            pushNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun setTheme() {
        settingsViewModel.loadSettings()

        settingsViewModel.isLightMode.observe(this) { isLightTheme ->
            when(isLightTheme) {
                true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    private val pushNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->

    }

    private fun updateToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            if (user?.token != task.result) {
                userViewModel.updateToken(myId, task.result)
            }
        }

    }

    private fun observeUserLiveData() {
        userViewModel.userLiveData.observe(this) { user ->
            when (user) {
                is Result.Success -> {
                    this.user = user.data
                    if (user.data.status !in listOf(UserStatus.IN_BATTLE, UserStatus.WAITING, UserStatus.ONLINE)) {
                        userViewModel.updateStatus(myId, UserStatus.ONLINE)
                    }
                    observeBattleInvitations()
                    updateToken()
                }
                is Result.Failure -> {}
            }
        }
    }

    private fun observeBattleInvitations() {
        invitationsViewModel.invitesLiveData.removeObservers(this)
        invitationsViewModel.invitesLiveData.observe(this) { invitations ->
            when (invitations) {
                is Result.Success -> {
                    this.invitations = invitations.data
                    if (invitations.data.isNotEmpty() && user?.status == UserStatus.ONLINE) {
                        showBottomSheetDialog(invitations.data.first())
                    } else {
                        dialog.dismiss()
                    }

                }
                is Result.Failure -> {
                    Log.d("TAG", "failure load me: ${invitations.error}")
                }
            }
        }
    }

    private fun showBottomSheetDialog(invitation: BattleInvitation) {
        val bsdBinding = BottomSheetFragmentBinding.inflate(layoutInflater)

        //dialog.setCancelable(true)
        dialog.setCancelable(false)
        dialog.setContentView(bsdBinding.root)
        dialog.show()

        bsdBinding.tvNickname.text = invitation.fromName

        bsdBinding.bnPlay.setOnClickListener {
            invitationsViewModel.acceptInvitation(invitation.roomId)
            createRoom(invitation)
            roomViewModel.connect(invitation.roomId, myId)
            navController.navigate(
                R.id.onlineFragment,
                bundleOf(Constants.ARG_ROOM_ID to invitation.roomId, Constants.ARG_ROOM_PRIVATE to true)
            )

        }
        bsdBinding.bnDecline.setOnClickListener {
            invitationsViewModel.declineInvitation(invitation.roomId)
            roomViewModel.removeRoom(invitation.roomId)
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            //invitationsViewModel.declineInvitation(invitation.roomId)
        }
    }

    private fun createRoom(invitation: BattleInvitation) {
        userViewModel.updateStatus(myId, UserStatus.IN_BATTLE)
        userViewModel.updateStatus(invitation.fromId, UserStatus.IN_BATTLE)
        userViewModel.updateRoomConnected(myId, invitation.roomId)
        userViewModel.updateRoomConnected(invitation.fromId, invitation.roomId)

        roomViewModel.createRoom(room = Room(id = invitation.roomId))

        Log.d("TAG", "accepting invitation to roomIdArg=${invitation.roomId}")
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        userViewModel.updateStatus(myId, UserStatus.OFFLINE)
    }

}