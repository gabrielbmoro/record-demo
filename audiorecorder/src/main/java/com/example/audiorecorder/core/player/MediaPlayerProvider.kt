package com.example.audiorecorder.core.player

import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException

class MediaPlayerProvider : AudioPlayerProvider {

    private var _player: MediaPlayer? = null

    private val _playerStatusStateFlow = MutableStateFlow(
        PlayerProgress(
            percentage = 0f,
            currentSeconds = 0,
            duration = 0
        )
    )

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun startPlaying(fileName: String): Boolean {
        return try {
            _player = MediaPlayer()
            _player!!.setDataSource(fileName)
            _player!!.run {
                prepare()
                start()

                setOnCompletionListener {
                    coroutineScope.launch {
                        _playerStatusStateFlow.emit(
                            PlayerProgress(
                                percentage = 100f,
                                duration = _player!!.duration,
                                currentSeconds = _player!!.duration
                            )
                        )
                    }
                }

                coroutineScope.launch { monitoring() }
                true
            }
        } catch (exception: Exception) {
            Log.e(TAG, "startPlaying: $exception")
            false
        }
    }

    private suspend fun monitoring() {
        while (_player != null && _player!!.isPlaying) {
            val percentage = (_player!!.currentPosition / _player!!.duration.toFloat())
            if (_player!!.isPlaying) {

                _playerStatusStateFlow.emit(
                    PlayerProgress(
                        percentage = percentage,
                        duration = _player!!.duration,
                        currentSeconds = _player!!.currentPosition
                    )
                )
            }
            delay(300L)
        }
    }

    override fun stopPlaying(): Boolean {
        return try {
            _player?.run {
                stop()
                release()
            }
            true
        } catch (exception: Exception) {
            Log.e(TAG, "stopPlaying: $exception")
            false
        } finally {
            coroutineScope.launch {
                _playerStatusStateFlow.emit(
                    PlayerProgress(
                        percentage = 100f,
                        currentSeconds = 0,
                        duration = 0
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