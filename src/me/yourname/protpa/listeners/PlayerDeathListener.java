package me.yourname.protpa.listeners;

import me.yourname.protpa.ProTPA;
import me.yourname.protpa.managers.DeathManager;
import me.yourname.protpa.managers.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    private final ProTPA plugin;
    private final DeathManager deathManager;
    private final MessageManager messageManager;

    public PlayerDeathListener(ProTPA plugin) {
        this.plugin = plugin;
        this.deathManager = plugin.getDeathManager();
        this.messageManager = plugin.getMessageManager();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Record death location
        deathManager.recordDeath(player.getUniqueId(), player.getLocation());

        // Send message about /back command
        if (plugin.getConfigManager().getConfig().getBoolean("back.enabled", true)) {
            player.sendMessage(messageManager.getMessage("back-death-recorded"));
        }
    }
}
