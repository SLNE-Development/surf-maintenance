package dev.slne.surf.maintenance.paper

import dev.slne.surf.cloud.api.common.CloudInstance
import dev.slne.surf.cloud.api.common.startSpringApplication
import dev.slne.surf.maintenance.core.MaintenanceApplication
import dev.slne.surf.maintenance.core.MaintenanceContextHolderImpl
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap

@Suppress("UnstableApiUsage")
class PaperBootstrap : PluginBootstrap {
    override fun bootstrap(context: BootstrapContext) {
        MaintenanceContextHolderImpl.instance.context =
            CloudInstance.startSpringApplication(MaintenanceApplication::class)
    }
}