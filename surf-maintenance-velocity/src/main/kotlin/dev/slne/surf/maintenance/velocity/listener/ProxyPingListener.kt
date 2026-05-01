package dev.slne.surf.maintenance.velocity.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyPingEvent
import com.velocitypowered.api.proxy.server.ServerPing
import dev.slne.surf.maintenance.velocity.MaintenanceService
import dev.slne.surf.maintenance.velocity.config.MaintenanceConfig
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

object ProxyPingListener {
    @Subscribe(priority = Short.MAX_VALUE)
    fun onProxyPing(event: ProxyPingEvent) {
        if (!MaintenanceService.globalEnabled) {
            return
        }

        event.ping = event.ping.asBuilder().apply {
            description(
                MiniMessage.miniMessage().deserialize(MaintenanceConfig.getConfig().maintenanceMotd)
            )

            version(
                ServerPing.Version(
                    1,
                    LegacyComponentSerializer.legacySection().serialize(
                        MiniMessage.miniMessage()
                            .deserialize(MaintenanceConfig.getConfig().versionMessage)
                    )
                )
            )
        }.build()
    }
}