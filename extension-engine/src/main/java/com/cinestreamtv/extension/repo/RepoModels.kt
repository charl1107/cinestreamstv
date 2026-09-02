package com.cinestreamtv.extension.repo

import kotlinx.serialization.Serializable

@Serializable
data class RepoManifest(
    val name: String,
    val description: String? = null,
    val manifestVersion: Int = 1,
    val pluginLists: List<String> = emptyList()
)

@Serializable
data class PluginManifest(
    val plugins: List<PluginEntry> = emptyList()
)

@Serializable
data class PluginEntry(
    val url: String? = null,
    val status: Int = 1,
    val internalName: String,
    val authors: List<String> = emptyList(),
    val repositoryUrl: String? = null,
    val name: String,
    val description: String? = null,
    val iconUrl: String? = null,
    val apiVersion: Int = 1,
    val version: Int = 1,
    val tvTypes: List<String>? = null,
    val language: String? = null,
    val fileSize: Long? = null
)

data class RepoConfig(
    val name: String,
    val url: String,
    val manifestVersion: Int = 1,
    val isDefault: Boolean = false,
    val isEnabled: Boolean = true
)

data class InstalledPlugin(
    val entry: PluginEntry,
    val repoUrl: String,
    val filePath: String,
    val isEnabled: Boolean = true
)
