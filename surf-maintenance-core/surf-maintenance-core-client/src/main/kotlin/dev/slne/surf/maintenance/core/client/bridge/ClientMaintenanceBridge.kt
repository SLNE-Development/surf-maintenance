package dev.slne.surf.maintenance.core.client.bridge

import dev.slne.surf.cloud.api.client.netty.packet.fireAndForget
import dev.slne.surf.maintenance.api.InternalMaintenanceApi
import dev.slne.surf.maintenance.api.bridge.MaintenanceBridge
import dev.slne.surf.maintenance.core.netty.protocol.clientbound.ClientboundMaintenanceStatusSyncPacket
import org.springframework.stereotype.Component

@InternalMaintenanceApi
@Component
class ClientMaintenanceBridge : MaintenanceBridge {
    override fun setMaintenanceMode(enabled: Boolean) =
        ClientboundMaintenanceStatusSyncPacket(enabled).fireAndForget()
}