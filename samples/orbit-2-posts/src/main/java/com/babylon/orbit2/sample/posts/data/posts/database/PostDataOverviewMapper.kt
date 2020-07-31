package com.babylon.orbit2.sample.posts.data.posts.database

import com.babylon.orbit2.sample.posts.domain.repositories.PostOverview

class PostDataOverviewMapper(private val avatarUrlGenerator: AvatarUrlGenerator) {
    fun convert(postDataOverview: PostDataOverview): PostOverview {
        return PostOverview(
            postDataOverview.id,
            avatarUrlGenerator.generateUrl(postDataOverview.email),
            postDataOverview.title,
            postDataOverview.name,
            postDataOverview.comments
        )
    }
}
