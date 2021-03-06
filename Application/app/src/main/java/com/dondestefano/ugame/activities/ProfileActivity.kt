package com.dondestefano.ugame.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.dondestefano.ugame.R
import com.dondestefano.ugame.data_managers.FriendDataManager
import com.dondestefano.ugame.data_managers.UserDataManager
import com.dondestefano.ugame.objects.User
import com.squareup.picasso.Picasso

const val USER_ID = "USER_ID"
const val STRANGER_STATE = "STRANGER_STATE"
const val USER_STATE = "USER_STATE"
const val SENT_STATE = "sent"
const val RECEIVED_STATE = "received"
const val FRIEND_STATE = "accepted"


class UserProfileActivity : AppCompatActivity() {
    private lateinit var userNameText: TextView
    private lateinit var userImageView: ImageView
    private lateinit var addFriendButton: Button
    private lateinit var toolbar: Toolbar
    private var currentState = STRANGER_STATE

    // User being displayed
    private var user: User? = null

    // Image
    var selectedPhotoUri : Uri? = null

    // Put extra helpers.
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        userNameText = findViewById(R.id.profileNameTextView)
        userImageView = findViewById(R.id.profilePictureImageView)

        getExtraFromIntent()
        determineState()
        setupUser()
        setupToolbar()
    }

    private fun getExtraFromIntent() {
        // Get users ID and find correct user
        userID = intent.getStringExtra(USER_ID)
        user = UserDataManager.getUser(userID)
    }

    private fun setupToolbar() {
        toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarProfile)
        setSupportActionBar(toolbar)

        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar?.setHomeAsUpIndicator(R.drawable.close)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // close this activity as oppose to navigating up
        return false
    }

    private fun determineState() {
        if (user?.userID == UserDataManager.loggedInUser.userID) {
            currentState = USER_STATE
            setUpFromState()
        }

        else {
            user?.userID?.let { currentState = FriendDataManager.checkStatus(it, this).toString() }
        }
    }

    // Call this function from UserDataManager when the appropriate data has been loaded.
    fun stateDetermined(state: String) {
        currentState = state
        setUpFromState()
    }

    private fun setUpFromState() {
        when (currentState) {
            FRIEND_STATE -> {
                addFriendButton = findViewById(R.id.sendFriendRequestButton)
                addFriendButton.text = getString(R.string.unfriend)
                addFriendButton.setBackgroundColor(ContextCompat.getColor(this, R.color.design_default_color_error))
                addFriendButton.setOnClickListener {
                    user?.let { FriendDataManager.removeFriend(this, it) }
                    addFriendButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
                    currentState = STRANGER_STATE
                    setUpFromState()
                }
            }
            STRANGER_STATE -> {
                addFriendButton = findViewById(R.id.sendFriendRequestButton)
                addFriendButton.text = getString(R.string.add_friend)
                addFriendButton.setOnClickListener {
                    user?.let { FriendDataManager.sendFriendRequest(it) }
                    currentState = SENT_STATE
                    setUpFromState()
                }
            }
            SENT_STATE -> {
                addFriendButton = findViewById(R.id.sendFriendRequestButton)
                addFriendButton.text = getString(R.string.cancel_request)
                addFriendButton.setOnClickListener {
                    user?.let { FriendDataManager.removeFriend(this, it) }
                    currentState = STRANGER_STATE
                    setUpFromState()
                }
            }
            RECEIVED_STATE -> {
                addFriendButton = findViewById(R.id.sendFriendRequestButton)
                addFriendButton.text = getString(R.string.accept_request)
                addFriendButton.setOnClickListener {
                    user?.let { FriendDataManager.acceptFriendRequest(it) }
                    currentState = FRIEND_STATE
                    setUpFromState()
                }
            }
            USER_STATE -> {
                addFriendButton = findViewById(R.id.sendFriendRequestButton)
                addFriendButton.visibility = GONE

                // Use ActivityForResult to upload images.
                userImageView.setOnClickListener {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, 0)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            userImageView.setImageBitmap(bitmap)

            addFriendButton.visibility = VISIBLE

            addFriendButton.text = getString(R.string.save_changes)
            addFriendButton.setOnClickListener {
                selectedPhotoUri?.let { UserDataManager.uploadImageToFirebaseStorage(it)}
                setupUser()
            }
        }
    }

    private fun setupUser() {
        userNameText.text = user?.name
        val uri = user?.profileImageURL
        Picasso.get().load(uri).into(userImageView)
    }
}
