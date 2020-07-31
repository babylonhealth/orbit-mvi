package com.mattdolan.babylon.app.features.postdetails.ui

import androidx.recyclerview.widget.DiffUtil
import com.mattdolan.babylon.domain.repositories.PostComment

class PostCommentsDiffCallback : DiffUtil.ItemCallback<PostComment>() {
    override fun areItemsTheSame(oldItem: PostComment, newItem: PostComment): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PostComment, newItem: PostComment): Boolean {
        return oldItem == newItem
    }
}
