package com.cinestreamtv.extension.repo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepoManager @Inject constructor(
    private val httpClient: OkHttpClient,
    private val json: Json
) {
    private val repos = mutableListOf<RepoConfig>()
    
    companion object {
        val DEFAULT_REPO = RepoConfig(
            name = "Mega Repository",
            url = "https://raw.githubusercontent.com/self-similarity/MegaRepo/builds/repo.json",
            manifestVersion = 1,
            isDefault = true
        )
    }
    
    init {
        repos.add(DEFAULT_REPO)
    }
    
    fun getRepos(): List<RepoConfig> = repos.toList()
    
    fun addRepo(config: RepoConfig) {
        if (repos.none { it.url == config.url }) {
            repos.add(config)
        }
    }
    
    fun removeRepo(url: String) {
        repos.removeAll { it.url == url && !it.isDefault }
    }
    
    suspend fun fetchRepoManifest(repoUrl: String): Result<RepoManifest> = withContext(Dispatchers.IO) {
        runCatching {
            val request = Request.Builder().url(repoUrl).build()
            val response = httpClient.newCall(request).execute()
            val body = response.body?.string() ?: throw Exception("Empty response from $repoUrl")
            json.decodeFromString<RepoManifest>(body)
        }
    }
    
    suspend fun fetchPluginList(pluginListUrl: String): Result<List<PluginEntry>> = withContext(Dispatchers.IO) {
        runCatching {
            val request = Request.Builder().url(pluginListUrl).build()
            val response = httpClient.newCall(request).execute()
            val body = response.body?.string() ?: throw Exception("Empty response from $pluginListUrl")
            json.decodeFromString<List<PluginEntry>>(body)
        }
    }
    
    suspend fun getAllAvailablePlugins(): List<PluginEntry> {
        val allPlugins = mutableListOf<PluginEntry>()
        for (repo in repos.filter { it.isEnabled }) {
            val manifest = fetchRepoManifest(repo.url).getOrNull() ?: continue
            for (pluginListUrl in manifest.pluginLists) {
                val plugins = fetchPluginList(pluginListUrl).getOrNull() ?: continue
                allPlugins.addAll(plugins)
            }
        }
        return allPlugins
    }
}
