package com.cinestreamtv.extension.di

import android.content.Context
import com.cinestreamtv.extension.ExtensionManager
import com.cinestreamtv.extension.loader.PluginLoader
import com.cinestreamtv.extension.registry.ProviderRegistry
import com.cinestreamtv.extension.repo.RepoManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExtensionModule {
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .followRedirects(true)
        .followSslRedirects(true)
        .build()
    
    @Provides
    @Singleton
    fun providePluginLoader(
        @ApplicationContext context: Context,
        httpClient: OkHttpClient
    ): PluginLoader = PluginLoader(context, httpClient)
    
    @Provides
    @Singleton
    fun provideRepoManager(
        httpClient: OkHttpClient,
        json: Json
    ): RepoManager = RepoManager(httpClient, json)
    
    @Provides
    @Singleton
    fun provideProviderRegistry(): ProviderRegistry = ProviderRegistry()
    
    @Provides
    @Singleton
    fun provideExtensionManager(
        repoManager: RepoManager,
        pluginLoader: PluginLoader,
        providerRegistry: ProviderRegistry
    ): ExtensionManager = ExtensionManager(repoManager, pluginLoader, providerRegistry)
}
