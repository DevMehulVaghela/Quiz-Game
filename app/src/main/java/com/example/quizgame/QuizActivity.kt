package com.example.quizgame

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.quizgame.databinding.ActivityQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random

class QuizActivity : AppCompatActivity() {
    lateinit var binding: ActivityQuizBinding

    val database = FirebaseDatabase.getInstance()
    val databaseReference = database.reference.child("Category").child("Kotlin").child("Question")

    var question = ""
    var ansA = ""
    var ansB = ""
    var ansC = ""
    var ansD = ""
    var currectAns = ""
    var questionCount = 0
    var questionNumber = 0

    var userAnswer = ""
    var userCorrect = 0
    var userWrong = 0


    lateinit var timer: CountDownTimer
    var timerContinue = false
    private val totalTime = 20000L
    var timeLeft = totalTime

    val auth = FirebaseAuth.getInstance()
    val scoreRef = database.reference
    val userId = auth.currentUser?.uid

    val questions = HashSet<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        do {
            val number = Random.nextInt(1, 11)
            questions.add(number)
            Log.d("TAG", "onCreate Number: "+number)
        } while (questions.size < 5)

        Log.d("TAG", "onCreate Number:"+questions.toString())

        gameLogic()

        binding.tvA.setOnClickListener {
            pauseTimer()
            userAnswer = "a"
            if (currectAns == userAnswer) {
                binding.tvA.setBackgroundDrawable(getDrawable(R.drawable.correct_back))
                userCorrect++
                binding.tvCorrect.text = userCorrect.toString()
            } else {
                binding.tvA.setBackgroundDrawable(getDrawable(R.drawable.wrong_back))
                findAnswer()
                userWrong++
                binding.tvWrong.text = userWrong.toString()
            }
            disableClickOption()
        }
        binding.tvB.setOnClickListener {
            pauseTimer()
            userAnswer = "b"
            if (currectAns == userAnswer) {
                binding.tvB.setBackgroundDrawable(getDrawable(R.drawable.correct_back))
                userCorrect++
                binding.tvCorrect.text = userCorrect.toString()
            } else {
                binding.tvB.setBackgroundDrawable(getDrawable(R.drawable.wrong_back))
                findAnswer()
                userWrong++
                binding.tvWrong.text = userWrong.toString()
            }
            disableClickOption()
        }
        binding.tvC.setOnClickListener {
            pauseTimer()
            userAnswer = "c"
            if (currectAns == userAnswer) {
                binding.tvC.setBackgroundDrawable(getDrawable(R.drawable.correct_back))
                userCorrect++
                binding.tvCorrect.text = userCorrect.toString()
            } else {
                binding.tvC.setBackgroundDrawable(getDrawable(R.drawable.wrong_back))
                findAnswer()
                userWrong++
                binding.tvWrong.text = userWrong.toString()
            }
            disableClickOption()
        }
        binding.tvD.setOnClickListener {
            pauseTimer()
            userAnswer = "d"
            if (currectAns == userAnswer) {
                binding.tvD.setBackgroundDrawable(getDrawable(R.drawable.correct_back))
                userCorrect++
                binding.tvCorrect.text = userCorrect.toString()
            } else {
                binding.tvD.setBackgroundDrawable(getDrawable(R.drawable.wrong_back))
                findAnswer()
                userWrong++
                binding.tvWrong.text = userWrong.toString()
            }
            disableClickOption()
        }
        binding.btnNext.setOnClickListener {
            resetTimer()
            gameLogic()
        }
        binding.btnFinish.setOnClickListener {
            sendScore()
        }

    }

    private fun gameLogic() {
        restoreFunction()

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                questionCount = snapshot.childrenCount.toInt()

                //if (questionNumber <= questionCount)
                if (questionNumber < questions.size) {
                    question = snapshot.child(questions.elementAt(questionNumber).toString()).child("q").value.toString()
                    ansA = snapshot.child(questions.elementAt(questionNumber).toString()).child("a").value.toString()
                    ansB = snapshot.child(questions.elementAt(questionNumber).toString()).child("b").value.toString()
                    ansC = snapshot.child(questions.elementAt(questionNumber).toString()).child("c").value.toString()
                    ansD = snapshot.child(questions.elementAt(questionNumber).toString()).child("d").value.toString()
                    currectAns = snapshot.child(questions.elementAt(questionNumber).toString()).child("answer").value.toString()

                    binding.tvQuestion.text = question
                    binding.tvA.text = ansA
                    binding.tvB.text = ansB
                    binding.tvC.text = ansC
                    binding.tvD.text = ansD

                    binding.progressBar.visibility = View.INVISIBLE
                    binding.llInfo.visibility = View.VISIBLE
                    binding.llQuestion.visibility = View.VISIBLE
                    binding.llBtn.visibility = View.VISIBLE

                    //Timer Function
                    startTimer()
                } else {
                    var dialogMessage = AlertDialog.Builder(this@QuizActivity)
                    dialogMessage.setTitle("Quiz Game")
                    dialogMessage.setMessage("congratulations!\n You Have Answered All Questions.\n Do You want to see result ?")
                    dialogMessage.setPositiveButton(
                        "See Result",
                        DialogInterface.OnClickListener { dialog, which ->
                            sendScore()
                        })
                    dialogMessage.setNegativeButton(
                        "Play Again",
                        DialogInterface.OnClickListener { dialog, which ->
                            var intent = Intent(this@QuizActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        })

                    dialogMessage.show().create()
                }
                questionNumber++
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun findAnswer() {
        when (currectAns) {
            "a" -> binding.tvA.setBackgroundDrawable(getDrawable(R.drawable.correct_back))
            "b" -> binding.tvB.setBackgroundDrawable(getDrawable(R.drawable.correct_back))
            "c" -> binding.tvC.setBackgroundDrawable(getDrawable(R.drawable.correct_back))
            "d" -> binding.tvD.setBackgroundDrawable(getDrawable(R.drawable.correct_back))

        }
    }

    fun disableClickOption() {
        binding.tvA.isClickable = false
        binding.tvB.isClickable = false
        binding.tvC.isClickable = false
        binding.tvD.isClickable = false
    }

    fun restoreFunction() {
        binding.tvA.setBackgroundDrawable(getDrawable(R.drawable.textview_back))
        binding.tvB.setBackgroundDrawable(getDrawable(R.drawable.textview_back))
        binding.tvC.setBackgroundDrawable(getDrawable(R.drawable.textview_back))
        binding.tvD.setBackgroundDrawable(getDrawable(R.drawable.textview_back))

        binding.tvA.isClickable = true
        binding.tvB.isClickable = true
        binding.tvC.isClickable = true
        binding.tvD.isClickable = true

    }

    private fun startTimer() {
        timer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                updateTimerText()
            }

            override fun onFinish() {
                disableClickOption()
                resetTimer()
                updateTimerText()
                gameLogic()
                userWrong++
                binding.tvWrong.text = userWrong.toString()
//                binding.tvQuestion.text = "Sorrry, Time is Up! Click on Next Button."
                timerContinue = false
            }

        }.start()
        timerContinue = true
    }

    fun updateTimerText() {
        val remainingTime: Int = (timeLeft / 1000).toInt()
        binding.tvTimer.text = remainingTime.toString()
    }

    fun pauseTimer() {
        timer.cancel()
        timerContinue = false
    }

    fun resetTimer() {
        pauseTimer()
        timeLeft = totalTime
        updateTimerText()
    }

    fun sendScore() {
        scoreRef.child("Scores").child(userId.toString()).child("Correct").setValue(userCorrect)
        scoreRef.child("Scores").child(userId.toString()).child("Wrong").setValue(userWrong)
            .addOnSuccessListener {
                Toast.makeText(this@QuizActivity, "Data Send Successfully.", Toast.LENGTH_SHORT)
                    .show()
                var intent = Intent(this@QuizActivity, FinishActivity::class.java)
                intent.putExtra("correct", userCorrect.toString())
                intent.putExtra("wrong", userWrong.toString())
                Log.d("TAG", "onDataChangeAnswer: " + userCorrect + " " + userWrong)
                startActivity(intent)
                finish()
            }
    }
}