package com.cinestreamtv.tv.ui.player

import android.view.KeyEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.ui.PlayerView
import androidx.tv.material3.*
import com.cinestreamtv.core.common.utils.toReadableTime
import com.cinestreamtv.player.PlayerViewModel

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PlayerScreen(
    providerName: String,
    data: String,
    title: String,
    mediaId: String,
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val playerState by viewModel.playerState.collectAsState()

    LaunchedEffect(data) {
        viewModel.loadAndPlay(
            data = data,
            providerName = providerName,
            mediaTitle = title,
            mediaId = mediaId
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .onKeyEvent { keyEvent ->
                if (keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                    when (keyEvent.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                            if (uiState.showControls) viewModel.togglePlayPause()
                            else viewModel.showControls()
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            viewModel.seekBackward()
                            viewModel.showControls()
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            viewModel.seekForward()
                            viewModel.showControls()
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN -> {
                            viewModel.toggleControls()
                            true
                        }
                        KeyEvent.KEYCODE_BACK -> {
                            onBack()
                            true
                        }
                        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                            viewModel.togglePlayPause()
                            true
                        }
                        else -> false
                    }
                } else false
            }
            .focusable()
    ) {
        // ExoPlayer Surface
        val player = viewModel.getPlayer()
        if (player != null) {
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        this.player = player
                        useController = false
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Loading overlay
        if (uiState.isLoading || playerState.isBuffering) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...", color = Color.White, style = MaterialTheme.typography.headlineMedium)
            }
        }

        // Controls overlay
        AnimatedVisibility(
            visible = uiState.showControls,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                // Title at top
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(24.dp)
                )

                // Playback controls at bottom
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Progress bar
                    val progress = if (playerState.duration > 0) {
                        playerState.currentPosition.toFloat() / playerState.duration.toFloat()
                    } else 0f

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(Color.Gray.copy(alpha = 0.4f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .background(Color.Red)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = playerState.currentPosition.toReadableTime(),
                            color = Color.White
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            Button(onClick = { viewModel.seekBackward() }) { Text("⏪ 10s") }
                            Button(onClick = { viewModel.togglePlayPause() }) {
                                Text(if (playerState.isPlaying) "⏸ Pause" else "▶ Play")
                            }
                            Button(onClick = { viewModel.seekForward() }) { Text("10s ⏩") }
                        }
                        Text(
                            text = playerState.duration.toReadableTime(),
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Error
        uiState.error?.let { error ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = error, color = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBack) { Text("Go Back") }
                }
            }
        }
    }
}
