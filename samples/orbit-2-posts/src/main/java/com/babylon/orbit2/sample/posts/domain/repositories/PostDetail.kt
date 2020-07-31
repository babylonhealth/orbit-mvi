package com.babylon.orbit2.sample.posts.domain.repositories

data class PostDetail(
    val id: Int,
    val avatarUrl: String,
    val title: String,
    val body: String,
    val username: String,
    val comments: List<PostComment>
)
