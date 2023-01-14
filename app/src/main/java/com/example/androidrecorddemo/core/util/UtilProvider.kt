package com.example.androidrecorddemo.core.util

import android.content.Context

interface UtilProvider {
    fun fileCacheLocationFullPath(context: Context, fileName: String): String
    fun convertToSecondsFormatted(duration: Int): String
}