package dev.slne.surf.maintenance.velocity.command

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.maintenance.velocity.MaintenanceService
import dev.slne.surf.maintenance.velocity.config.MaintenanceConfig
import dev.slne.surf.maintenance.velocity.proxy
import dev.slne.surf.maintenance.velocity.redis.event.MaintenanceKickRedisEvent
import dev.slne.surf.maintenance.velocity.redis.event.MaintenanceServerKickRedisEvent
import dev.slne.surf.maintenance.velocity.redisApi
import dev.slne.surf.maintenance.velocity.util.MaintenancePermissions

fun maintenanceCommand() = commandTree("maintenance") {
    withPermission(MaintenancePermissions.MAINTENANCE_COMMAND)

    literalArgument("enable") {
        anyExecutor { executor, _ ->
            if (MaintenanceService.globalEnabled) {
                executor.sendText {
                    appendErrorPrefix()
                    error("Der globale Wartungsmodus ist bereits aktiviert.")
                }
                return@anyExecutor
            }

            MaintenanceService.enableGlobal()

            executor.sendText {
                appendSuccessPrefix()
                success("Der globale Wartungsmodus wurde aktiviert.")
            }
        }

        integerArgument("countdown", 1, 86400) {
            anyExecutor { executor, args ->
                val seconds = args[0] as Int

                if (MaintenanceService.globalEnabled) {
                    executor.sendText {
                        appendErrorPrefix()
                        error("Der globale Wartungsmodus ist bereits aktiviert.")
                    }
                    return@anyExecutor
                }

                if (MaintenanceService.hasActiveCountdown(null)) {
                    executor.sendText {
                        appendErrorPrefix()
                        error("Es läuft bereits ein globaler Wartungs-Countdown.")
                    }
                    return@anyExecutor
                }

                MaintenanceService.startGlobalCountdown(seconds) {
                    MaintenanceService.enableGlobal()
                    @Suppress("DeferredResultUnused")
                    redisApi.publishEvent(MaintenanceKickRedisEvent())
                }

                executor.sendText {
                    appendSuccessPrefix()
                    success("Der globale Wartungs-Countdown wurde für ")
                    variableValue("$seconds Sekunden")
                    success(" gestartet.")
                }
            }
        }
    }

    literalArgument("disable") {
        anyExecutor { executor, _ ->
            if (!MaintenanceService.globalEnabled && !MaintenanceService.hasActiveCountdown(null)) {
                executor.sendText {
                    appendErrorPrefix()
                    error("Der globale Wartungsmodus ist nicht aktiviert.")
                }
                return@anyExecutor
            }

            MaintenanceService.disableGlobal()

            executor.sendText {
                appendSuccessPrefix()
                success("Der globale Wartungsmodus wurde deaktiviert.")
            }
        }
    }

    literalArgument("status") {
        anyExecutor { executor, _ ->
            executor.sendText {
                appendInfoPrefix()
                info("Globaler Wartungsmodus: ")
                if (MaintenanceService.globalEnabled) {
                    error("aktiviert")
                } else {
                    success("deaktiviert")
                }
                if (MaintenanceService.hasActiveCountdown(null)) {
                    info(" (Countdown läuft)")
                }
                info(".")
            }
        }
    }

    literalArgument("kick") {
        anyExecutor { executor, _ ->
            if (!MaintenanceService.globalEnabled) {
                executor.sendText {
                    appendErrorPrefix()
                    error("Der globale Wartungsmodus ist nicht aktiviert.")
                }
                return@anyExecutor
            }

            @Suppress("DeferredResultUnused")
            redisApi.publishEvent(MaintenanceKickRedisEvent())

            executor.sendText {
                appendSuccessPrefix()
                success("Alle Spieler ohne Bypass-Berechtigung wurden gekickt.")
            }
        }
    }

    literalArgument("reload") {
        anyExecutor { executor, _ ->
            MaintenanceConfig.reloadFromFile()

            executor.sendText {
                appendSuccessPrefix()
                success("Die Wartungskonfiguration wurde neu geladen.")
            }
        }
    }

    literalArgument("server") {
        stringArgument("serverName") {
            replaceSuggestions(ArgumentSuggestions.strings { _ ->
                proxy.allServers.map { it.serverInfo.name }.toTypedArray()
            })

            literalArgument("enable") {
                anyExecutor { executor, args ->
                    val serverName = args[0] as String

                    if (proxy.getServer(serverName).isEmpty) {
                        executor.sendText {
                            appendErrorPrefix()
                            error("Der Server <yellow>$serverName</yellow> existiert nicht.")
                        }
                        return@anyExecutor
                    }

                    if (MaintenanceService.isServerEnabled(serverName)) {
                        executor.sendText {
                            appendErrorPrefix()
                            error("Der Wartungsmodus für <yellow>$serverName</yellow> ist bereits aktiviert.")
                        }
                        return@anyExecutor
                    }

                    MaintenanceService.enableServer(serverName)

                    executor.sendText {
                        appendSuccessPrefix()
                        success("Der Wartungsmodus für <yellow>$serverName</yellow> wurde aktiviert.")
                    }
                }

                integerArgument("countdown", 1, 86400) {
                    anyExecutor { executor, args ->
                        val serverName = args[0] as String
                        val seconds = args[1] as Int

                        if (proxy.getServer(serverName).isEmpty) {
                            executor.sendText {
                                appendErrorPrefix()
                                error("Der Server <yellow>$serverName</yellow> existiert nicht.")
                            }
                            return@anyExecutor
                        }

                        if (MaintenanceService.isServerEnabled(serverName)) {
                            executor.sendText {
                                appendErrorPrefix()
                                error("Der Wartungsmodus für <yellow>$serverName</yellow> ist bereits aktiviert.")
                            }
                            return@anyExecutor
                        }

                        if (MaintenanceService.hasActiveCountdown(serverName)) {
                            executor.sendText {
                                appendErrorPrefix()
                                error("Für den Server <yellow>$serverName</yellow> läuft bereits ein Wartungs-Countdown.")
                            }
                            return@anyExecutor
                        }

                        MaintenanceService.startServerCountdown(serverName, seconds) {
                            MaintenanceService.enableServer(serverName)
                            @Suppress("DeferredResultUnused")
                            redisApi.publishEvent(MaintenanceServerKickRedisEvent(serverName))
                        }

                        executor.sendText {
                            appendSuccessPrefix()
                            success("Der Wartungs-Countdown für <yellow>$serverName</yellow> wurde für ")
                            variableValue("$seconds Sekunden")
                            success(" gestartet.")
                        }
                    }
                }
            }

            literalArgument("disable") {
                anyExecutor { executor, args ->
                    val serverName = args[0] as String

                    if (!MaintenanceService.isServerEnabled(serverName) && !MaintenanceService.hasActiveCountdown(serverName)) {
                        executor.sendText {
                            appendErrorPrefix()
                            error("Der Wartungsmodus für <yellow>$serverName</yellow> ist nicht aktiviert.")
                        }
                        return@anyExecutor
                    }

                    MaintenanceService.disableServer(serverName)

                    executor.sendText {
                        appendSuccessPrefix()
                        success("Der Wartungsmodus für <yellow>$serverName</yellow> wurde deaktiviert.")
                    }
                }
            }

            literalArgument("status") {
                anyExecutor { executor, args ->
                    val serverName = args[0] as String

                    executor.sendText {
                        appendInfoPrefix()
                        info("Wartungsmodus für <yellow>$serverName</yellow>: ")
                        if (MaintenanceService.isServerEnabled(serverName)) {
                            error("aktiviert")
                        } else {
                            success("deaktiviert")
                        }
                        if (MaintenanceService.hasActiveCountdown(serverName)) {
                            info(" (Countdown läuft)")
                        }
                        info(".")
                    }
                }
            }

            literalArgument("kick") {
                anyExecutor { executor, args ->
                    val serverName = args[0] as String

                    if (!MaintenanceService.isServerEnabled(serverName)) {
                        executor.sendText {
                            appendErrorPrefix()
                            error("Der Wartungsmodus für <yellow>$serverName</yellow> ist nicht aktiviert.")
                        }
                        return@anyExecutor
                    }

                    @Suppress("DeferredResultUnused")
                    redisApi.publishEvent(MaintenanceServerKickRedisEvent(serverName))

                    executor.sendText {
                        appendSuccessPrefix()
                        success("Alle Spieler auf <yellow>$serverName</yellow> ohne Bypass-Berechtigung wurden gekickt.")
                    }
                }
            }
        }
    }
}