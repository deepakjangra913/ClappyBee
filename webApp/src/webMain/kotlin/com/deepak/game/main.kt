package com.deepak.game

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.deepak.game.di.initializeKoin

/**
 * Entry point for the WebAssembly application.
 *
 * Initializes Koin dependency injection and mounts the Compose UI
 * into the browser viewport. This function serves as the starting
 * point for the web application.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initializeKoin()
    ComposeViewport {
        App()
    }
}