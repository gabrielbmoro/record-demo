package com.example.androidrecorddemo.ui.widgets

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidrecorddemo.ui.theme.Orange
import com.example.androidrecorddemo.ui.theme.RecordDemoTheme

data class PlayerLineArg(
    val percentage: Float,
    val durationTimestamp: String,
    val currentTimestamp: String,
)

@Composable
fun PlayerLine(playerLineArg: PlayerLineArg, modifier: Modifier = Modifier) {

    val progressAnimation by animateFloatAsState(
        targetValue = playerLineArg.percentage,
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearEasing
        )
    )
    Column(modifier = modifier) {
        LinearProgressIndicator(
            progress = progressAnimation,
            modifier = Modifier
                .fillMaxWidth()
                .size(8.dp)
                .clip(RoundedCornerShape(16.dp)),
            color = Orange,
            trackColor = Color.DarkGray
        )

        Spacer(
            modifier = Modifier
                .height(8.dp)
        )

        Row {
            Text(
                text = playerLineArg.currentTimestamp,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.weight(0.5f),
                textAlign = TextAlign.Start
            )

            Text(
                text = playerLineArg.durationTimestamp,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(0.5f),
                textAlign = TextAlign.End
            )
        }
    }
}

@Preview
@Composable
fun PlayerLinePreview() {
    RecordDemoTheme {
        PlayerLine(
            PlayerLineArg(
                0.5f,
                "0:00",
                "2:00"
            )
        )
    }
}