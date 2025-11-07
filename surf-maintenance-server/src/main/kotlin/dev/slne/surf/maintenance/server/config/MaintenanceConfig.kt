package dev.slne.surf.maintenance.server.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class MaintenanceConfig(
    val maintenanceServers: MutableSet<String> = mutableSetOf(),
    val maintenanceGroups: MutableSet<String> = mutableSetOf(),
    var maintenanceMotd: String = " <#6fa8dc> CASTCRAFTER.DE </#6fa8dc><gray>- </gray><red>WARTUNGSARBEITEN</red><gray> - </gray><white>[</white><light_purple>1.21.10</light_purple><white>]\n" +
            "</white><#5ACFF5>     Weitere Informationen findest du im Discord.",
    var versionMessage: String = "ᴡᴀʀᴛᴜɴɢѕᴀʀʙᴇɪᴛᴇɴ"
)
