package com.mattdolan.babylon.data.posts.database

import org.junit.Assert.assertEquals
import org.junit.Test

class AvatarUrlGeneratorShould {
    @Test
    fun `generate url based on parameter`() {
        val actual = AvatarUrlGenerator().generateUrl("info@mattdolan.com")
        assertEquals("https://api.adorable.io/avatars/285/info@mattdolan.com", actual)
    }
}
