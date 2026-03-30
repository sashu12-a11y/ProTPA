package me.yourname.protpa.commands;

import me.yourname.protpa.ProTPA;
import me.yourname.protpa.managers.ConfigManager;
import me.yourname.protpa.managers.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {
    private final ProTPA plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;

    public ReloadCommand(ProTPA plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("protpa.reload")) {
            sender.sendMessage(messageManager.getMessage("no-permission"));
            return true;
        }

        if (args.length < 1 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage("&cUsage: /protpa reload");
            return true;
        }

        plugin.reloadConfig();
        configManager.reload();
        messageManager.reload();

        sender.sendMessage("&aProTPA plugin reloaded!");
        return true;
    }
}
