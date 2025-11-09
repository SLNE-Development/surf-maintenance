package dev.slne.surf.maintenance.velocity

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.PluginManager
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.slne.surf.cloud.api.common.CloudInstance
import dev.slne.surf.cloud.api.common.startSpringApplication
import dev.slne.surf.cloud.api.common.sync.SyncSet
import dev.slne.surf.cloud.api.common.sync.SyncValue
import dev.slne.surf.maintenance.MaintenanceApplication
import dev.slne.surf.maintenance.core.MaintenanceContextHolderImpl
import dev.slne.surf.maintenance.velocity.command.maintenanceCommand
import dev.slne.surf.maintenance.velocity.listener.ProxyConnectionsListener
import dev.slne.surf.maintenance.velocity.listener.ProxyPingListener
import dev.slne.surf.maintenance.velocity.service.MaintenanceService
import java.nio.file.Path

class VelocityMain @Inject constructor(
    val proxy: ProxyServer,
    @param:DataDirectory val dataPath: Path,
    val pluginManager: PluginManager,
    val eventManager: EventManager,
    val pluginContainer: PluginContainer,
    val suspendingPluginContainer: SuspendingPluginContainer
) {
    init {
        instance = this
        MaintenanceContextHolderImpl.instance.context =
            CloudInstance.startSpringApplication(MaintenanceApplication::class)

        maintenanceService.maintenanceServers = SyncSet<String>("maintenance:servers")
        maintenanceService.maintenanceGroups = SyncSet<String>("maintenance:groups")
        maintenanceService.maintenanceMotd =
            SyncValue<String>("maintenance:motd", "Internal Server Error")
        maintenanceService.maintenanceVersion =
            SyncValue<String>("maintenance:version", "Maintenance")

        suspendingPluginContainer.initialize(this)
    }

    @Subscribe
    fun onProxyInitialize(event: ProxyInitializeEvent) {
        maintenanceCommand()
        eventManager.register(this, ProxyPingListener)
        eventManager.register(this, ProxyConnectionsListener)
    }

    companion object {
        lateinit var instance: VelocityMain
    }
}

val plugin get() = VelocityMain.instance
val proxy get() = plugin.proxy
val maintenanceService = MaintenanceService()