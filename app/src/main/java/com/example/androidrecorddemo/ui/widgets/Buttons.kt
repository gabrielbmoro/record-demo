package com.example.androidrecorddemo.ui.widgets

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidrecorddemo.R
import com.example.androidrecorddemo.ui.theme.RecordDemoTheme

enum class ButtonStatusType(@DrawableRes val iconRes: Int, val contentDescription: String) {
    PLAY(android.R.drawable.ic_media_play, "Play"),
    PAUSE(android.R.drawable.ic_media_pause, "Pause"),
    RECORD(R.drawable.ic_mic, "Record"),
    STOP(R.drawable.ic_stop, "Stop")
}

@Composable
fun MediaButton(
    modifier: Modifier = Modifier,
    buttonStatus: ButtonStatusType,
    enabled: Boolean,
    onClick: (() -> Unit),
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
    ) {
        Icon(
            painter = painterResource(id = buttonStatus.iconRes),
            modifier = Modifier.size(56.dp),
            contentDescription = buttonStatus.contentDescription,
            tint = if(enabled) Color.White else Color.Gray
        )
    }
}

@Preview
@Composable
fun PlayButtonPreview() {
    RecordDemoTheme {
        MediaButton(enabled = true, buttonStatus = ButtonStatusType.PLAY) {}
    }
}