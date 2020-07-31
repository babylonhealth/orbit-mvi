package com.babylon.orbit2.sample.posts.app.features.postdetails.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.babylon.orbit2.sample.posts.domain.repositories.PostComment
import com.babylon.orbit2.sample.posts.R

class PostCommentsAdapter :
    ListAdapter<PostComment, PostCommentsViewHolder>(PostCommentsDiffCallback()) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PostCommentsViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.post_comment_list_item, viewGroup, false)
        return PostCommentsViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: PostCommentsViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }
}
