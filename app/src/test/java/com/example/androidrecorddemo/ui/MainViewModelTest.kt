package com.example.androidrecorddemo.ui

import android.content.Context
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
    private val context: Context = mockk()

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
    }

    @Test
    fun `Start player`() {
        runTest {
            // arrange
            every { audioPlayerProvider.startPlaying(any()) }.returns(Unit)

            val viewModel = MainViewModel(audioPlayerProvider, audioRecorderProvider, utilsProvider)

            // act
            viewModel.onPlay(mockk())

            // assert
            Truth.assertThat(viewModel.uiState.value.playerCardContent.playButtonEnabled).isFalse()
            Truth.assertThat(viewModel.uiState.value.playerCardContent.stopButtonEnabled).isTrue()
        }
    }

    @Test
    fun `Stop player`() {
        runTest {
            // arrange
            every { audioPlayerProvider.stopPlaying() }.returns(Unit)

            val viewModel = MainViewModel(audioPlayerProvider, audioRecorderProvider, utilsProvider)

            // act
            viewModel.onStopPlayer()

            // assert
            Truth.assertThat(viewModel.uiState.value.playerCardContent.playButtonEnabled).isTrue()
            Truth.assertThat(viewModel.uiState.value.playerCardContent.stopButtonEnabled).isFalse()
        }
    }

    @Test
    fun `Stop recording`() {
        runTest {
            // arrange
            every { audioRecorderProvider.stopRecording() }.returns(Unit)

            val viewModel = MainViewModel(audioPlayerProvider, audioRecorderProvider, utilsProvider)

            // act
            viewModel.onStopRecord()

            // assert
            Truth.assertThat(viewModel.uiState.value.recordCardContent.recordButtonEnabled).isTrue()
            Truth.assertThat(viewModel.uiState.value.recordCardContent.stopRecordButtonEnabled)
                .isFalse()
        }
    }

    @Test
    fun `Start recording`() {
        runTest {
            // arrange
            every { audioRecorderProvider.startRecording(context, any()) }.returns(Unit)

            val viewModel = MainViewModel(audioPlayerProvider, audioRecorderProvider, utilsProvider)

            // act
            viewModel.onRecord(context)

            // assert
            Truth.assertThat(viewModel.uiState.value.recordCardContent.recordButtonEnabled)
                .isFalse()
            Truth.assertThat(viewModel.uiState.value.recordCardContent.stopRecordButtonEnabled)
                .isTrue()
        }
    }
}