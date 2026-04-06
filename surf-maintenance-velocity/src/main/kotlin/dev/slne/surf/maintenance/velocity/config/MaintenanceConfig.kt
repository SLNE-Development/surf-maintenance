package dev.slne.surf.maintenance.velocity.config

import dev.slne.surf.api.core.config.SpongeYmlConfigClass
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.maintenance.velocity.plugin
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class MaintenanceConfig(
    var enabled: Boolean = false,
    var maintenanceMotd: String = "<#6fa8dc> CASTCRAFTER.DE </#6fa8dc><gray>- </gray><red><b>WARTUNGSARBEITEN</b></red><gray> - </gray><white>[</white><light_purple>1.21.10</light_purple><white>]\n" +
            "</white><#5ACFF5>     Weitere Informationen findest du im Discord.",
    var versionMessage: String = "wartungsarbeiten".toSmallCaps()
) {
    companion object : SpongeYmlConfigClass<MaintenanceConfig>(
        MaintenanceConfig::class.java,
        plugin.dataPath,
        "config.yml"
    )
}
