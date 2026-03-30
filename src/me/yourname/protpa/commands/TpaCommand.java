package me.yourname.protpa.commands;

import me.yourname.protpa.ProTPA;
import me.yourname.protpa.data.RequestData;
import me.yourname.protpa.inventory.TpaRequestGUI;
import me.yourname.protpa.managers.ConfigManager;
import me.yourname.protpa.managers.MessageManager;
import me.yourname.protpa.managers.TPAManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaCommand implements CommandExecutor {
    private final ProTPA plugin;
    private final TPAManager tpaManager;
    private final MessageManager messageManager;
    private final ConfigManager configManager;

    public TpaCommand(ProTPA plugin) {
        this.plugin = plugin;
        this.tpaManager = plugin.getTPAManager();
        this.messageManager = plugin.getMessageManager();
        this.configManager = plugin.getConfigManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageManager.getMessage("no-permission"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("protpa.use")) {
            player.sendMessage(messageManager.getMessage("no-permission"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(messageManager.getMessage("usage-tpa"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(messageManager.getMessage("player-not-found").replace("{player}", args[0]));
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(messageManager.getMessage("cannot-request-self"));
            return true;
        }

        if (!tpaManager.isToggleEnabled(target.getUniqueId())) {
            player.sendMessage(messageManager.getMessage("target-toggled-off").replace("{player}", target.getName()));
            return true;
        }

        if (tpaManager.isOnCooldown(player.getUniqueId())) {
            long remainingTime = tpaManager.getRemainingCooldown(player.getUniqueId());
            player.sendMessage(messageManager.getMessage("on-cooldown").replace("{time}", String.valueOf(remainingTime)));
            return true;
        }

        if (tpaManager.hasRequest(target.getUniqueId(), player.getUniqueId())) {
            player.sendMessage(messageManager.getMessage("already-has-request").replace("{player}", target.getName()));
            return true;
        }

        RequestData request = new RequestData(player.getUniqueId(), RequestData.RequestType.TPA);
        tpaManager.addRequest(target.getUniqueId(), request);

        tpaManager.addCooldown(player.getUniqueId());

        player.sendMessage(messageManager.getMessage("request-sent").replace("{player}", target.getName()));
        
        // Show GUI to target
        TpaRequestGUI gui = new TpaRequestGUI(plugin);
        gui.openRequestGUI(target, player, request);

        if (configManager.getConfig().getBoolean("sounds.enabled", true)) {
            String soundName = configManager.getConfig().getString("sounds.request", "ENTITY_EXPERIENCE_ORB_PICKUP");
            try {
                Sound sound = Sound.valueOf(soundName);
                target.playSound(target.getLocation(), sound, 1.0f, 1.0f);
            } catch (IllegalArgumentException ignored) {
            }
        }

        return true;
    }
}
