package com.deepak.game.domain

import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine
import kotlin.concurrent.thread

/**
 * Loads an audio file from the Compose Multiplatform resources packaged
 * inside the Desktop application's JAR.
 *
 * Audio assets are located under:
 *
 * `composeResources/clappybee.shared.generated.resources/files/`
 *
 * The method uses the application's class loader to access the resource
 * stream at runtime, allowing audio playback to work both when running
 * from the IDE and from a packaged desktop distribution.
 *
 * Loaded audio data is returned as a [ByteArray] and cached by the caller
 * to avoid repeatedly reading the same resource from disk/JAR.
 *
 * @param fileName Name of the audio file to load
 * (for example, `jump.wav`, `falling.wav`, or `game_over.wav`).
 *
 * @return Raw audio file bytes.
 *
 * @throws FileNotFoundException If the requested audio resource cannot
 * be found in the packaged application resources.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AudioPlayer {
    private val audioCache = mutableMapOf<String, ByteArray>()
    private val playingLines = mutableMapOf<String, SourceDataLine>()

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
        playSound(fileName = "game_sound.wav", loop = true)
    }

    actual fun stopGameSound() {
        playGameOverSound()
        stopSound("game_sound.wav")
    }

    actual fun release() {
        audioCache.clear()
        stopAllSounds()
    }

    private fun playSound(fileName: String, loop: Boolean = false) {
        thread {
            try {
                val audioData = audioCache[fileName] ?: loadAudioFile(fileName).also {
                    audioCache[fileName] = it
                }

                val inputStream = AudioSystem.getAudioInputStream(audioData.inputStream())
                val format = inputStream.format
                val info = DataLine.Info(SourceDataLine::class.java, format)
                val line = AudioSystem.getLine(info) as SourceDataLine

                line.open(format)
                line.start()

                synchronized(playingLines) {
                    playingLines[fileName] = line
                }

                val buffer = ByteArray(4096)
                var bytesRead = 0
                var shouldContinue = true

                if (loop) {
                    while (shouldContinue) {
                        inputStream.reset()
                        while (shouldContinue && inputStream.read(buffer)
                                .also { bytesRead = it } != -1
                        ) {
                            synchronized(playingLines) {
                                shouldContinue = playingLines.containsKey(fileName)
                            }
                            if (shouldContinue) {
                                line.write(buffer, 0, bytesRead)
                            }
                        }
                    }
                } else {
                    while (shouldContinue && inputStream.read(buffer)
                            .also { bytesRead = it } != -1
                    ) {
                        synchronized(playingLines) {
                            shouldContinue = playingLines.containsKey(fileName)
                        }
                        if (shouldContinue) {
                            line.write(buffer, 0, bytesRead)
                        }
                    }

                    line.drain()
                    line.close()
                    synchronized(playingLines) {
                        playingLines.remove(fileName)
                    }
                }
            } catch (e: Exception) {
                println("Error with playing the audio: $fileName. $e")
            }
        }
    }

    private fun stopSound(fileName: String) {
        synchronized(playingLines) {
            playingLines[fileName]?.let { line ->
                line.stop()
                line.close()
                playingLines.remove(fileName)
            }
        }
    }

    private fun stopAllSounds() {
        synchronized(playingLines) {
            playingLines.forEach { (string, line) ->
                line.stop()
                line.close()
            }
            playingLines.clear()
        }
    }

    private fun loadAudioFile(fileName: String): ByteArray {
        val path =
            "composeResources/clappybee.shared.generated.resources/files/$fileName"

        println(
            this::class.java.classLoader?.getResource(path)
        )

        val resourceStream =
            this::class.java.classLoader?.getResourceAsStream(path)
                ?: throw FileNotFoundException("Resource not found: $path")

        return resourceStream.use { it.readBytes() }
    }
}