package com.mattdolan.babylon.app.features.postlist.viewmodel

import android.content.Context
import androidx.core.app.ActivityOptionsCompat
import com.mattdolan.babylon.app.common.NavigationEvent
import com.mattdolan.babylon.app.features.postdetails.ui.PostDetailsActivity
import com.mattdolan.babylon.domain.repositories.PostOverview

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
