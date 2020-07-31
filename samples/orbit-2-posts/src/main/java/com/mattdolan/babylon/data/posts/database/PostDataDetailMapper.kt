package com.mattdolan.babylon.data.posts.database

import com.mattdolan.babylon.data.posts.common.model.CommentData
import com.mattdolan.babylon.domain.repositories.PostComment
import com.mattdolan.babylon.domain.repositories.PostDetail

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
