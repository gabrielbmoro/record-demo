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
                false,
                false,
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
                stopRecordButtonEnabled = false
            )
        )
    )
    val uiState: State<MainUIState> = _uiState

    init {
        viewModelScope.launch {
            audioPlayerProvider.playerStatus().collect { playerProgress ->
                _uiState.value = _uiState.value.copy(
                    playerCardContent = _uiState.value.playerCardContent.copy(
                        playerLineArg = _uiState.value.playerCardContent.playerLineArg.copy(
                            percentage = playerProgress.percentage,
                            currentTimestamp = if (playerProgress.percentage == 100f)
                                utilProvider.convertToSecondsFormatted(playerProgress.duration)
                            else utilProvider.convertToSecondsFormatted(playerProgress.currentSeconds),
                            durationTimestamp = utilProvider.convertToSecondsFormatted(playerProgress.duration)
                        )
                    )
                )
            }
        }
    }

    fun onPlay(context: Context) {
        _uiState.value = _uiState.value.copy(
            playerCardContent = _uiState.value.playerCardContent.copy(
                playButtonEnabled = false,
                stopButtonEnabled = true
            )
        )
        audioPlayerProvider.startPlaying(
            utilProvider.fileCacheLocationFullPath(
                context,
                OUTPUT_FILE_NAME_WITH_EXTENSION
            )
        )
    }

    fun onStopPlayer() {
        _uiState.value = _uiState.value.copy(
            playerCardContent = _uiState.value.playerCardContent.copy(
                playButtonEnabled = true,
                stopButtonEnabled = false
            ),
        )

        audioPlayerProvider.stopPlaying()
    }

    fun onRecord(context: Context) {
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

        val recordArgument = RecordArgument(
            outputFile = utilProvider.fileCacheLocationFullPath(context, OUTPUT_FILE_NAME_WITH_EXTENSION),
            audioEncoder = _uiState.value.recordCardContent.currentEncoderSelected.currentOption,
            audioSource = AudioSourceType.MIC,
            outputFormat = AudioOutputFormat.THREE_GPP
        )
        audioRecorderProvider.startRecording(context, recordArgument)
    }

    fun onEncoderSelectedChange(it: DropDownValue<AudioEncoder>) {
        _uiState.value = _uiState.value.copy(
            recordCardContent = _uiState.value.recordCardContent.copy(
                currentEncoderSelected = it
            )
        )
    }

    fun onStopRecord() {
        audioRecorderProvider.stopRecording()

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
                playerLineArg = PlayerLineArg(
                    0f,
                    "",
                    ""
                )
            )
        )
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