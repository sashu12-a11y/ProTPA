package me.yourname.protpa.listeners;

import me.yourname.protpa.ProTPA;
import me.yourname.protpa.data.RequestData;
import me.yourname.protpa.managers.MessageManager;
import me.yourname.protpa.managers.TPAManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;

public class InventoryClickListener implements Listener {
    private final ProTPA plugin;
    private final TPAManager tpaManager;
    private final MessageManager messageManager;

    public InventoryClickListener(ProTPA plugin) {
        this.plugin = plugin;
        this.tpaManager = plugin.getTPAManager();
        this.messageManager = plugin.getMessageManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();

        // Check if this is a TPA request GUI
        String title = event.getView().getTitle();
        String configTitle = plugin.getConfigManager().getConfig().getString("gui.tpa-request-title", "&aTeleport Request");
        String expectedTitleFormat = configTitle.replace("&", "§");
        
        if (!title.startsWith(expectedTitleFormat)) {
            return;
        }

        if (event.getCurrentItem() == null) return;

        int slot = event.getRawSlot();

        // Get the requester from stored GUI info
        Map<UUID, RequestData> playerRequests = tpaManager.getPlayerRequests(player.getUniqueId());
        if (playerRequests == null || playerRequests.isEmpty()) {
            player.sendMessage(messageManager.getMessage("no-pending-request"));
            player.closeInventory();
            return;
        }

        UUID requesterUUID = playerRequests.keySet().iterator().next();
        RequestData requestData = playerRequests.get(requesterUUID);

        if (requestData == null) {
            player.sendMessage(messageManager.getMessage("no-pending-request"));
            player.closeInventory();
            return;
        }

        Player requester = Bukkit.getPlayer(requesterUUID);

        if (slot == 3) { // ACCEPT button
            if (requester == null) {
                player.sendMessage(messageManager.getMessage("requester-offline"));
                tpaManager.removeRequest(player.getUniqueId(), requesterUUID);
                player.closeInventory();
                return;
            }

            RequestData.RequestType type = requestData.getType();

            if (type == RequestData.RequestType.TPA) {
                // Requester teleports to player
                teleportPlayer(requester, player);
            } else if (type == RequestData.RequestType.TPAHERE) {
                // Player teleports to requester
                teleportPlayer(player, requester);
            }

            player.sendMessage(messageManager.getMessage("request-accepted").replace("{player}", requester.getName()));
            requester.sendMessage(messageManager.getMessage("your-request-accepted").replace("{player}", player.getName()));

            // Play success sound
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            requester.playSound(requester.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

            tpaManager.removeRequest(player.getUniqueId(), requesterUUID);

        } else if (slot == 5) { // DENY button
            player.sendMessage(messageManager.getMessage("request-denied").replace("{player}", requester.getName()));
            if (requester != null) {
                requester.sendMessage(messageManager.getMessage("your-request-denied").replace("{player}", player.getName()));
            }

            // Play deny sound
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
            if (requester != null) {
                requester.playSound(requester.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
            }

            tpaManager.removeRequest(player.getUniqueId(), requesterUUID);
        }

        player.closeInventory();
    }

    private void teleportPlayer(Player player, Player target) {
        // Store destination before teleporting
        Location destination = target.getLocation();
        tpaManager.storeTeleportDestination(player.getUniqueId(), destination);

        plugin.getCountdownManager().startCountdown(player,
            // onComplete - execute teleport
            () -> {
                Location teleportDest = tpaManager.getTeleportDestination(player.getUniqueId());
                if (teleportDest != null) {
                    player.teleport(teleportDest);
                    player.sendMessage(messageManager.getMessage("teleport-complete"));
                }
                tpaManager.removeTeleportDestination(player.getUniqueId());
            },
            // onCancel - cleanup
            () -> {
                tpaManager.removeTeleportDestination(player.getUniqueId());
            }
        );
    }

    private void playSound(Player player, String soundKey) {
        if (plugin.getConfigManager().getConfig().getBoolean("sounds.enabled", true)) {
            String soundName = plugin.getConfigManager().getConfig().getString("sounds." + soundKey, "");
            try {
                Sound sound = Sound.valueOf(soundName);
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }
}
