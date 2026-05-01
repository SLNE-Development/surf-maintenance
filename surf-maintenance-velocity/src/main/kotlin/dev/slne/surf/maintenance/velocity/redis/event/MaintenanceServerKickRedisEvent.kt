package dev.slne.surf.maintenance.velocity.redis.event

import dev.slne.surf.redis.event.RedisEvent
import kotlinx.serialization.Serializable

@Serializable
data class MaintenanceServerKickRedisEvent(val serverName: String) : RedisEvent()
