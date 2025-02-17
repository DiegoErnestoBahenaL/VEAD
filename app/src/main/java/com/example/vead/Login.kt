package com.example.vead

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.vead.data.entities.User
import com.example.vead.data.repositories.UserRepository
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)


        val emailField = findViewById<EditText>(R.id.editTextEmail)
        val passwordField = findViewById<EditText>(R.id.editTextPassword)
        val rememberMeCheckBox = findViewById<CheckBox>(R.id.checkBoxRememberMe)
        val loginButton = findViewById<Button>(R.id.buttonLogin)

        val savedEmail = sharedPreferences.getString("email", "")
        val savedPassword = sharedPreferences.getString("password", "")
        val isRemembered = sharedPreferences.getBoolean("rememberMe", false)

        if (isRemembered) {
            emailField.setText(savedEmail)
            passwordField.setText(savedPassword)
            rememberMeCheckBox.isChecked = true
        }

        loginButton.setOnClickListener {


            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, complete ambos campos", Toast.LENGTH_SHORT).show()
            } else {

                val userRepo = UserRepository()

                userRepo.getUserByEmail(email){
                    user ->
                    if (user != null && user.password == password){

                        if (rememberMeCheckBox.isChecked) {
                            saveLoginDetails(email, password, true)
                        } else {
                            clearLoginDetails()
                        }

                        navegarADashboard(user)
                    }
                    else {
                        Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()

                    }
                }

            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun saveLoginDetails(email: String, password: String, rememberMe: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.putBoolean("rememberMe", rememberMe)
        editor.apply()
    }

    private fun clearLoginDetails() {
        val editor = sharedPreferences.edit()
        editor.remove("email")
        editor.remove("password")
        editor.remove("rememberMe")
        editor.apply()
    }

    private fun navegarADashboard(user: User) {

        val intent = Intent(this, MainActivity::class.java)
            .putExtra("UserType", user.userType)
            .putExtra("Email", user.email)
            .putExtra("Password", user.password)
            .putExtra("Name", user.name)
            .putExtra("LastName", user.lastName)
            .putExtra("Code", user.code)
            .putExtra("PhoneNumber", user.phoneNumber)
        startActivity(intent)
        finish()
    }
}