package com.example.androidrecorddemo.ui

import com.example.androidrecorddemo.ui.widgets.PlayerCardContent
import com.example.androidrecorddemo.ui.widgets.RecordCardContent

data class MainUIState(
    val playBackAvailable: Boolean,
    val recordAvailable: Boolean,
    val playerCardContent: PlayerCardContent,
    val recordCardContent: RecordCardContent,
)