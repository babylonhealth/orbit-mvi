package com.babylon.orbit2.sample.posts.data.posts.database

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString

class PostDataOverviewMapperShould {

    private val avatarUrlGenerator = mock<AvatarUrlGenerator>()

    @BeforeEach
    fun setupMocks() {
        whenever(avatarUrlGenerator.generateUrl(anyString()))
            .then { (it.getArgument(0) as String).reversed() }
    }

    @Test
    fun `map when no comments`() {
        // given we configure details with no comments
        val postData = PostDataOverview(1, "title", "name", "email", 5)

        // when we convert
        val result = PostDataOverviewMapper(avatarUrlGenerator).convert(postData)

        // then we get the expected data
        assertEquals(1, result.id)
        assertEquals("liame", result.avatarUrl)
        assertEquals("title", result.title)
        assertEquals("name", result.username)
        assertEquals(5, result.comments)
    }
}
