package me.yourname.protpa.inventory;

import me.yourname.protpa.ProTPA;
import me.yourname.protpa.data.RequestData;
import me.yourname.protpa.managers.MessageManager;
import me.yourname.protpa.managers.TPAManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.UUID;

public class TpaRequestGUI {
    private final ProTPA plugin;
    private final TPAManager tpaManager;
    private final MessageManager messageManager;

    // Slot constants
    private static final int ACCEPT_SLOT = 3;
    private static final int REJECT_SLOT = 5;
    private static final int INFO_SLOT = 4;

    // NBT-like storage for request info (using inventory title)
    private static final String GUI_ID = "tpa_request_";

    public TpaRequestGUI(ProTPA plugin) {
        this.plugin = plugin;
        this.tpaManager = plugin.getTPAManager();
        this.messageManager = plugin.getMessageManager();
    }

    public void openRequestGUI(Player target, Player requester, RequestData requestData) {
        String configTitle = plugin.getConfigManager().getConfig().getString("gui.tpa-request-title", "&aTeleport Request");
        String title = configTitle.replace("&", "§") + " - " + requester.getName();
        Inventory inv = Bukkit.createInventory(null, 9, title);

        // Accept button (Green wool)
        ItemStack acceptItem = new ItemStack(Material.LIME_WOOL);
        ItemMeta acceptMeta = acceptItem.getItemMeta();
        if (acceptMeta != null) {
            acceptMeta.setDisplayName("§a✓ ACCEPT");
            acceptMeta.setLore(Arrays.asList(
                "§7Click to accept",
                "§7this teleport request"
            ));
            acceptItem.setItemMeta(acceptMeta);
        }

        // Reject button (Red wool)
        ItemStack rejectItem = new ItemStack(Material.RED_WOOL);
        ItemMeta rejectMeta = rejectItem.getItemMeta();
        if (rejectMeta != null) {
            rejectMeta.setDisplayName("§c✗ DENY");
            rejectMeta.setLore(Arrays.asList(
                "§7Click to deny",
                "§7this teleport request"
            ));
            rejectItem.setItemMeta(rejectMeta);
        }

        // Info item (Paper/Book)
        ItemStack infoItem = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = infoItem.getItemMeta();
        if (infoMeta != null) {
            String type = requestData.getType() == RequestData.RequestType.TPA ? "TPA" : "TPAHERE";
            infoMeta.setDisplayName("§bRequest Type: " + type);
            infoMeta.setLore(Arrays.asList(
                "§7From: " + requester.getName(),
                "§7To: " + target.getName(),
                "§7Type: " + type,
                " ",
                "§7Request expires in",
                "§769 seconds"
            ));
            infoItem.setItemMeta(infoMeta);
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
        inv.setItem(2, glass);
        inv.setItem(ACCEPT_SLOT, acceptItem);
        inv.setItem(INFO_SLOT, infoItem);
        inv.setItem(REJECT_SLOT, rejectItem);
        inv.setItem(6, glass);
        inv.setItem(7, glass);
        inv.setItem(8, glass);

        // Store request info in a map for GUI handler to reference
        tpaManager.storeRequestGUI(target.getUniqueId(), requester.getUniqueId(), requestData);

        target.openInventory(inv);
    }

    public static String createGUIKey(UUID targetUUID, UUID requesterUUID) {
        return GUI_ID + targetUUID.toString() + "_" + requesterUUID.toString();
    }
}
