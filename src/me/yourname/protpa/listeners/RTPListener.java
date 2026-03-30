package me.yourname.protpa.listeners;

import me.yourname.protpa.ProTPA;
import me.yourname.protpa.inventory.RTPGUI;
import me.yourname.protpa.managers.CombatManager;
import me.yourname.protpa.managers.ConfigManager;
import me.yourname.protpa.managers.MessageManager;
import me.yourname.protpa.managers.RTPManager;
import me.yourname.protpa.managers.TPAManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class RTPListener implements Listener {
    private final ProTPA plugin;
    private final RTPManager rtpManager;
    private final TPAManager tpaManager;
    private final MessageManager messageManager;
    private final ConfigManager configManager;
    private final CombatManager combatManager;

    public RTPListener(ProTPA plugin) {
        this.plugin = plugin;
        this.rtpManager = new RTPManager(plugin);
        this.tpaManager = plugin.getTPAManager();
        this.messageManager = plugin.getMessageManager();
        this.configManager = plugin.getConfigManager();
        this.combatManager = plugin.getCombatManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();

        // Check if this is an RTP world selection GUI
        String title = event.getView().getTitle();
        String configTitle = configManager.getConfig().getString("gui.world-select-title", "&aSelect World");
        String expectedTitle = configTitle.replace("&", "§");

        if (!title.equals(expectedTitle)) {
            return;
        }

        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;

        int slot = event.getRawSlot();
        RTPGUI.WorldType worldType = RTPGUI.WorldType.fromSlot(slot);

        if (worldType == null) return;

        player.closeInventory();

        // Check permissions and cooldowns
        if (!player.hasPermission("protpa.rtp")) {
            player.sendMessage(messageManager.getMessage("no-permission"));
            return;
        }

        if (configManager.getConfig().getBoolean("combat.block-teleport", true) && combatManager.isInCombat(player.getUniqueId())) {
            if (!player.hasPermission("protpa.bypass.combat")) {
                player.sendMessage(messageManager.getMessage("in-combat"));
                return;
            }
        }

        if (tpaManager.isOnRTPCooldown(player.getUniqueId())) {
            long remainingTime = tpaManager.getRemainingRTPCooldown(player.getUniqueId());
            player.sendMessage(messageManager.getMessage("on-cooldown").replace("{time}", String.valueOf(remainingTime)));
            return;
        }

        // Get the selected world
        World selectedWorld = rtpManager.getWorldByType(worldType);
        if (selectedWorld == null) {
            player.sendMessage(messageManager.getMessage("rtp-world-not-found").replace("{world}", rtpManager.getWorldDisplayName(worldType)));
            return;
        }

        player.sendMessage(messageManager.getMessage("rtp-searching").replace("{world}", rtpManager.getWorldDisplayName(worldType)));

        // Start RTP process asynchronously
        new BukkitRunnable() {
            @Override
            public void run() {
                Location safeLocation = rtpManager.findSafeLocation(selectedWorld, player);

                if (safeLocation == null) {
                    player.sendMessage(messageManager.getMessage("rtp-fail"));
                    return;
                }

                tpaManager.storeTeleportDestination(player.getUniqueId(), safeLocation);
                tpaManager.addRTPCooldown(player.getUniqueId());

                // Start countdown
                plugin.getCountdownManager().startCountdown(player,
                    () -> {
                        // Teleport completion logic
                        if (!player.isOnline()) {
                            return;
                        }

                        Location destination = tpaManager.getTeleportDestination(player.getUniqueId());
                        if (destination != null) {
                            player.teleport(destination);
                            player.sendMessage(messageManager.getMessage("rtp-complete").replace("{world}", rtpManager.getWorldDisplayName(worldType)));
                            playSound(player, "teleport");

                            // Spawn particles at teleport location
                            if (configManager.getConfig().getBoolean("animations.particles.enabled", true)) {
                                destination.getWorld().spawnParticle(
                                    org.bukkit.Particle.valueOf(configManager.getConfig().getString("animations.particles.type", "PORTAL")),
                                    destination,
                                    50,
                                    0.5, 0.5, 0.5,
                                    0.1
                                );
                            }
                        }

                        tpaManager.removeTeleportDestination(player.getUniqueId());
                        tpaManager.removeTeleportTask(player.getUniqueId());
                        tpaManager.removeCountdownTask(player.getUniqueId());
                    },
                    () -> {
                        // Cancellation cleanup
                        tpaManager.removeTeleportDestination(player.getUniqueId());
                        tpaManager.removeTeleportTask(player.getUniqueId());
                        tpaManager.removeCountdownTask(player.getUniqueId());
                    }
                );
            }
        }.runTaskAsynchronously(plugin);
    }

    private void playSound(Player player, String soundKey) {
        if (configManager.getConfig().getBoolean("sounds.enabled", true)) {
            String soundName = configManager.getConfig().getString("sounds." + soundKey, "");
            try {
                Sound sound = Sound.valueOf(soundName);
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }
}
