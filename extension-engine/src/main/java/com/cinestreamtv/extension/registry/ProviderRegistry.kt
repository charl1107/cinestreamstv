package com.cinestreamtv.extension.registry

import com.cinestreamtv.extension.api.MainAPI
import com.cinestreamtv.extension.api.TvType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProviderRegistry @Inject constructor() {
    private val providers = mutableMapOf<String, MainAPI>()
    
    fun register(provider: MainAPI) {
        providers[provider.name] = provider
    }
    
    fun unregister(providerName: String) {
        providers.remove(providerName)
    }
    
    fun getProvider(name: String): MainAPI? = providers[name]
    
    fun getAllProviders(): List<MainAPI> = providers.values.toList()
    
    fun getProvidersByType(type: TvType): List<MainAPI> {
        return providers.values.filter { it.supportedTypes.contains(type) }
    }
    
    fun getProviderNames(): List<String> = providers.keys.toList()
    
    fun getProviderCount(): Int = providers.size
    
    fun clear() {
        providers.clear()
    }
}
