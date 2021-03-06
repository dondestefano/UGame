package com.dondestefano.ugame.recycle_adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dondestefano.ugame.R
import com.dondestefano.ugame.activities.UserProfileActivity
import com.dondestefano.ugame.objects.User
import com.squareup.picasso.Picasso

class AllUsersRecycleAdapter(private val context: Context) : RecyclerView.Adapter<AllUsersRecycleAdapter.SearchUserViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)
    private var users = listOf<User>()

    override fun getItemCount() = users.size

    fun updateItemsToList(list: MutableList<User>) {
        users = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchUserViewHolder {
        val userView = layoutInflater.inflate(R.layout.user_blank_card_layout, parent, false)
        return SearchUserViewHolder(userView)
    }

    override fun onBindViewHolder(holder: SearchUserViewHolder, position: Int) {
        val currentUser = users[position]
        holder.nameSearchView.text = currentUser.name
        holder.userID = currentUser.userID
        val uri = currentUser.profileImageURL
        Picasso.get().load(uri).into(holder.imageView)
    }

    inner class SearchUserViewHolder(userView: View) : RecyclerView.ViewHolder(userView) {
        val nameSearchView: TextView = itemView.findViewById<TextView>(R.id.friendName)
        val imageView: ImageView = itemView.findViewById<ImageView>(R.id.friendImage)
        var userID: String? = null

        init {
            itemView.setOnClickListener {
                val intent = Intent(context, UserProfileActivity::class.java)
                intent.putExtra("USER_ID", userID)
                context.startActivity(intent)
            }
        }
    }
}