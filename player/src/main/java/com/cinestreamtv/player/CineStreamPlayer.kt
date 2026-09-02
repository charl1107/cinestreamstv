package com.cinestreamtv.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.cinestreamtv.core.common.utils.Constants
import com.cinestreamtv.core.domain.model.StreamLink
import com.cinestreamtv.core.domain.model.SubtitleTrack
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(UnstableApi::class)
@Singleton
class CineStreamPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var exoPlayer: ExoPlayer? = null

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    data class PlayerState(
        val isPlaying: Boolean = false,
        val currentPosition: Long = 0L,
        val duration: Long = 0L,
        val bufferedPercentage: Int = 0,
        val isBuffering: Boolean = false,
        val error: String? = null,
        val currentQuality: String = "Auto",
        val playbackSpeed: Float = 1.0f
    )

    fun initialize(): ExoPlayer {
        release()
        val player = ExoPlayer.Builder(context)
            .setHandleAudioBecomingNoisy(true)
            .build()
            .apply {
                playWhenReady = true
                addListener(playerListener)
            }
        exoPlayer = player
        return player
    }

    fun play(streamLink: StreamLink, startPosition: Long = 0L) {
        val player = exoPlayer ?: initialize()

        val dataSourceFactory = DefaultHttpDataSource.Factory().apply {
            setDefaultRequestProperties(streamLink.headers)
            if (streamLink.referer != null) {
                setDefaultRequestProperties(mapOf("Referer" to streamLink.referer))
            }
        }

        val mediaSource = when {
            streamLink.isM3U8 -> {
                HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(streamLink.url))
            }
            streamLink.isDash -> {
                DashMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(streamLink.url))
            }
            else -> {
                ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(streamLink.url))
            }
        }

        player.setMediaSource(mediaSource)
        player.prepare()
        if (startPosition > 0) {
            player.seekTo(startPosition)
        }
    }

    fun addSubtitle(subtitle: SubtitleTrack) {
        val player = exoPlayer ?: return
        val currentItem = player.currentMediaItem ?: return

        val subtitleConfig = MediaItem.SubtitleConfiguration.Builder(android.net.Uri.parse(subtitle.url))
            .setMimeType(subtitle.mimeType ?: MimeTypes.APPLICATION_SUBRIP)
            .setLanguage(subtitle.language)
            .setLabel(subtitle.label ?: subtitle.language)
            .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
            .build()

        val updatedItem = currentItem.buildUpon()
            .setSubtitleConfigurations(listOf(subtitleConfig))
            .build()

        val position = player.currentPosition
        player.setMediaItem(updatedItem)
        player.prepare()
        player.seekTo(position)
    }

    fun togglePlayPause() {
        exoPlayer?.let { player ->
            if (player.isPlaying) player.pause() else player.play()
        }
    }

    fun seekForward() {
        exoPlayer?.let { player ->
            player.seekTo(player.currentPosition + Constants.DEFAULT_SEEK_INCREMENT_MS)
        }
    }

    fun seekBackward() {
        exoPlayer?.let { player ->
            val newPosition = (player.currentPosition - Constants.DEFAULT_SEEK_BACK_MS).coerceAtLeast(0)
            player.seekTo(newPosition)
        }
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }

    fun setPlaybackSpeed(speed: Float) {
        exoPlayer?.setPlaybackSpeed(speed)
        _playerState.value = _playerState.value.copy(playbackSpeed = speed)
    }

    fun getCurrentPosition(): Long = exoPlayer?.currentPosition ?: 0L
    fun getDuration(): Long = exoPlayer?.duration ?: 0L

    fun release() {
        exoPlayer?.apply {
            removeListener(playerListener)
            stop()
            release()
        }
        exoPlayer = null
    }

    fun getPlayer(): ExoPlayer? = exoPlayer

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updateState { copy(isPlaying = isPlaying) }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            updateState {
                copy(
                    isBuffering = playbackState == Player.STATE_BUFFERING,
                    duration = exoPlayer?.duration ?: 0L
                )
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            updateState { copy(error = error.message) }
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            updateState { copy(currentPosition = newPosition.positionMs) }
        }
    }

    private fun updateState(update: PlayerState.() -> PlayerState) {
        _playerState.value = _playerState.value.update()
    }
}
