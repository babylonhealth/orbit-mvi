package com.mattdolan.babylon.domain.repositories

data class PostOverview(
    val id: Int,
    val avatarUrl: String,
    val title: String,
    val username: String,
    val comments: Int
)
