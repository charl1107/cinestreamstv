package com.cinestreamtv.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "extensions")
data class ExtensionEntity(
    @PrimaryKey val internalName: String,
    val name: String,
    val description: String?,
    val version: Int,
    val repoUrl: String,
    val filePath: String,
    val isEnabled: Boolean = true,
    val iconUrl: String? = null,
    val installedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "repos")
data class RepoEntity(
    @PrimaryKey val url: String,
    val name: String,
    val isEnabled: Boolean = true,
    val isDefault: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
)
