package com.example.androidrecorddemo.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.mutableStateOf
import com.example.androidrecorddemo.core.audioplayer.AudioPlayerProvider
import com.example.androidrecorddemo.core.audioplayer.AudioPlayerStatus
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

@OptIn(DelicateCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var audioRecorderProvider: AudioRecorderProvider
    private lateinit var audioPlayerProvider: AudioPlayerProvider
    private lateinit var utilsProvider: UtilProvider

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun init() {
        this.audioRecorderProvider = mockk()
        this.audioPlayerProvider = mockk()
        this.utilsProvider = mockk()

        every { audioPlayerProvider.playerStatus() }.returns(
            MutableStateFlow(PlayerProgress(100f, 0, 0))
        )
        every { utilsProvider.convertToSecondsFormatted(0) }.returns("")

        Dispatchers.setMain(mainThreadSurrogate)
    }

    @Test
    fun `Test playing stuff`() {
        runTest {
            // arrange
            every { utilsProvider.fileCacheLocationFullPath(any(), MainViewModel.OUTPUT_FILE_NAME_WITH_EXTENSION) }.returns("filePath/${MainViewModel.OUTPUT_FILE_NAME_WITH_EXTENSION}")
            every { audioPlayerProvider.startPlaying("filePath/${MainViewModel.OUTPUT_FILE_NAME_WITH_EXTENSION}") }.returns(Unit)

            val viewModel = MainViewModel(audioPlayerProvider, audioRecorderProvider, utilsProvider)

            // act
            viewModel.onPlay(mockk())

            // assert
            every { audioPlayerProvider.startPlaying(MainViewModel.OUTPUT_FILE_NAME_WITH_EXTENSION) }
            Truth.assertThat(viewModel.uiState.value.playerCardContent.playButtonEnabled).isFalse()
            Truth.assertThat(viewModel.uiState.value.playerCardContent.pauseButtonEnabled).isTrue()
        }
    }
}