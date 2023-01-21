package com.example.androidrecorddemo.core.audiorecord

import android.content.Context
import kotlinx.coroutines.flow.StateFlow

interface AudioRecorderProvider {

    fun recorderTimeElapsed(): StateFlow<Int>

    fun startRecording(context: Context, recordArgument: RecordArgument)

    fun stopRecording()

    fun release()
}