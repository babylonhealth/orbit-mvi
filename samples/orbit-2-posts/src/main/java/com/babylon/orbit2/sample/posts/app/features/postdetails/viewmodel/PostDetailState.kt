package com.babylon.orbit2.sample.posts.app.features.postdetails.viewmodel

import android.os.Parcelable
import com.babylon.orbit2.sample.posts.domain.repositories.PostDetail
import kotlinx.android.parcel.Parcelize

sealed class PostDetailState : Parcelable {
    @Parcelize
    data class Details(val post: PostDetail) : PostDetailState()

    @Parcelize
    object NoDetailsAvailable : PostDetailState()
}
