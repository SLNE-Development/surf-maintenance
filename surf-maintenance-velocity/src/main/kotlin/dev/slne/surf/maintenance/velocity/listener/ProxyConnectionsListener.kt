package dev.slne.surf.maintenance.velocity.listener

import com.velocitypowered.api.event.ResultedEvent
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.LoginEvent
import dev.slne.surf.maintenance.core.client.permission.MaintenancePermissions
import dev.slne.surf.maintenance.velocity.plugin
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

object ProxyConnectionsListener {
    @Subscribe
    fun onPreConnect(event: LoginEvent) {
        if (plugin.maintenanceMode) {
            val player = event.player

            if (!player.hasPermission(MaintenancePermissions.MAINTENANCE_BYPASS)) {
                event.result = ResultedEvent.ComponentResult.denied(buildText {
                    CommonComponents.renderDisconnectMessage(
                        this,
                        "WARTUNGSARBEITEN",
                        {
                            error("Der Server befindet sich derzeit im Wartungsmodus.")
                        },
                        false
                    )
                })
            }
        }
    }
}