package com.babylon.orbit2.sample.posts.data.posts.database

import androidx.room.Embedded
import androidx.room.Relation
import com.babylon.orbit2.sample.posts.data.posts.common.model.CommentData
import com.babylon.orbit2.sample.posts.data.posts.common.model.PostData
import com.babylon.orbit2.sample.posts.data.posts.common.model.UserData

class PostDataDetail {
    @Embedded
    lateinit var post: PostData

    @Relation(parentColumn = "userId", entityColumn = "id")
    lateinit var users: List<UserData>

    @Relation(parentColumn = "id", entityColumn = "postId")
    lateinit var comments: List<CommentData>
}
