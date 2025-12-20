package dev.slne.surf.maintenance.velocity.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class MaintenanceConfig(
    var enabled: Boolean = false,
    var maintenanceMotd: String = "<#6fa8dc> CASTCRAFTER.DE </#6fa8dc><gray>- </gray><red><b>WARTUNGSARBEITEN</red><gray> - </gray><white>[</white><light_purple>1.21.10</light_purple><white>]\n" +
            "</white><#5ACFF5>     Weitere Informationen findest du im Discord.",
    var versionMessage: String = "ᴡᴀʀᴛᴜɴɢѕᴀʀʙᴇɪᴛᴇɴ"
)
