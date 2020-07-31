package com.babylon.orbit2.sample.posts.app.features.postlist.viewmodel

import android.content.Context
import androidx.core.app.ActivityOptionsCompat
import com.babylon.orbit2.sample.posts.app.common.NavigationEvent
import com.babylon.orbit2.sample.posts.app.features.postdetails.ui.PostDetailsActivity
import com.babylon.orbit2.sample.posts.domain.repositories.PostOverview

class OpenPostNavigationEvent(
    private val post: PostOverview,
    private val transitionOptions: ActivityOptionsCompat
) : NavigationEvent {
    override fun navigate(context: Context) {
        context.startActivity(
            PostDetailsActivity.startIntent(context, post),
            transitionOptions.toBundle()
        )
    }
}
