package com.deepak.game

import androidx.compose.ui.window.ComposeUIViewController
import com.deepak.game.di.initializeKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initializeKoin() }
) {
    App()
}