package com.mattdolan.babylon.domain.repositories

data class PostComment(
    val id: Int,
    val name: String,
    val email: String,
    val body: String
)
