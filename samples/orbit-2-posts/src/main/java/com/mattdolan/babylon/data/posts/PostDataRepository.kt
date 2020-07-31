package com.mattdolan.babylon.data.posts

import com.mattdolan.babylon.data.posts.database.PostDataDetailMapper
import com.mattdolan.babylon.data.posts.database.PostDataOverviewMapper
import com.mattdolan.babylon.data.posts.database.PostDatabaseDataSource
import com.mattdolan.babylon.data.posts.network.PostNetworkDataSource
import com.mattdolan.babylon.domain.repositories.PostDetail
import com.mattdolan.babylon.domain.repositories.PostOverview
import com.mattdolan.babylon.domain.repositories.PostRepository

class PostDataRepository(
    private val networkDataSource: PostNetworkDataSource,
    private val databaseDataSource: PostDatabaseDataSource,
    private val overviewMapper: PostDataOverviewMapper,
    private val detailMapper: PostDataDetailMapper
) : PostRepository {
    override suspend fun getOverviews(): List<PostOverview> {
        populateDatabaseFromNetwork()

        return databaseDataSource.getOverviews().map(overviewMapper::convert)
    }

    private suspend fun populateDatabaseFromNetwork() {
        if (!databaseIsPopulated()) {
            val posts = networkDataSource.getPosts()
            val users = networkDataSource.getUsers()
            val comments = networkDataSource.getComments()

            databaseDataSource.replaceAllData(posts, users, comments)
        }
    }

    override suspend fun getDetail(id: Int): PostDetail? {
        populateDatabaseFromNetwork()

        return databaseDataSource.getPost(id)?.let(detailMapper::convert)
    }

    private suspend fun databaseIsPopulated() = databaseDataSource.isPopulated()
}
