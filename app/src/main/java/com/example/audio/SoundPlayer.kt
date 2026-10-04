package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundPlayer {
    private const val SAMPLE_RATE = 44100
    private val scope = CoroutineScope(Dispatchers.Default)

    fun playCorrectChime(enabled: Boolean) {
        if (!enabled) return
        scope.launch {
            val note1 = generateSineWave(523.25, 0.11) // C5
            val pause = ShortArray((SAMPLE_RATE * 0.02).toInt())
            val note2 = generateSineWave(659.25, 0.18) // E5
            val fullTone = note1 + pause + note2
            playAudioTrack(fullTone)
        }
    }

    fun playWrongBuzz(enabled: Boolean) {
        if (!enabled) return
        scope.launch {
            val buzz = generateBuzzWave(140.0, 0.22)
            playAudioTrack(buzz)
        }
    }

    fun playLevelUpArpeggio(enabled: Boolean) {
        if (!enabled) return
        scope.launch {
            val note1 = generateSineWave(523.25, 0.09) // C5
            val pause = ShortArray((SAMPLE_RATE * 0.015).toInt())
            val note2 = generateSineWave(659.25, 0.09) // E5
            val note3 = generateSineWave(783.99, 0.25) // G5
            val fullTone = note1 + pause + note2 + pause + note3
            playAudioTrack(fullTone)
        }
    }

    private fun generateSineWave(freq: Double, durationSec: Double): ShortArray {
        val totalSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(totalSamples)
        val attackSamples = (SAMPLE_RATE * 0.01).toInt().coerceAtMost(totalSamples / 4)
        val releaseSamples = (SAMPLE_RATE * 0.03).toInt().coerceAtMost(totalSamples / 2)

        for (i in 0 until totalSamples) {
            val time = i.toDouble() / SAMPLE_RATE
            val raw = sin(2.0 * Math.PI * freq * time)
            
            // Apply simple envelope to avoid clicks
            val envelope = when {
                i < attackSamples -> i.toDouble() / attackSamples
                i > totalSamples - releaseSamples -> (totalSamples - i).toDouble() / releaseSamples
                else -> 1.0
            }
            buffer[i] = (raw * envelope * 24000).toInt().coerceIn(-32768, 32767).toShort()
        }
        return buffer
    }

    private fun generateBuzzWave(freq: Double, durationSec: Double): ShortArray {
        val totalSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(totalSamples)
        val releaseSamples = (SAMPLE_RATE * 0.04).toInt().coerceAtMost(totalSamples / 2)

        for (i in 0 until totalSamples) {
            val time = i.toDouble() / SAMPLE_RATE
            // Mix fundamental + odd harmonics for a buzz/saw timbre
            val tone = 0.6 * sin(2.0 * Math.PI * freq * time) +
                    0.3 * sin(2.0 * Math.PI * freq * 2.0 * time) +
                    0.2 * sin(2.0 * Math.PI * freq * 3.0 * time)
            val envelope = if (i > totalSamples - releaseSamples) {
                (totalSamples - i).toDouble() / releaseSamples
            } else {
                1.0
            }
            buffer[i] = (tone * envelope * 18000).toInt().coerceIn(-32768, 32767).toShort()
        }
        return buffer
    }

    private fun playAudioTrack(pcmData: ShortArray) {
        var track: AudioTrack? = null
        try {
            val bufferSize = pcmData.size * 2
            track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(pcmData, 0, pcmData.size)
            track.play()
            Thread.sleep((pcmData.size * 1000L / SAMPLE_RATE) + 50)
        } catch (_: Exception) {
            // Ignore audio interruptions safely
        } finally {
            try {
                track?.stop()
                track?.release()
            } catch (_: Exception) {}
        }
    }
}
