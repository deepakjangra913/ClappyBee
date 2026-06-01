package com.deepak.game.domain

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.Foundation.NSURL.Companion.fileURLWithPath

@OptIn(ExperimentalForeignApi::class)
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AudioPlayer {
    private var audioPlayers: MutableMap<String, AVAudioPlayer?> = mutableMapOf()
    private var fallingSoundPlayer: AVAudioPlayer? = null

    init {

        // Configure the audio session for playback
        val session = AVAudioSession.sharedInstance()
        session.setCategory(AVAudioSessionCategoryPlayback, error = null)
        session.setActive(true, null)
    }

    actual fun playGameOverSound() {
        stopFallingSound()
        playSound(soundName = "game_over")
    }

    actual fun playJumpSound() {
        stopFallingSound()
        playSound(soundName = "jump")
    }

    actual fun playFallingSound() {
        fallingSoundPlayer = playSound(soundName = "falling")
    }

    actual fun stopFallingSound() {
        fallingSoundPlayer?.stop()
        fallingSoundPlayer = null
    }

    actual fun playGameSoundInLoop() {
        val url = getSoundURL(resourceName = "game_sound")
        val player = url?.let { AVAudioPlayer(it, null) }
        player?.numberOfLoops = -1
        player?.prepareToPlay()
        player?.play()
        audioPlayers["game_sound"] = player
    }

    actual fun stopGameSound() {
        playGameOverSound()
        audioPlayers["game_sound"]?.stop()
        audioPlayers["game_sound"] = null
    }

    actual fun release() {
        audioPlayers.values.forEach {
            it?.stop()
        }
        audioPlayers.clear()
        fallingSoundPlayer?.stop()
        fallingSoundPlayer = null
    }

    private fun playSound(soundName: String): AVAudioPlayer? {
        val url = getSoundURL(soundName)
        val player = url?.let { AVAudioPlayer(it, null) }
        player?.prepareToPlay()
        player?.play()
        audioPlayers[soundName] = player
        return player
    }

    private fun getSoundURL(resourceName: String): NSURL? {
        val bundle = NSBundle.mainBundle()
        val path = bundle.pathForResource(resourceName, "wav")
        return path?.let { fileURLWithPath(it) }
    }
}