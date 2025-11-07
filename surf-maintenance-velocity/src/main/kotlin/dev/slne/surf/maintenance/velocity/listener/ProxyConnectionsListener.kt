package dev.slne.surf.maintenance.velocity.listener

import com.velocitypowered.api.event.ResultedEvent
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.LoginEvent
import dev.slne.surf.cloud.api.client.server.current
import dev.slne.surf.cloud.api.common.server.CommonCloudServer
import dev.slne.surf.maintenance.core.client.permission.MaintenancePermissions
import dev.slne.surf.maintenance.velocity.maintenanceService
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

object ProxyConnectionsListener {
    @Subscribe
    fun onPreConnect(event: LoginEvent) {
        val currentServer = CommonCloudServer.current()

        if (!maintenanceService.isMaintenanceEnabled(currentServer.name) && !maintenanceService.isMaintenanceEnabled(
                currentServer.group
            )
        ) {
            return
        }

        val player = event.player
        if (player.hasPermission(MaintenancePermissions.MAINTENANCE_BYPASS)) {
            return
        }

        event.result = ResultedEvent.ComponentResult.denied(buildText {
            appendDisconnectMessage("DER SERVER BEFINDET SICH IM WARTUNGSMODUS", {
                variableValue("Zurzeit werden Wartungen am Server durchgeführt.")
                appendNewline()
                spacer("Weitere Informationen findest du in unserem Discord.")
            }, {
                append(CommonComponents.RETRY_LATER_FOOTER)
            })
        })
    }
}