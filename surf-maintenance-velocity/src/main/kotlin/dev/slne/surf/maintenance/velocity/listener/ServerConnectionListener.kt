package dev.slne.surf.maintenance.velocity.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import dev.slne.surf.api.core.messages.CommonComponents
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.maintenance.velocity.MaintenanceService
import dev.slne.surf.maintenance.velocity.util.MaintenancePermissions

object ServerConnectionListener {
    @Subscribe
    fun onServerPreConnect(event: ServerPreConnectEvent) {
        val server = event.result.server.orElse(null) ?: return
        val serverName = server.serverInfo.name

        if (!MaintenanceService.isServerEnabled(serverName)) {
            return
        }

        if (event.player.hasPermission(MaintenancePermissions.MAINTENANCE_BYPASS)) {
            return
        }

        event.result = ServerPreConnectEvent.ServerResult.denied()
        event.player.sendText {
            appendDisconnectMessage("DER SERVER BEFINDET SICH IM WARTUNGSMODUS", {
                variableValue("Zurzeit werden Wartungen am Server durchgeführt.")
                appendNewline()
                spacer("Weitere Informationen findest du in unserem Discord.")
            }, {
                append(CommonComponents.RETRY_LATER_FOOTER)
            })
        }
    }
}
