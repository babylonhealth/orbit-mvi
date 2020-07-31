package com.babylon.orbit2.sample.posts.data.posts.database

class AvatarUrlGenerator {
    fun generateUrl(email: String) = "https://api.adorable.io/avatars/285/$email"
}
