package dev.slne.surf.maintenance.core.netty.protocol.clientbound

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.NettyPacket
import kotlinx.serialization.Serializable

@Serializable
@SurfNettyPacket("maintenance:clientbound:status_sync", PacketFlow.CLIENTBOUND)
data class ClientboundMaintenanceStatusSyncPacket(val status: Boolean) : NettyPacket()