package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.sin

object RelaxingMusicPlayer {
    private const val SAMPLE_RATE = 44100
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    @Volatile
    private var isPlaying = false

    // Relaxing ambient chords in slow progression (Hz)
    // Cmaj9 -> Fmaj7 -> Am9 -> Em7 -> Dm9 -> Gsus4
    private val chords = listOf(
        // Cmaj9: C3, G3, B3, D4, E4
        doubleArrayOf(130.81, 196.00, 246.94, 293.66, 329.63),
        // Fmaj7: F3, C4, E4, A4
        doubleArrayOf(174.61, 261.63, 329.63, 440.00),
        // Am9: A2, E3, B3, C4, G4
        doubleArrayOf(110.00, 164.81, 246.94, 261.63, 392.00),
        // Em7: E3, B3, D4, G4
        doubleArrayOf(164.81, 246.94, 293.66, 392.00),
        // Dm9: D3, A3, C4, E4, F4
        doubleArrayOf(146.83, 220.00, 261.63, 329.63, 349.23),
        // Gsus4 -> G: G2, D3, G3, C4, D4
        doubleArrayOf(98.00, 146.83, 196.00, 261.63, 293.66)
    )

    // Soft meditation chimes/bells (Hz)
    private val chimes = doubleArrayOf(
        523.25, // C5
        659.25, // E5
        587.33, // D5
        783.99, // G5
        659.25, // E5
        493.88  // B4
    )

    // Duration of each chord in seconds (slow, relaxing tempo)
    private const val CHORD_DURATION_SEC = 4.2

    @Synchronized
    fun start() {
        if (isPlaying) return
        isPlaying = true

        playbackJob = scope.launch {
            try {
                val minBufferSize = AudioTrack.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val bufferSize = minBufferSize.coerceAtLeast(SAMPLE_RATE)

                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
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
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack?.play()

                var chordIndex = 0

                while (isActive && isPlaying) {
                    val chordFreqs = chords[chordIndex % chords.size]
                    val chimeFreq = chimes[chordIndex % chimes.size]
                    chordIndex++

                    val pcmData = generateCalmPad(
                        frequencies = chordFreqs,
                        chimeFreq = chimeFreq,
                        durationSec = CHORD_DURATION_SEC
                    )

                    var written = 0
                    while (written < pcmData.size && isActive && isPlaying) {
                        val toWrite = (pcmData.size - written).coerceAtMost(4096)
                        val result = audioTrack?.write(pcmData, written, toWrite) ?: -1
                        if (result <= 0) break
                        written += result
                    }
                }
            } catch (_: CancellationException) {
                // Expected on stop
            } catch (_: Exception) {
                // Ignore audio disruptions gracefully
            } finally {
                cleanupAudioTrack()
            }
        }
    }

    @Synchronized
    fun stop() {
        isPlaying = false
        playbackJob?.cancel()
        playbackJob = null
        cleanupAudioTrack()
    }

    private fun cleanupAudioTrack() {
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }

    /**
     * Generates a warm, slow-attack, slow-release ambient pad chord
     * with low gentle amplitude ("slow voice" / soft background level).
     */
    private fun generateCalmPad(
        frequencies: DoubleArray,
        chimeFreq: Double,
        durationSec: Double
    ): ShortArray {
        val totalSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(totalSamples)

        val attackSamples = (SAMPLE_RATE * 1.0).toInt().coerceAtMost(totalSamples / 3)
        val releaseSamples = (SAMPLE_RATE * 1.2).toInt().coerceAtMost(totalSamples / 3)

        // Soft, calming volume level (~15-18% of maximum)
        val masterAmplitude = 4500.0

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE

            // 1. Smooth, breathing envelope to keep audio relaxing
            val envelope = when {
                i < attackSamples -> {
                    // Cosine smooth rise
                    0.5 * (1.0 - kotlin.math.cos(Math.PI * i / attackSamples))
                }
                i > totalSamples - releaseSamples -> {
                    // Cosine smooth fade out
                    val rem = totalSamples - i
                    0.5 * (1.0 - kotlin.math.cos(Math.PI * rem / releaseSamples))
                }
                else -> 1.0
            }

            // 2. Sum harmonic sine waves for the chord
            var chordSignal = 0.0
            for (freq in frequencies) {
                // Fundamental + subtle warm octave harmonic
                chordSignal += sin(2.0 * Math.PI * freq * t) +
                        0.25 * sin(2.0 * Math.PI * (freq * 2.0) * t)
            }
            chordSignal /= frequencies.size

            // 3. Gentle bell/chime note with exponential decay
            val chimeEnv = exp(-2.5 * t)
            val chimeSignal = sin(2.0 * Math.PI * chimeFreq * t) * chimeEnv * 0.4

            // 4. Subtle slow LFO (Low Frequency Oscillation) to give a gentle wave-like shimmer
            val lfo = 1.0 + 0.12 * sin(2.0 * Math.PI * 0.3 * t)

            val sampleVal = (chordSignal * envelope + chimeSignal) * lfo * masterAmplitude
            buffer[i] = sampleVal.toInt().coerceIn(-32768, 32767).toShort()
        }

        return buffer
    }
}
