package com.cinestreamtv.tv.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinestreamtv.core.domain.model.MediaItem
import com.cinestreamtv.core.domain.usecase.SearchContentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchContentUseCase: SearchContentUseCase
) : ViewModel() {

    data class SearchUiState(
        val query: String = "",
        val results: List<MediaItem> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        searchJob?.cancel()
        if (query.length >= 2) {
            searchJob = viewModelScope.launch {
                delay(500)
                performSearch(query)
            }
        } else {
            _uiState.value = _uiState.value.copy(results = emptyList())
        }
    }

    private suspend fun performSearch(query: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        searchContentUseCase(query)
            .onSuccess { results ->
                _uiState.value = _uiState.value.copy(isLoading = false, results = results)
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
            }
    }
}
