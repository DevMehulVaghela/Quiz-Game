package com.example.quizgame

import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quizgame.databinding.ActivityFinishBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FinishActivity : AppCompatActivity() {
    lateinit var binding: ActivityFinishBinding
    var auth = FirebaseAuth.getInstance()
    val user = auth.currentUser?.uid
    var database = FirebaseDatabase.getInstance()
    var reference = database.reference.child("Scores")

    var userCorrect = ""
    var userWrong = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*binding.correct.text = "Correct Answer :" + userCorrect
        binding.wrong.text = "Wrong Answer :" + userWrong*/

        binding.playAgain.setOnClickListener {
            var intent = Intent(this@FinishActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.exit.setOnClickListener {
            finish()
        }

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userCorrect = snapshot.child(user.toString()).child("Correct").value.toString()
                userWrong = snapshot.child(user.toString()).child("Wrong").value.toString()

                binding.correct.text = "Correct Answer :"+userCorrect
                binding.wrong.text = "Wrong Answer :"+userWrong
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}