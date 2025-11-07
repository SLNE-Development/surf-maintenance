package dev.slne.surf.maintenance.paper.protocol.listener

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacketHandler
import dev.slne.surf.maintenance.core.netty.protocol.clientbound.ClientboundMaintenanceStatusSyncPacket
import dev.slne.surf.maintenance.paper.plugin
import org.springframework.stereotype.Component

@Component
class MaintenancePacketListener {
    @SurfNettyPacketHandler
    suspend fun handleMaintenanceStatusSyncPacket(packet: ClientboundMaintenanceStatusSyncPacket) {
        println("Received maintenance status sync packet: ${packet.status}")
        plugin.maintenanceMode = packet.status
    }
}