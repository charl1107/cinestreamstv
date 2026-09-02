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
            var pluginClassName: String? = null
            
            // Extract manifest.json from the .cs3 zip archive if present
            try {
                java.util.zip.ZipFile(file).use { zip ->
                    val manifestEntry = zip.getEntry("manifest.json")
                    if (manifestEntry != null) {
                        val manifestJsonStr = zip.getInputStream(manifestEntry).bufferedReader().use { it.readText() }
                        // Quick extract pluginClassName
                        val match = Regex(""""pluginClassName"\s*:\s*"([^"]+)"""").find(manifestJsonStr)
                        if (match != null) {
                            pluginClassName = match.groupValues[1]
                        }
                    }
                }
            } catch (e: Exception) {
                // Fallback if not a zip
            }

            val classLoader = dalvik.system.DexClassLoader(
                file.absolutePath,
                context.codeCacheDir.absolutePath,
                null,
                context.classLoader
            )
            
            val candidates = listOfNotNull(
                pluginClassName,
                "com.cinestreamtv.plugins.${entry.internalName}.${entry.internalName}Plugin",
                "${entry.internalName}Plugin",
                entry.internalName
            )
            
            var pluginInstance: Any? = null
            var lastError: Throwable? = null
            for (candidate in candidates) {
                try {
                    val pluginClass = classLoader.loadClass(candidate)
                    pluginInstance = pluginClass.getDeclaredConstructor().newInstance()
                    break
                } catch (e: Throwable) {
                    lastError = e
                }
            }

            val providers = mutableListOf<MainAPI>()
            if (pluginInstance is MainAPI) {
                providers.add(pluginInstance)
            } else if (pluginInstance != null) {
                // Check if it has a load or register method
                try {
                    val loadMethod = pluginInstance.javaClass.methods.firstOrNull { it.name == "load" }
                    if (loadMethod != null) {
                        if (loadMethod.parameterTypes.size == 1 && loadMethod.parameterTypes[0].isAssignableFrom(Context::class.java)) {
                            loadMethod.invoke(pluginInstance, context)
                        } else if (loadMethod.parameterTypes.isEmpty()) {
                            loadMethod.invoke(pluginInstance)
                        }
                    }
                } catch (_: Throwable) {}
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
