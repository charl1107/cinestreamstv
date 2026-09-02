package com.cinestreamtv.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinestreamtv.core.domain.model.MediaType
import com.cinestreamtv.core.domain.model.StreamLink
import com.cinestreamtv.core.domain.model.WatchHistoryItem
import com.cinestreamtv.core.domain.usecase.GetStreamLinksUseCase
import com.cinestreamtv.core.domain.usecase.WatchHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val cineStreamPlayer: CineStreamPlayer,
    private val getStreamLinksUseCase: GetStreamLinksUseCase,
    private val watchHistoryUseCase: WatchHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    val playerState = cineStreamPlayer.playerState

    private var progressJob: Job? = null

    data class PlayerUiState(
        val isLoading: Boolean = false,
        val availableLinks: List<StreamLink> = emptyList(),
        val selectedLink: StreamLink? = null,
        val showControls: Boolean = true,
        val mediaTitle: String = "",
        val mediaId: String = "",
        val providerName: String = "",
        val error: String? = null
    )

    fun loadAndPlay(
        data: String,
        providerName: String,
        mediaTitle: String,
        mediaId: String,
        resumePosition: Long = 0L
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                mediaTitle = mediaTitle,
                mediaId = mediaId,
                providerName = providerName,
                error = null
            )

            getStreamLinksUseCase(data, providerName)
                .onSuccess { links ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        availableLinks = links
                    )
                    if (links.isNotEmpty()) {
                        val bestLink = links.maxByOrNull { it.quality.ordinal } ?: links.first()
                        selectAndPlay(bestLink, resumePosition)
                    }
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    fun selectAndPlay(link: StreamLink, resumePosition: Long = 0L) {
        _uiState.value = _uiState.value.copy(selectedLink = link)
        cineStreamPlayer.play(link, resumePosition)
        startProgressTracking()
    }

    fun togglePlayPause() = cineStreamPlayer.togglePlayPause()
    fun seekForward() = cineStreamPlayer.seekForward()
    fun seekBackward() = cineStreamPlayer.seekBackward()
    fun seekTo(position: Long) = cineStreamPlayer.seekTo(position)

    fun toggleControls() {
        _uiState.value = _uiState.value.copy(showControls = !_uiState.value.showControls)
    }

    fun showControls() {
        _uiState.value = _uiState.value.copy(showControls = true)
    }

    fun hideControls() {
        _uiState.value = _uiState.value.copy(showControls = false)
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive) {
                delay(5000)
                saveCurrentProgress()
            }
        }
    }

    private suspend fun saveCurrentProgress() {
        val state = _uiState.value
        if (state.mediaId.isNotEmpty()) {
            watchHistoryUseCase.saveProgress(
                WatchHistoryItem(
                    mediaId = state.mediaId,
                    title = state.mediaTitle,
                    posterUrl = null,
                    type = MediaType.MOVIE,
                    providerName = state.providerName,
                    lastPosition = cineStreamPlayer.getCurrentPosition(),
                    totalDuration = cineStreamPlayer.getDuration(),
                    lastWatched = System.currentTimeMillis()
                )
            )
        }
    }

    fun getPlayer() = cineStreamPlayer.getPlayer()

    override fun onCleared() {
        super.onCleared()
        progressJob?.cancel()
        viewModelScope.launch { saveCurrentProgress() }
        cineStreamPlayer.release()
    }
}
