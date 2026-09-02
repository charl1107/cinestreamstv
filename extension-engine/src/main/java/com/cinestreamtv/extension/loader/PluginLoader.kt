package com.cinestreamtv.extension.loader

import android.content.Context
import com.cinestreamtv.extension.api.MainAPI
import com.cinestreamtv.extension.repo.PluginEntry
import dalvik.system.PathClassLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PluginLoader @Inject constructor(
    private val context: Context,
    private val httpClient: OkHttpClient
) {
    private val pluginDir: File by lazy {
        File(context.filesDir, "plugins").also { it.mkdirs() }
    }
    
    private val loadedPlugins = mutableMapOf<String, LoadedPlugin>()
    
    data class LoadedPlugin(
        val entry: PluginEntry,
        val classLoader: ClassLoader,
        val providers: List<MainAPI>,
        val file: File
    )
    
    suspend fun downloadPlugin(entry: PluginEntry): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val url = entry.url ?: throw Exception("No download URL for plugin ${entry.name}")
            val file = File(pluginDir, "${entry.internalName}.cs3")
            
            val request = Request.Builder().url(url).build()
            val response = httpClient.newCall(request).execute()
            val body = response.body ?: throw Exception("Empty response downloading ${entry.name}")
            
            file.outputStream().use { output ->
                body.byteStream().use { input ->
                    input.copyTo(output)
                }
            }
            file
        }
    }
    
    suspend fun loadPlugin(entry: PluginEntry, file: File): Result<LoadedPlugin> = withContext(Dispatchers.IO) {
        runCatching {
            val classLoader = PathClassLoader(
                file.absolutePath,
                context.classLoader
            )
            
            // Load the plugin class - convention: package.PluginName
            val className = "com.cinestreamtv.plugins.${entry.internalName}.${entry.internalName}Plugin"
            val pluginClass = classLoader.loadClass(className)
            val pluginInstance = pluginClass.getDeclaredConstructor().newInstance()
            
            val providers = if (pluginInstance is MainAPI) {
                listOf(pluginInstance)
            } else {
                emptyList()
            }
            
            val loaded = LoadedPlugin(
                entry = entry,
                classLoader = classLoader,
                providers = providers,
                file = file
            )
            
            loadedPlugins[entry.internalName] = loaded
            loaded
        }
    }
    
    suspend fun installAndLoad(entry: PluginEntry): Result<LoadedPlugin> {
        val file = downloadPlugin(entry).getOrElse { return Result.failure(it) }
        return loadPlugin(entry, file)
    }
    
    fun unloadPlugin(internalName: String) {
        val loaded = loadedPlugins.remove(internalName)
        loaded?.file?.delete()
    }
    
    fun getLoadedPlugins(): Map<String, LoadedPlugin> = loadedPlugins.toMap()
    
    fun getInstalledPluginFiles(): List<File> = pluginDir.listFiles()?.toList() ?: emptyList()
    
    fun isInstalled(internalName: String): Boolean {
        return File(pluginDir, "$internalName.cs3").exists()
    }
}
