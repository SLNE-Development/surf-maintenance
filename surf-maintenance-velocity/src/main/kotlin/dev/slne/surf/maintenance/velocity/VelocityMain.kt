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
import dev.slne.surf.maintenance.velocity.config.MaintenanceConfiguration
import dev.slne.surf.maintenance.velocity.listener.ProxyConnectionsListener
import dev.slne.surf.maintenance.velocity.listener.ProxyPingListener
import java.nio.file.Path

class VelocityMain @Inject constructor(
    val proxy: ProxyServer,
    @param:DataDirectory val dataPath: Path,
    val eventManager: EventManager,
    suspendingPluginContainer: SuspendingPluginContainer
) {
    var enabled: Boolean = true

    init {
        instance = this
        suspendingPluginContainer.initialize(this)
    }

    @Subscribe
    fun onProxyInitialize(event: ProxyInitializeEvent) {
        maintenanceCommand()
        eventManager.register(this, ProxyPingListener)
        eventManager.register(this, ProxyConnectionsListener)

        loadFromConfig()
    }

    @Subscribe
    fun onProxyShutdown(event: ProxyShutdownEvent) {
        saveToConfig()
    }

    companion object {
        lateinit var instance: VelocityMain
    }

    private fun loadFromConfig() {
        enabled = configuration.config.enabled
    }

    private fun saveToConfig() {
        configuration.edit {
            this.enabled = this@VelocityMain.enabled
        }
    }
}

val plugin get() = VelocityMain.instance
val proxy get() = plugin.proxy
val configuration = MaintenanceConfiguration()