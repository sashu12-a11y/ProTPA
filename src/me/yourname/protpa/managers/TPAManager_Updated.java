// Extension methods for TPAManager class
// Add these methods to the existing TPAManager class

package me.yourname.protpa.managers;

import me.yourname.protpa.data.RequestData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TPAManager_Updated {
    // Add these fields to the existing TPAManager class:
    // private Map<UUID, Map<UUID, RequestData>> requests;
    // private Map<UUID, Long> rtpCooldowns;

    /**
     * Add a teleport request with type handling
     */
    public void addRequest(UUID targetUUID, RequestData request) {
        this.requests.computeIfAbsent(targetUUID, k -> new HashMap<>())
                .put(request.getRequesterUUID(), request);
    }

    /**
     * Get request data with type information
     */
    public RequestData getRequest(UUID targetUUID, UUID requesterUUID) {
        Map<UUID, RequestData> playerRequests = this.requests.get(targetUUID);
        if (playerRequests != null) {
            return playerRequests.get(requesterUUID);
        }
        return null;
    }

    /**
     * Check if a player has a request from another player
     */
    public boolean hasRequest(UUID targetUUID, UUID requesterUUID) {
        return getRequest(targetUUID, requesterUUID) != null;
    }

    /**
     * Remove request and return the request data
     */
    public RequestData removeRequest(UUID targetUUID, UUID requesterUUID) {
        Map<UUID, RequestData> playerRequests = this.requests.get(targetUUID);
        if (playerRequests != null) {
            return playerRequests.remove(requesterUUID);
        }
        return null;
    }

    /**
     * Check if RTP is on cooldown
     */
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

    /**
     * Get remaining RTP cooldown time in seconds
     */
    public long getRemainingRTPCooldown(UUID playerUUID) {
        if (!rtpCooldowns.containsKey(playerUUID)) {
            return 0;
        }
        long remainingMs = rtpCooldowns.get(playerUUID) - System.currentTimeMillis();
        return Math.max(0, (remainingMs + 999) / 1000);
    }

    /**
     * Add RTP cooldown
     */
    public void addRTPCooldown(UUID playerUUID) {
        long cooldownSeconds = this.configManager.getConfig().getLong("rtp.cooldown", 30L);
        long expireTime = System.currentTimeMillis() + (cooldownSeconds * 1000);
        rtpCooldowns.put(playerUUID, expireTime);
    }

    /**
     * Start teleport delay with cancellation on move/damage
     */
    public void startTeleportDelay(Player player, Location destination, int delayTicks) {
        // Implementation handled by listeners
        // Teleport task tracking should be managed here
    }
}
