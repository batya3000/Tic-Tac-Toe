package com.android.batya.tictactoe.presentation.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.android.batya.tictactoe.R
import com.android.batya.tictactoe.databinding.DialogNicknameBinding
import com.android.batya.tictactoe.databinding.FragmentProfileBinding
import com.android.batya.tictactoe.domain.model.BattleInvitation
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.model.Room
import com.android.batya.tictactoe.domain.model.User
import com.android.batya.tictactoe.presentation.BattleInvitationsViewModel
import com.android.batya.tictactoe.presentation.auth.AuthActivity
import com.android.batya.tictactoe.presentation.menu.UserViewModel
import com.android.batya.tictactoe.util.Constants
import com.android.batya.tictactoe.util.getStatusColor
import com.android.batya.tictactoe.util.gone
import com.android.batya.tictactoe.util.toast
import com.android.batya.tictactoe.util.visible
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel by viewModel<ProfileViewModel>()
    private val userViewModel by viewModel<UserViewModel>()
    private val invitationsViewModel by viewModel<BattleInvitationsViewModel>()
    private val notificationsViewModel by viewModel<NotificationsViewModel>()

    private lateinit var googleSignInClient: GoogleSignInClient
    private var isMyProfile = false
    private var currentUser: User? = null
    private var users: List<User> = listOf()
    private var me: User? = null

    private var myId: String = ""
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        initGoogleSignInClient()
        myId = Firebase.auth.currentUser!!.uid
        userId = arguments?.getString(Constants.ARG_USER_ID)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListeners()

        if (userId != null) {
            profileViewModel.getProfile(userId!!)
            observeOpenedProfileLiveData()
            userViewModel.getUser(myId)
            observeUserLiveData()
        } else {
            profileViewModel.getProfile(myId)
            observeOpenedProfileLiveData()
        }
        profileViewModel.getUsers()
        observeUsers()

        postponeEnterTransition()

        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move).apply {
            duration = 100
        }
    }

    private fun observeUsers() {
        profileViewModel.usersLiveData.observe(viewLifecycleOwner) { users ->
            when (users) {
                is Result.Success -> {
                    this@ProfileFragment.users = users.data
                    if (currentUser != null) {
                        updateUI(currentUser!!, currentUser!!.id == myId)
                    }

                }
                is Result.Failure -> {}
            }
        }

    }

    private fun setOnClickListeners() {
        binding.cvFriends.setOnClickListener {
            if (isMyProfile) {
                findNavController().navigate(R.id.action_profileFragment_to_friendsFragment)
            }
        }
        binding.cvSignOut.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(context, AuthActivity::class.java)
            startActivity(intent)
        }

        binding.cvLinkWithGoogle.setOnClickListener {
            linkWithGoogle()
        }
        binding.cvRemoveFriend.setOnClickListener {
            profileViewModel.removeFriend(myId, currentUser!!.id)
        }
        binding.cvEdit.setOnClickListener {
            if (currentUser?.isAnonymousAccount == false) {
                showEditTextDialog() {
                    if (it.length in 4..15) {
                        profileViewModel.updateUserName(myId, it)
                    } else {
                        requireContext().toast("Ник должен быть длиной от 4 до 15 символов")
                    }
                }
            } else {
                Toast.makeText(context, "Для изменения ника вы должны войти в Google", Toast.LENGTH_SHORT).show()
            }
        }


        binding.cvBattle.setOnClickListener {
            val room = Room()
            val invitation = BattleInvitation(
                roomId = room.id,
                fromName = me!!.name,
                fromId = me!!.id,
                toId = currentUser?.id ?: "",
                toToken = currentUser?.token ?: "",
            )
            if (me != null) {
                invitationsViewModel.sendInvitation(battleInvitation = invitation)

                Log.d("TAG", "sending invitation ${room.id}")
                findNavController().navigate(
                    R.id.action_profileFragment_to_waitingFragment,
                    bundleOf(Constants.ARG_ROOM_ID to room.id)
                )
            }

            notificationsViewModel.sendNotification(
                fromName = invitation.fromName,
                toToken = invitation.toToken
            )
        }
    }

    private fun observeOpenedProfileLiveData() {
        profileViewModel.profileLiveData.observe(viewLifecycleOwner) { user ->
            when (user) {
                is Result.Success -> {
                    Log.d("TAG", "success: userId=${user.data.id}")
                    isMyProfile = user.data.id == myId
                    currentUser = user.data
                    updateUI(user.data, isMyProfile)
                }
                is Result.Failure -> {
                    Log.d("TAG", "failure log in: ${user.error}")
                    with (binding) {
                        tvNickname.text = "null"
                        tvId.text = "ID: ..."
                        tvCrowns.text = ""

                        tvGames.text = "0"
                        tvVictories.text = "0"
                        tvDefeats.text = "0"
                    }
                }
            }
        }
    }

    private fun observeUserLiveData() {
        userViewModel.userLiveData.observe(viewLifecycleOwner) { user ->
            when (user) {
                is Result.Success -> {
                    Log.d("TAG", "success: userId=${user.data.id}")
                    me = user.data
                }
                is Result.Failure -> {
                    Log.d("TAG", "failure log in: ${user.error}")
                }
            }
        }
    }

    private fun showEditTextDialog(nickname: (String) -> Unit) {

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
        val dialogBinding: DialogNicknameBinding = DialogNicknameBinding.inflate(layoutInflater)

        with(builder) {
            setView(dialogBinding.root)
            setTitle("Изменение ника")
            setPositiveButton("Сменить ник") { _, _ ->
                nickname(dialogBinding.etNickname.text.toString())
            }
            setNegativeButton("Отменить") { _, _ ->
                Log.d("Main", "Negative button clicked")
            }
            show()
        }
    }


    private fun updateUI(user: User, isMyProfile: Boolean) {
        (view?.parent as? ViewGroup)?.doOnPreDraw {
            startPostponedEnterTransition()
        }
        with(binding) {
            tvGames.text = user.games.size.toString()
            tvVictories.text = user.games.filter { it.value.winnerId == user.id }.size.toString()
            tvDefeats.text = user.games.filter { it.value.winnerId != user.id }.size.toString()
            tvFriends.text = user.friends.size.toString()
            tvNickname.text = user.name
            tvId.text = "ID: ${user.id.take(6)}"

            ivStatus.setImageResource(getStatusColor(currentUser!!.status))

            tvCrowns.text = user.points.toString()
            tvRatingInWorld.text = "#${users.sortedByDescending { it.points }.indexOf(user) + 1}"
            if (user.photoUri != null) {
                ivPhoto.load(user.photoUri)
            }


            if (isMyProfile) {
                cvSignOut.visible()
                cvEdit.visible()
                cvBattle.gone()
                if (user.isAnonymousAccount) {
                    cvLinkWithGoogle.visible()
                    cvFriends.gone()
                    tvId.gone()
                    tvNickname.text = getString(R.string.quest)
                } else {
                    cvLinkWithGoogle.gone()
                    cvFriends.visible()
                    tvId.visible()
                }
                clRatingInFriends.visible()
                tvRatingInFriends.text = "#${users.filter { it.id in user.friends.values || it.id == user.id}.sortedByDescending { it.points }.indexOf(user) + 1}"
            } else {
                cvSignOut.gone()
                cvEdit.gone()
                cvFriends.gone()
                cvBattle.visible()
                if (myId in user.friends) {
                    cvRemoveFriend.visible()
                } else {
                    cvRemoveFriend.gone()
                }
                clRatingInFriends.gone()
            }
        }
    }


    private fun initGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    getGoogleAuthCredential(account)
                }

            } catch (e: ApiException) {
                Log.d("TAG", "failed signin: ${e.message}")
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    private fun linkWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
        googleSignInClient.signOut()
    }



    private fun getGoogleAuthCredential(account: GoogleSignInAccount) {
        val googleTokenId = account.idToken
        val credential = GoogleAuthProvider.getCredential(googleTokenId, null)
        linkWithGoogleAuthCredentials(credential)

    }

    private fun linkWithGoogleAuthCredentials(googleAuthCredential: AuthCredential) {
        Firebase.auth.currentUser!!.linkWithCredential(googleAuthCredential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "linkWithCredential:success")
                    val user = task.result?.user

                    profileViewModel.updateAccountType(user!!.uid, isAnonymousAccount = false)
                } else {
                    Log.w("TAG", "linkWithCredential:failure", task.exception)
                }
            }

    }
}