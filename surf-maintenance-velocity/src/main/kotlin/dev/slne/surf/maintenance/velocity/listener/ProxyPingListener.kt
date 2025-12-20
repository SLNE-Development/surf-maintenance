package dev.slne.surf.maintenance.velocity.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyPingEvent
import com.velocitypowered.api.proxy.server.ServerPing
import dev.slne.surf.maintenance.velocity.configuration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

object ProxyPingListener {
    @Subscribe(priority = Short.MAX_VALUE)
    fun onProxyPing(event: ProxyPingEvent) {
        event.ping = event.ping.asBuilder().apply {
            description(
                MiniMessage.miniMessage().deserialize(configuration.config.maintenanceMotd)
            )

            version(
                ServerPing.Version(
                    1,
                    LegacyComponentSerializer.legacySection().serialize(
                        MiniMessage.miniMessage()
                            .deserialize(configuration.config.versionMessage)
                    )
                )
            )
        }.build()
    }
}