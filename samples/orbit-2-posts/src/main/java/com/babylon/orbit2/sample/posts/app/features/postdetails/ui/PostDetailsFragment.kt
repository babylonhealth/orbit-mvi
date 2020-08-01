/*
 * Copyright 2020 Babylon Partners Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.babylon.orbit2.sample.posts.app.features.postdetails.ui

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.babylon.orbit2.livedata.state
import com.babylon.orbit2.sample.posts.R
import com.babylon.orbit2.sample.posts.app.common.SeparatorDecoration
import com.babylon.orbit2.sample.posts.app.features.postdetails.viewmodel.PostDetailState
import com.babylon.orbit2.sample.posts.app.features.postdetails.viewmodel.PostDetailsViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.post_details_fragment.*
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import org.koin.core.parameter.parametersOf


class PostDetailsFragment : Fragment() {

    private val args: PostDetailsFragmentArgs by navArgs()
    private val viewModel: PostDetailsViewModel by stateViewModel { parametersOf(args.id) }

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
                (activity as AppCompatActivity?)?.supportActionBar?.apply {
                    title = it.post.username

                    val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, resources.displayMetrics)

                    Glide.with(requireContext()).load(it.post.avatarUrl)
                        .apply(RequestOptions.overrideOf(px.toInt()))
                        .apply(RequestOptions.circleCropTransform()).into(object : CustomTarget<Drawable>() {
                            override fun onLoadCleared(placeholder: Drawable?) {
                                placeholder?.let(::setLogo)
                            }

                            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                val logo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    LayerDrawable(arrayOf(resource)).apply {
                                        setLayerInsetRight(0, px.toInt())
                                    }
                                } else {
                                    resource
                                }

                                setLogo(logo)
                            }
                        })
                }

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
