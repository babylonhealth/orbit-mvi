package com.babylon.orbit2.sample.posts.app.features.postlist.viewmodel

import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.babylon.orbit2.ContainerHost
import com.babylon.orbit2.coroutines.transformSuspend
import com.babylon.orbit2.reduce
import com.babylon.orbit2.sample.posts.app.common.NavigationEvent
import com.babylon.orbit2.sample.posts.domain.repositories.PostOverview
import com.babylon.orbit2.sample.posts.domain.repositories.PostRepository
import com.babylon.orbit2.sideEffect
import com.babylon.orbit2.viewmodel.container

class PostListViewModel(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository
) : ViewModel(), ContainerHost<PostListState, NavigationEvent> {

    override val container = container<PostListState, NavigationEvent>(PostListState(), savedStateHandle) {
        orbit {
            sideEffect {
                if (state.overviews.isEmpty()) {
                    loadOverviews()
                }
            }
        }
    }

    private fun loadOverviews() = orbit {
        transformSuspend {
            postRepository.getOverviews()
        }.reduce {
            state.copy(overviews = event)
        }
    }

    fun onPostClicked(post: PostOverview, transitionOptions: ActivityOptionsCompat) = orbit {
        sideEffect {
            post(OpenPostNavigationEvent(post, transitionOptions))
        }
    }
}
