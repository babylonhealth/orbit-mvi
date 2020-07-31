package com.babylon.orbit2.sample.posts.data.posts

import com.babylon.orbit2.sample.posts.data.posts.database.PostDataDetailMapper
import com.babylon.orbit2.sample.posts.data.posts.database.PostDataOverviewMapper
import com.babylon.orbit2.sample.posts.data.posts.database.PostDatabaseDataSource
import com.babylon.orbit2.sample.posts.data.posts.network.PostNetworkDataSource
import com.babylon.orbit2.sample.posts.domain.repositories.PostDetail
import com.babylon.orbit2.sample.posts.domain.repositories.PostOverview
import com.babylon.orbit2.sample.posts.domain.repositories.PostRepository

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
