package com.deepak.game

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform