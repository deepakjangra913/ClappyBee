package com.deepak.game

import android.app.Application
import com.deepak.game.di.initializeKoin
import org.koin.android.ext.koin.androidContext

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initializeKoin {
            androidContext(this@MyApplication)
        }
    }
}