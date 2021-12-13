package cn.jiongjionger.log4jfixer.listener;

import cn.jiongjionger.log4jfixer.utils.PatternUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemRenameLowVersionListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onRenameItem(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory instanceof AnvilInventory)) {
            return;
        }
        AnvilInventory anvilInventory = (AnvilInventory) inventory;
        if (event.getRawSlot() != 2) {
            return;
        }
        ItemStack item = anvilInventory.getItem(2);
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        if (!item.hasItemMeta()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) {
            return;
        }
        if (PatternUtils.match(meta.getDisplayName())) {
            event.setCancelled(true);
        }
    }
}
