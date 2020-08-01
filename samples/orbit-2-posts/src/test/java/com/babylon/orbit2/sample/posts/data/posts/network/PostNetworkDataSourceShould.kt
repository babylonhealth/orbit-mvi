package com.babylon.orbit2.sample.posts.data.posts.network

import com.babylon.orbit2.sample.posts.data.posts.common.model.CommentData
import com.babylon.orbit2.sample.posts.data.posts.common.model.PostData
import com.babylon.orbit2.sample.posts.data.posts.common.model.UserData
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.util.concurrent.TimeUnit

class PostNetworkDataSourceShould {
    private lateinit var behavior: NetworkBehavior
    private lateinit var api: TypicodeService

    @Before
    fun setup() {
        mockNetworkApi()
    }

    @Test
    fun `return posts when network ok`() {
        runBlocking {
            assertEquals(2, PostNetworkDataSource(api).getPosts().size)
        }
    }

    @Test
    fun `return empty posts when network errors`() {
        runBlocking {
            behavior.setErrorPercent(100)

            assertEquals(0, PostNetworkDataSource(api).getPosts().size)
        }
    }

    @Test
    fun `return empty posts when network fails`() {
        runBlocking {
            behavior.setFailurePercent(100)

            assertEquals(0, PostNetworkDataSource(api).getPosts().size)
        }
    }

    @Test
    fun `return users when network ok`() {
        runBlocking {
            assertEquals(2, PostNetworkDataSource(api).getUsers().size)
        }
    }

    @Test
    fun `return empty users when network errors`() {
        runBlocking {
            behavior.setErrorPercent(100)

            assertEquals(0, PostNetworkDataSource(api).getUsers().size)
        }
    }

    @Test
    fun `return empty users when network fails`() {
        runBlocking {
            behavior.setFailurePercent(100)

            assertEquals(0, PostNetworkDataSource(api).getUsers().size)
        }
    }

    @Test
    fun `return comments when network ok`() {
        runBlocking {
            assertEquals(2, PostNetworkDataSource(api).getComments().size)
        }
    }

    @Test
    fun `return empty comments when network errors`() {
        runBlocking {
            behavior.setErrorPercent(100)

            assertEquals(0, PostNetworkDataSource(api).getComments().size)
        }
    }

    @Test
    fun `return empty comments when network fails`() {
        runBlocking {
            behavior.setFailurePercent(100)

            assertEquals(0, PostNetworkDataSource(api).getComments().size)
        }
    }

    private fun mockNetworkApi() {
        behavior = NetworkBehavior.create().apply {
            setDelay(0, TimeUnit.MILLISECONDS)
            setVariancePercent(0)
            setErrorPercent(0)
            setFailurePercent(0)
        }

        val retrofit = Retrofit.Builder().baseUrl("http://mock.com").build()

        api = MockRetrofit.Builder(retrofit)
            .networkBehavior(behavior)
            .build()
            .create(TypicodeService::class.java)
            .let(PostNetworkDataSourceShould::MockApi)
    }

    class MockApi(private val delegate: BehaviorDelegate<TypicodeService>) :
        TypicodeService {
        private val posts = listOf(
            PostData(1, 1, "hi", "body"),
            PostData(2, 2, "hello", "body 2")
        )
        private val users = listOf(
            UserData(1, "bob", "username", "email"),
            UserData(2, "matt", "matt", "email")
        )
        private val comments = listOf(
            CommentData(1, 1, "content", "email", "body 3"),
            CommentData(
                2,
                1,
                "content 2",
                "email2",
                "body 4"
            )
        )

        override suspend fun posts(): List<PostData> {
            return delegate.returningResponse(posts).posts()
        }

        override suspend fun users(): List<UserData> {
            return delegate.returningResponse(users).users()
        }

        override suspend fun comments(): List<CommentData> {
            return delegate.returningResponse(comments).comments()
        }
    }
}
