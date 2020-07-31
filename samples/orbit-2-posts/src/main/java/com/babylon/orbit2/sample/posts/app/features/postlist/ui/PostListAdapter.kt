package com.babylon.orbit2.sample.posts.app.features.postlist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.babylon.orbit2.sample.posts.app.features.postlist.viewmodel.PostListViewModel
import com.babylon.orbit2.sample.posts.domain.repositories.PostOverview
import com.babylon.orbit2.sample.posts.R

class PostListAdapter(
    private val viewModel: PostListViewModel
) : ListAdapter<PostOverview, PostListViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PostListViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.post_list_item, viewGroup, false)
        return PostListViewHolder(view, viewModel)
    }

    override fun onBindViewHolder(viewHolder: PostListViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }
}
