package com.ahxp.iptvclient.ui.player

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class) // Media3 API is currently marked unstable but safe to use
@Composable
fun PlayerScreen(
    streamId: String,
    type: String = "live",
    extension: String = "",
    viewModel: PlayerViewModel = viewModel()
) {
    val context = LocalContext.current
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    
    // Get the full stream URL
    val streamUrl = remember(streamId, type, extension) { 
        viewModel.getStreamUrl(streamId, type, extension)
    }

    // Initialize Player
    LaunchedEffect(streamUrl) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri(streamUrl)
                setMediaItem(mediaItem)

                // Seek to saved position
                val savedPosition = viewModel.getSavedPosition(streamId)
                if (savedPosition > 0) {
                    seekTo(savedPosition)
                }

                prepare()
                playWhenReady = true
            }
        }
    }

    // Save position periodically
    LaunchedEffect(exoPlayer) {
        exoPlayer?.let {
            while (true) {
                if (it.isPlaying) {
                    viewModel.savePosition(streamId, it.currentPosition)
                }
                delay(10_000) // Save every 10 seconds
            }
        }
    }

    // Cleanup Player and save final position on Dispose
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer?.let {
                // Save final position
                viewModel.savePosition(streamId, it.currentPosition)
                it.release()
            }
            exoPlayer = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    useController = true // Show play/pause controls
                }
            },
            update = { playerView ->
                playerView.player = exoPlayer
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
