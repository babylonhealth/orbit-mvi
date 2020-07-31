package com.babylon.orbit2.sample.posts.data.posts.database

import com.babylon.orbit2.sample.posts.data.posts.common.model.CommentData
import com.babylon.orbit2.sample.posts.domain.repositories.PostComment
import com.babylon.orbit2.sample.posts.domain.repositories.PostDetail

class PostDataDetailMapper(private val avatarUrlGenerator: AvatarUrlGenerator) {
    fun convert(postDataDetail: PostDataDetail): PostDetail {
        return PostDetail(
            postDataDetail.post.id,
            avatarUrlGenerator.generateUrl(postDataDetail.users[0].email),
            postDataDetail.post.title,
            postDataDetail.post.body,
            postDataDetail.users[0].name,
            postDataDetail.comments.map(::convert)
        )
    }

    private fun convert(commentData: CommentData): PostComment {
        return PostComment(commentData.id, commentData.name, commentData.email, commentData.body)
    }
}
