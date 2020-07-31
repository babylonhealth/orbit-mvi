package com.babylon.orbit2.sample.posts.app.features.postlist.ui

import androidx.recyclerview.widget.DiffUtil
import com.babylon.orbit2.sample.posts.domain.repositories.PostOverview

class PostDiffCallback : DiffUtil.ItemCallback<PostOverview>() {
    override fun areItemsTheSame(oldItem: PostOverview, newItem: PostOverview): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PostOverview, newItem: PostOverview): Boolean {
        return oldItem == newItem
    }
}
