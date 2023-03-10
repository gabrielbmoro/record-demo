package com.example.audiorecorder.core.recorder

import android.content.Context
import kotlinx.coroutines.flow.StateFlow

interface AudioRecorderProvider {

    fun recorderTimeElapsed(): StateFlow<Int>

    fun startRecording(context: Context, recordArgument: RecordArgument): Boolean

    fun stopRecording(): Boolean

    fun release()
}