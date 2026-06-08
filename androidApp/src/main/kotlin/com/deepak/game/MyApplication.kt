package com.deepak.game

import android.app.Application
import com.deepak.game.di.initializeKoin
import org.koin.android.ext.koin.androidContext

/**
 * Application entry point for the Android target.
 *
 * Initializes Koin during application startup and provides the Android
 * application context required by platform-specific dependencies.
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initializeKoin {
            androidContext(this@MyApplication)
        }
    }
}