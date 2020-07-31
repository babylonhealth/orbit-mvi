package com.mattdolan.babylon.data.posts

import com.mattdolan.babylon.data.posts.common.model.PostData
import com.mattdolan.babylon.data.posts.common.model.UserData
import com.mattdolan.babylon.data.posts.database.PostDataDetail
import com.mattdolan.babylon.data.posts.database.PostDataDetailMapper
import com.mattdolan.babylon.data.posts.database.PostDataOverview
import com.mattdolan.babylon.data.posts.database.PostDataOverviewMapper
import com.mattdolan.babylon.data.posts.database.PostDatabaseDataSource
import com.mattdolan.babylon.data.posts.network.PostNetworkDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyList
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations.initMocks

class PostDataRepositoryShould {

    @Mock
    private lateinit var networkDataSource: PostNetworkDataSource

    @Mock
    private lateinit var databaseDataSource: PostDatabaseDataSource

    @Mock
    private lateinit var dataOverviewMapper: PostDataOverviewMapper

    @Mock
    private lateinit var dataDetailMapper: PostDataDetailMapper

    private lateinit var repository: PostDataRepository


    @Before
    fun setup() {
        initMocks(this)

        repository = PostDataRepository(
            networkDataSource,
            databaseDataSource,
            dataOverviewMapper,
            dataDetailMapper
        )
    }

    private fun mockEmptyDatabase() {
        runBlocking {
            `when`(databaseDataSource.getOverviews())
                .then { listOf<PostDataOverview>() }
            `when`(databaseDataSource.getPost(anyInt()))
                .then { null }
            `when`(databaseDataSource.isPopulated())
                .then { false }
        }
    }

    private fun mockPopulatedDatabase() {
        runBlocking {
            `when`(databaseDataSource.getOverviews())
                .then { listOf(PostDataOverview(1, "title", "name", "email", 5)) }
            `when`(databaseDataSource.getPost(anyInt()))
                .then {
                    PostDataDetail().apply {
                        post = PostData(1, 1, "title", "body")
                        users = listOf(UserData(1, "bob", "username", "email"))
                        comments = listOf()
                    }
                }
            `when`(databaseDataSource.isPopulated())
                .then { true }
        }
    }

    @Test
    fun `populate database when empty from overviews`() {
        runBlocking {
            // given we have an empty database
            mockEmptyDatabase()

            // when we request overviews
            repository.getOverviews()

            // then the network source is accessed
            verify(networkDataSource, times(1)).getPosts()
            verify(networkDataSource, times(1)).getUsers()
            verify(networkDataSource, times(1)).getComments()

            // and the database is populated and accessed
            verify(databaseDataSource, times(1)).replaceAllData(anyList(), anyList(), anyList())
            verify(databaseDataSource, times(1)).getOverviews()
        }
    }

    @Test
    fun `not access the network when database is populated from overviews`() {
        runBlocking {
            // given we have a populated database
            mockPopulatedDatabase()

            // when we request overviews
            repository.getOverviews()

            // then the network source is not accessed
            verify(networkDataSource, times(0)).getPosts()
            verify(networkDataSource, times(0)).getUsers()
            verify(networkDataSource, times(0)).getComments()

            // and the database is accessed
            verify(databaseDataSource, times(1)).getOverviews()
        }
    }

    @Test
    fun `populate database when empty from details`() {
        runBlocking {
            // given we have an empty database
            mockEmptyDatabase()

            // when we request details
            repository.getDetail(1)

            // then the network source is accessed
            verify(networkDataSource, times(1)).getPosts()
            verify(networkDataSource, times(1)).getUsers()
            verify(networkDataSource, times(1)).getComments()

            // and the database is populated and accessed
            verify(databaseDataSource, times(1)).replaceAllData(anyList(), anyList(), anyList())
            verify(databaseDataSource, times(1)).getPost(1)
        }
    }

    @Test
    fun `not access the network when database is populated from details`() {
        runBlocking {
            // given we have a populated database
            mockPopulatedDatabase()

            // when we request details
            repository.getDetail(1)

            // then the network source is not accessed
            verify(networkDataSource, times(0)).getPosts()
            verify(networkDataSource, times(0)).getUsers()
            verify(networkDataSource, times(0)).getComments()

            // and the database is accessed
            verify(databaseDataSource, times(1)).getPost(1)
        }
    }
}
