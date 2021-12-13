package cn.jiongjionger.log4jfixer;

import cn.jiongjionger.log4jfixer.listener.ChatPacketListener;
import cn.jiongjionger.log4jfixer.listener.ItemRenameListener;
import cn.jiongjionger.log4jfixer.listener.ItemRenameLowVersionListener;
import cn.jiongjionger.log4jfixer.utils.Log4jFixerUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class EastLandLog4jFixer extends JavaPlugin {
    private static EastLandLog4jFixer eastLandLog4jFixer;

    public static EastLandLog4jFixer getInstance() {
        return eastLandLog4jFixer;
    }

    @Override
    public void onEnable() {
        eastLandLog4jFixer = this;
        // 注册物品改名监听器
        registerItemRenameListener();
        // 注册客户端发给服务端与服务端发给客户端的聊天数据包监听器
        ProtocolLibrary.getProtocolManager().addPacketListener(new ChatPacketListener(this, PacketType.Play.Server.CHAT, PacketType.Play.Client.CHAT));
        // 尝试从根源修复Log4j漏洞
        if (Log4jFixerUtils.findInterpolator()) {
            if (Log4jFixerUtils.tryFix()) {
                Bukkit.getLogger().info("[EastLandLog4jFixer] Successfully fixed log4j exploit!");
            } else {
                Bukkit.getLogger().warning("[EastLandLog4jFixer] Log4j exploit fix failed!!!");
            }
        } else {
            Bukkit.getLogger().info("[EastLandLog4jFixer] Your server doesn't need to fix log4j exploit :)");
        }
    }

    /**
     * 注册物品改名监听器
     * 如果是不存在PrepareAnvilEvent事件的低版本服务端，则通过InventoryClickEvent事件拦截
     */
    private void registerItemRenameListener() {
        try {
            Class.forName("org.bukkit.event.inventory.PrepareAnvilEvent");
            Bukkit.getPluginManager().registerEvents(new ItemRenameListener(), this);
            Bukkit.getLogger().info("[EastLandLog4jFixer] Register PrepareAnvilEvent for item renames.");
        } catch (ClassNotFoundException e) {
            Bukkit.getPluginManager().registerEvents(new ItemRenameLowVersionListener(), this);
            Bukkit.getLogger().info("[EastLandLog4jFixer] Register InventoryClickEvent for item renames.");
        }
    }
}