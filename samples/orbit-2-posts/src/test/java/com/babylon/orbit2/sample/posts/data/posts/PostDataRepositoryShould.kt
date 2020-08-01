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

package com.babylon.orbit2.sample.posts.data.posts

import com.babylon.orbit2.sample.posts.data.posts.common.model.PostData
import com.babylon.orbit2.sample.posts.data.posts.common.model.UserData
import com.babylon.orbit2.sample.posts.data.posts.database.PostDataDetail
import com.babylon.orbit2.sample.posts.data.posts.database.PostDataDetailMapper
import com.babylon.orbit2.sample.posts.data.posts.database.PostDataOverview
import com.babylon.orbit2.sample.posts.data.posts.database.PostDataOverviewMapper
import com.babylon.orbit2.sample.posts.data.posts.database.PostDatabaseDataSource
import com.babylon.orbit2.sample.posts.data.posts.network.PostNetworkDataSource
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyList
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class PostDataRepositoryShould {

    private val networkDataSource = mock<PostNetworkDataSource> {
        onBlocking { getPosts() } doReturn emptyList()
        onBlocking { getComments() } doReturn emptyList()
        onBlocking { getUsers() } doReturn emptyList()
    }

    private val databaseDataSource = mock<PostDatabaseDataSource>()

    private val dataOverviewMapper = mock<PostDataOverviewMapper>()

    private val dataDetailMapper = mock<PostDataDetailMapper>()

    private val repository = PostDataRepository(
        networkDataSource,
        databaseDataSource,
        dataOverviewMapper,
        dataDetailMapper
    )

    private fun mockEmptyDatabase() {
        runBlocking {
            whenever(databaseDataSource.getOverviews())
                .then { listOf<PostDataOverview>() }
            whenever(databaseDataSource.getPost(anyInt()))
                .then { null }
            whenever(databaseDataSource.isPopulated())
                .then { false }
        }
    }

    private fun mockPopulatedDatabase() {
        runBlocking {
            whenever(databaseDataSource.getOverviews())
                .then { listOf(PostDataOverview(1, "title", "name", "email", 5)) }
            whenever(databaseDataSource.getPost(anyInt()))
                .then {
                    PostDataDetail().apply {
                        post = PostData(1, 1, "title", "body")
                        users = listOf(UserData(1, "bob", "username", "email"))
                        comments = listOf()
                    }
                }
            whenever(databaseDataSource.isPopulated())
                .then { true }
        }
    }

    @Test
    fun `populate database when empty from overviews`() {
        runBlocking {
            // given we have an empty database
            mockEmptyDatabase()

            // when we request overviews
            repository.getOverviews()

            // then the network source is accessed
            verify(networkDataSource, times(1)).getPosts()
            verify(networkDataSource, times(1)).getUsers()
            verify(networkDataSource, times(1)).getComments()

            // and the database is populated and accessed
            verify(databaseDataSource, times(1)).replaceAllData(anyList(), anyList(), anyList())
            verify(databaseDataSource, times(1)).getOverviews()
        }
    }

    @Test
    fun `not access the network when database is populated from overviews`() {
        runBlocking {
            // given we have a populated database
            mockPopulatedDatabase()

            // when we request overviews
            repository.getOverviews()

            // then the network source is not accessed
            verify(networkDataSource, times(0)).getPosts()
            verify(networkDataSource, times(0)).getUsers()
            verify(networkDataSource, times(0)).getComments()

            // and the database is accessed
            verify(databaseDataSource, times(1)).getOverviews()
        }
    }

    @Test
    fun `populate database when empty from details`() {
        runBlocking {
            // given we have an empty database
            mockEmptyDatabase()

            // when we request details
            repository.getDetail(1)

            // then the network source is accessed
            verify(networkDataSource, times(1)).getPosts()
            verify(networkDataSource, times(1)).getUsers()
            verify(networkDataSource, times(1)).getComments()

            // and the database is populated and accessed
            verify(databaseDataSource, times(1)).replaceAllData(anyList(), anyList(), anyList())
            verify(databaseDataSource, times(1)).getPost(1)
        }
    }

    @Test
    fun `not access the network when database is populated from details`() {
        runBlocking {
            // given we have a populated database
            mockPopulatedDatabase()

            // when we request details
            repository.getDetail(1)

            // then the network source is not accessed
            verify(networkDataSource, times(0)).getPosts()
            verify(networkDataSource, times(0)).getUsers()
            verify(networkDataSource, times(0)).getComments()

            // and the database is accessed
            verify(databaseDataSource, times(1)).getPost(1)
        }
    }
}
