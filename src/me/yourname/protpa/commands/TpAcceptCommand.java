package me.yourname.protpa.commands;

import me.yourname.protpa.ProTPA;
import me.yourname.protpa.data.RequestData;
import me.yourname.protpa.managers.MessageManager;
import me.yourname.protpa.managers.TPAManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;

public class TpAcceptCommand implements CommandExecutor {
    private final ProTPA plugin;
    private final TPAManager tpaManager;
    private final MessageManager messageManager;

    public TpAcceptCommand(ProTPA plugin) {
        this.plugin = plugin;
        this.tpaManager = plugin.getTPAManager();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageManager.getMessage("no-permission"));
            return true;
        }

        Player target = (Player) sender;

        if (!target.hasPermission("protpa.accept")) {
            target.sendMessage(messageManager.getMessage("no-permission"));
            return true;
        }

        // Get the most recent request for this player
        Map<UUID, RequestData> playerRequests = tpaManager.getPlayerRequests(target.getUniqueId());
        if (playerRequests == null || playerRequests.isEmpty()) {
            target.sendMessage(messageManager.getMessage("no-pending-request"));
            return true;
        }

        // Get the first (most recent) request
        UUID requesterUUID = playerRequests.keySet().iterator().next();
        RequestData requestData = playerRequests.get(requesterUUID);
        
        if (requestData == null) {
            target.sendMessage(messageManager.getMessage("no-pending-request"));
            return true;
        }

        Player requester = Bukkit.getPlayer(requesterUUID);
        if (requester == null) {
            target.sendMessage(messageManager.getMessage("requester-offline"));
            tpaManager.removeRequest(target.getUniqueId(), requesterUUID);
            return true;
        }

        RequestData.RequestType type = requestData.getType();

        if (type == RequestData.RequestType.TPA) {
            // Requester teleports to target
            teleportPlayer(requester, target);
        } else if (type == RequestData.RequestType.TPAHERE) {
            // Target teleports to requester
            teleportPlayer(target, requester);
        }

        target.sendMessage(messageManager.getMessage("request-accepted").replace("{player}", requester.getName()));
        requester.sendMessage(messageManager.getMessage("your-request-accepted").replace("{player}", target.getName()));

        tpaManager.removeRequest(target.getUniqueId(), requesterUUID);

        playSound(target, "accept");
        playSound(requester, "accept");

        return true;
    }

    private void teleportPlayer(Player player, Player destination) {
        tpaManager.storeTeleportDestination(player.getUniqueId(), destination.getLocation());

        plugin.getCountdownManager().startCountdown(player,
            // onComplete - execute teleport
            () -> {
                Location destination_loc = tpaManager.getTeleportDestination(player.getUniqueId());
                if (destination_loc != null) {
                    player.teleport(destination_loc);
                    player.sendMessage(messageManager.getMessage("teleported"));
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
