package dev.slne.surf.maintenance.velocity.redis.listener.event

import dev.slne.surf.api.core.messages.CommonComponents
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.maintenance.velocity.plugin
import dev.slne.surf.maintenance.velocity.redis.event.MaintenanceKickRedisEvent
import dev.slne.surf.maintenance.velocity.redis.event.MaintenanceServerKickRedisEvent
import dev.slne.surf.maintenance.velocity.util.MaintenancePermissions
import dev.slne.surf.redis.event.OnRedisEvent

object MaintenanceRedisListener {
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

    @OnRedisEvent
    fun onMaintenanceServerKick(event: MaintenanceServerKickRedisEvent) {
        val serverName = event.serverName
        plugin.proxy.allPlayers
            .filter { player ->
                player.currentServer
                    .map { cs -> cs.serverInfo.name == serverName }
                    .orElse(false)
            }
            .forEach { player ->
                if (!player.hasPermission(MaintenancePermissions.MAINTENANCE_BYPASS)) {
                    player.disconnect(buildText {
                        appendDisconnectMessage("SERVER IN WARTUNGSMODUS", {
                            variableValue("Der Server <yellow>$serverName</yellow> befindet sich im Wartungsmodus.")
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