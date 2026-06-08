package com.deepak.game.di

import com.deepak.game.domain.AudioPlayer
import org.koin.dsl.module

/**
 * Web-specific dependency injection module.
 *
 * Provides platform-specific implementations required by the WebAssembly
 * target, including a singleton instance of [AudioPlayer] that uses browser
 * audio APIs for sound effects and background audio playback.
 */
actual val targetModule = module {
    single<AudioPlayer> {
        AudioPlayer()
    }
}