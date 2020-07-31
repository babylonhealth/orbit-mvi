package com.babylon.orbit2.sample.posts.app

import android.app.Application
import com.babylon.orbit2.sample.posts.app.di.module
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@Suppress("unused")
class PostsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PostsApplication)
            modules(listOf(module()))
        }
    }
}
