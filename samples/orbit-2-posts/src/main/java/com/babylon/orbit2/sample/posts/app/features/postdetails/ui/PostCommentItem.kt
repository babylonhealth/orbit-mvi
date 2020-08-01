package com.babylon.orbit2.sample.posts.app.features.postdetails.ui

import android.widget.TextView
import com.babylon.orbit2.sample.posts.R
import com.babylon.orbit2.sample.posts.domain.repositories.PostComment
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

data class PostCommentItem(private val post: PostComment) : Item() {

    override fun isSameAs(other: com.xwray.groupie.Item<*>) = other is PostCommentItem && post.id == other.post.id

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>) = other is PostCommentItem && post == other.post

    override fun getLayout() = R.layout.post_comment_list_item

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.apply {
            findViewById<TextView>(R.id.comment_username).text = post.name
            findViewById<TextView>(R.id.comment_email).text = post.email
            findViewById<TextView>(R.id.comment_body).text = post.body
        }
    }
}
