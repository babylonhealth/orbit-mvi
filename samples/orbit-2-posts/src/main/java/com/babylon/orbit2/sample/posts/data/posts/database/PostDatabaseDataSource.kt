package com.babylon.orbit2.sample.posts.data.posts.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.babylon.orbit2.sample.posts.data.posts.common.model.CommentData
import com.babylon.orbit2.sample.posts.data.posts.common.model.PostData
import com.babylon.orbit2.sample.posts.data.posts.common.model.UserData

@Dao
@Suppress("TooManyFunctions")
abstract class PostDatabaseDataSource {

    @Query(
        """
        SELECT
          post.id, post.title, users.name, users.email, (
            select count(*)
            from CommentData comments
            where post.id = comments.postId) as comments
        FROM
          PostData post,
          UserData users
        WHERE
          post.userId == users.id
        ORDER BY
          post.id ASC"""
    )
    abstract suspend fun getOverviews(): List<PostDataOverview>

    @Transaction
    @Query("SELECT * FROM PostData WHERE id = :postId")
    abstract suspend fun getPost(postId: Int): PostDataDetail?

    @Query("DELETE FROM PostData")
    protected abstract suspend fun deleteAllPosts()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertPosts(posts: List<PostData>)

    @Query("DELETE FROM UserData")
    protected abstract suspend fun deleteAllUsers()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertUsers(users: List<UserData>)

    @Query("DELETE FROM CommentData")
    protected abstract suspend fun deleteAllComments()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertComments(comments: List<CommentData>)

    @Transaction
    open suspend fun replaceAllData(
        posts: List<PostData>,
        users: List<UserData>,
        comments: List<CommentData>
    ) {
        deleteAllPosts()
        insertPosts(posts)

        deleteAllUsers()
        insertUsers(users)

        deleteAllComments()
        insertComments(comments)
    }

    @Transaction
    @Query("SELECT count(*) FROM PostData")
    protected abstract suspend fun postCount(): Int

    open suspend fun isPopulated() = postCount() > 0
}
