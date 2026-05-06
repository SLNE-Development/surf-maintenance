package dev.slne.surf.maintenance.velocity.redis.event

import dev.slne.surf.api.core.serializer.adventure.component.SerializableComponent
import dev.slne.surf.redis.event.RedisEvent
import kotlinx.serialization.Serializable

@Serializable
data class MaintenanceMessageRedisEvent(
    val message: SerializableComponent
) : RedisEvent()