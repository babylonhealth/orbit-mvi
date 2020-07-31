package com.mattdolan.babylon.data.posts.database

data class PostDataOverview(
    val id: Int,
    val title: String,
    val name: String,
    val email: String,
    val comments: Int
)
