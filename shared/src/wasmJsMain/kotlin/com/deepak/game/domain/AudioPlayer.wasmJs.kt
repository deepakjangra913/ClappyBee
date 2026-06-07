package com.deepak.game.domain

import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.w3c.dom.Audio

/**
 * Creates and returns an HTML audio element for the given audio file.
 *
 * Audio files are packaged inside Compose Multiplatform resources and are
 * available in the generated resources directory at runtime:
 *
 * `composeResources/clappybee.shared.generated.resources/files/`
 *
 * The created [Audio] element is configured with an error callback to log
 * resource loading failures, which helps diagnose missing or incorrectly
 * packaged audio assets in WebAssembly builds.
 *
 * @param fileName Name of the audio file located under the `files` resource
 * directory (for example, `jump.wav` or `game_over.wav`).
 *
 * @return Configured [Audio] instance for playback.
 */
actual class AudioPlayer {

    private val audioElements = mutableMapOf<String, Audio>()

    actual fun playGameOverSound() {
        stopFallingSound()
        playSound(fileName = "game_over.wav")
    }

    actual fun playJumpSound() {
        stopFallingSound()
        playSound(fileName = "jump.wav")
    }

    actual fun playFallingSound() {
        playSound(fileName = "falling.wav")
    }

    actual fun stopFallingSound() {
        stopSound(fileName = "falling.wav")
    }

    actual fun playGameSoundInLoop() {
        playSound(fileName = "game_sound.wav", true)
    }

    actual fun stopGameSound() {
        playGameOverSound()
        stopSound(fileName = "game_sound.wav")
    }

    actual fun release() {
        stopAllSounds()
        audioElements.clear()
    }

    @OptIn(ExperimentalWasmJsInterop::class)
    private fun playSound(fileName: String, loop: Boolean = false) {
        val audio = (audioElements[fileName] ?: createAudioElements(fileName)).also {
            audioElements[fileName] = it
        }

        audio.loop = loop
        audio.play().catch {
            println("Error playing sound: $fileName")
            it
        }
    }

    private fun stopSound(fileName: String) {
        audioElements[fileName]?.let { audio ->
            audio.pause()
            audio.currentTime = 0.0
        }
    }

    private fun stopAllSounds() {
        audioElements.values.forEach { audio ->
            audio.pause()
            audio.currentTime = 0.0
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    private fun createAudioElements(fileName: String): Audio {

        // Getting values from path
        val path = "composeResources/clappybee.shared.generated.resources/files/$fileName"
        return Audio(path).apply {
            onerror = { _, _, _, _, _ ->
                println("Error loading audio file: $path")
                null
            }
        }

        return Audio(path)
    }
}