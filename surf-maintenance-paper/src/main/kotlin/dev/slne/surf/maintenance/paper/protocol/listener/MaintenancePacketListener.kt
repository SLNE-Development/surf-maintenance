package dev.slne.surf.maintenance.paper.protocol.listener

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacketHandler
import dev.slne.surf.maintenance.api.InternalMaintenanceApi
import dev.slne.surf.maintenance.core.netty.protocol.clientbound.ClientboundMaintenanceStatusSyncPacket
import dev.slne.surf.maintenance.paper.plugin
import org.springframework.stereotype.Component

@Component
@InternalMaintenanceApi
class MaintenancePacketListener {
    @SurfNettyPacketHandler
    suspend fun handleMaintenanceStatusSyncPacket(packet: ClientboundMaintenanceStatusSyncPacket) {
        plugin.maintenanceMode = packet.status
    }
}