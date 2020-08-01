package com.babylon.orbit2.sample.posts.domain.repositories

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostComment(
    val id: Int,
    val name: String,
    val email: String,
    val body: String
) : Parcelable
