package com.cinestreamtv.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cinestreamtv.core.data.local.dao.BookmarkDao
import com.cinestreamtv.core.data.local.dao.ExtensionDao
import com.cinestreamtv.core.data.local.dao.WatchHistoryDao
import com.cinestreamtv.core.data.local.entity.BookmarkEntity
import com.cinestreamtv.core.data.local.entity.ExtensionEntity
import com.cinestreamtv.core.data.local.entity.RepoEntity
import com.cinestreamtv.core.data.local.entity.WatchHistoryEntity

@Database(
    entities = [
        WatchHistoryEntity::class,
        BookmarkEntity::class,
        ExtensionEntity::class,
        RepoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CineStreamDatabase : RoomDatabase() {
    abstract fun watchHistoryDao(): WatchHistoryDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun extensionDao(): ExtensionDao
}
