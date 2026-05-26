package com.deepak.game.domain

/**
 * Represents the bee character in the game.
 *
 * Stores the bee's current position and size on screen.
 * The bee moves vertically based on gravity and jump input,
 * while the horizontal position remains fixed.
 *
 * @property x Horizontal position of the bee on the screen.
 * @property y Vertical position of the bee on the screen.
 * @property radius Radius of the bee used for rendering and collision handling.
 */
data class Bee(
    val x: Float,
    val y: Float,
    val radius: Float
)