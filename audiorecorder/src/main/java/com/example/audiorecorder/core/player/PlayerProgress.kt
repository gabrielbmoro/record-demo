package com.example.audiorecorder.core.player

data class PlayerProgress(
    val percentage: Float,
    val currentSeconds: Int,
    val duration: Int
)
