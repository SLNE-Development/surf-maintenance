package dev.slne.surf.maintenance.velocity.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
data class MaintenanceConfig(
    var enabled: Boolean = false,
    @param:Comment("The MOTD to display when the server is in maintenance mode")
    val maintenanceMotd: String = "",
    @param:Comment("The kick message to display when a player is kicked due to maintenance mode, contains surf formatting")
    val maintenanceKickMessage: String = "",
    val maxPlayerCount: Int = 0,
    val versionMessageEnabled: Boolean = true,
    val versionMessage: String = "Wartungsarbeiten"
)
