package dev.slne.surf.maintenance.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import org.bukkit.plugin.java.JavaPlugin

class PaperMain : SuspendingJavaPlugin() {
    var maintenanceMode: Boolean = false
}

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)