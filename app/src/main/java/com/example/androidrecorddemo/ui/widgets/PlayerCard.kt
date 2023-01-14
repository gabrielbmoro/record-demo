package com.example.androidrecorddemo.ui.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidrecorddemo.ui.theme.RecordDemoTheme

data class PlayerCardContent(
    val pauseButtonEnabled: Boolean,
    val playButtonEnabled: Boolean,
    val playerLineArg: PlayerLineArg
)

@Composable
fun PlayerCard(
    playerCardContent: PlayerCardContent,
    onPause: (() -> Unit),
    onPlay: (() -> Unit),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                ),
                text = "Play recording"
            )

            Spacer(modifier = Modifier.height(22.dp))

            PlayerLine(playerLineArg = playerCardContent.playerLineArg)

            Row {
                MediaButton(
                    buttonStatus = ButtonStatusType.PAUSE,
                    enabled = playerCardContent.pauseButtonEnabled,
                    onClick = onPause
                )

                Spacer(modifier = Modifier.width(8.dp))

                MediaButton(
                    buttonStatus = ButtonStatusType.PLAY,
                    enabled = playerCardContent.playButtonEnabled,
                    onClick = onPlay
                )
            }
        }
    }
}

@Preview
@Composable
fun PlayerCardPreview() {
    RecordDemoTheme {
        PlayerCard(
            PlayerCardContent(
                playButtonEnabled = true,
                pauseButtonEnabled = false,
                playerLineArg = PlayerLineArg(
                    0.5f,
                    "2:00",
                    "0:00"
                )
            ),
            {},
            {}
        )
    }
}