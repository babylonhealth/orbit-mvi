package com.babylon.orbit2.sample.posts.app.features.postlist.viewmodel

import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.babylon.orbit2.sample.posts.app.common.NavigationEvent
import com.babylon.orbit2.sample.posts.app.common.SingleLiveEvent
import com.babylon.orbit2.sample.posts.domain.repositories.PostOverview
import com.babylon.orbit2.sample.posts.domain.repositories.PostRepository

class PostListViewModel(private val postRepository: PostRepository) : ViewModel() {

    val navigationEvents by lazy { SingleLiveEvent<NavigationEvent>() }

    val overviews: LiveData<List<PostOverview>> = liveData {
        emit(postRepository.getOverviews())
    }

    fun onPostClicked(post: PostOverview, transitionOptions: ActivityOptionsCompat) {
        navigationEvents.postValue(OpenPostNavigationEvent(post, transitionOptions))
    }
}
