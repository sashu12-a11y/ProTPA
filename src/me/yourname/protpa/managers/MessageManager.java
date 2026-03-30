package me.yourname.protpa.managers;

import me.yourname.protpa.ProTPA;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageManager {
    private final ProTPA plugin;
    private FileConfiguration messages;
    private File messagesFile;

    public MessageManager(ProTPA plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    private void loadMessages() {
        // Ensure data folder exists
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        // Always try to save the resource first
        try {
            plugin.saveResource("messages.yml", true); // Overwrite existing
            plugin.getLogger().info("Successfully saved messages.yml resource to data folder");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save messages.yml resource: " + e.getMessage());
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);

        // Debug: Check if titles section exists
        if (!messages.contains("titles")) {
            plugin.getLogger().warning("titles section not found in messages.yml! File path: " + messagesFile.getAbsolutePath());
            plugin.getLogger().warning("Available keys: " + messages.getKeys(true).toString());
        } else {
            plugin.getLogger().info("titles section found in messages.yml");
        }
    }

    public void reload() {
        loadMessages();
    }

    public void forceReload() {
        loadMessages();
    }

    public String getMessage(String key) {
        String message = messages.getString(key, "&c" + key);

        // Debug logging for titles keys
        if (key.startsWith("titles.") || key.startsWith("title.")) {
            plugin.getLogger().info("Getting message for key '" + key + "': '" + message + "'");
            if (message.equals("&c" + key)) {
                plugin.getLogger().warning("Message key '" + key + "' not found, using fallback!");
            }
        }

        return message.replace("&", "§");
    }

    public String getMessage(String key, String... replacements) {
        String message = getMessage(key);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace("{" + replacements[i] + "}", replacements[i + 1]);
            }
        }
        return message;
    }
}
