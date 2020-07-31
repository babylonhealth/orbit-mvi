package com.mattdolan.babylon.data.posts.database

import com.mattdolan.babylon.domain.repositories.PostOverview

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
