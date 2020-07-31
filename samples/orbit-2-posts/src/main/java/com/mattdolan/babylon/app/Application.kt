package com.mattdolan.babylon.app

import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.StrictMode
import androidx.multidex.MultiDexApplication
import com.mattdolan.babylon.BuildConfig
import com.mattdolan.babylon.app.di.module
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@Suppress("unused")
class Application : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@Application)
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
