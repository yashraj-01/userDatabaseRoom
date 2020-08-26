package com.example.userregistrationdemo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var authorizeButton: MaterialButton
    lateinit var sharedPreferences: SharedPreferences
    private val username = "admin"
    private val password = "admin123456"

    private fun findViews(){
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        authorizeButton = findViewById(R.id.authorizeButton)
    }

    fun authorize(view: View){
        if (usernameEditText.text.toString() == username && passwordEditText.text.toString() == password){
            Toast.makeText(this,"Authorization Successful",Toast.LENGTH_SHORT).show()
            sharedPreferences.edit().putBoolean("authorized",true).apply()
            startActivity(Intent(this,UserRegistration::class.java))
            finish()

        }else{
            Toast.makeText(this,"Invalid Credentials",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViews()

        sharedPreferences = getSharedPreferences("admin", Context.MODE_PRIVATE)

        if (sharedPreferences.getBoolean("authorized", false)){
            startActivity(Intent(this,UserRegistration::class.java))
            finish()
        }
    }
}