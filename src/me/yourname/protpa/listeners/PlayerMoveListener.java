package me.yourname.protpa.listeners;

import me.yourname.protpa.ProTPA;
import me.yourname.protpa.managers.MessageManager;
import me.yourname.protpa.managers.TPAManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    private final ProTPA plugin;
    private final TPAManager tpaManager;
    private final MessageManager messageManager;

    public PlayerMoveListener(ProTPA plugin) {
        this.plugin = plugin;
        this.tpaManager = plugin.getTPAManager();
        this.messageManager = plugin.getMessageManager();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Only check if player actually moved (not just rotation)
        if (event.getFrom().getX() == event.getTo().getX()
                && event.getFrom().getY() == event.getTo().getY()
                && event.getFrom().getZ() == event.getTo().getZ()) {
            return;
        }

        Player player = event.getPlayer();

        if (tpaManager.getTeleportTask(player.getUniqueId()) != null) {
            tpaManager.removeTeleportTask(player.getUniqueId());
            tpaManager.removeTeleportDestination(player.getUniqueId());
            player.sendMessage(messageManager.getMessage("teleport-cancelled-move"));
        }
    }
}
