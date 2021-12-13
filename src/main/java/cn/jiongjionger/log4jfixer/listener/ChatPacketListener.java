package cn.jiongjionger.log4jfixer.listener;

import cn.jiongjionger.log4jfixer.utils.PatternUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.plugin.Plugin;

public class ChatPacketListener extends PacketAdapter {

    public ChatPacketListener(Plugin plugin, PacketType... types) {
        super(plugin, types);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (PatternUtils.match(event.getPacket().getStrings().read(0))) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        WrappedChatComponent chatComponent = event.getPacket().getChatComponents().read(0);
        if (chatComponent != null && PatternUtils.match(chatComponent.getJson())) {
            event.setCancelled(true);
        }
    }
}
