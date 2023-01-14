package com.example.androidrecorddemo.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DropDownValue<T>(
    val currentOption: T,
    val expanded: Boolean
)

@Composable
fun <T> CommonDropDown(
    modifier: Modifier = Modifier,
    options: List<T>,
    currentValue: DropDownValue<T>,
    onValueChanged: (DropDownValue<T>) -> Unit,
) {
    Box(modifier) {
        OutlinedButton(
            modifier = modifier
                .align(alignment = Alignment.CenterStart)
                .padding(vertical = 12.dp),
            onClick = { onValueChanged(currentValue.copy(expanded = true)) }) {
            Text(
                text = currentValue.currentOption.toString(),
                fontSize = 12.sp
            )
        }

        DropdownMenu(
            expanded = currentValue.expanded,
            onDismissRequest = { onValueChanged(currentValue.copy(expanded = false)) }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onValueChanged(
                            currentValue.copy(
                                currentOption = option,
                                expanded = false
                            )
                        )
                    },
                    text = {
                        Text(
                            option.toString(),
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }
    }
}