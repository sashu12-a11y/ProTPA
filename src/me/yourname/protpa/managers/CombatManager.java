package me.yourname.protpa.managers;

import me.yourname.protpa.ProTPA;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatManager {
    private final ProTPA plugin;
    private final Map<UUID, Long> combatPlayers = new HashMap<>();
    private final Map<UUID, BukkitTask> combatTasks = new HashMap<>();

    public CombatManager(ProTPA plugin) {
        this.plugin = plugin;
    }

    public void tagInCombat(UUID playerUUID) {
        combatPlayers.put(playerUUID, System.currentTimeMillis());

        // Cancel existing task if any
        BukkitTask existingTask = combatTasks.get(playerUUID);
        if (existingTask != null && !existingTask.isCancelled()) {
            existingTask.cancel();
        }

        // Schedule new combat expiration
        long tagDurationSeconds = plugin.getConfigManager().getConfig().getLong("combat.tag-duration", 10L);
        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            combatPlayers.remove(playerUUID);
            combatTasks.remove(playerUUID);
        }, tagDurationSeconds * 20L);

        combatTasks.put(playerUUID, task);
    }

    public boolean isInCombat(UUID playerUUID) {
        if (!combatPlayers.containsKey(playerUUID)) {
            return false;
        }
        long tagDurationMs = plugin.getConfigManager().getConfig().getLong("combat.tag-duration", 10L) * 1000;
        long combatTime = combatPlayers.get(playerUUID);
        if (System.currentTimeMillis() - combatTime > tagDurationMs) {
            combatPlayers.remove(playerUUID);
            BukkitTask task = combatTasks.remove(playerUUID);
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
            return false;
        }
        return true;
    }

    public void removeFromCombat(UUID playerUUID) {
        combatPlayers.remove(playerUUID);
        BukkitTask task = combatTasks.remove(playerUUID);
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }
}
