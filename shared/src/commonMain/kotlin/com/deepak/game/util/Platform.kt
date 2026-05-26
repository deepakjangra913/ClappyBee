package com.deepak.game.util

/**
 * Represents the platform on which the app is currently running.
 *
 * Used to handle platform-specific behavior in a Kotlin Multiplatform project.
 */
enum class Platform {
    Android,
    iOS,
    Desktop,
    Web
}

/**
 * Returns the current runtime platform.
 *
 * This is implemented separately for each platform using `actual`
 * declarations (Android, iOS, Desktop, Web).
 *
 * @return The current [Platform].
 */
expect fun getPlatform(): Platform