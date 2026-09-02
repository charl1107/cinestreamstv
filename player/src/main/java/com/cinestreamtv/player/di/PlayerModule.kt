package com.cinestreamtv.player.di

import android.content.Context
import com.cinestreamtv.player.CineStreamPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {
    @Provides
    @Singleton
    fun provideCineStreamPlayer(
        @ApplicationContext context: Context
    ): CineStreamPlayer = CineStreamPlayer(context)
}
