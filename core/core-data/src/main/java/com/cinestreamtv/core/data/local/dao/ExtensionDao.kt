package com.cinestreamtv.core.data.local.dao

import androidx.room.*
import com.cinestreamtv.core.data.local.entity.ExtensionEntity
import com.cinestreamtv.core.data.local.entity.RepoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExtensionDao {
    @Query("SELECT * FROM extensions ORDER BY name ASC")
    fun getAllExtensions(): Flow<List<ExtensionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExtension(entity: ExtensionEntity)

    @Query("DELETE FROM extensions WHERE internalName = :internalName")
    suspend fun deleteExtension(internalName: String)

    @Query("SELECT * FROM repos ORDER BY name ASC")
    fun getAllRepos(): Flow<List<RepoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepo(entity: RepoEntity)

    @Query("DELETE FROM repos WHERE url = :url")
    suspend fun deleteRepo(url: String)
}
