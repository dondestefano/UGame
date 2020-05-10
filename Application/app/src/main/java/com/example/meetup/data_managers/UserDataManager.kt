package com.example.meetup.data_managers

import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.objects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

object UserDataManager {
    // Lists //
    var loggedInUser = User(null, null, null)
    val allUsersList = mutableListOf<User>()
    val inviteList = mutableListOf<User>()

    // Database-helpers //
    var db = FirebaseFirestore.getInstance()
    val allUsersRef = db.collection("users")
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var userDataRef : DocumentReference //QUESTION: Do I need this?

    fun getLoggedInUser() {
        val loggedInUserID = auth.currentUser?.uid
        userDataRef = loggedInUserID?.let { allUsersRef.document(it) }!!
        if(loggedInUserID != null) {
            userDataRef?.addSnapshotListener { snapshot, e ->
                // Load user with the correct id from Firebase
                if (snapshot != null) {
                    loggedInUser = snapshot.toObject(User::class.java)!!
                } else {

                }
            }
        }
    }

    fun setFirebaseListenerForUsers(userRecyclerView: RecyclerView) {
        allUsersRef.addSnapshotListener { snapshot, e ->
            // Clear list
            allUsersList.clear()
            // Load all users from Firebase
            if (snapshot != null) {
                for (document in snapshot.documents) {
                    val loadUser = document.toObject(User::class.java)
                    loadUser?.let { allUsersList.add(it) }
                    userRecyclerView.adapter?.notifyDataSetChanged()
                    println("!!! ${loadUser?.name}")
                }
            }
        }
    }
}