package me.yourname.protpa.managers;

import me.yourname.protpa.ProTPA;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CountdownManager {
    private final ProTPA plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private final Map<UUID, BukkitTask> activeCountdowns = new HashMap<>();

    public CountdownManager(ProTPA plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.messageManager = plugin.getMessageManager();
    }

    public void startCountdown(Player player, Runnable onComplete, Runnable onCancel) {
        startCountdown(player, onComplete, onCancel, true);
    }

    public void startCountdown(Player player, Runnable onComplete, Runnable onCancel, boolean checkCombat) {
        // Cancel any existing countdown for this player
        cancelCountdown(player.getUniqueId());

        int delaySeconds = configManager.getConfig().getInt("teleport.delay", 3);
        boolean animationsEnabled = configManager.getConfig().getBoolean("animations.enabled", true);
        boolean particlesEnabled = configManager.getConfig().getBoolean("animations.particles.enabled", true);
        boolean soundsEnabled = configManager.getConfig().getBoolean("animations.sounds.enabled", true);

        String subtitle = getMessageWithFallback("titles.subtitle", "title.subtitle", "&7Teleporting...");
        String successTitle = getMessageWithFallback("titles.success", "title.success", "&aSuccess!");
        String cancelTitle = getMessageWithFallback("titles.cancel", "title.cancel", "&cCancelled!");

        int[] secondsLeft = {delaySeconds};
        Location playerLocation = player.getLocation().clone();

        BukkitRunnable countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                // Check if player is still online
                if (!player.isOnline()) {
                    cancelCountdown(player.getUniqueId());
                    if (onCancel != null) onCancel.run();
                    return;
                }

                // Check if player moved (block-level precision)
                if (hasPlayerMoved(player, playerLocation)) {
                    cancelCountdown(player.getUniqueId());
                    showCancelEffect(player, cancelTitle);
                    if (onCancel != null) onCancel.run();
                    return;
                }

                // Check if player is in combat (skip for /back command)
                if (checkCombat && plugin.getCombatManager().isInCombat(player.getUniqueId())) {
                    cancelCountdown(player.getUniqueId());
                    showCancelEffect(player, cancelTitle);
                    if (onCancel != null) onCancel.run();
                    return;
                }

                if (secondsLeft[0] > 0) {
                    // Show animated countdown
                    if (animationsEnabled) {
                        showCountdownTick(player, secondsLeft[0], subtitle);
                    }

                    // Update action bar
                    String actionBar = "§7Teleporting in §a" + secondsLeft[0] + "...";
                    player.sendActionBar(actionBar);

                    // Play tick sound with increasing pitch
                    if (soundsEnabled) {
                        playTickSound(player, secondsLeft[0]);
                    }

                    // Spawn particles
                    if (particlesEnabled) {
                        spawnCountdownParticles(player);
                    }

                    secondsLeft[0]--;
                } else {
                    // Countdown complete - execute teleport
                    cancel();

                    // Show success title
                    if (animationsEnabled) {
                        player.sendTitle(successTitle, "§7Success", 0, 40, 20);
                    }

                    // Play teleport sound
                    if (soundsEnabled) {
                        playTeleportSound(player);
                    }

                    // Execute completion callback
                    if (onComplete != null) {
                        onComplete.run();
                    }

                    // Remove from active countdowns
                    activeCountdowns.remove(player.getUniqueId());
                }
            }
        };

        // Store the task
        BukkitTask task = countdownTask.runTaskTimer(plugin, 0, 20); // Run every second (20 ticks)
        activeCountdowns.put(player.getUniqueId(), task);
    }

    private void showCountdownTick(Player player, int number, String subtitle) {
        String color;
        switch (number) {
            case 3: color = "§a"; break; // Green
            case 2: color = "§e"; break; // Yellow
            case 1: color = "§c"; break; // Red
            default: color = "§f"; break;
        }

        String title = color + number;
        int fadeIn = configManager.getConfig().getInt("animations.titles.fade-in", 0);
        int stay = configManager.getConfig().getInt("animations.titles.stay", 15);
        int fadeOut = configManager.getConfig().getInt("animations.titles.fade-out", 0);
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    private void showCancelEffect(Player player, String cancelTitle) {
        player.sendTitle(cancelTitle, "", 0, 40, 20);
        player.sendActionBar("");
    }

    private void playTickSound(Player player, int number) {
        String soundName = configManager.getConfig().getString("animations.sounds.tick", "UI_BUTTON_CLICK");
        try {
            Sound sound = Sound.valueOf(soundName);
            float pitch = 1.0f + (4 - number) * 0.2f; // 3→1.0, 2→1.2, 1→1.5
            player.playSound(player.getLocation(), sound, 1.0f, pitch);
        } catch (IllegalArgumentException ignored) {
            // Fallback to default sound
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
        }
    }

    private void playTeleportSound(Player player) {
        String soundName = configManager.getConfig().getString("animations.sounds.teleport", "ENTITY_ENDERMAN_TELEPORT");
        try {
            Sound sound = Sound.valueOf(soundName);
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (IllegalArgumentException ignored) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        }
    }

    private void spawnCountdownParticles(Player player) {
        Location loc = player.getLocation().add(0, 1, 0); // Above player's head

        // Use lightweight particles
        try {
            Particle particle = Particle.valueOf(configManager.getConfig().getString("animations.particles.type", "CRIT"));
            player.getWorld().spawnParticle(particle, loc, 3, 0.3, 0.3, 0.3, 0.1); // Small burst
        } catch (IllegalArgumentException ignored) {
            // Fallback to crit particles
            player.getWorld().spawnParticle(Particle.CRIT, loc, 3, 0.3, 0.3, 0.3, 0.1);
        }
    }

    private boolean hasPlayerMoved(Player player, Location originalLocation) {
        Location currentLocation = player.getLocation();
        return originalLocation.getBlockX() != currentLocation.getBlockX() ||
               originalLocation.getBlockY() != currentLocation.getBlockY() ||
               originalLocation.getBlockZ() != currentLocation.getBlockZ();
    }

    private String getMessageWithFallback(String primaryKey, String fallbackKey, String defaultValue) {
        String message = messageManager.getMessage(primaryKey);
        if (message == null || message.trim().isEmpty() || message.equals("&c" + primaryKey)) {
            message = messageManager.getMessage(fallbackKey);
        }
        if (message == null || message.trim().isEmpty() || message.equals("&c" + fallbackKey)) {
            return defaultValue.replace("&", "§");
        }
        return message;
    }

    public void cancelCountdown(UUID playerUUID) {
        BukkitTask task = activeCountdowns.remove(playerUUID);
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }

    public boolean hasActiveCountdown(UUID playerUUID) {
        BukkitTask task = activeCountdowns.get(playerUUID);
        return task != null && !task.isCancelled();
    }

    public void cancelAllCountdowns() {
        activeCountdowns.values().forEach(task -> {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
        });
        activeCountdowns.clear();
    }

    public void onPlayerQuit(UUID playerUUID) {
        cancelCountdown(playerUUID);
    }

    public void onPlayerDamage(UUID playerUUID) {
        if (hasActiveCountdown(playerUUID)) {
            cancelCountdown(playerUUID);
            // Show cancel effect to player if they're online
            Player player = plugin.getServer().getPlayer(playerUUID);
            if (player != null) {
                String cancelTitle = getMessageWithFallback("titles.cancel", "title.cancel", "&cTeleport Cancelled!");
                showCancelEffect(player, cancelTitle);
            }
        }
    }
}