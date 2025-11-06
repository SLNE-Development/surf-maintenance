package dev.slne.surf.maintenance.api.bridge

import dev.slne.surf.maintenance.api.InternalMaintenanceApi
import dev.slne.surf.maintenance.api.MaintenanceContextHolder
import org.springframework.beans.factory.getBean

@InternalMaintenanceApi
interface MaintenanceBridge {
    fun setMaintenanceMode(enabled: Boolean)

    companion object {
        @OptIn(InternalMaintenanceApi::class)
        val INSTANCE get() = MaintenanceContextHolder.instance.context.getBean<MaintenanceBridge>()
    }
}