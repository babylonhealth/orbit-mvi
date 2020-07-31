package com.mattdolan.babylon.app.features.postdetails.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.mattdolan.babylon.domain.repositories.PostDetail
import com.mattdolan.babylon.domain.repositories.PostRepository

class PostDetailsViewModel(postRepository: PostRepository, postId: Int) : ViewModel() {
    val details: LiveData<PostDetail> = liveData {
        postRepository.getDetail(postId)?.let { emit(it) }
    }
}
