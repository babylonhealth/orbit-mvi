package com.babylon.orbit2.sample.posts.app.features.postlist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.babylon.orbit2.livedata.sideEffect
import com.babylon.orbit2.livedata.state
import com.babylon.orbit2.sample.posts.R
import com.babylon.orbit2.sample.posts.app.common.SeparatorDecoration
import com.babylon.orbit2.sample.posts.app.features.postlist.viewmodel.PostListViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.post_list_fragment.*
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class PostListFragment : Fragment() {

    private val viewModel: PostListViewModel by stateViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.container.sideEffect.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                activity?.let(it::navigate)
            }
        })

        return inflater.inflate(R.layout.post_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        content.layoutManager = LinearLayoutManager(activity)
        content.addItemDecoration(SeparatorDecoration(requireActivity(), R.dimen.separator_margin_start_icon, R.dimen.separator_margin_end))

        val adapter = GroupAdapter<GroupieViewHolder>()

        content.adapter = adapter

        viewModel.container.state.observe(viewLifecycleOwner, Observer {
            adapter.update(it.overviews.map { PostListItem(it, viewModel) })
        })
    }
}
