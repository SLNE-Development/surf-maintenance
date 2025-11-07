@file:OptIn(InternalMaintenanceApi::class)

package dev.slne.surf.maintenance.velocity.command

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.cloud.api.client.server.CloudClientServerManager
import dev.slne.surf.cloud.api.client.velocity.command.args.cloudServerArgument
import dev.slne.surf.cloud.api.client.velocity.command.args.cloudServerGroupArgument
import dev.slne.surf.cloud.api.common.server.CloudServer
import dev.slne.surf.maintenance.api.InternalMaintenanceApi
import dev.slne.surf.maintenance.core.client.permission.MaintenancePermissions
import dev.slne.surf.maintenance.velocity.maintenanceService
import dev.slne.surf.maintenance.velocity.plugin
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import java.util.*

fun maintenanceCommand() = commandTree("maintenance") {
    withPermission(MaintenancePermissions.MAINTENANCE_COMMAND)
    literalArgument("enable") {
        literalArgument("server") {
            cloudServerArgument("cloudServer") {
                anyExecutor { executor, args ->
                    val cloudServer: CloudServer by args

                    if (maintenanceService.isMaintenanceEnabled(cloudServer)) {
                        executor.sendText {
                            appendPrefix()
                            error("Der Wartungsmodus ist bereits für den Server ${cloudServer.displayName} aktiviert.")
                        }
                        return@anyExecutor
                    }

                    maintenanceService.setMaintenanceModeForServer(cloudServer, true)

                    executor.sendText {
                        appendPrefix()
                        success("Der Wartungsmodus wurde für den Server ")
                        variableValue(cloudServer.displayName)
                        success(" aktiviert.")
                    }
                }
            }
        }
        literalArgument("group") {
            cloudServerGroupArgument("cloudServerGroup") {
                anyExecutor { executor, args ->
                    val cloudServerGroup: String by args

                    if (maintenanceService.isMaintenanceEnabled(cloudServerGroup)) {
                        executor.sendText {
                            appendPrefix()
                            error("Der Wartungsmodus ist bereits für die Gruppe $cloudServerGroup aktiviert.")
                        }
                        return@anyExecutor
                    }

                    maintenanceService.setMaintenanceModeForGroup(cloudServerGroup, true)

                    executor.sendText {
                        appendPrefix()
                        success("Der Wartungsmodus wurde für die Gruppe ")
                        variableValue(cloudServerGroup)
                        success(" aktiviert.")
                    }
                }
            }
        }
    }

    literalArgument("disable") {
        literalArgument("server") {
            cloudServerArgument("cloudServer") {
                anyExecutor { executor, args ->
                    val cloudServer: CloudServer by args

                    if (!maintenanceService.isMaintenanceEnabled(cloudServer)) {
                        executor.sendText {
                            appendPrefix()
                            error("Der Wartungsmodus ist für den Server ${cloudServer.displayName} nicht aktiviert.")
                        }
                        return@anyExecutor
                    }

                    maintenanceService.setMaintenanceModeForServer(cloudServer, false)

                    executor.sendText {
                        appendPrefix()
                        success("Der Wartungsmodus wurde für den Server ")
                        variableValue(cloudServer.displayName)
                        success(" deaktiviert.")
                    }
                }
            }
        }
        literalArgument("group") {
            cloudServerGroupArgument("cloudServerGroup") {
                anyExecutor { executor, args ->
                    val cloudServerGroup: String by args

                    if (!maintenanceService.isMaintenanceEnabled(cloudServerGroup)) {
                        executor.sendText {
                            appendPrefix()
                            error("Der Wartungsmodus ist für die Gruppe $cloudServerGroup nicht aktiviert.")
                        }
                        return@anyExecutor
                    }

                    maintenanceService.setMaintenanceModeForGroup(cloudServerGroup, false)

                    executor.sendText {
                        appendPrefix()
                        success("Der Wartungsmodus wurde für die Gruppe ")
                        variableValue(cloudServerGroup)
                        success(" deaktiviert.")
                    }
                }
            }
        }
    }

    literalArgument("status") {
        literalArgument("server") {
            cloudServerArgument("cloudServer") {
                anyExecutor { executor, args ->
                    val cloudServer: CloudServer by args

                    executor.sendText {
                        appendPrefix()
                        info("Der Wartungsmodus für den Server ${cloudServer.displayName} ist aktuell ")
                        if (maintenanceService.isMaintenanceEnabled(cloudServer)) {
                            error("aktiviert.")
                        } else {
                            success("deaktiviert.")
                        }
                    }
                }
            }
        }
        literalArgument("group") {
            cloudServerGroupArgument("cloudServerGroup") {
                anyExecutor { executor, args ->
                    val cloudServerGroup: String by args

                    executor.sendText {
                        appendPrefix()
                        info("Der Wartungsmodus für die Gruppe $cloudServerGroup ist aktuell ")
                        if (maintenanceService.isMaintenanceEnabled(cloudServerGroup)) {
                            error("aktiviert.")
                        } else {
                            success("deaktiviert.")
                        }
                    }
                }
            }
        }
    }


    literalArgument("kick") {
        literalArgument("server") {
            cloudServerArgument("cloudServer") {
                anyExecutor { executor, args ->
                    val cloudServer: CloudServer by args

                    if (!maintenanceService.isMaintenanceEnabled(cloudServer)) {
                        executor.sendText {
                            appendPrefix()
                            error("Der Wartungsmodus ist für den Server ${cloudServer.displayName} nicht aktiviert.")
                        }
                        return@anyExecutor
                    }

                    executor.sendText {
                        appendPrefix()
                        info("Die Cloud-Server Anfrage wurde gesendet...")
                    }

                    plugin.pluginContainer.launch {
                        val kickedPlayers = kickPlayers(cloudServer)

                        executor.sendText {
                            appendPrefix()
                            success("Es wurden ")
                            variableValue(kickedPlayers.size)
                            success(" Spieler vom Server ${cloudServer.displayName} gekickt.")
                        }
                    }
                }
            }
        }

        literalArgument("group") {
            cloudServerGroupArgument("cloudServerGroup") {
                anyExecutor { executor, args ->
                    val cloudServerGroup: String by args

                    if (!maintenanceService.isMaintenanceEnabled(cloudServerGroup)) {
                        executor.sendText {
                            appendPrefix()
                            error("Der Wartungsmodus ist für die Gruppe $cloudServerGroup nicht aktiviert.")
                        }
                        return@anyExecutor
                    }

                    executor.sendText {
                        appendPrefix()
                        info("Die Cloud-Server Anfrage wurde gesendet...")
                    }

                    plugin.pluginContainer.launch {
                        val kickedPlayers = kickPlayers(cloudServerGroup)

                        executor.sendText {
                            appendPrefix()
                            success("Es wurden ")
                            variableValue(kickedPlayers.size)
                            success(" Spieler aus der Gruppe $cloudServerGroup gekickt.")
                        }
                    }
                }
            }
        }
    }
}

private suspend fun kickPlayers(server: CloudServer): List<UUID> {
    val list = mutableListOf<UUID>()

    server.users.forEach {
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
            list.add(it.uuid)
        }
    }
    return list
}

private suspend fun kickPlayers(group: String): List<UUID> {
    val list = mutableListOf<UUID>()

    CloudClientServerManager.retrieveServersInGroup(group).forEach { svr ->
        svr.users.forEach {
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
                list.add(it.uuid)
            }
        }
    }
    return list
}