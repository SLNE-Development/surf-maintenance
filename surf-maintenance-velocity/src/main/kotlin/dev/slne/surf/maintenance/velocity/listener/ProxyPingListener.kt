package dev.slne.surf.maintenance.velocity.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyPingEvent
import com.velocitypowered.api.proxy.server.ServerPing
import dev.slne.surf.maintenance.velocity.config
import dev.slne.surf.maintenance.velocity.plugin
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

object ProxyPingListener {
    @Subscribe(priority = Short.MAX_VALUE)
    fun onProxyPing(event: ProxyPingEvent) {
        if (plugin.maintenanceMode) {
            event.ping = event.ping.asBuilder().apply {
                maximumPlayers(config.maxPlayerCount)
                description(MiniMessage.miniMessage().deserialize(config.maintenanceMotd))

                if (config.versionMessageEnabled) {
                    version(
                        ServerPing.Version(
                            1,
                            LegacyComponentSerializer.legacySection()
                                .serialize(
                                    MiniMessage.miniMessage().deserialize(config.versionMessage)
                                )
                        )
                    )
                }
            }.build()
        }
    }
}