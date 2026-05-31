package com.deepak.game.di

import com.deepak.game.domain.AudioPlayer
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val targetModule = module {
    single<AudioPlayer> {
        AudioPlayer(context = androidContext())
    }
}