package com.mattdolan.babylon.data.posts.database

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class PostDataOverviewMapperShould {
    @Mock
    private lateinit var avatarUrlGenerator: AvatarUrlGenerator

    @Before
    fun setupMocks() {
        MockitoAnnotations.initMocks(this)

        `when`(avatarUrlGenerator.generateUrl(anyString()))
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
