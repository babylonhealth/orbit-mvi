package com.mattdolan.babylon.app.features.postlist.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mattdolan.babylon.domain.repositories.PostOverview
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

class PostListViewModelShould {
    @Suppress("unused")
    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: PostRepository

    @Before
    fun setupMocks() {
        initMocks(this)
    }

    private fun mockRepository() {
        runBlocking {
            `when`(repository.getOverviews())
                .then { listOf(PostOverview(1, "url", "title", "name", 1)) }
        }
    }

    @Test
    fun `request post overviews from repository`() {
        runBlocking {
            // given we mock the repository
            mockRepository()

            // when we initialise the view model and wait
            PostListViewModel(repository)
            sleep(100)

            // then the post details are loaded from the repository
            verify(repository, times(1)).getOverviews()
        }
    }

    @Test
    fun `return post overviews from repository`() {
        val latch = CountDownLatch(1)

        // given we mock the repository
        mockRepository()

        // when we observe details from the view model
        PostListViewModel(repository).overviews.observeForever {
            it?.run { latch.countDown() }
        }

        // then the data is posted, and the latch counts down
        assertTrue(latch.await(250, TimeUnit.MILLISECONDS))
    }
}
