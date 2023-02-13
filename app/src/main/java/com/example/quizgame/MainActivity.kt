package com.example.quizgame

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quizgame.CheckInternet.networkChnage
import com.example.quizgame.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var networkChnage: networkChnage
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            signOut()
            var intent = Intent(this, LoginAndRegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.startQuiz.setOnClickListener {
            var intent = Intent(this@MainActivity, QuizActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("365560320661-kni731an6dv683qtrk2q9miagp13t0ij.apps.googleusercontent.com")
            .requestEmail().build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
    }

   /* override fun onStart() {
        var filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChnage, filter)
        super.onStart()
    }

    override fun onStop() {
        unregisterReceiver(networkChnage)
        super.onStop()
    }*/
}