package com.mattdolan.babylon.app.features.postdetails.ui

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mattdolan.babylon.R
import com.mattdolan.babylon.domain.repositories.PostComment

class PostCommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val name: TextView = itemView.findViewById(R.id.comment_username)
    private val email: TextView = itemView.findViewById(R.id.comment_email)
    private val body: TextView = itemView.findViewById(R.id.comment_body)

    fun bind(post: PostComment) {
        name.text = post.name
        email.text = post.email
        body.text = post.body
    }
}
