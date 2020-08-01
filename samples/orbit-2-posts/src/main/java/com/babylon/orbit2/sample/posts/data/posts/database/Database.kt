/*
 * Copyright 2020 Babylon Partners Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
