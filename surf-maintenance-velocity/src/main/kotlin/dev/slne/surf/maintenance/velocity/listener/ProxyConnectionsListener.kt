package dev.slne.surf.maintenance.velocity.listener

import com.velocitypowered.api.event.ResultedEvent
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.LoginEvent
import dev.slne.surf.api.core.messages.CommonComponents
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.maintenance.velocity.plugin
import dev.slne.surf.maintenance.velocity.util.MaintenancePermissions

object ProxyConnectionsListener {
    @Subscribe
    fun onPreConnect(event: LoginEvent) {
        val player = event.player

        if (!plugin.enabled) {
            return
        }

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