package com.babylon.orbit2.sample.posts.app.features.postlist.ui

import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import com.babylon.orbit2.sample.posts.R
import com.babylon.orbit2.sample.posts.app.features.postlist.viewmodel.PostListViewModel
import com.babylon.orbit2.sample.posts.domain.repositories.PostOverview
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

data class PostListItem(private val post: PostOverview, private val viewModel: PostListViewModel) : Item() {

    override fun isSameAs(other: com.xwray.groupie.Item<*>) = other is PostListItem && post.id == other.post.id

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>) = other is PostListItem && post == other.post

    override fun getLayout() = R.layout.post_list_item

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val avatar: ImageView = viewHolder.itemView.findViewById(R.id.post_avatar)
        val title: TextView = viewHolder.itemView.findViewById(R.id.post_title)
        val username: TextView = viewHolder.itemView.findViewById(R.id.post_username)
        val comments: TextView = viewHolder.itemView.findViewById(R.id.post_comments)

        Glide.with(viewHolder.itemView.context).load(post.avatarUrl)
            .apply(RequestOptions.circleCropTransform()).into(avatar)

        title.text = post.title
        username.text = post.username
        comments.text = viewHolder.itemView.context.resources.getQuantityString(
            R.plurals.comments,
            post.comments,
            post.comments
        )

        viewHolder.itemView.setOnClickListener {
            val options = ActivityOptionsCompat.makeBasic()
            viewModel.onPostClicked(post, options)
        }
    }
}
