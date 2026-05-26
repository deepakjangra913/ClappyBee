package com.deepak.game.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Holds the core game state and gameplay logic for Clappy Bee.
 *
 * This class manages:
 * - Bee position and movement
 * - Gravity simulation
 * - Jump mechanics
 * - Game lifecycle state
 *
 * The bee moves vertically under the effect of [gravity]. Calling [jump]
 * applies an upward impulse, while [updateGameProgress] updates the bee's
 * position on each frame.
 *
 * Game flow:
 * - [start] → begins gameplay
 * - [jump] → makes the bee move upward
 * - [updateGameProgress] → applies gravity and updates movement every frame
 * - [gameOver] → marks the game as finished
 * - [restart] → resets the bee position and starts again
 *
 * State is exposed using Compose [mutableStateOf], allowing the UI to react
 * automatically to position, velocity, and status updates.
 *
 * @property screenWidth Width of the game screen in pixels.
 * @property screenHeight Height of the game screen in pixels.
 * @property gravity Downward force applied to the bee every frame.
 * @property beeRadius Radius of the bee hitbox.
 * @property beeJumpImpulse Upward velocity applied when the player taps.
 * @property beeMaxVelocity Maximum vertical speed allowed for the bee.
 */
data class Game(
    val screenWidth: Int = 0,
    val screenHeight: Int = 0,
    val gravity: Float = 0.8f,
    val beeRadius: Float = 30f,
    val beeJumpImpulse: Float = -12f,
    val beeMaxVelocity: Float = 25f
) {

    var status by mutableStateOf(GameStatus.Idle)
        private set

    var beeVelocity by mutableStateOf(0f)
        private set

    var bee by mutableStateOf(
        Bee(
            (screenWidth / 4).toFloat(),
            y = (screenHeight / 2).toFloat(),
            radius = beeRadius
        )
    )
        private set


    /** Starts the game loop. */
    fun start() {
        status = GameStatus.Started
    }

    /** Ends the game and switches to Game Over state. */
    fun gameOver() {
        status = GameStatus.Over
    }

    /** Ends the game and switches to Game Over state. */
    fun jump() {
        beeVelocity = beeJumpImpulse
    }

    /** Ends the game and switches to Game Over state. */
    private fun resetBeePosition() {
        bee = bee.copy(
            y = (screenHeight / 2).toFloat()
        )
        beeVelocity = 0f
    }

    /** Ends the game and switches to Game Over state. */
    fun restart() {
        resetBeePosition()
        start()
    }

    /** Ends the game and switches to Game Over state. */
    fun updateGameProgress() {
        if (bee.y < 0) {
            stopBee()
            return
        } else if (bee.y > screenHeight) {
            gameOver()
            return
        }

        beeVelocity = (beeVelocity + gravity).coerceIn(-beeMaxVelocity, beeMaxVelocity)
        bee = bee.copy(
            y = bee.y + beeVelocity
        )
    }

    /** Ends the game and switches to Game Over state. */
    fun stopBee() {
        beeVelocity = 0f
        bee = bee.copy(y = 0f)
    }
}