package com.deepak.game.util

enum class Platform {

    Android,
    iOS,
    Desktop,
    Web
}

expect fun getPlatform(): Platform