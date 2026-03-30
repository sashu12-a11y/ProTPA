package me.yourname.protpa.managers;

import me.yourname.protpa.ProTPA;
import me.yourname.protpa.data.RequestData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TPAManager {
    private final ProTPA plugin;
    private final ConfigManager configManager;
    
    private final Map<UUID, Map<UUID, RequestData>> requests = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Long> rtpCooldowns = new HashMap<>();
    private final Map<UUID, Boolean> toggleStatus = new HashMap<>();
    private final Map<UUID, BukkitTask> teleportTasks = new HashMap<>();
    private final Map<UUID, Location> teleportDestinations = new HashMap<>();
    private final Map<String, RequestData> guiRequests = new HashMap<>();
    private final Map<UUID, BukkitTask> countdownTasks = new HashMap<>();

    public TPAManager(ProTPA plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    // ORIGINAL TPA FUNCTIONALITY
    
    public void addRequest(UUID targetUUID, RequestData request) {
        this.requests.computeIfAbsent(targetUUID, k -> new HashMap<>())
                .put(request.getRequesterUUID(), request);
    }

    public RequestData getRequest(UUID targetUUID, UUID requesterUUID) {
        Map<UUID, RequestData> playerRequests = this.requests.get(targetUUID);
        if (playerRequests != null) {
            RequestData request = playerRequests.get(requesterUUID);
            if (request != null && !request.isExpired(configManager.getConfig().getLong("request.timeout", 60L) * 1000)) {
                return request;
            } else {
                playerRequests.remove(requesterUUID);
            }
        }
        return null;
    }

    public RequestData removeRequest(UUID targetUUID, UUID requesterUUID) {
        Map<UUID, RequestData> playerRequests = this.requests.get(targetUUID);
        if (playerRequests != null) {
            return playerRequests.remove(requesterUUID);
        }
        return null;
    }

    public boolean hasRequest(UUID targetUUID, UUID requesterUUID) {
        return getRequest(targetUUID, requesterUUID) != null;
    }

    public void clearRequests(UUID targetUUID) {
        this.requests.remove(targetUUID);
    }

    // COOLDOWN MANAGEMENT
    
    public void addCooldown(UUID playerUUID) {
        if (!playerUUID.toString().isEmpty()) {
            long cooldownSeconds = configManager.getConfig().getLong("cooldown.duration", 30L);
            long expireTime = System.currentTimeMillis() + (cooldownSeconds * 1000);
            cooldowns.put(playerUUID, expireTime);
        }
    }

    public boolean isOnCooldown(UUID playerUUID) {
        if (!cooldowns.containsKey(playerUUID)) {
            return false;
        }
        long expireTime = cooldowns.get(playerUUID);
        if (System.currentTimeMillis() > expireTime) {
            cooldowns.remove(playerUUID);
            return false;
        }
        return true;
    }

    public long getRemainingCooldown(UUID playerUUID) {
        if (!cooldowns.containsKey(playerUUID)) {
            return 0;
        }
        long remainingMs = cooldowns.get(playerUUID) - System.currentTimeMillis();
        return Math.max(0, (remainingMs + 999) / 1000);
    }

    // RTP COOLDOWN MANAGEMENT
    
    public void addRTPCooldown(UUID playerUUID) {
        long cooldownSeconds = configManager.getConfig().getLong("rtp.cooldown", 30L);
        long expireTime = System.currentTimeMillis() + (cooldownSeconds * 1000);
        rtpCooldowns.put(playerUUID, expireTime);
    }

    public boolean isOnRTPCooldown(UUID playerUUID) {
        if (!rtpCooldowns.containsKey(playerUUID)) {
            return false;
        }
        long expireTime = rtpCooldowns.get(playerUUID);
        if (System.currentTimeMillis() > expireTime) {
            rtpCooldowns.remove(playerUUID);
            return false;
        }
        return true;
    }

    public long getRemainingRTPCooldown(UUID playerUUID) {
        if (!rtpCooldowns.containsKey(playerUUID)) {
            return 0;
        }
        long remainingMs = rtpCooldowns.get(playerUUID) - System.currentTimeMillis();
        return Math.max(0, (remainingMs + 999) / 1000);
    }

    // TOGGLE MANAGEMENT
    
    public void setToggle(UUID playerUUID, boolean enabled) {
        toggleStatus.put(playerUUID, enabled);
    }

    public boolean isToggleEnabled(UUID playerUUID) {
        return toggleStatus.getOrDefault(playerUUID, true);
    }

    // TELEPORT DELAY MANAGEMENT
    
    public void storeTeleportDestination(UUID playerUUID, Location destination) {
        teleportDestinations.put(playerUUID, destination);
    }

    public Location getTeleportDestination(UUID playerUUID) {
        return teleportDestinations.get(playerUUID);
    }

    public void removeTeleportDestination(UUID playerUUID) {
        teleportDestinations.remove(playerUUID);
    }

    public void addTeleportTask(UUID playerUUID, BukkitTask task) {
        teleportTasks.put(playerUUID, task);
    }

    public BukkitTask getTeleportTask(UUID playerUUID) {
        return teleportTasks.get(playerUUID);
    }

    public void removeTeleportTask(UUID playerUUID) {
        BukkitTask task = teleportTasks.remove(playerUUID);
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }

    public void cancelAllTasks() {
        teleportTasks.values().forEach(task -> {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
        });
        teleportTasks.clear();
        
        countdownTasks.values().forEach(task -> {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
        });
        countdownTasks.clear();
    }

    // COUNTDOWN TASK MANAGEMENT

    public void storeCountdownTask(UUID playerUUID, BukkitTask task) {
        countdownTasks.put(playerUUID, task);
    }

    public BukkitTask getCountdownTask(UUID playerUUID) {
        return countdownTasks.get(playerUUID);
    }

    public void removeCountdownTask(UUID playerUUID) {
        BukkitTask task = countdownTasks.remove(playerUUID);
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }

    public void clearPlayerData(UUID playerUUID) {
        removeTeleportTask(playerUUID);
        removeCountdownTask(playerUUID);
        removeTeleportDestination(playerUUID);
        clearRequests(playerUUID);
    }

    public Map<UUID, RequestData> getPlayerRequests(UUID playerUUID) {
        return requests.getOrDefault(playerUUID, new HashMap<>());
    }

    public boolean hasAnyRequest(UUID playerUUID) {
        Map<UUID, RequestData> playerRequests = requests.get(playerUUID);
        return playerRequests != null && !playerRequests.isEmpty();
    }

    public UUID getLatestRequester(UUID playerUUID) {
        Map<UUID, RequestData> playerRequests = requests.get(playerUUID);
        if (playerRequests == null || playerRequests.isEmpty()) {
            return null;
        }
        return playerRequests.keySet().iterator().next();
    }

    // GUI STORAGE
    
    public void storeRequestGUI(UUID targetUUID, UUID requesterUUID, RequestData requestData) {
        String key = targetUUID.toString() + "_" + requesterUUID.toString();
        guiRequests.put(key, requestData);
    }

    public RequestData getStoredRequestGUI(UUID targetUUID, UUID requesterUUID) {
        String key = targetUUID.toString() + "_" + requesterUUID.toString();
        return guiRequests.get(key);
    }

    public void removeStoredRequestGUI(UUID targetUUID, UUID requesterUUID) {
        String key = targetUUID.toString() + "_" + requesterUUID.toString();
        guiRequests.remove(key);
    }
}
