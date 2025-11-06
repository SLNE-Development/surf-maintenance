package dev.slne.surf.maintenance.velocity.config

import dev.slne.surf.maintenance.velocity.plugin
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi

class MaintenanceConfiguration {
    private val configManager: SpongeConfigManager<MaintenanceConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            MaintenanceConfig::class.java,
            plugin.dataPath,
            "config.yml"
        )
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(
            MaintenanceConfig::class.java
        )
        reload()
    }

    fun edit(block: MaintenanceConfig.() -> Unit) {
        configManager.config = configManager.config.apply(block)
        configManager.save()
    }

    fun reload() {
        configManager.reloadFromFile()
    }

    val config get() = configManager.config
}