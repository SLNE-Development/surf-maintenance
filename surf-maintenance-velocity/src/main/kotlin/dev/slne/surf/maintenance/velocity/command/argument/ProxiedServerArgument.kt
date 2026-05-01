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
import dev.slne.surf.maintenance.velocity.plugin
import kotlin.jvm.optionals.getOrNull

open class ProxiedServerArgument(nodeName: String) :
    Argument<String>(nodeName, StringArgumentType::string) {
    override fun getPrimitiveType(): Class<String> {
        return String::class.java
    }

    override fun getArgumentType(): CommandAPIArgumentType? {
        return CommandAPIArgumentType.PRIMITIVE_STRING
    }

    override fun <Source> parseArgument(
        cmdCtx: CommandContext<Source>,
        key: String,
        previousArgs: CommandArguments,
    ): String = plugin.proxy.getServer(StringArgumentType.getString(cmdCtx, key))
        .getOrNull()?.serverInfo?.name
        ?: throw SimpleCommandExceptionType(
            VelocityBrigadierMessage.tooltip(
                buildText {
                    appendErrorPrefix()
                    error("Der Server wurde nicht gefunden.")
                }
            )
        ).create()

    init {
        replaceSuggestions(ArgumentSuggestions.strings {
            plugin.proxy.allServers.map { it.serverInfo.name }.toTypedArray()
        })
    }
}

inline fun CommandTree.proxiedServerArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    ProxiedServerArgument(nodeName).setOptional(optional).apply(block)
)

inline fun Argument<*>.proxiedServerArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    ProxiedServerArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandAPICommand.proxiedServerArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(ProxiedServerArgument(nodeName).setOptional(optional).apply(block))