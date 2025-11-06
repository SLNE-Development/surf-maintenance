package dev.slne.surf.maintenance.api

import org.springframework.beans.factory.getBean

interface SurfMaintenanceApi {
    suspend fun setMaintenanceMode(enabled: Boolean)

    companion object {
        @OptIn(InternalMaintenanceApi::class)
        val INSTANCE get() = MaintenanceContextHolder.instance.context.getBean<SurfMaintenanceApi>()
    }
}

val surfMaintenanceApi get() = SurfMaintenanceApi.INSTANCE