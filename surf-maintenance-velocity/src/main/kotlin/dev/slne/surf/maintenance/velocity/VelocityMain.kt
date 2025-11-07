package dev.slne.surf.maintenance.velocity

import com.google.inject.Inject
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.PluginManager
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.slne.surf.cloud.api.common.CloudInstance
import dev.slne.surf.cloud.api.common.startSpringApplication
import dev.slne.surf.maintenance.core.MaintenanceApplication
import dev.slne.surf.maintenance.core.MaintenanceContextHolderImpl
import dev.slne.surf.maintenance.velocity.command.maintenanceCommand
import dev.slne.surf.maintenance.velocity.config.MaintenanceConfiguration
import dev.slne.surf.maintenance.velocity.listener.ProxyConnectionsListener
import dev.slne.surf.maintenance.velocity.listener.ProxyPingListener
import java.nio.file.Path

class VelocityMain @Inject constructor(
    val proxy: ProxyServer,
    @param:DataDirectory val dataPath: Path,
    val pluginManager: PluginManager,
    val eventManager: EventManager,
) {
    var maintenanceMode: Boolean = false

    init {
        instance = this
        MaintenanceContextHolderImpl.instance.context =
            CloudInstance.startSpringApplication(MaintenanceApplication::class)
    }

    @Subscribe
    fun onProxyInitialize(event: ProxyInitializeEvent) {
        maintenanceCommand()
        eventManager.register(this, ProxyPingListener)
        eventManager.register(this, ProxyConnectionsListener)

        maintenanceMode = config.enabled
    }

    companion object {
        lateinit var instance: VelocityMain
    }

    val configuration = MaintenanceConfiguration()
}

val config get() = plugin.configuration.config


val plugin get() = VelocityMain.instance
val proxy get() = plugin.proxy