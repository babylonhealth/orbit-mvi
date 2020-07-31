package com.mattdolan.babylon.app.features.postlist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.mattdolan.babylon.R
import com.mattdolan.babylon.app.features.postlist.viewmodel.PostListViewModel
import com.mattdolan.babylon.domain.repositories.PostOverview

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
