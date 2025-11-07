package dev.slne.surf.maintenance.core.client.bridge

import dev.slne.surf.cloud.api.client.netty.packet.fireAndForget
import dev.slne.surf.maintenance.api.InternalMaintenanceApi
import dev.slne.surf.maintenance.api.bridge.MaintenanceBridge
import dev.slne.surf.maintenance.core.netty.protocol.serverbound.ServerboundMaintenanceStatusSyncPacket
import org.springframework.stereotype.Component

@InternalMaintenanceApi
@Component
class ClientMaintenanceBridge : MaintenanceBridge {
    override fun setMaintenanceMode(enabled: Boolean) =
        ServerboundMaintenanceStatusSyncPacket(enabled).fireAndForget().also {
            println("Sent maintenance mode change to server (enabled=$enabled)")
        }
}