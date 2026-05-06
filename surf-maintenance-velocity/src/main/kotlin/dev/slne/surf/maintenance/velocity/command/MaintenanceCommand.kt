package dev.slne.surf.maintenance.velocity.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.maintenance.velocity.MaintenanceService
import dev.slne.surf.maintenance.velocity.MaintenanceService.broadcastMaintenanceMessage
import dev.slne.surf.maintenance.velocity.command.argument.durationArgument
import dev.slne.surf.maintenance.velocity.command.argument.proxiedServerArgument
import dev.slne.surf.maintenance.velocity.config.MaintenanceConfig
import dev.slne.surf.maintenance.velocity.proxy
import dev.slne.surf.maintenance.velocity.redis.event.MaintenanceKickRedisEvent
import dev.slne.surf.maintenance.velocity.redis.event.MaintenanceServerKickRedisEvent
import dev.slne.surf.maintenance.velocity.redisApi
import dev.slne.surf.maintenance.velocity.util.MaintenancePermissions
import net.kyori.adventure.text.format.TextDecoration
import java.time.Duration

fun maintenanceCommand() = commandTree("maintenance") {
    withPermission(MaintenancePermissions.MAINTENANCE_COMMAND)

    literalArgument("#cancel") {
        anyExecutor { source, _ ->
            if (!MaintenanceService.hasActiveCountdown(null)) {
                source.sendText {
                    appendErrorPrefix()
                    error("Es läuft kein globaler Wartungs-Countdown.")
                }
                return@anyExecutor
            }

            MaintenanceService.cancelGlobalCountdown()

            broadcastMaintenanceMessage {
                error("⚠ WARTUNGSARBEITEN", TextDecoration.BOLD)
                appendSpace()
                darkSpacer("|")
                appendSpace()
                white("Die Wartungsarbeiten wurden abgebrochen.")
            }

            source.sendText {
                appendSuccessPrefix()
                success("Der globale Wartungs-Countdown wurde abgebrochen.")
            }
        }
    }

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

        durationArgument("countdown") {
            anyExecutor { executor, args ->
                val countdown: Duration by args

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

                MaintenanceService.startGlobalCountdown(countdown.seconds) {
                    MaintenanceService.enableGlobal()
                    @Suppress("DeferredResultUnused")
                    redisApi.publishEvent(MaintenanceKickRedisEvent())
                }

                executor.sendText {
                    appendSuccessPrefix()
                    success("Der globale Wartungs-Countdown wurde für ")
                    variableValue(formatTime(countdown))
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
                info("Der globale Wartungsmodus ist derzeit ")
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
        proxiedServerArgument("serverName") {
            literalArgument("#cancel") {
                anyExecutor { source, args ->
                    val serverName: String by args

                    if (!MaintenanceService.hasActiveCountdown(serverName)) {
                        source.sendText {
                            appendErrorPrefix()
                            error("Es läuft kein Wartungs-Countdown für den Server $serverName.")
                        }
                        return@anyExecutor
                    }

                    MaintenanceService.cancelServerCountdown(serverName)

                    broadcastMaintenanceMessage {
                        error("⚠ WARTUNGSARBEITEN", TextDecoration.BOLD)
                        appendSpace()
                        darkSpacer("|")
                        appendSpace()
                        white("Der Wartungs-Countdown für den Server ")
                        variableValue(serverName)
                        white(" wurde abgebrochen.")
                    }

                    source.sendText {
                        appendSuccessPrefix()
                        success("Der Wartungs-Countdown für den Server ")
                        variableValue(serverName)
                        success(" wurde abgebrochen.")
                    }
                }
            }

            literalArgument("enable") {
                anyExecutor { executor, args ->
                    val serverName: String by args

                    if (proxy.getServer(serverName).isEmpty) {
                        executor.sendText {
                            appendErrorPrefix()
                            error("Der Server $serverName existiert nicht mehr.")
                        }
                        return@anyExecutor
                    }

                    if (MaintenanceService.isServerEnabled(serverName)) {
                        executor.sendText {
                            appendErrorPrefix()
                            error("Der Wartungsmodus für den Server $serverName ist bereits aktiviert.")
                        }
                        return@anyExecutor
                    }

                    MaintenanceService.enableServer(serverName)

                    executor.sendText {
                        appendSuccessPrefix()
                        success("Der Wartungsmodus wurde für den Server ")
                        variableValue(serverName)
                        success(" aktiviert.")
                    }
                }

                durationArgument("countdown") {
                    anyExecutor { executor, args ->
                        val serverName: String by args
                        val countdown: Duration by args

                        if (proxy.getServer(serverName).isEmpty) {
                            executor.sendText {
                                appendErrorPrefix()
                                error("Der Server $serverName existiert nicht mehr.")
                            }
                            return@anyExecutor
                        }

                        if (MaintenanceService.isServerEnabled(serverName)) {
                            executor.sendText {
                                appendErrorPrefix()
                                error("Der Wartungsmodus für den Server $serverName ist bereits aktiviert.")
                            }
                            return@anyExecutor
                        }

                        if (MaintenanceService.hasActiveCountdown(serverName)) {
                            executor.sendText {
                                appendErrorPrefix()
                                error("Für den Server $serverName läuft bereits ein Wartungs-Countdown.")
                            }
                            return@anyExecutor
                        }

                        MaintenanceService.startServerCountdown(
                            serverName,
                            countdown.seconds
                        ) {
                            MaintenanceService.enableServer(serverName)
                            @Suppress("DeferredResultUnused")
                            redisApi.publishEvent(MaintenanceServerKickRedisEvent(serverName))
                        }

                        executor.sendText {
                            appendSuccessPrefix()
                            success("Der Wartungs-Countdown für ")
                            variableValue(serverName)
                            success(" wurde für ")
                            variableValue(formatTime(countdown))
                            success(" gestartet.")
                        }
                    }
                }
            }

            literalArgument("disable") {
                anyExecutor { executor, args ->
                    val serverName: String by args

                    if (!MaintenanceService.isServerEnabled(serverName) && !MaintenanceService.hasActiveCountdown(
                            serverName
                        )
                    ) {
                        executor.sendText {
                            appendErrorPrefix()
                            error("Der Wartungsmodus für $serverName ist nicht aktiviert.")
                        }
                        return@anyExecutor
                    }

                    MaintenanceService.disableServer(serverName)

                    executor.sendText {
                        appendSuccessPrefix()
                        success("Der Wartungsmodus für den Server ")
                        variableValue(serverName)
                        success(" wurde deaktiviert.")
                    }
                }
            }

            literalArgument("status") {
                anyExecutor { executor, args ->
                    val serverName: String by args

                    executor.sendText {
                        appendInfoPrefix()
                        info("Der Wartungsmodus für den Server ")
                        variableValue(serverName)
                        info(" ist derzeit ")
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
                    val serverName: String by args

                    if (!MaintenanceService.isServerEnabled(serverName)) {
                        executor.sendText {
                            appendErrorPrefix()
                            error("Der Wartungsmodus für den Server $serverName ist nicht aktiviert.")
                        }
                        return@anyExecutor
                    }

                    @Suppress("DeferredResultUnused")
                    redisApi.publishEvent(MaintenanceServerKickRedisEvent(serverName))

                    executor.sendText {
                        appendSuccessPrefix()
                        success("Es wurden alle Spieler des Servers ")
                        variableValue(serverName)
                        success(" ohne Bypass-Berechtigung gekickt.")
                    }
                }
            }
        }
    }
}


private fun formatTime(duration: Duration): String {
    val hours = duration.toHours()
    val minutes = duration.toMinutesPart().toLong()
    val secs = duration.toSecondsPart().toLong()

    fun unit(value: Long, singular: String, plural: String) =
        if (value == 1L) "$value $singular" else "$value $plural"

    return buildString {
        if (hours > 0) append("${unit(hours, "Stunde", "Stunden")} ")
        if (minutes > 0) append("${unit(minutes, "Minute", "Minuten")} ")
        if (secs > 0 || (hours == 0L && minutes == 0L)) {
            append(unit(secs, "Sekunde", "Sekunden"))
        }
    }.trim()
}