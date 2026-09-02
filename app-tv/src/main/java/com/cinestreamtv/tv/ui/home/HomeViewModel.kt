package com.cinestreamtv.tv.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinestreamtv.core.domain.model.HomePageData
import com.cinestreamtv.core.domain.model.WatchHistoryItem
import com.cinestreamtv.core.domain.usecase.GetHomePageUseCase
import com.cinestreamtv.core.domain.usecase.WatchHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomePageUseCase: GetHomePageUseCase,
    private val watchHistoryUseCase: WatchHistoryUseCase
) : ViewModel() {

    data class HomeUiState(
        val isLoading: Boolean = true,
        val homePageData: HomePageData = HomePageData(),
        val continueWatching: List<WatchHistoryItem> = emptyList(),
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomePage()
        observeWatchHistory()
    }

    fun loadHomePage() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getHomePageUseCase()
                .onSuccess { data ->
                    _uiState.value = _uiState.value.copy(isLoading = false, homePageData = data)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                }
        }
    }

    private fun observeWatchHistory() {
        viewModelScope.launch {
            watchHistoryUseCase.getWatchHistory().collect { history ->
                _uiState.value = _uiState.value.copy(continueWatching = history)
            }
        }
    }
}
