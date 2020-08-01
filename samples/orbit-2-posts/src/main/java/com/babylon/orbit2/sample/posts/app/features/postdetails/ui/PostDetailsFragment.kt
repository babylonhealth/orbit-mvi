package com.babylon.orbit2.sample.posts.app.features.postdetails.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.babylon.orbit2.livedata.state
import com.babylon.orbit2.sample.posts.R
import com.babylon.orbit2.sample.posts.app.common.SeparatorDecoration
import com.babylon.orbit2.sample.posts.app.features.postdetails.viewmodel.PostDetailState
import com.babylon.orbit2.sample.posts.app.features.postdetails.viewmodel.PostDetailsViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.post_details_fragment.*
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import org.koin.core.parameter.parametersOf

class PostDetailsFragment : Fragment() {

    private val viewModel: PostDetailsViewModel by stateViewModel {
        parametersOf(
            activity?.intent?.getIntExtra(PostDetailsActivity.POST_ID_EXTRA, -1) ?: -1
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.post_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        post_comments_list.layoutManager = LinearLayoutManager(activity)
        ViewCompat.setNestedScrollingEnabled(post_comments_list, false)

        post_comments_list.addItemDecoration(
            SeparatorDecoration(
                requireActivity(),
                R.dimen.separator_margin_start,
                R.dimen.separator_margin_end
            )
        )

        val adapter = GroupAdapter<GroupieViewHolder>()
        post_comments_list.adapter = adapter

        viewModel.container.state.observe(viewLifecycleOwner, Observer {
            if (it is PostDetailState.Details) {
                Glide.with(this).load(it.post.avatarUrl)
                    .apply(RequestOptions.circleCropTransform()).into(post_avatar)

                post_username.text = it.post.username
                post_title.text = it.post.title
                post_body.text = it.post.body

                val comments = it.post.comments.size
                post_comment_count.text = context?.resources?.getQuantityString(
                    R.plurals.comments,
                    comments,
                    comments
                )

                adapter.update(it.post.comments.map(::PostCommentItem))
            }
        })
    }
}
