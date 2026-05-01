package dev.slne.surf.maintenance.velocity.command.argument

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.velocitypowered.api.command.VelocityBrigadierMessage
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CommandAPIArgumentType
import dev.jorel.commandapi.executors.CommandArguments
import dev.slne.surf.api.core.messages.adventure.buildText
import java.time.Duration

open class DurationArgument(nodeName: String) :
    Argument<Duration>(nodeName, StringArgumentType::string) {
    override fun getPrimitiveType(): Class<Duration> {
        return Duration::class.java
    }

    override fun getArgumentType(): CommandAPIArgumentType? {
        return CommandAPIArgumentType.PRIMITIVE_STRING
    }

    override fun <Source> parseArgument(
        cmdCtx: CommandContext<Source>,
        key: String,
        previousArgs: CommandArguments,
    ): Duration = parseDuration(StringArgumentType.getString(cmdCtx, key))
        ?: throw SimpleCommandExceptionType(
            VelocityBrigadierMessage.tooltip(
                buildText {
                    appendErrorPrefix()
                    error("Bitte gebe eine gültige Dauer an. (z.B. 10s, 5m, 1h, 2d, 1w)")
                }
            )
        ).create()

    init {
        replaceSuggestions(
            ArgumentSuggestions.stringCollection {
                listOf(
                    "30s",
                    "1m",
                    "1h"
                )
            }
        )
    }
}

private val regex = Regex("^(\\d+)([smhdw])$")
private fun parseDuration(input: String): Duration? {
    val match = regex.matchEntire(input.trim()) ?: return null

    val (valueStr, unit) = match.destructured
    val value = valueStr.toLongOrNull() ?: return null

    return when (unit) {
        "s" -> Duration.ofSeconds(value)
        "m" -> Duration.ofMinutes(value)
        "h" -> Duration.ofHours(value)
        "d" -> Duration.ofDays(value)
        "w" -> Duration.ofDays(value * 7)
        else -> null
    }
}

inline fun CommandTree.durationArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    DurationArgument(nodeName).setOptional(optional).apply(block)
)

inline fun Argument<*>.durationArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    DurationArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandAPICommand.durationArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(DurationArgument(nodeName).setOptional(optional).apply(block))