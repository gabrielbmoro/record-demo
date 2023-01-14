package com.example.androidrecorddemo.core.audiorecord

import android.media.MediaRecorder

enum class AudioSourceType(val value: Int) {
    DEFAULT(MediaRecorder.AudioSource.DEFAULT),
    MIC(MediaRecorder.AudioSource.MIC),
}

enum class AudioEncoder(val value: Int) {
    AAC(MediaRecorder.AudioEncoder.AAC),
    AAC_ELD(MediaRecorder.AudioEncoder.AAC_ELD),
    AMR_NB(MediaRecorder.AudioEncoder.AMR_NB),
    AMR_WB(MediaRecorder.AudioEncoder.AMR_WB),
    DEFAULT(MediaRecorder.AudioEncoder.DEFAULT),
    HE_AAC(MediaRecorder.AudioEncoder.HE_AAC),
    VORBIS(MediaRecorder.AudioEncoder.VORBIS)
}

enum class AudioOutputFormat(val value: Int) {
    THREE_GPP(MediaRecorder.OutputFormat.THREE_GPP)
}

data class RecordArgument(
    val outputFile: String,
    val audioSource: AudioSourceType = AudioSourceType.MIC,
    val audioEncoder: AudioEncoder,
    val outputFormat: AudioOutputFormat = AudioOutputFormat.THREE_GPP
)
