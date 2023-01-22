package com.example.androidrecorddemo.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.androidrecorddemo.core.audioplayer.AudioPlayerProvider
import com.example.androidrecorddemo.core.audioplayer.PlayerProgress
import com.example.androidrecorddemo.core.audiorecord.AudioRecorderProvider
import com.example.androidrecorddemo.core.util.UtilProvider
import com.google.common.truth.Truth
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var audioRecorderProvider: AudioRecorderProvider
    private lateinit var audioPlayerProvider: AudioPlayerProvider
    private lateinit var utilsProvider: UtilProvider
    private lateinit var viewModel: MainViewModel

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun init() {
        this.audioRecorderProvider = mockk()
        this.audioPlayerProvider = mockk()
        this.utilsProvider = mockk()

        every { audioPlayerProvider.playerStatus() }.returns(
            MutableStateFlow(PlayerProgress(100f, 0, 0))
        )
        every { audioRecorderProvider.recorderTimeElapsed() }.returns(
            MutableStateFlow(0)
        )

        every {
            utilsProvider.fileCacheLocationFullPath(
                any(),
                any()
            )
        }.returns("file")
        every { utilsProvider.convertToSecondsFormatted(0) }.returns("")

        Dispatchers.setMain(mainThreadSurrogate)
        this.viewModel = MainViewModel(audioPlayerProvider, audioRecorderProvider, utilsProvider)
    }

    @Test
    fun `Start player success`() {
        runTest {
            // arrange
            every { audioPlayerProvider.startPlaying(any()) }.returns(true)

            // act
            viewModel.onPlay(mockk())

            // assert
            Truth.assertThat(viewModel.uiState.value.playerCardContent.playButtonEnabled).isFalse()
            Truth.assertThat(viewModel.uiState.value.playerCardContent.stopButtonEnabled).isTrue()
        }
    }

    @Test
    fun `Stop player success`() {
        runTest {
            // arrange
            every { audioPlayerProvider.stopPlaying() }.returns(true)
            every { audioPlayerProvider.startPlaying(any()) }.returns(true)
            viewModel.onPlay(mockk())

            // act
            viewModel.onStopPlayer()

            // assert
            Truth.assertThat(viewModel.uiState.value.playerCardContent.playButtonEnabled).isTrue()
            Truth.assertThat(viewModel.uiState.value.playerCardContent.stopButtonEnabled).isFalse()
        }
    }

    @Test
    fun `Stop player failed`() {
        runTest {
            // arrange
            every { audioPlayerProvider.stopPlaying() }.returns(false)
            every { audioPlayerProvider.startPlaying(any()) }.returns(true)
            viewModel.onPlay(mockk())

            // act
            viewModel.onStopPlayer()

            // assert
            Truth.assertThat(viewModel.uiState.value.playerCardContent.playButtonEnabled).isFalse()
            Truth.assertThat(viewModel.uiState.value.playerCardContent.stopButtonEnabled).isTrue()
        }
    }

    @Test
    fun `Stop recording success`() {
        runTest {
            // arrange
            every { audioRecorderProvider.startRecording(any(), any()) }.returns(true)
            every { audioRecorderProvider.stopRecording() }.returns(true)
            viewModel.onRecord(mockk())

            // act
            viewModel.onStopRecord()

            // assert
            Truth.assertThat(viewModel.uiState.value.recordCardContent.recordButtonEnabled).isTrue()
            Truth.assertThat(viewModel.uiState.value.recordCardContent.stopRecordButtonEnabled)
                .isFalse()
        }
    }

    @Test
    fun `Stop recording failed`() {
        runTest {
            // arrange
            every { audioRecorderProvider.startRecording(any(), any()) }.returns(true)
            every { audioRecorderProvider.stopRecording() }.returns(false)
            viewModel.onRecord(mockk())

            // act
            viewModel.onStopRecord()

            // assert
            Truth.assertThat(viewModel.uiState.value.recordCardContent.recordButtonEnabled)
                .isFalse()
            Truth.assertThat(viewModel.uiState.value.recordCardContent.stopRecordButtonEnabled)
                .isTrue()
        }
    }

    @Test
    fun `Start recording success`() {
        runTest {
            // arrange
            every { audioRecorderProvider.startRecording(any(), any()) }.returns(true)

            // act
            viewModel.onRecord(mockk())

            // assert
            Truth.assertThat(viewModel.uiState.value.recordCardContent.recordButtonEnabled)
                .isFalse()
            Truth.assertThat(viewModel.uiState.value.recordCardContent.stopRecordButtonEnabled)
                .isTrue()
        }
    }
}