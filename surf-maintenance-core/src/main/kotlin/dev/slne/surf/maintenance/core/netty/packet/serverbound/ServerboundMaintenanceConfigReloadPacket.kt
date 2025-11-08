package dev.slne.surf.maintenance.core.netty.packet.serverbound

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.NettyPacket
import kotlinx.serialization.Serializable

@Serializable
@SurfNettyPacket("maintenance:serverbound:config_reload", PacketFlow.SERVERBOUND)
class ServerboundMaintenanceConfigReloadPacket : NettyPacket()