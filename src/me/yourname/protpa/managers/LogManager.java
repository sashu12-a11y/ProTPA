package me.yourname.protpa.managers;

import me.yourname.protpa.ProTPA;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogManager {
    private final ProTPA plugin;
    private final File logsFile;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LogManager(ProTPA plugin) {
        this.plugin = plugin;
        this.logsFile = new File(plugin.getDataFolder(), "logs.txt");
        if (!logsFile.exists()) {
            try {
                logsFile.getParentFile().mkdirs();
                logsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create logs file!");
            }
        }
    }

    public void log(String message) {
        try (FileWriter writer = new FileWriter(logsFile, true)) {
            String timestamp = "[" + LocalDateTime.now().format(dateTimeFormatter) + "] ";
            writer.write(timestamp + message + "\n");
        } catch (IOException e) {
            plugin.getLogger().warning("Could not write to logs file!");
        }
    }

    public void logTeleportRequest(String requester, String target, String type) {
        log("TeleportRequest (" + type + ") - Requester: " + requester + " -> Target: " + target);
    }

    public void logTeleportAccept(String acceptor, String requester, String type) {
        log("TeleportAccept (" + type + ") - Acceptor: " + acceptor + " -> Requester: " + requester);
    }

    public void logRTP(String player) {
        log("RTP - Player: " + player);
    }
}
