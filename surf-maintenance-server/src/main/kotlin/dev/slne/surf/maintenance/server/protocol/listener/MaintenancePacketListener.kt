package dev.slne.surf.maintenance.server.protocol.listener

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacketHandler
import dev.slne.surf.maintenance.api.surfMaintenanceApi
import dev.slne.surf.maintenance.core.netty.protocol.serverbound.ServerboundMaintenanceStatusSyncPacket
import org.springframework.stereotype.Component

@Component
class MaintenancePacketListener {
    @SurfNettyPacketHandler
    suspend fun handleMaintenanceStatusSyncPacket(packet: ServerboundMaintenanceStatusSyncPacket) {
        println("Handling maintenance mode change packet from client (enabled=${packet.status})")
        surfMaintenanceApi.setMaintenanceMode(packet.status)

        println("Maintenance mode changed to ${packet.status} by client")
    }
}