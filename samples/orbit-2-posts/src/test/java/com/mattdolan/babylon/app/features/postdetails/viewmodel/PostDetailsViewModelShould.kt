package com.mattdolan.babylon.app.features.postdetails.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mattdolan.babylon.domain.repositories.PostDetail
import com.mattdolan.babylon.domain.repositories.PostRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations.initMocks
import java.lang.Thread.sleep
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PostDetailsViewModelShould {
    @Suppress("unused")
    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: PostRepository

    @Before
    fun setupMocks() {
        initMocks(this)
    }

    @Test
    fun `request post details from repository`() {
        runBlocking {
            // given we mock the repository
            `when`(repository.getDetail(1))
                .then { PostDetail(1, "url", "title", "body", "username", listOf()) }

            // when we initialise the view model and wait
            PostDetailsViewModel(repository, 1)
            sleep(100)

            // then the post details are loaded from the repository
            verify(repository, times(1)).getDetail(1)
        }
    }

    @Test
    fun `return post details from repository`() {
        runBlocking {
            val latch = CountDownLatch(1)

            // given we mock the repository
            `when`(repository.getDetail(1))
                .then { PostDetail(1, "url", "title", "body", "username", listOf()) }

            // when we observe details from the view model
            PostDetailsViewModel(repository, 1).details.observeForever {
                it?.run { latch.countDown() }
            }

            // then the data is posted, and the latch counts down
            assertTrue(latch.await(250, TimeUnit.MILLISECONDS))
        }
    }
}
