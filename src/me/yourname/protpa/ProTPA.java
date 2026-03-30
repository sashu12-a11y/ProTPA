package me.yourname.protpa;

import me.yourname.protpa.commands.*;
import me.yourname.protpa.listeners.*;
import me.yourname.protpa.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for ProTPA - Advanced Minecraft Teleportation Plugin
 *
 * This plugin provides comprehensive teleportation features including:
 * - TPA/TPAHERE requests with animated countdowns
 * - Random teleportation (RTP) with safe location finding
 * - Death location recovery (/back command)
 * - GUI interfaces for user interaction
 * - Configurable animations and safety features
 *
 * @author Sashank
 * @version 1.0.0
 * @since 1.20
 */
public class ProTPA extends JavaPlugin {

    // Manager instances for handling different aspects of the plugin
    private ConfigManager configManager;
    private MessageManager messageManager;
    private TPAManager tpaManager;
    private CombatManager combatManager;
    private CountdownManager countdownManager;
    private DeathManager deathManager;

    /**
     * Called when the plugin is enabled.
     * Initializes all managers, registers commands and event listeners.
     */
    @Override
    public void onEnable() {
        // Initialize all manager classes
        initializeManagers();

        // Force reload messages after all managers are initialized
        messageManager.forceReload();

        // Register all plugin commands
        registerCommands();

        // Register all event listeners
        registerListeners();

        getLogger().info("ProTPA v" + getDescription().getVersion() + " enabled successfully!");
    }

    /**
     * Initializes all manager instances.
     * Order is important as some managers depend on others.
     */
    private void initializeManagers() {
        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        tpaManager = new TPAManager(this);
        combatManager = new CombatManager(this);
        countdownManager = new CountdownManager(this);
        deathManager = new DeathManager(this);
    }

    /**
     * Registers all plugin commands with their respective executors.
     */
    private void registerCommands() {
        getCommand("tpa").setExecutor(new TpaCommand(this));
        getCommand("tpahere").setExecutor(new TpahereCommand(this));
        getCommand("tpaccept").setExecutor(new TpAcceptCommand(this));
        getCommand("tpdeny").setExecutor(new TpDenyCommand(this));
        getCommand("tpatoggle").setExecutor(new TpaToggleCommand(this));
        getCommand("rtp").setExecutor(new RTPCommand(this));
        getCommand("back").setExecutor(new BackCommand(this));
        getCommand("protpa").setExecutor(new ReloadCommand(this));
    }

    /**
     * Registers all event listeners for handling player interactions.
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        getServer().getPluginManager().registerEvents(new RTPListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
    }

    /**
     * Called when the plugin is disabled.
     * Cleans up all running tasks and saves data.
     */
    @Override
    public void onDisable() {
        // Cancel all pending tasks to prevent memory leaks
        if (tpaManager != null) {
            tpaManager.cancelAllTasks();
        }
        if (countdownManager != null) {
            countdownManager.cancelAllCountdowns();
        }

        getLogger().info("ProTPA disabled successfully!");
    }

    // ===== GETTER METHODS =====

    /**
     * Gets the configuration manager instance.
     * @return ConfigManager instance
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Gets the message manager instance.
     * @return MessageManager instance
     */
    public MessageManager getMessageManager() {
        return messageManager;
    }

    /**
     * Gets the TPA manager instance.
     * @return TPAManager instance
     */
    public TPAManager getTPAManager() {
        return tpaManager;
    }

    /**
     * Gets the combat manager instance.
     * @return CombatManager instance
     */
    public CombatManager getCombatManager() {
        return combatManager;
    }

    /**
     * Gets the countdown manager instance.
     * @return CountdownManager instance
     */
    public CountdownManager getCountdownManager() {
        return countdownManager;
    }

    /**
     * Gets the death manager instance.
     * @return DeathManager instance
     */
    public DeathManager getDeathManager() {
        return deathManager;
    }
}
