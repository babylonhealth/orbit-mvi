package com.mattdolan.babylon.domain.repositories

data class PostDetail(
    val id: Int,
    val avatarUrl: String,
    val title: String,
    val body: String,
    val username: String,
    val comments: List<PostComment>
)
