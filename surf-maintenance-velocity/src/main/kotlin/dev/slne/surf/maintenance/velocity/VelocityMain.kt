package dev.slne.surf.maintenance.velocity

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.slne.surf.maintenance.velocity.command.maintenanceCommand
import dev.slne.surf.maintenance.velocity.config.MaintenanceConfig
import dev.slne.surf.maintenance.velocity.listener.ProxyConnectionsListener
import dev.slne.surf.maintenance.velocity.listener.ProxyPingListener
import dev.slne.surf.maintenance.velocity.listener.ServerConnectionListener
import java.nio.file.Path

class VelocityMain @Inject constructor(
    val proxy: ProxyServer,
    @param:DataDirectory val dataPath: Path,
    val eventManager: EventManager,
    suspendingPluginContainer: SuspendingPluginContainer
) {
    val enabled: Boolean get() = MaintenanceService.globalEnabled

    init {
        instance = this
        suspendingPluginContainer.initialize(this)
    }

    @Subscribe
    fun onProxyInitialize(event: ProxyInitializeEvent) {
        maintenanceCommand()

        eventManager.register(this, ProxyPingListener)
        eventManager.register(this, ProxyConnectionsListener)
        eventManager.register(this, ServerConnectionListener)

        val config = MaintenanceConfig.getConfig()
        redisLoader.connect(config.enabled)

        if (redisLoader.serverMaintenanceSet.size() == 0 && config.serversInMaintenance.isNotEmpty()) {
            config.serversInMaintenance.forEach { serverName ->
                redisLoader.serverMaintenanceSet.add(serverName)
            }
        }

        MaintenanceService.initialize(redisLoader.globalState, redisLoader.serverMaintenanceSet)
    }

    @Subscribe
    fun onProxyShutdown(event: ProxyShutdownEvent) {
        MaintenanceService.shutdown()
        saveToConfig()
        redisLoader.disconnect()
    }

    companion object {
        lateinit var instance: VelocityMain
    }

    private fun saveToConfig() {
        MaintenanceConfig.edit {
            this.enabled = MaintenanceService.globalEnabled
            this.serversInMaintenance = MaintenanceService.snapshotServerStates().toMutableSet()
        }
    }
}

val plugin get() = VelocityMain.instance
val proxy get() = plugin.proxy