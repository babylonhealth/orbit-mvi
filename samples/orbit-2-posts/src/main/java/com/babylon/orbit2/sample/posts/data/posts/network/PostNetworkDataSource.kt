package com.babylon.orbit2.sample.posts.data.posts.network

import com.babylon.orbit2.sample.posts.data.posts.common.model.CommentData
import com.babylon.orbit2.sample.posts.data.posts.common.model.PostData
import com.babylon.orbit2.sample.posts.data.posts.common.model.UserData
import retrofit2.HttpException
import java.io.IOException

class PostNetworkDataSource(private val typicodeService: TypicodeService) {
    suspend fun getPosts(): List<PostData> {
        return try {
            typicodeService.posts()
        } catch (e: IOException) {
            emptyList()
        } catch (e: HttpException) {
            emptyList()
        }
    }

    suspend fun getUsers(): List<UserData> {
        return try {
            typicodeService.users()
        } catch (e: IOException) {
            emptyList()
        } catch (e: HttpException) {
            emptyList()
        }
    }

    suspend fun getComments(): List<CommentData> {
        return try {
            typicodeService.comments()
        } catch (e: IOException) {
            emptyList()
        } catch (e: HttpException) {
            emptyList()
        }
    }
}
