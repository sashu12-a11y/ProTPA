package me.yourname.protpa.managers;

import me.yourname.protpa.ProTPA;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final ProTPA plugin;
    private FileConfiguration config;

    public ConfigManager(ProTPA plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
