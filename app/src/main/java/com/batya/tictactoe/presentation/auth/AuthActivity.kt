package com.batya.tictactoe.presentation.auth

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import com.batya.tictactoe.R
import com.batya.tictactoe.databinding.ActivityAuthBinding
import com.batya.tictactoe.domain.model.Result
import com.batya.tictactoe.presentation.MainActivity
import com.batya.tictactoe.presentation.menu.UserViewModel
import com.batya.tictactoe.presentation.settings.SettingsViewModel
import com.batya.tictactoe.util.gone
import com.batya.tictactoe.util.visible
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

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    private val authViewModel by viewModel<AuthViewModel>()
    private val settingsViewModel by viewModel<SettingsViewModel>()
    private val userViewModel by viewModel<UserViewModel>()

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initGoogleSignInClient()

    }

    override fun onStart() {
        super.onStart()


        if (authViewModel.isAuthenticated()) {
            if (Firebase.auth.currentUser!!.photoUrl != null) {
                authViewModel.updatePhoto(Firebase.auth.currentUser!!.photoUrl.toString())
            }
            openMainActivity()
        }

        binding.bnLoginGoogle.setOnClickListener {
            signInUsingGoogle()
            observeUserLiveData()
        }
        binding.bnLoginAnonymously.setOnClickListener {
            authViewModel.signInAnonymously()
            observeUserLiveData()
        }
        setTheme()
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


    private fun initGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("TAG", "launcher result = ${result.resultCode} ")
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
//        if (result.resultCode == Activity.RESULT_CANCELED) {
//            Toast.makeText(this, "Вход гостем", Toast.LENGTH_SHORT).show()
//            authViewModel.signInAnonymously()
//            observeUserLiveData()
//
//        }
    }

    private fun signInUsingGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
        googleSignInClient.signOut()
    }



    private fun getGoogleAuthCredential(account: GoogleSignInAccount) {
        binding.progressBar.visible()
        val googleTokenId = account.idToken
        val googleAuthCredential = GoogleAuthProvider.getCredential(googleTokenId, null)
        signInWithGoogleAuthCredential(googleAuthCredential)
    }

    private fun signInWithGoogleAuthCredential(googleAuthCredential: AuthCredential) {
        authViewModel.signInWithGoogle(googleAuthCredential)
        observeUserLiveData()
    }

    private fun observeUserLiveData() {
        authViewModel.authenticatedUserLiveData.observe(this) { user ->

            when (user) {
                is Result.Success -> {
                    Log.d("TAG", "success auth ${user.data}")
                    openMainActivity()
                    binding.progressBar.gone()

                }
                is Result.Failure -> {
                    Log.d("TAG", "failure log in: ${user.error}")
                    binding.progressBar.gone()
                }
            }
        }
    }
    private fun openMainActivity() {

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}