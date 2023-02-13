package com.example.quizgame

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.quizgame.databinding.ActivityLoginAndRegisterBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginAndRegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginAndRegisterBinding
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    var reference: DatabaseReference = database.reference.child("Users")

    //Google SignIn
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var arl: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginAndRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        registerActivityForGoogleSignIn()

        binding.loginToRegister.setOnClickListener {
            binding.loginLayout.visibility = View.INVISIBLE
            binding.registerLayout.visibility = View.VISIBLE
        }

        binding.registerToLogin.setOnClickListener {
            binding.loginLayout.visibility = View.VISIBLE
            binding.registerLayout.visibility = View.INVISIBLE
        }

        binding.btnForogtPassword.setOnClickListener {
            var intent = Intent(this@LoginAndRegisterActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignUp.setOnClickListener {
            binding.animationView.visibility = View.VISIBLE
            var email: String = binding.editRegisterEmail.text.toString()
            var password: String = binding.editRegisterPassword.text.toString()

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.editRegisterEmail.setText("")
                    binding.editRegisterPassword.setText("")
                    binding.animationView.visibility = View.INVISIBLE
                    binding.loginLayout.visibility = View.VISIBLE
                    binding.registerLayout.visibility = View.INVISIBLE
                    var userModel = UserModel(auth.currentUser!!.uid, email, password)
                    reference.child(auth.currentUser!!.uid).setValue(userModel)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Register Sucess.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(
                                    this,
                                    task.exception?.localizedMessage.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    binding.animationView.visibility = View.INVISIBLE
                    Toast.makeText(
                        this,
                        task.exception?.localizedMessage.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }

        binding.btnSignIn.setOnClickListener {
            binding.animationView.visibility = View.VISIBLE
            var email: String = binding.editEmail.text.toString()
            var password: String = binding.editPassword.text.toString()

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.animationView.visibility = View.INVISIBLE
                    startActivity(Intent(this@LoginAndRegisterActivity, MainActivity::class.java))
                    Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this,
                        task.exception?.localizedMessage.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.animationView.visibility = View.INVISIBLE
                }
            }
        }

        //Google SignIn Button
        val textOfGoogleBtn = binding.btnGoogleSignin.getChildAt(0) as TextView
        textOfGoogleBtn.text = "Continue With Google"
        textOfGoogleBtn.setTextColor(Color.BLACK)
        textOfGoogleBtn.textSize = 18F
        binding.btnGoogleSignin.setOnClickListener {
            signInGoogle()
        }
    }

    private fun signInGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("365560320661-kni731an6dv683qtrk2q9miagp13t0ij.apps.googleusercontent.com")
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        signIn()
    }

    private fun signIn() {
        var signInIntent: Intent = googleSignInClient.signInIntent
        arl.launch(signInIntent)
    }

    private fun registerActivityForGoogleSignIn() {
        arl = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { res ->
                val resultCode = res.resultCode
                val data = res.data

                if (resultCode == RESULT_OK && data != null) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(data)

                    firebaseSignInWithGoogle(task)
                }
            })
    }

    private fun firebaseSignInWithGoogle(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            Toast.makeText(this, "Welcome To Quiz Game.", Toast.LENGTH_SHORT).show()
            var intent = Intent(this@LoginAndRegisterActivity, MainActivity::class.java)
            startActivity(intent)
            finish()

            firebaseGoogleAccount(account)

        } catch (e: ApiException) {
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseGoogleAccount(account: GoogleSignInAccount) {
        val authCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(authCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                var userEmail: String = user!!.email.toString()
                var userId: String = user!!.uid

                val userModel = UserModel(userId, userEmail, "")
                reference.child(userId).setValue(userModel).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Toast.makeText(this,"Data Stored in Database.",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this,task.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}