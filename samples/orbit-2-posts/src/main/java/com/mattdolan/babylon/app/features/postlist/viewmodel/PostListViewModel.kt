package com.mattdolan.babylon.app.features.postlist.viewmodel

import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.mattdolan.babylon.app.common.NavigationEvent
import com.mattdolan.babylon.app.common.SingleLiveEvent
import com.mattdolan.babylon.domain.repositories.PostOverview
import com.mattdolan.babylon.domain.repositories.PostRepository

class PostListViewModel(private val postRepository: PostRepository) : ViewModel() {

    val navigationEvents by lazy { SingleLiveEvent<NavigationEvent>() }

    val overviews: LiveData<List<PostOverview>> = liveData {
        emit(postRepository.getOverviews())
    }

    fun onPostClicked(post: PostOverview, transitionOptions: ActivityOptionsCompat) {
        navigationEvents.postValue(OpenPostNavigationEvent(post, transitionOptions))
    }
}
