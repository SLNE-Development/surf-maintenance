package dev.slne.surf.maintenance.velocity

import dev.slne.surf.maintenance.velocity.redis.listener.event.MaintenanceRedisListener
import dev.slne.surf.redis.RedisApi

val redisLoader = VelocityRedisLoader()
val redisApi get() = redisLoader.redisApi

class VelocityRedisLoader {
    lateinit var redisApi: RedisApi

    fun connect() {
        redisApi = RedisApi.create()
        redisApi.subscribeToEvents(MaintenanceRedisListener)
        redisApi.freezeAndConnect()
    }

    fun disconnect() {
        redisApi.disconnect()
    }
}