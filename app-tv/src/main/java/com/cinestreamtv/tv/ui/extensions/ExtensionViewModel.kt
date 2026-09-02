package com.cinestreamtv.tv.ui.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinestreamtv.extension.ExtensionManager
import com.cinestreamtv.extension.repo.PluginEntry
import com.cinestreamtv.extension.repo.RepoConfig
import com.cinestreamtv.extension.repo.RepoManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExtensionViewModel @Inject constructor(
    private val extensionManager: ExtensionManager,
    private val repoManager: RepoManager
) : ViewModel() {

    data class ExtensionUiState(
        val isLoading: Boolean = false,
        val availablePlugins: List<PluginEntry> = emptyList(),
        val repos: List<RepoConfig> = emptyList(),
        val installingPlugin: String? = null,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(ExtensionUiState())
    val uiState: StateFlow<ExtensionUiState> = _uiState.asStateFlow()

    init {
        refreshRepos()
        observePlugins()
        refreshPlugins()
    }

    private fun observePlugins() {
        viewModelScope.launch {
            extensionManager.availablePlugins.collect { plugins ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    availablePlugins = plugins
                )
            }
        }
    }

    fun refreshPlugins() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                extensionManager.refreshAvailablePlugins()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun installPlugin(entry: PluginEntry) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(installingPlugin = entry.internalName)
            extensionManager.installExtension(entry)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(installingPlugin = null)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        installingPlugin = null,
                        error = "Failed to install ${entry.name}: ${error.message}"
                    )
                }
        }
    }

    fun uninstallPlugin(internalName: String) {
        extensionManager.uninstallExtension(internalName)
    }

    fun isInstalled(internalName: String): Boolean {
        return extensionManager.isInstalled(internalName)
    }

    fun addRepo(url: String, name: String) {
        repoManager.addRepo(RepoConfig(name = name, url = url))
        refreshRepos()
        refreshPlugins()
    }

    fun removeRepo(url: String) {
        repoManager.removeRepo(url)
        refreshRepos()
    }

    private fun refreshRepos() {
        _uiState.value = _uiState.value.copy(repos = repoManager.getRepos())
    }
}
