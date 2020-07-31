package com.mattdolan.babylon.app.features.postdetails.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.mattdolan.babylon.R
import com.mattdolan.babylon.domain.repositories.PostComment

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
