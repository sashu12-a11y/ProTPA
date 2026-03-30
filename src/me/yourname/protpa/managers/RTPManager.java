package me.yourname.protpa.managers;

import me.yourname.protpa.ProTPA;
import me.yourname.protpa.inventory.RTPGUI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Random;

public class RTPManager {
    private final ProTPA plugin;
    private final ConfigManager configManager;
    private final Random random = new Random();

    public RTPManager(ProTPA plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    public Location findSafeLocation(World world, Player player) {
        if (world == null) {
            return null;
        }

        int minRadius = configManager.getConfig().getInt("rtp.min-radius", 100);
        int maxRadius = configManager.getConfig().getInt("rtp.max-radius", 1000);
        int maxAttempts = 20; // More attempts for better success rate

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double radius = minRadius + random.nextDouble() * (maxRadius - minRadius);

            int x = (int) (Math.cos(angle) * radius);
            int z = (int) (Math.sin(angle) * radius);

            // Get the highest block Y at this location
            int highestY = world.getHighestBlockYAt(x, z);

            // Try to find a safe location starting from the highest block going up
            Location safeLocation = findSafeLocationAboveGround(world, x, highestY, z);
            if (safeLocation != null) {
                return safeLocation;
            }
        }

        return null; // No safe location found after all attempts
    }

    private Location findSafeLocationAboveGround(World world, int x, int startY, int z) {
        // Search upward from the highest block for 2 consecutive air blocks
        for (int y = startY; y < startY + 50 && y < world.getMaxHeight(); y++) {
            Block blockAtY = world.getBlockAt(x, y, z);
            Block blockAboveY = world.getBlockAt(x, y + 1, z);
            Block blockBelowY = world.getBlockAt(x, y - 1, z);

            // Found space for player (2 air blocks vertically)
            if (!blockAtY.getType().isSolid() && !blockAboveY.getType().isSolid()) {
                // Make sure there's solid ground below or we landed on something
                if (blockBelowY.getType().isSolid()) {
                    // Check for dangerous blocks
                    if (!isDangerousBlock(blockBelowY)) {
                        return new Location(world, x + 0.5, y, z + 0.5);
                    }
                }
            }
        }

        return null;
    }

    private boolean isDangerousBlock(Block block) {
        Material type = block.getType();
        return type == Material.LAVA ||
               type == Material.FIRE ||
               type == Material.CACTUS ||
               type == Material.MAGMA_BLOCK ||
               type.name().contains("FIRE") ||
               type.name().contains("LAVA");
    }

    public World getWorldByType(RTPGUI.WorldType worldType) {
        if (worldType == null) {
            return null;
        }

        return plugin.getServer().getWorld(worldType.getWorldName());
    }

    public String getWorldDisplayName(RTPGUI.WorldType worldType) {
        switch (worldType) {
            case OVERWORLD: return "Overworld";
            case NETHER: return "Nether";
            case END: return "End";
            default: return "Unknown";
        }
    }
}
