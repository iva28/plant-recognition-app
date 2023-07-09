package com.example.plantrecognition

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity()
{
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        FirebaseApp.initializeApp(this)
        firebaseAuth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_login)

        val edtEmail: EditText = findViewById(R.id.email)
        val edtPassword: EditText = findViewById(R.id.password)
        val btnLogin: Button = findViewById(R.id.login)
        
        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            login(email, password)
        }
    }

    private fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Successfully logged in", Toast.LENGTH_SHORT)
                        .show()
                    switchToMainActivity()
                } else {
                    // User doesn't exist, so we'll sign them up
                    signup(email, password)
                }
            }
    }

    private fun signup(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Successfully signed up", Toast.LENGTH_SHORT)
                        .show()
                    switchToMainActivity()
                } else {
                    AlertDialog.Builder(this@LoginActivity)
                        .setTitle("Error")
                        .setMessage("Couldn't login nor signup")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
    }

    private fun switchToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        finish()
        startActivity(intent)
    }


}