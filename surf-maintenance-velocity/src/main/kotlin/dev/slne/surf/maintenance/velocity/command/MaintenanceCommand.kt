@file:OptIn(InternalMaintenanceApi::class)

package dev.slne.surf.maintenance.velocity.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.maintenance.api.InternalMaintenanceApi
import dev.slne.surf.maintenance.api.bridge.MaintenanceBridge
import dev.slne.surf.maintenance.core.client.permission.MaintenancePermissions
import dev.slne.surf.maintenance.velocity.plugin
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun maintenanceCommand() = commandTree("maintenance") {
    withPermission(MaintenancePermissions.MAINTENANCE_COMMAND)
    literalArgument("reload") {
        anyExecutor { executor, _ ->
            plugin.configuration.reload()

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

            MaintenanceBridge.INSTANCE.setMaintenanceMode(true)

            executor.sendText {
                appendPrefix()
                success("Der Wartungsmodus wurde aktiviert.")
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

            MaintenanceBridge.INSTANCE.setMaintenanceMode(false)

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
}