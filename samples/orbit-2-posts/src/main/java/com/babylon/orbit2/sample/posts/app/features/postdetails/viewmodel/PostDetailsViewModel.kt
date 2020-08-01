package com.babylon.orbit2.sample.posts.app.features.postdetails.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.babylon.orbit2.ContainerHost
import com.babylon.orbit2.coroutines.transformSuspend
import com.babylon.orbit2.reduce
import com.babylon.orbit2.sample.posts.domain.repositories.PostRepository
import com.babylon.orbit2.sideEffect
import com.babylon.orbit2.viewmodel.container

class PostDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val postId: Int
) : ViewModel(), ContainerHost<PostDetailState, Nothing> {

    override val container = container<PostDetailState, Nothing>(PostDetailState.NoDetailsAvailable, savedStateHandle) {
        orbit {
            sideEffect {
                if (state !is PostDetailState.Details) {
                    loadDetails()
                }
            }
        }
    }

    private fun loadDetails() = orbit {
        transformSuspend {
            postRepository.getDetail(postId)?.let { PostDetailState.Details(it) } ?: PostDetailState.NoDetailsAvailable
        }.reduce {
            event
        }
    }
}
