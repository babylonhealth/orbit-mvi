package com.babylon.orbit2.sample.posts.app.features.postdetails.ui

import androidx.recyclerview.widget.DiffUtil
import com.babylon.orbit2.sample.posts.domain.repositories.PostComment

class PostCommentsDiffCallback : DiffUtil.ItemCallback<PostComment>() {
    override fun areItemsTheSame(oldItem: PostComment, newItem: PostComment): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PostComment, newItem: PostComment): Boolean {
        return oldItem == newItem
    }
}
