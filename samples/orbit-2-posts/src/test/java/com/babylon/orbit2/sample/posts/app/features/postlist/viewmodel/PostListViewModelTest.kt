/*
 * Copyright 2020 Babylon Partners Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.babylon.orbit2.sample.posts.app.features.postlist.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.appmattus.kotlinfixture.kotlinFixture
import com.babylon.orbit2.assert
import com.babylon.orbit2.sample.posts.InstantTaskExecutorExtension
import com.babylon.orbit2.sample.posts.domain.repositories.PostOverview
import com.babylon.orbit2.sample.posts.domain.repositories.PostRepository
import com.babylon.orbit2.test
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
class PostListViewModelTest {

    private val fixture = kotlinFixture()
    private val repository = mock<PostRepository>()

    @Test
    fun `loads post overviews from repository`() {
        val overviews = fixture<List<PostOverview>>()

        // given we mock the repository
        runBlocking {
            whenever(repository.getOverviews())
        }.thenReturn(overviews)

        // when we observe details from the view model
        val viewModel = PostListViewModel(SavedStateHandle(), repository).test(
            initialState = PostListState(),
            runOnCreate = true
        )

        // then the view model loads the overviews
        viewModel.assert {
            states(
                { copy(overviews = overviews) }
            )
        }
    }
}
