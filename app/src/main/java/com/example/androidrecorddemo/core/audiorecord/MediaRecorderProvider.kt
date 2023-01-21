package com.example.androidrecorddemo.core.audiorecord

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Timer
import java.util.TimerTask

class MediaRecorderProvider : AudioRecorderProvider {

    private var _recorder: MediaRecorder? = null

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _recordTimeElapsed = MutableStateFlow(0)

    private val _timer = Timer(true)

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

                _recordTimeElapsed.value = 0
                _timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            coroutineScope.launch {
                                _recordTimeElapsed.emit(_recordTimeElapsed.value + 1000)
                            }
                        }
                    },
                    1000,
                    1000
                )
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
                _timer.cancel()
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

        coroutineScope.cancel()
        _timer.cancel()
    }

    override fun recorderTimeElapsed(): StateFlow<Int> {
        return _recordTimeElapsed
    }

    companion object {
        private const val TAG = "RecordManager"
    }
}