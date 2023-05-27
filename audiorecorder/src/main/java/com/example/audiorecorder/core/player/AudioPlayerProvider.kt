package com.example.audiorecorder.core.player

import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerProvider {

    fun startPlaying(fileName: String): Boolean

    fun stopPlaying(): Boolean

    fun playerStatus(): StateFlow<PlayerProgress>

    fun release()
}