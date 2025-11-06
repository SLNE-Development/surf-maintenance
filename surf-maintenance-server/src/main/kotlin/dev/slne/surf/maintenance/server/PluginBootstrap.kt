package dev.slne.surf.maintenance.server

import dev.slne.surf.cloud.api.common.CloudInstance
import dev.slne.surf.cloud.api.common.startSpringApplication
import dev.slne.surf.cloud.api.server.plugin.bootstrap.BootstrapContext
import dev.slne.surf.cloud.api.server.plugin.bootstrap.StandalonePluginBootstrap
import dev.slne.surf.maintenance.core.MaintenanceApplication
import dev.slne.surf.maintenance.core.MaintenanceContextHolderImpl

class PluginBootstrap : StandalonePluginBootstrap {
    override suspend fun bootstrap(context: BootstrapContext) {
        MaintenanceContextHolderImpl.instance.context =
            CloudInstance.startSpringApplication(MaintenanceApplication::class)
    }
}