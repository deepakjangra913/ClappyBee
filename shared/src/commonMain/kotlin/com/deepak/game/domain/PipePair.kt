package com.deepak.game.domain

data class PipePair(
    var x: Float,
    var y: Float,
    val topHeight: Float,
    val bottomHeight: Float,
    var scored: Boolean = false
)
