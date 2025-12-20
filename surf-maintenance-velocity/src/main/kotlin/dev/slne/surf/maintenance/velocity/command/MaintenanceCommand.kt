package dev.slne.surf.maintenance.velocity.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.maintenance.velocity.configuration
import dev.slne.surf.maintenance.velocity.plugin
import dev.slne.surf.maintenance.velocity.util.MaintenancePermissions
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import java.util.*

fun maintenanceCommand() = commandTree("maintenance") {
    withPermission(MaintenancePermissions.MAINTENANCE_COMMAND)
    literalArgument("enable") {
        anyExecutor { executor, _ ->
            if (plugin.enabled) {
                executor.sendText {
                    appendPrefix()
                    error("Der Wartungsmodus ist bereits aktiviert.")
                }
                return@anyExecutor
            }

            plugin.enabled = true

            executor.sendText {
                appendPrefix()
                success("Der Wartungsmodus wurde für die Gruppe aktiviert.")
            }
        }
    }

    literalArgument("disable") {
        anyExecutor { executor, _ ->
            if (!plugin.enabled) {
                executor.sendText {
                    appendPrefix()
                    error("Der Wartungsmodus ist nicht aktiviert.")
                }
                return@anyExecutor
            }

            plugin.enabled = false

            executor.sendText {
                appendPrefix()
                success("Der Wartungsmodus wurde deaktiviert.")
            }
        }
    }

    literalArgument("status") {
        anyExecutor { executor, _ ->
            executor.sendText {
                appendPrefix()
                info("Der Wartungsmodus ist aktuell ")
                if (plugin.enabled) {
                    error("aktiviert.")
                } else {
                    success("deaktiviert.")
                }
            }
        }
    }


    literalArgument("kick") {
        anyExecutor { executor, _ ->
            if (!plugin.enabled) {
                executor.sendText {
                    appendPrefix()
                    error("Der Wartungsmodus ist nicht aktiviert.")
                }
                return@anyExecutor
            }

            executor.sendText {
                appendPrefix()
                success("Es wurden ")
                variableValue(kickPlayers().size)
                success(" Spieler gekickt.")
            }
        }
    }

    literalArgument("reload") {
        anyExecutor { executor, _ ->
            configuration.reload()

            executor.sendText {
                appendPrefix()
                success("Die Wartungskonfiguration wurde neu geladen.")
            }
        }
    }
}

private fun kickPlayers(): List<UUID> {
    val list = mutableListOf<UUID>()

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
            list.add(it.uniqueId)
        }
    }
    return list
}