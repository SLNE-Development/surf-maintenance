package dev.slne.surf.maintenance.velocity.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyPingEvent
import com.velocitypowered.api.proxy.server.ServerPing
import dev.slne.surf.cloud.api.client.server.current
import dev.slne.surf.cloud.api.common.server.CommonCloudServer
import dev.slne.surf.maintenance.velocity.maintenanceService
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

object ProxyPingListener {
    @Subscribe(priority = Short.MAX_VALUE)
    fun onProxyPing(event: ProxyPingEvent) {
        val currentServer = CommonCloudServer.current()

        if (!maintenanceService.isMaintenanceEnabled(currentServer.name) && !maintenanceService.isMaintenanceEnabled(
                currentServer.group
            )
        ) {
            return
        }

        event.ping = event.ping.asBuilder().apply {
            description(
                MiniMessage.miniMessage().deserialize(maintenanceService.maintenanceMotd.get())
            )

            version(
                ServerPing.Version(
                    1,
                    LegacyComponentSerializer.legacySection().serialize(
                        MiniMessage.miniMessage()
                            .deserialize(maintenanceService.maintenanceVersion.get())
                    )
                )
            )
        }.build()
    }
}