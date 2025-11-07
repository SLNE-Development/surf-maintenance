package dev.slne.surf.maintenance.server.service

import dev.slne.surf.cloud.api.common.sync.SyncSet
import dev.slne.surf.cloud.api.common.sync.SyncValue

class MaintenanceService {
    lateinit var maintenanceServers: SyncSet<String>
    lateinit var maintenanceGroups: SyncSet<String>

    lateinit var maintenanceMotd: SyncValue<String>
    lateinit var maintenanceVersion: SyncValue<String>
}