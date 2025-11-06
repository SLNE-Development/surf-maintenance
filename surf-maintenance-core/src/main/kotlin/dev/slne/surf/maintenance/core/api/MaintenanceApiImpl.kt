package dev.slne.surf.maintenance.core.api

import dev.slne.surf.maintenance.api.InternalMaintenanceApi
import dev.slne.surf.maintenance.api.SurfMaintenanceApi
import dev.slne.surf.maintenance.api.bridge.MaintenanceBridge
import org.springframework.stereotype.Component

@Component
@OptIn(InternalMaintenanceApi::class)
class MaintenanceApiImpl : SurfMaintenanceApi {
    override suspend fun setMaintenanceMode(enabled: Boolean) =
        MaintenanceBridge.INSTANCE.setMaintenanceMode(enabled)
}