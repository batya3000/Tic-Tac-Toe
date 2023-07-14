package com.android.batya.tictactoe.presentation.auth

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.android.batya.tictactoe.R
import com.android.batya.tictactoe.databinding.ActivityAuthBinding
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.presentation.MainActivity
import com.android.batya.tictactoe.util.Constants
import com.android.batya.tictactoe.util.gone
import com.android.batya.tictactoe.util.visible
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    private val authViewModel by viewModel<AuthViewModel>()

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
            openMainActivity()
        } else {
            signInUsingGoogle()
        }

        binding.bnLoginGoogle.setOnClickListener {
            signInUsingGoogle()
            observeUserLiveData()
        }
        binding.bnLoginAnonymously.setOnClickListener {
            authViewModel.signInAnonymously()
            observeUserLiveData()
        }
    }

    private fun initGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("TAG", "byyy")
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
        if (result.resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "Вход гостем", Toast.LENGTH_SHORT).show()
            authViewModel.signInAnonymously()
            observeUserLiveData()

        }
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
        authViewModel.authenticateUserLiveData.observe(this) { user ->

            when (user) {
                is Result.Success -> {
                    Log.d("TAG", "success: userId=${user.data}")
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