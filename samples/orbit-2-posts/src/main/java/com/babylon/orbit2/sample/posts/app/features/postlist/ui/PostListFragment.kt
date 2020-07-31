package com.babylon.orbit2.sample.posts.app.features.postlist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.babylon.orbit2.sample.posts.app.common.SeparatorDecoration
import com.babylon.orbit2.sample.posts.app.features.postlist.viewmodel.PostListViewModel
import com.babylon.orbit2.sample.posts.R
import kotlinx.android.synthetic.main.post_list_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PostListFragment : Fragment() {

    private val viewModel: PostListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.navigationEvents.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                activity?.let(it::navigate)
            }
        })

        return inflater.inflate(R.layout.post_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        content.layoutManager = LinearLayoutManager(activity)
        activity?.let {
            SeparatorDecoration(
                it,
                R.dimen.separator_margin_start_icon,
                R.dimen.separator_margin_end
            )
        }?.let(content::addItemDecoration)

        val adapter = PostListAdapter(viewModel)
        content.adapter = adapter

        viewModel.overviews.observe(this, Observer {
            it?.let(adapter::submitList)
        })
    }
}
