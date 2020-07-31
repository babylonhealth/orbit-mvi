package com.babylon.orbit2.sample.posts.data.posts.common.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PostData(@PrimaryKey val id: Int, val userId: Int, val title: String, val body: String)
