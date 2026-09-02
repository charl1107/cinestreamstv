package com.cinestreamtv.extension

import com.cinestreamtv.extension.api.MainAPI
import com.cinestreamtv.extension.loader.PluginLoader
import com.cinestreamtv.extension.registry.ProviderRegistry
import com.cinestreamtv.extension.repo.InstalledPlugin
import com.cinestreamtv.extension.repo.PluginEntry
import com.cinestreamtv.extension.repo.RepoManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExtensionManager @Inject constructor(
    private val repoManager: RepoManager,
    private val pluginLoader: PluginLoader,
    private val providerRegistry: ProviderRegistry
) {
    private val _availablePlugins = MutableStateFlow<List<PluginEntry>>(emptyList())
    val availablePlugins: StateFlow<List<PluginEntry>> = _availablePlugins.asStateFlow()
    
    private val _installedPlugins = MutableStateFlow<List<InstalledPlugin>>(emptyList())
    val installedPlugins: StateFlow<List<InstalledPlugin>> = _installedPlugins.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    suspend fun refreshAvailablePlugins() {
        _isLoading.value = true
        try {
            val plugins = repoManager.getAllAvailablePlugins()
            _availablePlugins.value = plugins
        } finally {
            _isLoading.value = false
        }
    }
    
    suspend fun installExtension(entry: PluginEntry): Result<Unit> {
        return pluginLoader.installAndLoad(entry).map { loaded ->
            loaded.providers.forEach { provider ->
                providerRegistry.register(provider)
            }
            refreshInstalledList()
        }
    }
    
    fun uninstallExtension(internalName: String) {
        val loaded = pluginLoader.getLoadedPlugins()[internalName]
        loaded?.providers?.forEach { provider ->
            providerRegistry.unregister(provider.name)
        }
        pluginLoader.unloadPlugin(internalName)
        refreshInstalledList()
    }
    
    fun getInstalledProviders(): List<MainAPI> = providerRegistry.getAllProviders()
    
    fun getProvider(name: String): MainAPI? = providerRegistry.getProvider(name)
    
    fun isInstalled(internalName: String): Boolean = pluginLoader.isInstalled(internalName)
    
    private fun refreshInstalledList() {
        val installed = pluginLoader.getLoadedPlugins().map { (_, loaded) ->
            InstalledPlugin(
                entry = loaded.entry,
                repoUrl = "",
                filePath = loaded.file.absolutePath
            )
        }
        _installedPlugins.value = installed
    }
}
