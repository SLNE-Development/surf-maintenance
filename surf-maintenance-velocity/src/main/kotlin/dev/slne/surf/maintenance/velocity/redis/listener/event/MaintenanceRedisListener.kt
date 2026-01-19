package dev.slne.surf.maintenance.velocity.redis.listener.event

import dev.slne.surf.maintenance.velocity.plugin
import dev.slne.surf.maintenance.velocity.redis.event.MaintenanceKickRedisEvent
import dev.slne.surf.maintenance.velocity.redis.event.MaintenanceStatusChangeRedisEvent
import dev.slne.surf.maintenance.velocity.util.MaintenancePermissions
import dev.slne.surf.redis.event.OnRedisEvent
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

object MaintenanceRedisListener {
    @OnRedisEvent
    fun onMaintenanceStatusChange(event: MaintenanceStatusChangeRedisEvent) {
        plugin.enabled = event.enabled
    }

    @OnRedisEvent
    fun onMaintenanceKick(event: MaintenanceKickRedisEvent) {
        plugin.proxy.allPlayers.forEach {
            if (!it.hasPermission(MaintenancePermissions.MAINTENANCE_BYPASS)) {
                it.disconnect(buildText {
                    appendDisconnectMessage("DER SERVER BEFINDET SICH IM WARTUNGSMODUS", {
                        variableValue("Es werden nun Wartungen am Server durchgeführt.")
                        appendNewline()
                        spacer("Weitere Informationen findest du in unserem Discord.")
                    }, {
                        append(CommonComponents.RETRY_LATER_FOOTER)
                    })
                })
            }
        }
    }
}