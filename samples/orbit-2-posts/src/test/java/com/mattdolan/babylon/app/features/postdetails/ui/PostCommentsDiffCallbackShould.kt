package com.mattdolan.babylon.app.features.postdetails.ui

import com.mattdolan.babylon.domain.repositories.PostComment
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PostCommentsDiffCallbackShould {
    @Test
    fun `return items as same when the post id matches`() {
        val comment1 = PostComment(1, "name1", "email1", "body1")
        val comment2 = PostComment(1, "name2", "email2", "body2")

        assertTrue(PostCommentsDiffCallback().areItemsTheSame(comment1, comment2))
    }

    @Test
    fun `return items as same when content matches`() {
        val comment1 = PostComment(1, "name", "email", "body")
        val comment2 = PostComment(1, "name", "email", "body")

        assertTrue(PostCommentsDiffCallback().areItemsTheSame(comment1, comment2))
    }

    @Test
    fun `return items as different when the post ids differ`() {
        val comment1 = PostComment(1, "name", "email", "body")
        val comment2 = PostComment(2, "name", "email", "body")

        assertFalse(PostCommentsDiffCallback().areItemsTheSame(comment1, comment2))
    }

    @Test
    fun `return items as same when items are same`() {
        val comment = PostComment(1, "name", "email", "body")

        assertTrue(PostCommentsDiffCallback().areItemsTheSame(comment, comment))
    }

    @Test
    fun `return contents as same when content matches`() {
        val comment1 = PostComment(1, "name", "email", "body")
        val comment2 = PostComment(1, "name", "email", "body")

        assertTrue(PostCommentsDiffCallback().areContentsTheSame(comment1, comment2))
    }

    @Test
    fun `return contents as same when items are same`() {
        val comment = PostComment(1, "name", "email", "body")

        assertTrue(PostCommentsDiffCallback().areContentsTheSame(comment, comment))
    }

    @Test
    fun `return contents as different when post id differs`() {
        val comment1 = PostComment(1, "name", "email", "body")
        val comment2 = PostComment(2, "name", "email", "body")

        assertFalse(PostCommentsDiffCallback().areContentsTheSame(comment1, comment2))
    }

    @Test
    fun `return contents as same when content differs`() {
        val comment1 = PostComment(1, "name1", "email1", "body1")
        val comment2 = PostComment(1, "name2", "email2", "body2")

        assertFalse(PostCommentsDiffCallback().areContentsTheSame(comment1, comment2))
    }
}