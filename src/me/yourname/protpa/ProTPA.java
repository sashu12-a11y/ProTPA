package me.yourname.protpa;

import me.yourname.protpa.commands.*;
import me.yourname.protpa.listeners.*;
import me.yourname.protpa.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class ProTPA extends JavaPlugin {
    private ConfigManager configManager;
    private MessageManager messageManager;
    private TPAManager tpaManager;
    private CombatManager combatManager;
    private CountdownManager countdownManager;
    private DeathManager deathManager;

    @Override
    public void onEnable() {
        // Initialize managers
        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        tpaManager = new TPAManager(this);
        combatManager = new CombatManager(this);
        countdownManager = new CountdownManager(this);
        deathManager = new DeathManager(this);

        // Force reload messages after all managers are initialized
        messageManager.forceReload();

        // Register commands
        getCommand("tpa").setExecutor(new TpaCommand(this));
        getCommand("tpahere").setExecutor(new TpahereCommand(this));
        getCommand("tpaccept").setExecutor(new TpAcceptCommand(this));
        getCommand("tpdeny").setExecutor(new TpDenyCommand(this));
        getCommand("tpatoggle").setExecutor(new TpaToggleCommand(this));
        getCommand("rtp").setExecutor(new RTPCommand(this));
        getCommand("back").setExecutor(new BackCommand(this));
        getCommand("protpa").setExecutor(new ReloadCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        getServer().getPluginManager().registerEvents(new RTPListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);

        getLogger().info("ProTPA v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        if (tpaManager != null) {
            tpaManager.cancelAllTasks();
        }
        if (countdownManager != null) {
            countdownManager.cancelAllCountdowns();
        }
        getLogger().info("ProTPA disabled!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public TPAManager getTPAManager() {
        return tpaManager;
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }

    public CountdownManager getCountdownManager() {
        return countdownManager;
    }

    public DeathManager getDeathManager() {
        return deathManager;
    }
}
