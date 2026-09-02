package com.cinestreamtv.core.data.local.dao

import androidx.room.*
import com.cinestreamtv.core.data.local.entity.WatchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchHistoryDao {
    @Query("SELECT * FROM watch_history ORDER BY lastWatched DESC")
    fun getAll(): Flow<List<WatchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WatchHistoryEntity)

    @Query("DELETE FROM watch_history WHERE mediaId = :mediaId")
    suspend fun delete(mediaId: String)

    @Query("DELETE FROM watch_history")
    suspend fun deleteAll()

    @Query("SELECT * FROM watch_history WHERE mediaId = :mediaId")
    suspend fun getById(mediaId: String): WatchHistoryEntity?
}
