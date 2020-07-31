package com.babylon.orbit2.sample.posts.data.posts.common.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CommentData(
    @PrimaryKey val id: Int,
    val postId: Int,
    val name: String,
    val email: String,
    val body: String
)
