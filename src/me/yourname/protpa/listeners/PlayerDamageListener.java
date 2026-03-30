package me.yourname.protpa.listeners;

import me.yourname.protpa.ProTPA;
import me.yourname.protpa.managers.CombatManager;
import me.yourname.protpa.managers.CountdownManager;
import me.yourname.protpa.managers.MessageManager;
import me.yourname.protpa.managers.TPAManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamageListener implements Listener {
    private final ProTPA plugin;
    private final TPAManager tpaManager;
    private final MessageManager messageManager;
    private final CombatManager combatManager;
    private final CountdownManager countdownManager;

    public PlayerDamageListener(ProTPA plugin) {
        this.plugin = plugin;
        this.tpaManager = plugin.getTPAManager();
        this.messageManager = plugin.getMessageManager();
        this.combatManager = plugin.getCombatManager();
        this.countdownManager = plugin.getCountdownManager();
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        // Cancel teleport if player takes damage
        if (tpaManager.getTeleportTask(player.getUniqueId()) != null) {
            tpaManager.removeTeleportTask(player.getUniqueId());
            tpaManager.removeTeleportDestination(player.getUniqueId());
            player.sendMessage(messageManager.getMessage("teleport-cancelled-damage"));
        }

        // Cancel countdown if active
        countdownManager.onPlayerDamage(player.getUniqueId());

        // Mark players in combat
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            combatManager.tagInCombat(player.getUniqueId());
            combatManager.tagInCombat(damager.getUniqueId());
        }
    }
}
