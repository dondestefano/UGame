package com.dondestefano.ugame.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.dondestefano.ugame.R
import com.dondestefano.ugame.data_managers.EventDataManager
import com.dondestefano.ugame.data_managers.FriendDataManager
import com.dondestefano.ugame.data_managers.UserDataManager
import com.dondestefano.ugame.notification.MyFirebaseMessagingService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId

class LoginActivity : AppCompatActivity() {

    lateinit var textEmail: EditText
    lateinit var passwordText: EditText
    lateinit var createAccount : TextView
    lateinit var loginButton : Button
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        textEmail = findViewById(R.id.emailEditText)
        passwordText = findViewById(R.id.passwordEditText)
        createAccount = findViewById(R.id.createAccTextView)
        loginButton = findViewById<Button>(R.id.loginButton)


        loginButton.setOnClickListener {
            login()
        }

        createAccount.setOnClickListener{
            goToCreateAccountActivity()
        }
    }

    private fun login() {
        if (textEmail.text.isNotEmpty() || passwordText.text.isNotEmpty()) {
            auth.signInWithEmailAndPassword(textEmail.text.toString(), passwordText.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Get users from database
                        UserDataManager.getLoggedInUser(this)
                        UserDataManager.setFirebaseListenerForUsers(null)
                        // Get friends from database
                        FriendDataManager.resetFriendDataManagerUser()
                        // Get Events from database
                        EventDataManager.resetEventDataManagerUser()
                        // Set a new registrationToken if the user doesn't have one.
                        val registrationToken = FirebaseInstanceId.getInstance().token
                        if (registrationToken != null) {
                            MyFirebaseMessagingService.addTokenToFirestore(registrationToken)
                        }
                        goToListActivity()
                        finish()
                    } else {
                        Toast.makeText(this, "Wrong e-mail or password.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        } else {Toast.makeText(this, "Please enter your e-mail and password.", Toast.LENGTH_SHORT)
            .show()
        }
    }

    private fun goToListActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun goToCreateAccountActivity() {
        val intent = Intent(this, CreateAccountActivity::class.java)
        startActivity(intent)
    }
}