package com.babylon.orbit2.sample.posts.app.features.postlist.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.babylon.orbit2.sample.posts.app.features.postlist.viewmodel.PostListViewModel
import com.babylon.orbit2.sample.posts.domain.repositories.PostOverview
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.babylon.orbit2.sample.posts.R

class PostListViewHolder(itemView: View, private val viewModel: PostListViewModel) :
    RecyclerView.ViewHolder(itemView) {

    private val avatar: ImageView = itemView.findViewById(R.id.post_avatar)
    private val title: TextView = itemView.findViewById(R.id.post_title)
    private val username: TextView = itemView.findViewById(R.id.post_username)
    private val comments: TextView = itemView.findViewById(R.id.post_comments)

    private lateinit var currentPost: PostOverview

    init {
        itemView.setOnClickListener {
            val options = ActivityOptionsCompat.makeBasic()
            viewModel.onPostClicked(currentPost, options)
        }
    }

    fun bind(post: PostOverview) {
        currentPost = post

        Glide.with(itemView.context).load(post.avatarUrl)
            .apply(RequestOptions.circleCropTransform()).into(avatar)

        title.text = post.title
        username.text = post.username
        comments.text = itemView.context.resources.getQuantityString(
            R.plurals.comments,
            post.comments,
            post.comments
        )
    }
}
