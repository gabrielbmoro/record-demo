package com.example.androidrecorddemo.core.audiorecord

import android.content.Context

interface AudioRecorderProvider {

    fun startRecording(context: Context, recordArgument: RecordArgument)

    fun stopRecording()

    fun release()
}