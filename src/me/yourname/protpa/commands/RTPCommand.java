package me.yourname.protpa.commands;

import me.yourname.protpa.ProTPA;
import me.yourname.protpa.inventory.RTPGUI;
import me.yourname.protpa.managers.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RTPCommand implements CommandExecutor {
    private final ProTPA plugin;
    private final MessageManager messageManager;
    private final RTPGUI rtpGUI;

    public RTPCommand(ProTPA plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
        this.rtpGUI = new RTPGUI(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageManager.getMessage("no-permission"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("protpa.rtp")) {
            player.sendMessage(messageManager.getMessage("no-permission"));
            return true;
        }

        // Open the world selection GUI
        rtpGUI.openWorldSelectionGUI(player);
        player.sendMessage(messageManager.getMessage("rtp-select-world"));

        return true;
    }
}
