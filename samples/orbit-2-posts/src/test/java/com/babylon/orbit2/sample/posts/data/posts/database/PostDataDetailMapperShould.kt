package com.babylon.orbit2.sample.posts.data.posts.database

import com.babylon.orbit2.sample.posts.data.posts.common.model.CommentData
import com.babylon.orbit2.sample.posts.data.posts.common.model.PostData
import com.babylon.orbit2.sample.posts.data.posts.common.model.UserData
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.anyString

class PostDataDetailMapperShould {

    private val avatarUrlGenerator = mock<AvatarUrlGenerator>()

    @BeforeEach
    fun setupMocks() {
        whenever(avatarUrlGenerator.generateUrl(anyString()))
            .then { (it.getArgument(0) as String).reversed() }
    }

    @Test
    fun `throw exception when not initialised`() {
        assertThrows<UninitializedPropertyAccessException> {
            // given we create an empty detail object
            val postData = PostDataDetail()

            // when we try and convert it
            PostDataDetailMapper(avatarUrlGenerator).convert(postData)

            // then an exception is thrown
        }
    }

    fun `throw exception when no linked user`() {
        assertThrows<IndexOutOfBoundsException> {
            // given we create a detail object without a user
            val postData = PostDataDetail().apply {
                post = PostData(1, 1, "title", "body")
                users = listOf()
                comments = listOf()
            }

            // when we try and convert it
            PostDataDetailMapper(avatarUrlGenerator).convert(postData)

            // then an exception is thrown
        }
    }

    @Test
    fun `map when no comments`() {
        // given we configure details with no comments
        val postData = PostDataDetail().apply {
            post = PostData(1, 1, "title", "body")
            users = listOf(UserData(1, "bob", "username", "email"))
            comments = listOf()
        }

        // when we convert
        val result = PostDataDetailMapper(avatarUrlGenerator).convert(postData)

        // then we get the expected data
        assertEquals(1, result.id)
        assertEquals("liame", result.avatarUrl)
        assertEquals("title", result.title)
        assertEquals("body", result.body)
        assertEquals("bob", result.username)
        assertEquals(0, result.comments.size)
    }

    @Test
    fun `map comments`() {
        // given we configure details with no comments
        val postData = PostDataDetail().apply {
            post = PostData(1, 1, "title", "body")
            users = listOf(UserData(1, "bob", "bobthebuilder", "email"))
            comments = listOf(CommentData(5, 1, "name", "email", "body"))
        }

        // when we convert
        val result = PostDataDetailMapper(avatarUrlGenerator).convert(postData).comments

        // then we get the expected data
        assertEquals(1, result.size)
        assertEquals(5, result[0].id)
        assertEquals("name", result[0].name)
        assertEquals("email", result[0].email)
        assertEquals("body", result[0].body)
    }
}
