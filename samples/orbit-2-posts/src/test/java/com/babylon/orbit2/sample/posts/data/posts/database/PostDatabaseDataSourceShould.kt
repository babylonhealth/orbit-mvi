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

package com.babylon.orbit2.sample.posts.data.posts.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.babylon.orbit2.sample.posts.data.posts.common.model.CommentData
import com.babylon.orbit2.sample.posts.data.posts.common.model.PostData
import com.babylon.orbit2.sample.posts.data.posts.common.model.UserData
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = "src/main/AndroidManifest.xml", application = android.app.Application::class, sdk = [28])
class PostDatabaseDataSourceShould {

    private lateinit var database: Database
    private lateinit var dao: PostDatabaseDataSource

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, Database::class.java)
            .allowMainThreadQueries().build()
        dao = database.postDao()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        database.close()
    }

    @Test
    fun `return no overviews on an empty database`() {
        runBlocking {
            assertTrue(dao.getOverviews().isEmpty())
        }
    }

    @Test
    fun `return null details on an empty database`() {
        runBlocking {
            assertNull(dao.getPost(1))
        }
    }

    @Test
    fun `provide overview data`() {
        runBlocking {
            // given we configure some data
            val posts = listOf(
                PostData(1, 1, "hi", "body"),
                PostData(2, 2, "hello", "body 2")
            )
            val users = listOf(
                UserData(1, "bob", "bob", "email"),
                UserData(2, "matt", "matt", "email")
            )
            val comments = listOf(
                CommentData(1, 1, "content", "email", "body 3"),
                CommentData(
                    2,
                    1,
                    "content 2",
                    "email2",
                    "body 4"
                )
            )
            dao.replaceAllData(posts, users, comments)

            // when we get the overviews
            val overviews = dao.getOverviews()

            // then the data is formatted as expected
            assertEquals(2, overviews.size)

            assertEquals("bob", overviews.first { it.id == 1 }.name)
            assertEquals(2, overviews.first { it.id == 1 }.comments)

            assertEquals("matt", overviews.first { it.id == 2 }.name)
            assertEquals(0, overviews.first { it.id == 2 }.comments)
        }
    }

    @Test
    fun `provide detailed data`() {
        runBlocking {
            // given we configure some data
            val posts = listOf(
                PostData(1, 1, "hi", "body"),
                PostData(2, 2, "hello", "body 2")
            )
            val users = listOf(
                UserData(1, "bob", "bob", "email"),
                UserData(2, "matt", "matt", "email")
            )
            val comments = listOf(
                CommentData(1, 1, "content", "email", "body 3"),
                CommentData(
                    2,
                    1,
                    "content 2",
                    "email2",
                    "body 4"
                )
            )
            dao.replaceAllData(posts, users, comments)

            // when we get the details
            val result = dao.getPost(1)

            // then the data is formatted as expected
            result?.let {
                assertEquals(1, result.post.id)
                assertEquals(1, result.users.size)
                assertEquals("bob", result.users[0].name)
                assertEquals(2, result.comments.size)
            }
        }
    }
}
