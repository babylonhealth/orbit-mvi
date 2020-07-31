package com.babylon.orbit2.sample.posts.app.features.postlist.viewmodel

import android.os.Parcelable
import com.babylon.orbit2.sample.posts.domain.repositories.PostOverview
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostListState(
    val overviews: List<PostOverview> = emptyList()
) : Parcelable
