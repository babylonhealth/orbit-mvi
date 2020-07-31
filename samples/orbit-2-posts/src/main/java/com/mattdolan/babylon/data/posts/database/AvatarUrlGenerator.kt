package com.mattdolan.babylon.data.posts.database

class AvatarUrlGenerator {
    fun generateUrl(email: String) = "https://api.adorable.io/avatars/285/$email"
}
