package cn.jiongjionger.log4jfixer.listener;

import cn.jiongjionger.log4jfixer.utils.PatternUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemRenameListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onRenameItem(PrepareAnvilEvent event) {
        ItemStack item = event.getResult();
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
            event.setResult(null);
        }
    }
}
