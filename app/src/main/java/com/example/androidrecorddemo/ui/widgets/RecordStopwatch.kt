package com.example.androidrecorddemo.ui.widgets

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.androidrecorddemo.ui.theme.RecordDemoTheme

@Composable
fun RecordStopwatch(duration: String, modifier: Modifier = Modifier) {
    Text(
        text = duration,
        style = TextStyle(
            color = Color.Red,
            fontWeight = FontWeight.Light,
            fontSize = 20.sp,
            letterSpacing = 2.sp
        ),
        modifier = modifier
    )
}

@Preview
@Composable
fun RecordStopwatchPreview() {
    RecordDemoTheme {
        RecordStopwatch("0:50")
    }
}