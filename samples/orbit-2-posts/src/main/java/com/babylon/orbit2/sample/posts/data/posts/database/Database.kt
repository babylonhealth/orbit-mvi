package com.babylon.orbit2.sample.posts.data.posts.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.babylon.orbit2.sample.posts.data.posts.common.model.CommentData
import com.babylon.orbit2.sample.posts.data.posts.common.model.PostData
import com.babylon.orbit2.sample.posts.data.posts.common.model.UserData

@Database(
    entities = [PostData::class, UserData::class, CommentData::class],
    version = 1,
    exportSchema = false
)
abstract class Database : RoomDatabase() {
    abstract fun postDao(): PostDatabaseDataSource
}
