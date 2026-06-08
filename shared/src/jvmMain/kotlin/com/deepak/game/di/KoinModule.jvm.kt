package com.deepak.game.di

import com.deepak.game.domain.AudioPlayer
import org.koin.dsl.module

/**
 * Desktop-specific dependency injection module.
 *
 * Provides platform-specific implementations required by the Desktop target,
 * including a singleton instance of [AudioPlayer] used for game sound effects
 * and background audio playback through the Java Sound API.
 */
actual val targetModule = module {
    single<AudioPlayer> { AudioPlayer() }
}