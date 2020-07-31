package com.babylon.orbit2.sample.posts.domain.repositories

interface PostRepository {
    suspend fun getOverviews(): List<PostOverview>
    suspend fun getDetail(id: Int): PostDetail?
}
