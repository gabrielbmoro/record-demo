package com.example.androidrecorddemo.core.util

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AndroidUtilProvider : UtilProvider {
    override fun fileCacheLocationFullPath(context: Context, fileName: String): String {
        return "${context.externalCacheDir!!.absolutePath}${File.separator}$fileName"
    }

    override fun convertToSecondsFormatted(duration: Int): String {
        return if (duration == 0) ""
        else {
            val simpleDateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
            val date = Calendar.getInstance().apply {
                timeInMillis = duration.toLong()
            }.time
            simpleDateFormat.format(date)
        }
    }
}