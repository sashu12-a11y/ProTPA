package me.yourname.protpa.commands;

import me.yourname.protpa.ProTPA;
import me.yourname.protpa.managers.MessageManager;
import me.yourname.protpa.managers.TPAManager;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TpDenyCommand implements CommandExecutor {
    private final ProTPA plugin;
    private final TPAManager tpaManager;
    private final MessageManager messageManager;

    public TpDenyCommand(ProTPA plugin) {
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

        Player player = (Player) sender;

        if (!player.hasPermission("protpa.deny")) {
            player.sendMessage(messageManager.getMessage("no-permission"));
            return true;
        }

        UUID playerUUID = player.getUniqueId();

        if (!tpaManager.hasAnyRequest(playerUUID)) {
            player.sendMessage(messageManager.getMessage("no-pending-request"));
            return true;
        }

        UUID requesterUUID = tpaManager.getLatestRequester(playerUUID);
        if (requesterUUID == null) {
            player.sendMessage(messageManager.getMessage("no-pending-request"));
            return true;
        }

        tpaManager.removeRequest(playerUUID, requesterUUID);

        player.sendMessage(messageManager.getMessage("request-denied"));

        Player requester = org.bukkit.Bukkit.getPlayer(requesterUUID);
        if (requester != null && requester.isOnline()) {
            requester.sendMessage(messageManager.getMessage("your-request-denied").replace("{player}", player.getName()));
            playSound(requester, "deny");
        }

        playSound(player, "deny");

        return true;
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
