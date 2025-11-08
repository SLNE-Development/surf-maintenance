package dev.slne.surf.maintenance.server.netty.listener

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacketHandler
import dev.slne.surf.maintenance.core.netty.packet.serverbound.ServerboundMaintenanceConfigReloadPacket
import dev.slne.surf.maintenance.server.maintenanceService
import dev.slne.surf.maintenance.server.plugin
import org.springframework.stereotype.Component

@Component
class ServerMaintenancePacketListener {
    @SurfNettyPacketHandler
    suspend fun handleConfigReloadPacket(packet: ServerboundMaintenanceConfigReloadPacket) {

        plugin.logger.info("Reloading maintenance configuration as requested...")
        plugin.configuration.reload()

        maintenanceService.maintenanceGroups.clear()
        maintenanceService.maintenanceGroups.addAll(plugin.configuration.config.maintenanceGroups)

        maintenanceService.maintenanceServers.clear()
        maintenanceService.maintenanceServers.addAll(plugin.configuration.config.maintenanceServers)

        maintenanceService.maintenanceMotd.set(plugin.configuration.config.maintenanceMotd)
        maintenanceService.maintenanceVersion.set(plugin.configuration.config.versionMessage)

        plugin.logger.info("Reloaded maintenance configuration.")
    }
}