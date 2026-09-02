package com.cinestreamtv.tv.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinestreamtv.core.domain.model.MediaDetails
import com.cinestreamtv.core.domain.usecase.GetMediaDetailsUseCase
import com.cinestreamtv.core.domain.usecase.ManageBookmarksUseCase
import com.cinestreamtv.core.domain.model.BookmarkItem
import com.cinestreamtv.core.domain.model.MediaType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getMediaDetailsUseCase: GetMediaDetailsUseCase,
    private val manageBookmarksUseCase: ManageBookmarksUseCase
) : ViewModel() {

    data class DetailUiState(
        val isLoading: Boolean = true,
        val details: MediaDetails? = null,
        val isBookmarked: Boolean = false,
        val selectedSeason: Int = 1,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadDetails(url: String, providerName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getMediaDetailsUseCase(url, providerName)
                .onSuccess { details ->
                    val isBookmarked = manageBookmarksUseCase.isBookmarked(details.item.id)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        details = details,
                        isBookmarked = isBookmarked
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            val details = _uiState.value.details ?: return@launch
            val item = details.item
            if (_uiState.value.isBookmarked) {
                manageBookmarksUseCase.removeBookmark(item.id)
            } else {
                manageBookmarksUseCase.addBookmark(
                    BookmarkItem(
                        mediaId = item.id,
                        title = item.title,
                        posterUrl = item.posterUrl,
                        type = item.type,
                        providerName = item.providerName,
                        url = item.url,
                        addedAt = System.currentTimeMillis()
                    )
                )
            }
            _uiState.value = _uiState.value.copy(isBookmarked = !_uiState.value.isBookmarked)
        }
    }

    fun selectSeason(season: Int) {
        _uiState.value = _uiState.value.copy(selectedSeason = season)
    }
}
