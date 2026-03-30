package me.yourname.protpa.commands;

import me.yourname.protpa.ProTPA;
import me.yourname.protpa.managers.DeathManager;
import me.yourname.protpa.managers.MessageManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackCommand implements CommandExecutor {
    private final ProTPA plugin;
    private final DeathManager deathManager;
    private final MessageManager messageManager;

    public BackCommand(ProTPA plugin) {
        this.plugin = plugin;
        this.deathManager = plugin.getDeathManager();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageManager.getMessage("no-permission"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("protpa.back")) {
            player.sendMessage(messageManager.getMessage("no-permission"));
            return true;
        }

        if (!plugin.getConfigManager().getConfig().getBoolean("back.enabled", true)) {
            player.sendMessage(messageManager.getMessage("back-disabled"));
            return true;
        }

        // Check if player has a death location
        if (!deathManager.hasDeathLocation(player.getUniqueId())) {
            player.sendMessage(messageManager.getMessage("back-no-death-location"));
            return true;
        }

        Location deathLocation = deathManager.getDeathLocation(player.getUniqueId());
        if (deathLocation == null) {
            player.sendMessage(messageManager.getMessage("back-location-expired"));
            deathManager.clearDeathLocation(player.getUniqueId());
            return true;
        }

        // Start countdown and teleport after complete
        plugin.getCountdownManager().startCountdown(player,
            // onComplete - execute teleport
            () -> {
                Location finalLocation = deathManager.getDeathLocation(player.getUniqueId());
                if (finalLocation != null) {
                    player.teleport(finalLocation);
                    player.sendMessage(messageManager.getMessage("back-teleported"));
                    
                    // Play sound effect
                    if (plugin.getConfigManager().getConfig().getBoolean("sounds.enabled", true)) {
                        try {
                            Sound sound = Sound.valueOf(plugin.getConfigManager().getConfig().getString("sounds.teleport", "ENTITY_ENDERMAN_TELEPORT"));
                            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                } else {
                    player.sendMessage(messageManager.getMessage("back-location-expired"));
                }
                deathManager.clearDeathLocation(player.getUniqueId());
            },
            // onCancel - cleanup
            () -> {
                deathManager.clearDeathLocation(player.getUniqueId());
            },
            false  // Don't check combat status for /back
        );

        return true;
    }
}
