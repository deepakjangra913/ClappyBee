package com.deepak.game.di
import com.deepak.game.domain.AudioPlayer
import org.koin.dsl.module

/**
 * iOS-specific dependency injection module.
 *
 * Provides platform-specific implementations required by the iOS target,
 * including a singleton instance of [AudioPlayer] responsible for handling
 * sound effects and background audio playback.
 */
actual val targetModule = module {
    single<AudioPlayer> {
        AudioPlayer()
    }
}