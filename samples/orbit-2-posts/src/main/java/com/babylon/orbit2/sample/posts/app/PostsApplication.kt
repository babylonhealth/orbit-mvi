package com.babylon.orbit2.sample.posts.app

import android.app.Application
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.StrictMode
import com.babylon.orbit2.sample.posts.app.di.module
import com.babylon.orbit2.sample.posts.BuildConfig
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

        enableStrictMode()
    }

    private fun enableStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            // OkHttp causes detectUntaggedSockets to trigger, see https://github.com/square/okhttp/issues/3537 for more details.
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .penaltyLog()
                    .apply {
                        detectActivityLeaks()
                        detectFileUriExposure()
                        detectLeakedClosableObjects()
                        detectLeakedRegistrationObjects()
                        detectLeakedSqlLiteObjects()

                        if (SDK_INT >= Build.VERSION_CODES.M) {
                            detectCleartextNetwork()
                        }
                        if (SDK_INT >= Build.VERSION_CODES.O) {
                            detectContentUriWithoutPermission()
                        }
                        if (SDK_INT >= Build.VERSION_CODES.P) {
                            detectNonSdkApiUsage()
                        }
                    }.build()
            )
        }
    }
}
