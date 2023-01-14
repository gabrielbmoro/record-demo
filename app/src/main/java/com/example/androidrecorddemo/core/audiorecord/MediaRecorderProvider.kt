package com.example.androidrecorddemo.core.audiorecord

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.IOException

class MediaRecorderProvider: AudioRecorderProvider {

    private var _recorder: MediaRecorder? = null

    override fun startRecording(
        context: Context,
        recordArgument: RecordArgument
    ) {
        _recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }.apply {
            setAudioSource(recordArgument.audioSource.value)
            setOutputFormat(recordArgument.outputFormat.value)
            setOutputFile(recordArgument.outputFile)
            setAudioEncoder(recordArgument.audioEncoder.value)
        }

        try {
            _recorder?.run {
                prepare()
                start()
            }
        } catch (ioException: IOException) {
            Log.e(TAG, "startRecording: $ioException")
        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, "startRecording: $illegalStateException")
        }
    }

    override fun stopRecording() {
        try {
            _recorder?.run {
                stop()
                release()
            }
        } catch (exception: Exception) {
            Log.e(TAG, "stopRecording: $exception")
        } finally {
            _recorder = null
        }
    }

    override fun release() {
        _recorder?.release()
        _recorder = null
    }

    companion object {
        private const val TAG = "RecordManager"
    }
}