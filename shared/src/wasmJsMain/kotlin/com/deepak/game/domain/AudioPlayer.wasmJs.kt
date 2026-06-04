package com.deepak.game.domain

import clappybee.shared.generated.resources.Res
import org.w3c.dom.Audio

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

    private fun createAudioElements(fileName: String): Audio {
        val path = Res.getUri("files/$fileName")
        return Audio(path).apply {
            onerror = { _, _, _, _, _ ->

                println("Error loading audio file: $path")
                null
            }
        }
    }
}