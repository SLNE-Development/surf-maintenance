package dev.slne.surf.maintenance.server.bridge

import dev.slne.surf.cloud.api.server.netty.packet.broadcast
import dev.slne.surf.maintenance.api.InternalMaintenanceApi
import dev.slne.surf.maintenance.api.bridge.MaintenanceBridge
import dev.slne.surf.maintenance.core.netty.protocol.clientbound.ClientboundMaintenanceStatusSyncPacket
import org.springframework.stereotype.Component

@InternalMaintenanceApi
@Component
class ServerMaintenanceBridge : MaintenanceBridge {
    override fun setMaintenanceMode(enabled: Boolean) =
        ClientboundMaintenanceStatusSyncPacket(enabled).broadcast().also {
            println("Broadcasted maintenance mode change to clients (enabled=$enabled)")
        }
}