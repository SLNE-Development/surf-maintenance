package dev.slne.surf.maintenance.velocity

import dev.slne.surf.redis.sync.set.SyncSet
import dev.slne.surf.redis.sync.value.SyncValue
import kotlinx.coroutines.*
import net.kyori.adventure.text.minimessage.MiniMessage

object MaintenanceService {
    private lateinit var _globalState: SyncValue<Boolean>
    private lateinit var _serverMaintenanceSet: SyncSet<String>

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val countdownJobs = mutableMapOf<String?, Job>()

    val globalEnabled: Boolean get() = _globalState.get()

    fun isServerEnabled(serverName: String): Boolean = serverName in _serverMaintenanceSet

    fun initialize(globalState: SyncValue<Boolean>, serverMaintenanceSet: SyncSet<String>) {
        _globalState = globalState
        _serverMaintenanceSet = serverMaintenanceSet
    }

    fun enableGlobal() {
        cancelCountdown(null)
        _globalState.set(true)
    }

    fun disableGlobal() {
        cancelCountdown(null)
        _globalState.set(false)
    }

    fun enableServer(serverName: String) {
        cancelCountdown(serverName)
        _serverMaintenanceSet.add(serverName)
    }

    fun disableServer(serverName: String) {
        cancelCountdown(serverName)
        _serverMaintenanceSet.remove(serverName)
    }

    fun startGlobalCountdown(seconds: Int, onEnable: suspend () -> Unit) {
        cancelCountdown(null)
        countdownJobs[null] = scope.launch {
            runCountdown(seconds, null, onEnable)
        }
    }

    fun startServerCountdown(serverName: String, seconds: Int, onEnable: suspend () -> Unit) {
        cancelCountdown(serverName)
        countdownJobs[serverName] = scope.launch {
            runCountdown(seconds, serverName, onEnable)
        }
    }

    fun cancelCountdown(key: String?) {
        countdownJobs.remove(key)?.cancel()
    }

    fun hasActiveCountdown(key: String?): Boolean = countdownJobs[key]?.isActive == true

    fun snapshotServerStates(): Set<String> = _serverMaintenanceSet.snapshot()

    fun shutdown() {
        scope.cancel()
    }

    private suspend fun runCountdown(seconds: Int, key: String?, onEnable: suspend () -> Unit) {
        val milestones = setOf(1800, 900, 600, 300, 120, 60, 30, 20, 10, 5, 4, 3, 2, 1)
        var remaining = seconds

        while (remaining > 0) {
            ensureActive()
            if (remaining in milestones) {
                broadcastCountdownMessage(remaining, key)
            }
            delay(1000L)
            remaining--
        }

        onEnable()
    }

    private fun broadcastCountdownMessage(seconds: Int, key: String?) {
        val timeText = formatTime(seconds)
        val serverText = if (key != null) "für <yellow>$key</yellow> " else ""
        val message = MiniMessage.miniMessage().deserialize(
            "<red><b>⚠ WARTUNG</b></red> <dark_gray>|</dark_gray> <white>Die Wartung ${serverText}startet in <yellow>$timeText</yellow>.</white>"
        )
        plugin.proxy.allPlayers.forEach { it.sendMessage(message) }
    }

    private fun formatTime(seconds: Int): String = when {
        seconds >= 3600 -> "${seconds / 3600} Stunde(n)"
        seconds >= 60 -> "${seconds / 60} Minute(n)"
        else -> "$seconds Sekunde(n)"
    }
}
