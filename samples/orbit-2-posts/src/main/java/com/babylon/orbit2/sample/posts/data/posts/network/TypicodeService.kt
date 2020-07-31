package com.babylon.orbit2.sample.posts.data.posts.network

import com.babylon.orbit2.sample.posts.data.posts.common.model.CommentData
import com.babylon.orbit2.sample.posts.data.posts.common.model.PostData
import com.babylon.orbit2.sample.posts.data.posts.common.model.UserData
import retrofit2.http.GET

// https://jsonplaceholder.typicode.com
interface TypicodeService {
    @GET("posts")
    suspend fun posts(): List<PostData>

    @GET("users")
    suspend fun users(): List<UserData>

    @GET("comments")
    suspend fun comments(): List<CommentData>
}
