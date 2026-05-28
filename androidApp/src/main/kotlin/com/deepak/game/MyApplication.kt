package com.deepak.game

import android.app.Application
import com.deepak.game.di.initializeKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeKoin()
    }
}