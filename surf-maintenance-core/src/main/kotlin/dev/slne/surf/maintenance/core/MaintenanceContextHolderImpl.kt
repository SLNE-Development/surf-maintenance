package dev.slne.surf.maintenance.core

import com.google.auto.service.AutoService
import dev.slne.surf.maintenance.api.InternalMaintenanceApi
import dev.slne.surf.maintenance.api.MaintenanceContextHolder
import net.kyori.adventure.util.Services
import org.springframework.context.ApplicationContext

@OptIn(InternalMaintenanceApi::class)
@AutoService(MaintenanceContextHolder::class)
class MaintenanceContextHolderImpl : MaintenanceContextHolder, Services.Fallback {
    override lateinit var context: ApplicationContext

    companion object {
        val instance = MaintenanceContextHolder.instance as MaintenanceContextHolderImpl
    }
}