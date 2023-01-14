package com.example.androidrecorddemo.core.audioplayer

import android.media.MediaPlayer
import android.util.Log
import com.example.androidrecorddemo.core.toSecondsFormatted
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException

class MediaPlayerProvider : AudioPlayerProvider {

    private var _player: MediaPlayer? = null

    private val _playerStatusStateFlow = MutableStateFlow(
        PlayerProgress(
            percentage = 0f,
            currentSeconds = "",
            duration = ""
        )
    )

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun startPlaying(fileName: String) {
        try {
            _player = MediaPlayer()
            _player!!.setDataSource(fileName)
            _player!!.run {
                prepare()
                start()

                coroutineScope.launch { monitoring() }
            }
        } catch (ioException: IOException) {
            Log.e(TAG, "startPlaying: $ioException")
        }
    }

    private suspend fun monitoring() {
        while (_player != null && _player!!.isPlaying) {
            val percentage = (_player!!.currentPosition / _player!!.duration.toFloat())
            if (_player!!.isPlaying) {

                _playerStatusStateFlow.emit(
                    PlayerProgress(
                        percentage = percentage,
                        duration = _player!!.duration.toSecondsFormatted(),
                        currentSeconds = _player!!.currentPosition.toSecondsFormatted()
                    )
                )
            }
            delay(300L)
        }
    }

    override fun stopPlaying() {
        try {
            _player?.run {
                stop()
                release()
            }
        } catch (exception: Exception) {
            Log.e(TAG, "stopPlaying: $exception")
        } finally {
            coroutineScope.launch {
                _playerStatusStateFlow.emit(
                    PlayerProgress(
                        percentage = 100f,
                        currentSeconds = "",
                        duration = ""
                    )
                )
            }
            _player = null
        }
    }

    override fun playerStatus(): StateFlow<PlayerProgress> {
        return _playerStatusStateFlow
    }

    override fun release() {
        coroutineScope.cancel()
        _player = null
    }

    companion object {
        private const val TAG = "AudioPlayerManager"
    }
}