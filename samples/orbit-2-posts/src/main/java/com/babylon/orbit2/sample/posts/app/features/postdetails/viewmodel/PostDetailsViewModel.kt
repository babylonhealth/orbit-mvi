package com.babylon.orbit2.sample.posts.app.features.postdetails.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.babylon.orbit2.sample.posts.domain.repositories.PostDetail
import com.babylon.orbit2.sample.posts.domain.repositories.PostRepository

class PostDetailsViewModel(postRepository: PostRepository, postId: Int) : ViewModel() {
    val details: LiveData<PostDetail> = liveData {
        postRepository.getDetail(postId)?.let { emit(it) }
    }
}
