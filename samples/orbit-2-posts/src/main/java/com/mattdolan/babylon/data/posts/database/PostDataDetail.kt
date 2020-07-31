package com.mattdolan.babylon.data.posts.database

import androidx.room.Embedded
import androidx.room.Relation
import com.mattdolan.babylon.data.posts.common.model.CommentData
import com.mattdolan.babylon.data.posts.common.model.PostData
import com.mattdolan.babylon.data.posts.common.model.UserData

class PostDataDetail {
    @Embedded
    lateinit var post: PostData

    @Relation(parentColumn = "userId", entityColumn = "id")
    lateinit var users: List<UserData>

    @Relation(parentColumn = "id", entityColumn = "postId")
    lateinit var comments: List<CommentData>
}
