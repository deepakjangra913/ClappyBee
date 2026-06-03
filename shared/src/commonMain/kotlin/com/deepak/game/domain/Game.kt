package com.deepak.game.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.deepak.game.util.Platform
import com.russhwolf.settings.ObservableSettings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.random.Random

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

const val SCORE_KEY = "best_score"

data class Game(
    val platform: Platform,
    val screenWidth: Int = 0,
    val screenHeight: Int = 0,
    val gravity: Float = 0.8f,
    val beeRadius: Float = 30f,
    val beeJumpImpulse: Float = -12f,
    val beeMaxVelocity: Float = if (platform == Platform.Android) 25f else 10f,
    val pipeWidth: Float = 150f,
    val pipeVelocity: Float = if (platform == Platform.Android) 5f else 2.5f,
    val pipeGapSize: Float = if (platform == Platform.Android) 250f else 300f
) : KoinComponent {

    private val audioPlayer: AudioPlayer by inject()
    private val settings: ObservableSettings by inject()

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

    var pipePairs = mutableListOf<PipePair>()
    var currentScore by mutableStateOf(0)
        private set
    var bestScore by mutableStateOf(0)
        private set

    private var isFallingSoundPlayed = false

    init {
        bestScore = settings.getInt(
            key = SCORE_KEY,
            defaultValue = 0
        )
        settings.addIntListener(
            key = SCORE_KEY,
            defaultValue = 0
        ) {
            bestScore = it
        }
    }

    /** Starts the game loop. */
    fun start() {
        status = GameStatus.Started
        audioPlayer.playGameSoundInLoop()
    }

    private fun saveScore() {
        if (bestScore < currentScore) {
            settings.putInt(key = SCORE_KEY, currentScore)
        }
    }

    /** Ends the game and switches to Game Over state. */
    fun gameOver() {
        status = GameStatus.Over
        audioPlayer.stopGameSound()
        saveScore()
        isFallingSoundPlayed = false
    }

    /** Ends the game and switches to Game Over state. */
    fun jump() {
        beeVelocity = beeJumpImpulse
        audioPlayer.playJumpSound()
        isFallingSoundPlayed = false
    }

    /** Ends the game and switches to Game Over state. */
    private fun resetBeePosition() {
        bee = bee.copy(
            y = (screenHeight / 2).toFloat()
        )
        beeVelocity = 0f
    }

    private fun removePipes() {
        pipePairs.clear()
    }

    /** Ends the game and switches to Game Over state. */
    fun restart() {
        resetBeePosition()
        removePipes()
        resetScore()
        start()
        isFallingSoundPlayed = false
    }

    /** Ends the game and switches to Game Over state. */
    fun updateGameProgress() {
        pipePairs.forEach { pipePair ->
            if (isCollision(pipePair)) {
                gameOver()
                return
            }

            if (!pipePair.scored && bee.x > pipePair.x + pipeWidth / 2) {
                pipePair.scored = true
                currentScore += 1
            }
        }
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

        // When to play the falling sound
        if (beeVelocity > (beeMaxVelocity / 1.1)) {
            if (isFallingSoundPlayed) {
                audioPlayer.playFallingSound()
                isFallingSoundPlayed = true
            }
        }

        spawnPipes()
    }

    fun resetScore() {
        currentScore = 0
    }

    private fun spawnPipes() {
        pipePairs.forEach { it.x -= pipeVelocity }
        pipePairs.removeAll { it.x + pipeWidth < 0 }

        val isLandScape = screenWidth > screenHeight
        val spawnThreshold = if(isLandScape) screenWidth / 1.25
        else screenWidth / 2.0

        if (pipePairs.isEmpty() || pipePairs.last().x < spawnThreshold) {
            val initialPipeX = screenWidth.toFloat() + pipeWidth
            val topHeight = Random.nextFloat() * (screenHeight / 2)
            val bottomHeight = screenHeight - topHeight - pipeGapSize
            val newPipePair = PipePair(
                x = initialPipeX,
                y = topHeight + pipeGapSize / 2,
                topHeight = topHeight,
                bottomHeight = bottomHeight
            )

            pipePairs.add(newPipePair)
        }
    }

    private fun isCollision(pipePair: PipePair): Boolean {
        // Check horizontal collision. Bee overlaps the pipe's X range
        val beeRightEdge = bee.x + bee.radius
        val beeLeftEdge = bee.x - bee.radius
        val pipeLeftEdge = pipePair.x - pipeWidth / 2
        val pipeRightEdge = pipePair.x - pipeWidth / 2
        val horizontalCollision = beeRightEdge > pipeLeftEdge
                && beeLeftEdge < pipeRightEdge

        // Check if bee is with in the vertical gap
        val beeTopEdge = bee.y - bee.radius
        val beeBottomEdge = bee.y - bee.radius
        val gapTopEdge = pipePair.y - pipeGapSize / 2
        val gapBottomEdge = pipePair.y + pipeGapSize / 2

        val beeInGap = beeTopEdge > gapTopEdge
                && beeBottomEdge < gapBottomEdge

        return horizontalCollision && !beeInGap
    }

    /** Ends the game and switches to Game Over state. */
    fun stopBee() {
        beeVelocity = 0f
        bee = bee.copy(y = 0f)
    }

    fun cleanUp() {
        audioPlayer.release()
    }
}