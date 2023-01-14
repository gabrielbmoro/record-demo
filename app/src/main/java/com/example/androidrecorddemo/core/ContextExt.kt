package com.example.androidrecorddemo.core

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import java.io.File

fun Context.fileCacheLocationFullPath(fileName: String) =
    "${externalCacheDir!!.absolutePath}${File.separator}$fileName"

fun Context.hasRecordAudioPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED
}