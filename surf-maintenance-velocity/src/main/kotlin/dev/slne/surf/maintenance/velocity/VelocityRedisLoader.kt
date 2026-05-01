package dev.slne.surf.maintenance.velocity

import dev.slne.surf.maintenance.velocity.redis.listener.event.MaintenanceRedisListener
import dev.slne.surf.redis.RedisApi
import dev.slne.surf.redis.sync.set.SyncSet
import dev.slne.surf.redis.sync.value.SyncValue
import kotlin.time.Duration.Companion.days

val redisLoader = VelocityRedisLoader()
val redisApi get() = redisLoader.redisApi

class VelocityRedisLoader {
    lateinit var redisApi: RedisApi
    lateinit var globalState: SyncValue<Boolean>
        private set
    lateinit var serverMaintenanceSet: SyncSet<String>
        private set

    fun connect(configEnabled: Boolean) {
        redisApi = RedisApi.create()
        redisApi.subscribeToEvents(MaintenanceRedisListener)

        globalState = redisApi.createSyncValue(
            id = "maintenance:global",
            defaultValue = configEnabled,
            ttl = 365.days
        )
        serverMaintenanceSet = redisApi.createSyncSet(
            id = "maintenance:servers",
            ttl = 365.days
        )

        redisApi.freezeAndConnect()
    }

    fun disconnect() {
        redisApi.disconnect()
    }
}