package com.deepak.game.domain

/**
 * Represents the current state of the game.
 *
 * Used to control gameplay flow and UI rendering.
 *
 * - [Idle] → initial state before gameplay starts
 * - [Started] → game is actively running
 * - [Over] → game has ended
 */
enum class GameStatus {
    Idle,
    Started,
    Over
}