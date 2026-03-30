package me.yourname.protpa.commands;

import me.yourname.protpa.ProTPA;
import me.yourname.protpa.managers.MessageManager;
import me.yourname.protpa.managers.TPAManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TpaToggleCommand implements CommandExecutor {
    private final ProTPA plugin;
    private final TPAManager tpaManager;
    private final MessageManager messageManager;

    public TpaToggleCommand(ProTPA plugin) {
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

        if (!player.hasPermission("protpa.toggle")) {
            player.sendMessage(messageManager.getMessage("no-permission"));
            return true;
        }

        UUID playerUUID = player.getUniqueId();
        boolean currentStatus = tpaManager.isToggleEnabled(playerUUID);
        boolean newStatus = !currentStatus;

        tpaManager.setToggle(playerUUID, newStatus);

        if (newStatus) {
            player.sendMessage(messageManager.getMessage("toggle-on"));
        } else {
            player.sendMessage(messageManager.getMessage("toggle-off"));
        }

        return true;
    }
}
