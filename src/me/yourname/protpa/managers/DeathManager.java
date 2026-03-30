package me.yourname.protpa.managers;

import me.yourname.protpa.ProTPA;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeathManager {
    private final ProTPA plugin;
    private final Map<UUID, DeathLocation> deathLocations = new HashMap<>();
    private final int deathLocationExpirySeconds;

    public DeathManager(ProTPA plugin) {
        this.plugin = plugin;
        this.deathLocationExpirySeconds = plugin.getConfigManager().getConfig().getInt("back.expiry-seconds", 300); // 5 minutes default
    }

    public void recordDeath(UUID playerUUID, Location deathLocation) {
        deathLocations.put(playerUUID, new DeathLocation(deathLocation, System.currentTimeMillis()));

        // Schedule expiry
        new BukkitRunnable() {
            @Override
            public void run() {
                if (deathLocations.containsKey(playerUUID)) {
                    DeathLocation recorded = deathLocations.get(playerUUID);
                    if (System.currentTimeMillis() - recorded.getTimestamp() >= deathLocationExpirySeconds * 1000L) {
                        deathLocations.remove(playerUUID);
                    }
                }
            }
        }.runTaskLater(plugin, deathLocationExpirySeconds * 20L); // Convert seconds to ticks
    }

    public Location getDeathLocation(UUID playerUUID) {
        DeathLocation deathLoc = deathLocations.get(playerUUID);
        if (deathLoc == null) {
            return null;
        }

        // Check if expired
        long elapsedSeconds = (System.currentTimeMillis() - deathLoc.getTimestamp()) / 1000;
        if (elapsedSeconds >= deathLocationExpirySeconds) {
            deathLocations.remove(playerUUID);
            return null;
        }

        return deathLoc.getLocation();
    }

    public long getRemainingTime(UUID playerUUID) {
        DeathLocation deathLoc = deathLocations.get(playerUUID);
        if (deathLoc == null) {
            return 0;
        }

        long elapsedSeconds = (System.currentTimeMillis() - deathLoc.getTimestamp()) / 1000;
        long remaining = deathLocationExpirySeconds - elapsedSeconds;
        return Math.max(0, remaining);
    }

    public boolean hasDeathLocation(UUID playerUUID) {
        return getDeathLocation(playerUUID) != null;
    }

    public void clearDeathLocation(UUID playerUUID) {
        deathLocations.remove(playerUUID);
    }

    private static class DeathLocation {
        private final Location location;
        private final long timestamp;

        DeathLocation(Location location, long timestamp) {
            this.location = location.clone();
            this.timestamp = timestamp;
        }

        public Location getLocation() {
            return location;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
