@file:OptIn(InternalMaintenanceApi::class)

package dev.slne.surf.maintenance.velocity.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.maintenance.api.InternalMaintenanceApi
import dev.slne.surf.maintenance.core.client.permission.MaintenancePermissions
import dev.slne.surf.maintenance.velocity.config
import dev.slne.surf.maintenance.velocity.plugin
import dev.slne.surf.maintenance.velocity.proxy
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import java.util.*

fun maintenanceCommand() = commandTree("maintenance") {
    withPermission(MaintenancePermissions.MAINTENANCE_COMMAND)
    literalArgument("reload") {
        anyExecutor { executor, _ ->
            plugin.configuration.reload()

            plugin.maintenanceMode = config.enabled

            executor.sendText {
                appendPrefix()
                success("Die Konfiguration wurde neu geladen.")
            }
        }
    }
    literalArgument("enable") {
        anyExecutor { executor, _ ->
            if (plugin.maintenanceMode) {
                executor.sendText {
                    appendPrefix()
                    error("Der Wartungsmodus ist bereits aktiviert.")
                }
                return@anyExecutor
            }

            plugin.maintenanceMode = true
            plugin.configuration.edit {
                enabled = true
            }

            executor.sendText {
                appendPrefix()
                success("Der Wartungsmodus wurde aktiviert.")
            }
        }

        literalArgument("--kick") {
            anyExecutor { executor, _ ->
                if (plugin.maintenanceMode) {
                    executor.sendText {
                        appendPrefix()
                        error("Der Wartungsmodus ist bereits aktiviert.")
                    }
                    return@anyExecutor
                }

                plugin.maintenanceMode = true
                plugin.configuration.edit {
                    enabled = true
                }

                val kickedPlayers = kickPlayers()

                executor.sendText {
                    appendPrefix()
                    success("Der Wartungsmodus wurde aktiviert und ")
                    variableValue(kickedPlayers.size)
                    success(" Spieler wurden gekickt.")
                }
            }
        }
    }
    literalArgument("disable") {
        anyExecutor { executor, _ ->
            if (!plugin.maintenanceMode) {
                executor.sendText {
                    appendPrefix()
                    error("Der Wartungsmodus ist nicht aktiviert.")
                }
                return@anyExecutor
            }

            plugin.maintenanceMode = false
            plugin.configuration.edit {
                enabled = false
            }

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

                if (plugin.maintenanceMode) {
                    error("aktiviert.")
                } else {
                    success("deaktiviert.")
                }
            }
        }
    }

    literalArgument("kick") {
        anyExecutor { executor, _ ->
            if (!plugin.maintenanceMode) {
                executor.sendText {
                    appendPrefix()
                    error("Der Wartungsmodus ist nicht aktiviert.")
                }
                return@anyExecutor
            }

            val kickedPlayers = kickPlayers()

            executor.sendText {
                appendPrefix()
                success("Es wurden ")
                variableValue(kickedPlayers.size)
                success(" Spieler gekickt.")
            }
        }
    }
}

private fun kickPlayers(): List<UUID> {
    val list = mutableListOf<UUID>()
    proxy.allPlayers.forEach {
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