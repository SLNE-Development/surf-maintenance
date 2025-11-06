package dev.slne.surf.maintenance.api

import dev.slne.surf.surfapi.core.api.util.requiredService
import org.springframework.context.ApplicationContext

@InternalMaintenanceApi
interface MaintenanceContextHolder {
    val context: ApplicationContext

    companion object {
        val instance = requiredService<MaintenanceContextHolder>()
    }
}