package com.mattdolan.babylon.domain.repositories

interface PostRepository {
    suspend fun getOverviews(): List<PostOverview>
    suspend fun getDetail(id: Int): PostDetail?
}
