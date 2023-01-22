package com.example.androidrecorddemo.ui

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.androidrecorddemo.core.audioplayer.AudioPlayerProvider
import com.example.androidrecorddemo.core.audioplayer.MediaPlayerProvider
import com.example.androidrecorddemo.core.audioplayer.PlayerProgress
import com.example.androidrecorddemo.core.audiorecord.*
import com.example.androidrecorddemo.core.util.AndroidUtilProvider
import com.example.androidrecorddemo.core.util.UtilProvider
import com.example.androidrecorddemo.ui.widgets.DropDownValue
import com.example.androidrecorddemo.ui.widgets.PlayerCardContent
import com.example.androidrecorddemo.ui.widgets.PlayerLineArg
import com.example.androidrecorddemo.ui.widgets.RecordCardContent
import kotlinx.coroutines.launch

class MainViewModel(
    private val audioPlayerProvider: AudioPlayerProvider,
    private val audioRecorderProvider: AudioRecorderProvider,
    private val utilProvider: UtilProvider,
) : ViewModel() {

    private val _uiState = mutableStateOf(
        MainUIState(
            playBackAvailable = false,
            recordAvailable = true,
            playerCardContent = PlayerCardContent(
                stopButtonEnabled = false,
                playButtonEnabled = false,
                playerLineArg = PlayerLineArg(
                    percentage = 0f,
                    durationTimestamp = "",
                    currentTimestamp = ""
                )
            ),
            recordCardContent = RecordCardContent(
                currentEncoderSelected = DropDownValue(
                    currentOption = AudioEncoder.AAC,
                    expanded = false,
                ),
                recordButtonEnabled = true,
                stopRecordButtonEnabled = false,
                duration = ""
            )
        )
    )
    val uiState: State<MainUIState> = _uiState

    init {
        viewModelScope.launch {
            audioPlayerProvider.playerStatus().collect { playerProgress ->
                onPlayerStatusChanged(playerProgress)
            }
        }

        viewModelScope.launch {
            audioRecorderProvider.recorderTimeElapsed().collect { timeElapsedInMs ->
                val duration = utilProvider.convertToSecondsFormatted(timeElapsedInMs)
                _uiState.value = _uiState.value.copy(
                    recordCardContent = _uiState.value.recordCardContent.copy(
                        duration = duration
                    )
                )
            }
        }
    }

    private fun onPlayerStatusChanged(playerProgress: PlayerProgress) {
        val duration = utilProvider.convertToSecondsFormatted(playerProgress.duration)
        val current = if (playerProgress.percentage < 100f) utilProvider.convertToSecondsFormatted(
            playerProgress.currentSeconds
        )
        else duration

        _uiState.value = _uiState.value.copy(
            playerCardContent = _uiState.value.playerCardContent.copy(
                stopButtonEnabled = playerProgress.percentage in 0f..99.9f,
                playButtonEnabled = (playerProgress.percentage == 0f) || (playerProgress.percentage == 100f),
                playerLineArg = _uiState.value.playerCardContent.playerLineArg.copy(
                    percentage = playerProgress.percentage,
                    currentTimestamp = current,
                    durationTimestamp = duration
                )
            )
        )
    }

    fun onPlay(context: Context, errorCallback: (() -> Unit) = {}) {
        val success = audioPlayerProvider.startPlaying(
            utilProvider.fileCacheLocationFullPath(
                context,
                OUTPUT_FILE_NAME_WITH_EXTENSION
            )
        )

        if (success) {
            _uiState.value = _uiState.value.copy(
                playerCardContent = _uiState.value.playerCardContent.copy(
                    playButtonEnabled = false,
                    stopButtonEnabled = true
                )
            )
        } else {
            errorCallback()
        }
    }

    fun onStopPlayer(errorCallback: (() -> Unit) = {}) {
        val success = audioPlayerProvider.stopPlaying()

        if (success) {
            _uiState.value = _uiState.value.copy(
                playerCardContent = _uiState.value.playerCardContent.copy(
                    playButtonEnabled = true,
                    stopButtonEnabled = false
                ),
            )
        } else run {
            errorCallback()
        }
    }

    fun onRecord(context: Context, errorCallback: () -> Unit = {}) {
        val recordArgument = RecordArgument(
            outputFile = utilProvider.fileCacheLocationFullPath(
                context,
                OUTPUT_FILE_NAME_WITH_EXTENSION
            ),
            audioEncoder = _uiState.value.recordCardContent.currentEncoderSelected.currentOption,
            audioSource = AudioSourceType.MIC,
            outputFormat = AudioOutputFormat.THREE_GPP
        )
        val success = audioRecorderProvider.startRecording(context, recordArgument)

        if (success) {
            _uiState.value = _uiState.value.copy(
                playBackAvailable = false,
                recordCardContent = _uiState.value.recordCardContent.copy(
                    recordButtonEnabled = false,
                    stopRecordButtonEnabled = true,
                    currentEncoderSelected = _uiState.value.recordCardContent.currentEncoderSelected.copy(
                        enabled = false
                    )
                )
            )
        } else run {
            errorCallback()
        }
    }

    fun onEncoderSelectedChange(it: DropDownValue<AudioEncoder>) {
        _uiState.value = _uiState.value.copy(
            recordCardContent = _uiState.value.recordCardContent.copy(
                currentEncoderSelected = it
            )
        )
    }

    fun onStopRecord(errorCallback: (() -> Unit) = {}) {
        val success = audioRecorderProvider.stopRecording()

        if (success) {
            _uiState.value = _uiState.value.copy(
                playBackAvailable = true,
                recordCardContent = _uiState.value.recordCardContent.copy(
                    recordButtonEnabled = true,
                    stopRecordButtonEnabled = false,
                    currentEncoderSelected = _uiState.value.recordCardContent.currentEncoderSelected.copy(
                        enabled = true
                    )
                ),
                playerCardContent = _uiState.value.playerCardContent.copy(
                    playButtonEnabled = true,
                    stopButtonEnabled = false,
                    playerLineArg = PlayerLineArg(
                        0f,
                        "",
                        ""
                    )
                )
            )
        } else run {
            errorCallback()
        }
    }

    override fun onCleared() {
        audioRecorderProvider.release()
        audioPlayerProvider.release()

        super.onCleared()
    }

    companion object {
        const val OUTPUT_FILE_NAME_WITH_EXTENSION = "audiorecordtest.3gp"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                return MainViewModel(
                    audioPlayerProvider = MediaPlayerProvider(),
                    audioRecorderProvider = MediaRecorderProvider(),
                    utilProvider = AndroidUtilProvider()
                ) as T
            }
        }
    }
}