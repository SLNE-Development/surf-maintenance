package dev.slne.surf.maintenance.velocity.service

import dev.slne.surf.cloud.api.common.server.CloudServer
import dev.slne.surf.cloud.api.common.server.CommonCloudServer
import dev.slne.surf.cloud.api.common.sync.SyncSet
import dev.slne.surf.cloud.api.common.sync.SyncValue

class MaintenanceService {
    lateinit var maintenanceServers: SyncSet<String>
    lateinit var maintenanceGroups: SyncSet<String>

    lateinit var maintenanceMotd: SyncValue<String>
    lateinit var maintenanceVersion: SyncValue<String>

    fun isMaintenanceEnabled(server: CommonCloudServer) = maintenanceServers.contains(server.name)
    fun isMaintenanceEnabled(group: String) = maintenanceGroups.contains(group)

    fun setMaintenanceModeForServer(server: CloudServer, enabled: Boolean) {
        if (enabled) {
            maintenanceServers.add(server.name)
        } else {
            maintenanceServers.remove(server.name)
        }
    }

    fun setMaintenanceModeForGroup(group: String, enabled: Boolean) {
        if (enabled) {
            maintenanceGroups.add(group)
        } else {
            maintenanceGroups.remove(group)
        }
    }
}