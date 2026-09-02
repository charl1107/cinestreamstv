package com.cinestreamtv.core.data.local.dao

import androidx.room.*
import com.cinestreamtv.core.data.local.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY addedAt DESC")
    fun getAll(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE mediaId = :mediaId")
    suspend fun delete(mediaId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE mediaId = :mediaId)")
    suspend fun exists(mediaId: String): Boolean
}
