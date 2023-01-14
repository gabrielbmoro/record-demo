package com.example.androidrecorddemo.ui

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.androidrecorddemo.core.hasRecordAudioPermission
import com.example.androidrecorddemo.ui.theme.RecordDemoTheme
import com.example.androidrecorddemo.ui.widgets.*

class MainActivity : ComponentActivity(), ActivityResultCallback<Boolean> {

    private lateinit var launcher: ActivityResultLauncher<String>
    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecordDemoTheme {
                MainScreen(viewModel)
            }
        }

        if (hasRecordAudioPermission().not()) {
            launcher = registerForActivityResult(
                ActivityResultContracts.RequestPermission(),
                this
            )
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    override fun onActivityResult(result: Boolean?) {
        if (result != true) {
            Toast.makeText(this, "Permissions are required", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {

    val uiState = remember { viewModel.uiState }
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if (uiState.value.recordAvailable) {
                Spacer(modifier = Modifier.height(22.dp))

                RecordCard(
                    content = uiState.value.recordCardContent,
                    onEncoderSelectedChanged = {
                        viewModel.onEncoderSelectedChange(it)
                    },
                    onStopRecord = {
                        viewModel.onStopRecord()
                    },
                    onRecord = {
                        viewModel.onRecord(context)
                    }
                )
            }

            if (uiState.value.playBackAvailable) {
                Spacer(modifier = Modifier.height(22.dp))

                PlayerCard(
                    playerCardContent = uiState.value.playerCardContent,
                    onPlay = {
                        viewModel.onPlay(context)
                    },
                    onPause = {
                        viewModel.onPause()
                    }
                )
            }
        }
    }
}