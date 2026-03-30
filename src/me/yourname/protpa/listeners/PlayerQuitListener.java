package me.yourname.protpa.listeners;

import me.yourname.protpa.ProTPA;
import me.yourname.protpa.managers.CountdownManager;
import me.yourname.protpa.managers.TPAManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final ProTPA plugin;
    private final TPAManager tpaManager;
    private final CountdownManager countdownManager;

    public PlayerQuitListener(ProTPA plugin) {
        this.plugin = plugin;
        this.tpaManager = plugin.getTPAManager();
        this.countdownManager = plugin.getCountdownManager();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        tpaManager.clearPlayerData(player.getUniqueId());
        countdownManager.onPlayerQuit(player.getUniqueId());
    }
}
