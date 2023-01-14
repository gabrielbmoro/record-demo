package com.example.androidrecorddemo.core.audioplayer

data class PlayerProgress(
    val percentage: Float,
    val currentSeconds: Int,
    val duration: Int
)
