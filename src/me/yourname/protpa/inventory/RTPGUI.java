package me.yourname.protpa.inventory;

import me.yourname.protpa.ProTPA;
import me.yourname.protpa.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class RTPGUI {
    private final ProTPA plugin;
    private final MessageManager messageManager;

    // GUI constants
    private static final int OVERWORLD_SLOT = 2;
    private static final int NETHER_SLOT = 4;
    private static final int END_SLOT = 6;

    public RTPGUI(ProTPA plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    public void openWorldSelectionGUI(Player player) {
        String configTitle = plugin.getConfigManager().getConfig().getString("gui.world-select-title", "&aSelect World");
        String title = configTitle.replace("&", "§");
        Inventory inv = Bukkit.createInventory(null, 9, title);

        // Overworld item
        ItemStack overworldItem = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta overworldMeta = overworldItem.getItemMeta();
        if (overworldMeta != null) {
            overworldMeta.setDisplayName("§a§lO§6§lv§e§le§a§lr§2§lw§b§lo§d§lr§c§ll§5§l ▸ §6§lTELEPORT");
            overworldMeta.setLore(Arrays.asList(
                "§7Click to teleport to the Overworld",
                "§7Safe random location"
            ));
            overworldItem.setItemMeta(overworldMeta);
        }

        // Nether item
        ItemStack netherItem = new ItemStack(Material.NETHERRACK);
        ItemMeta netherMeta = netherItem.getItemMeta();
        if (netherMeta != null) {
            netherMeta.setDisplayName("§4§lN§c§le§6§lt§e§lh§a§le§2§lr§5§l ▸ §c§lTELEPORT");
            netherMeta.setLore(Arrays.asList(
                "§7Click to teleport to the Nether",
                "§7Dangerous random location"
            ));
            netherItem.setItemMeta(netherMeta);
        }

        // End item
        ItemStack endItem = new ItemStack(Material.END_STONE);
        ItemMeta endMeta = endItem.getItemMeta();
        if (endMeta != null) {
            endMeta.setDisplayName("§5§lE§d§ln§6§ld§e§l ▸ §d§lTELEPORT");
            endMeta.setLore(Arrays.asList(
                "§7Click to teleport to the End",
                "§7Mysterious random location"
            ));
            endItem.setItemMeta(endMeta);
        }

        // Fill gaps with glass panes
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        if (glassMeta != null) {
            glassMeta.setDisplayName(" ");
            glass.setItemMeta(glassMeta);
        }

        inv.setItem(0, glass);
        inv.setItem(1, glass);
        inv.setItem(OVERWORLD_SLOT, overworldItem);
        inv.setItem(NETHER_SLOT, netherItem);
        inv.setItem(END_SLOT, endItem);
        inv.setItem(7, glass);
        inv.setItem(8, glass);

        player.openInventory(inv);
    }

    public enum WorldType {
        OVERWORLD("world"),
        NETHER("world_nether"),
        END("world_the_end");

        private final String worldName;

        WorldType(String worldName) {
            this.worldName = worldName;
        }

        public String getWorldName() {
            return worldName;
        }

        public static WorldType fromSlot(int slot) {
            switch (slot) {
                case OVERWORLD_SLOT: return OVERWORLD;
                case NETHER_SLOT: return NETHER;
                case END_SLOT: return END;
                default: return null;
            }
        }
    }
}
