package com.babylon.orbit2.sample.posts.app.features.postdetails.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.babylon.orbit2.livedata.state
import com.babylon.orbit2.sample.posts.InstantTaskExecutorExtension
import com.babylon.orbit2.sample.posts.domain.repositories.PostDetail
import com.babylon.orbit2.sample.posts.domain.repositories.PostRepository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.lang.Thread.sleep
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@ExtendWith(InstantTaskExecutorExtension::class)
class PostDetailsViewModelShould {

    private val repository = mock<PostRepository>()

    @Test
    fun `request post details from repository`() {
        runBlocking {
            // given we mock the repository
            whenever(repository.getDetail(1))
                .then { PostDetail(1, "url", "title", "body", "username", listOf()) }

            // when we initialise the view model and wait
            PostDetailsViewModel(SavedStateHandle(), repository, 1).container.stateStream.observe { }
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
            whenever(repository.getDetail(1))
                .then { PostDetail(1, "url", "title", "body", "username", listOf()) }

            // when we observe details from the view model
            PostDetailsViewModel(SavedStateHandle(), repository, 1).container.state.observeForever {
                if (it is PostDetailState.Details) {
                    latch.countDown()
                }
            }

            // then the data is posted, and the latch counts down
            assertTrue(latch.await(250, TimeUnit.MILLISECONDS))
        }
    }
}
