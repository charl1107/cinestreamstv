package com.cinestreamtv.core.data.di

import android.content.Context
import androidx.room.Room
import com.cinestreamtv.core.common.utils.Constants
import com.cinestreamtv.core.data.local.CineStreamDatabase
import com.cinestreamtv.core.data.local.dao.BookmarkDao
import com.cinestreamtv.core.data.local.dao.ExtensionDao
import com.cinestreamtv.core.data.local.dao.WatchHistoryDao
import com.cinestreamtv.core.data.repository.ContentRepositoryImpl
import com.cinestreamtv.core.data.repository.UserDataRepositoryImpl
import com.cinestreamtv.core.domain.repository.ContentRepository
import com.cinestreamtv.core.domain.repository.UserDataRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CineStreamDatabase =
        Room.databaseBuilder(context, CineStreamDatabase::class.java, Constants.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideWatchHistoryDao(db: CineStreamDatabase): WatchHistoryDao = db.watchHistoryDao()

    @Provides
    fun provideBookmarkDao(db: CineStreamDatabase): BookmarkDao = db.bookmarkDao()

    @Provides
    fun provideExtensionDao(db: CineStreamDatabase): ExtensionDao = db.extensionDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindsModule {
    @Binds
    @Singleton
    abstract fun bindContentRepository(impl: ContentRepositoryImpl): ContentRepository

    @Binds
    @Singleton
    abstract fun bindUserDataRepository(impl: UserDataRepositoryImpl): UserDataRepository
}
