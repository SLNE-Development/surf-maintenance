package dev.slne.surf.maintenance.server

import dev.slne.surf.cloud.api.common.sync.SyncSet
import dev.slne.surf.cloud.api.common.sync.SyncValue
import dev.slne.surf.cloud.api.server.plugin.StandalonePlugin
import dev.slne.surf.maintenance.server.config.MaintenanceConfiguration
import dev.slne.surf.maintenance.server.service.MaintenanceService
import dev.slne.surf.surfapi.core.api.util.logger

class PluginMain : StandalonePlugin() {
    private val pluginLogger = logger()

    init {
        maintenanceService.maintenanceServers = SyncSet<String>("maintenance:servers")
        maintenanceService.maintenanceGroups = SyncSet<String>("maintenance:groups")
        maintenanceService.maintenanceMotd =
            SyncValue<String>("maintenance:motd", "Internal Server Error")
        maintenanceService.maintenanceVersion =
            SyncValue<String>("maintenance:version", "Maintenance")
    }

    override suspend fun load() {}

    override suspend fun enable() {
        pluginLogger.atInfo().log("Loading Maintenance Configuration...")

        maintenanceService.maintenanceGroups.clear()
        maintenanceService.maintenanceGroups.addAll(configuration.config.maintenanceGroups)

        maintenanceService.maintenanceServers.clear()
        maintenanceService.maintenanceServers.addAll(configuration.config.maintenanceServers)

        maintenanceService.maintenanceMotd.set(configuration.config.maintenanceMotd)
        maintenanceService.maintenanceVersion.set(configuration.config.versionMessage)

        pluginLogger.atInfo().log("Loaded Maintenance Configuration.")
    }

    override suspend fun disable() {
        pluginLogger.atInfo().log("Saving Maintenance Configuration...")
        configuration.edit {
            maintenanceServers.clear()
            maintenanceServers.addAll(maintenanceService.maintenanceServers)

            maintenanceGroups.clear()
            maintenanceGroups.addAll(maintenanceService.maintenanceGroups)

            maintenanceMotd = maintenanceService.maintenanceMotd.get()
            versionMessage = maintenanceService.maintenanceVersion.get()
        }

        pluginLogger.atInfo().log("Saved Maintenance Configuration.")
    }

    val configuration = MaintenanceConfiguration()
}

val maintenanceService = MaintenanceService()

val plugin get() = StandalonePlugin.getPlugin(PluginMain::class)