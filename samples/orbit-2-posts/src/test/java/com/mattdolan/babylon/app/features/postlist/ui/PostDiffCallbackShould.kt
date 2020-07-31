package com.mattdolan.babylon.app.features.postlist.ui

import com.mattdolan.babylon.domain.repositories.PostOverview
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PostDiffCallbackShould {
    @Test
    fun `return items as same when the post id matches`() {
        val post1 = PostOverview(1, "url1", "title1", "name1", 1)
        val post2 = PostOverview(1, "url2", "title2", "name2", 2)

        assertTrue(PostDiffCallback().areItemsTheSame(post1, post2))
    }

    @Test
    fun `return items as same when content matches`() {
        val post1 = PostOverview(1, "url", "title", "name", 1)
        val post2 = PostOverview(1, "url", "title", "name", 1)

        assertTrue(PostDiffCallback().areItemsTheSame(post1, post2))
    }

    @Test
    fun `return items as different when the post ids differ`() {
        val post1 = PostOverview(1, "url", "title", "name", 1)
        val post2 = PostOverview(2, "url", "title", "name", 1)

        assertFalse(PostDiffCallback().areItemsTheSame(post1, post2))
    }

    @Test
    fun `return items as same when items are same`() {
        val post = PostOverview(1, "url", "title", "name", 1)

        assertTrue(PostDiffCallback().areItemsTheSame(post, post))
    }

    @Test
    fun `return contents as same when content matches`() {
        val post1 = PostOverview(1, "url", "title", "name", 1)
        val post2 = PostOverview(1, "url", "title", "name", 1)

        assertTrue(PostDiffCallback().areContentsTheSame(post1, post2))
    }

    @Test
    fun `return contents as same when items are same`() {
        val post = PostOverview(1, "url", "title", "name", 1)

        assertTrue(PostDiffCallback().areContentsTheSame(post, post))
    }

    @Test
    fun `return contents as different when post id differs`() {
        val post1 = PostOverview(1, "url", "title", "name", 1)
        val post2 = PostOverview(2, "url", "title", "name", 1)

        assertFalse(PostDiffCallback().areContentsTheSame(post1, post2))
    }

    @Test
    fun `return contents as same when content differs`() {
        val post1 = PostOverview(1, "url1", "title1", "name1", 1)
        val post2 = PostOverview(1, "url2", "title2", "name2", 2)

        assertFalse(PostDiffCallback().areContentsTheSame(post1, post2))
    }
}
