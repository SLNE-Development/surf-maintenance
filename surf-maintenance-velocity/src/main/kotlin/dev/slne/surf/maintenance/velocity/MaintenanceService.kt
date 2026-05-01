package dev.slne.surf.maintenance.velocity

import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.redis.sync.set.SyncSet
import dev.slne.surf.redis.sync.value.SyncValue
import kotlinx.coroutines.*
import net.kyori.adventure.text.format.TextDecoration
import kotlin.time.Duration.Companion.seconds

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

    fun cancelGlobalCountdown() = cancelCountdown(null)
    fun cancelServerCountdown(serverName: String) = cancelCountdown(serverName)

    fun startGlobalCountdown(seconds: Long, onEnable: suspend () -> Unit) {
        cancelCountdown(null)
        countdownJobs[null] = scope.launch {
            runCountdown(seconds, null, onEnable)
        }

        broadcastCountdownMessage(seconds, null)
    }

    fun startServerCountdown(serverName: String, seconds: Long, onEnable: suspend () -> Unit) {
        cancelCountdown(serverName)
        countdownJobs[serverName] = scope.launch {
            runCountdown(seconds, serverName, onEnable)
        }

        broadcastCountdownMessage(seconds, serverName)
    }

    fun cancelCountdown(key: String?) {
        countdownJobs.remove(key)?.cancel()
    }

    fun hasActiveCountdown(key: String?): Boolean = countdownJobs[key]?.isActive == true

    fun snapshotServerStates(): Set<String> = _serverMaintenanceSet.snapshot()

    fun shutdown() {
        scope.cancel()
    }

    private suspend fun runCountdown(seconds: Long, key: String?, onEnable: suspend () -> Unit) {
        var remaining = seconds

        while (remaining > 0) {
            if (shouldNotify(remaining, seconds)) {
                broadcastCountdownMessage(remaining, key)
            }
            delay(1.seconds)
            remaining--
        }

        onEnable()
    }

    private fun shouldNotify(remainingSeconds: Long, totalSeconds: Long): Boolean {
        val thresholds = mutableSetOf<Long>()

        var current = totalSeconds
        while (current > 600) {
            current /= 2
            thresholds.add(current)
        }

        val finerSteps = listOf(300L, 180L, 120L, 60L, 30L, 15L, 10L, 5L, 3L, 2L, 1L)
        thresholds.addAll(finerSteps.filter { it < totalSeconds })

        return remainingSeconds in thresholds
    }

    fun broadcastCountdownMessage(seconds: Long, key: String?) {
        plugin.proxy.allPlayers.forEach {
            it.sendText {
                error("⚠ WARTUNGSARBEITEN", TextDecoration.BOLD)
                appendSpace()
                darkSpacer("|")
                appendSpace()
                white("Die Wartungsarbeiten ")
                if (key != null) {
                    white("für den Server ")
                    variableValue(key)
                    appendSpace()
                }
                white("starten in ")
                variableValue(formatTime(seconds))
                white(".")
            }
        }
    }

    private fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        fun unit(value: Long, singular: String, plural: String) =
            if (value == 1L) "$value $singular" else "$value $plural"

        return buildString {
            if (hours > 0) append("${unit(hours, "Stunde", "Stunden")} ")
            if (minutes > 0) append("${unit(minutes, "Minute", "Minuten")} ")
            if (secs > 0 || (hours == 0L && minutes == 0L)) {
                append(unit(secs, "Sekunde", "Sekunden"))
            }
        }.trim()
    }
}
