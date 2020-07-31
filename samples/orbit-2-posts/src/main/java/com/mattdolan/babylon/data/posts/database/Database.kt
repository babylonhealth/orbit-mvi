package com.mattdolan.babylon.data.posts.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mattdolan.babylon.data.posts.common.model.CommentData
import com.mattdolan.babylon.data.posts.common.model.PostData
import com.mattdolan.babylon.data.posts.common.model.UserData

@Database(
    entities = [PostData::class, UserData::class, CommentData::class],
    version = 1,
    exportSchema = false
)
abstract class Database : RoomDatabase() {
    abstract fun postDao(): PostDatabaseDataSource
}
