package com.example.quizgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.quizgame.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityForgotPasswordBinding
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.forgotPasswordBtn.setOnClickListener {
            var email: String = binding.editForgotEmail.text.toString()
            binding.animationProgress.visibility = View.VISIBLE
            auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.animationProgress.visibility = View.INVISIBLE
                    finish()
                    Toast.makeText(
                        this,
                        "Password Reset Mail Send in Your Mail Id.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    binding.animationProgress.visibility = View.INVISIBLE
                    Toast.makeText(
                        this,
                        task.exception?.localizedMessage.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}