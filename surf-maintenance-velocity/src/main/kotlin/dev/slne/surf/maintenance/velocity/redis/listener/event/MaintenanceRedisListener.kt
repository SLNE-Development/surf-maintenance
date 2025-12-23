package dev.slne.surf.maintenance.velocity.redis.listener.event

import dev.slne.surf.maintenance.velocity.plugin
import dev.slne.surf.maintenance.velocity.redis.event.MaintenanceStatusChangeRedisEvent
import dev.slne.surf.redis.event.OnRedisEvent

object MaintenanceRedisListener {
    @OnRedisEvent
    fun onMaintenanceStatusChange(event: MaintenanceStatusChangeRedisEvent) {
        plugin.enabled = event.enabled
    }
}