package com.deepak.game

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.deepak.game.di.initializeKoin

fun main() = application {
    initializeKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "ClappyBee",
    ) {
        App()
    }
}