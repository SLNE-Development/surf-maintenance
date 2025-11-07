package dev.slne.surf.maintenance.core.netty.protocol.serverbound

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.NettyPacket
import kotlinx.serialization.Serializable

@Serializable
@SurfNettyPacket("maintenance:serverbound:status_sync", PacketFlow.SERVERBOUND)
data class ServerboundMaintenanceStatusSyncPacket(val status: Boolean) : NettyPacket()