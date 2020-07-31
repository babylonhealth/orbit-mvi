package com.babylon.orbit2.sample.posts.data.posts.common.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserData(
    @PrimaryKey val id: Int,
    val name: String,
    val username: String,
    val email: String
)
