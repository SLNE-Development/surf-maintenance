package dev.slne.surf.maintenance.velocity.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.maintenance.velocity.configuration
import dev.slne.surf.maintenance.velocity.plugin
import dev.slne.surf.maintenance.velocity.redis.event.MaintenanceKickRedisEvent
import dev.slne.surf.maintenance.velocity.redis.event.MaintenanceStatusChangeRedisEvent
import dev.slne.surf.maintenance.velocity.redisApi
import dev.slne.surf.maintenance.velocity.util.MaintenancePermissions
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun maintenanceCommand() = commandTree("maintenance") {
    withPermission(MaintenancePermissions.MAINTENANCE_COMMAND)
    literalArgument("enable") {
        anyExecutor { executor, _ ->
            if (plugin.enabled) {
                executor.sendText {
                    appendErrorPrefix()
                    error("Der Wartungsmodus ist bereits aktiviert.")
                }
                return@anyExecutor
            }

            redisApi.publishEvent(
                MaintenanceStatusChangeRedisEvent(
                    enabled = true
                )
            )

            plugin.enabled = true

            executor.sendText {
                appendSuccessPrefix()
                success("Der Wartungsmodus wurde aktiviert.")
            }
        }
    }

    literalArgument("disable") {
        anyExecutor { executor, _ ->
            if (!plugin.enabled) {
                executor.sendText {
                    appendErrorPrefix()
                    error("Der Wartungsmodus ist nicht aktiviert.")
                }
                return@anyExecutor
            }

            redisApi.publishEvent(
                MaintenanceStatusChangeRedisEvent(
                    enabled = false
                )
            )

            plugin.enabled = false

            executor.sendText {
                appendSuccessPrefix()
                success("Der Wartungsmodus wurde deaktiviert.")
            }
        }
    }

    literalArgument("status") {
        anyExecutor { executor, _ ->
            executor.sendText {
                appendInfoPrefix()
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
                    appendErrorPrefix()
                    error("Der Wartungsmodus ist nicht aktiviert.")
                }
                return@anyExecutor
            }

            redisApi.publishEvent(MaintenanceKickRedisEvent())

            executor.sendText {
                appendSuccessPrefix()
                success("Es wurden ")
                variableValue("(Ich weiß nicht wie viele Spieler, aber bestimmt ein paar)")
                success(" Spieler gekickt.")
            }
        }
    }

    literalArgument("reload") {
        anyExecutor { executor, _ ->
            configuration.reload()

            executor.sendText {
                appendSuccessPrefix()
                success("Die Wartungskonfiguration wurde neu geladen.")
            }
        }
    }
}