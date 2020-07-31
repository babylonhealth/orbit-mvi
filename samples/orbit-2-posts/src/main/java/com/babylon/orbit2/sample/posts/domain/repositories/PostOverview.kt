package com.babylon.orbit2.sample.posts.domain.repositories

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostOverview(
    val id: Int,
    val avatarUrl: String,
    val title: String,
    val username: String,
    val comments: Int
) : Parcelable
