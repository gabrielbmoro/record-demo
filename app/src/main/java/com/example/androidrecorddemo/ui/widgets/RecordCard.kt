package com.example.androidrecorddemo.ui.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidrecorddemo.core.audiorecord.AudioEncoder
import com.example.androidrecorddemo.ui.theme.RecordDemoTheme

data class RecordCardContent(
    val currentEncoderSelected: DropDownValue<AudioEncoder>,
    val stopRecordButtonEnabled: Boolean,
    val recordButtonEnabled: Boolean,
)

@Composable
fun RecordCard(
    content: RecordCardContent,
    onStopRecord: (() -> Unit),
    onRecord: (() -> Unit),
    onEncoderSelectedChanged: ((DropDownValue<AudioEncoder>) -> Unit),
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
                text = "Record"
            )

            Spacer(modifier = Modifier.height(22.dp))

            CommonDropDown(
                modifier = Modifier.fillMaxWidth(),
                onValueChanged = onEncoderSelectedChanged,
                currentValue = content.currentEncoderSelected,
                options = AudioEncoder.values().toList(),
            )

            Row {
                MediaButton(
                    buttonStatus = ButtonStatusType.RECORD,
                    enabled = content.recordButtonEnabled,
                    onClick = onRecord,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                MediaButton(
                    buttonStatus = ButtonStatusType.STOP,
                    enabled = content.stopRecordButtonEnabled,
                    onClick = onStopRecord,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview
@Composable
fun RecordCardPreview() {
    RecordDemoTheme {
        RecordCard(
            RecordCardContent(
                recordButtonEnabled = true,
                stopRecordButtonEnabled = false,
                currentEncoderSelected = DropDownValue(
                    currentOption = AudioEncoder.AAC,
                    expanded = false,
                ),
            ),
            {},
            {},
            {}
        )
    }
}