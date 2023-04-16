package com.namemc.mlv;

import com.namemc.mlv.commands.MuteUser;
import com.namemc.mlv.commands.SetVolume;
import com.namemc.mlv.utils.LabyModProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public final class MuteLabyVoice extends JavaPlugin implements PluginMessageListener {
    public static String LABY_CHANNEL_NAME = "labymod3:main";

    @Override
    public void onEnable() {
        getServer().getMessenger().registerIncomingPluginChannel(this, LABY_CHANNEL_NAME, this);
        this.getCommand("labymute").setExecutor(new MuteUser());
        this.getCommand("labysetvolume").setExecutor(new SetVolume());
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(LABY_CHANNEL_NAME)) {
            return;
        }

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));

        ByteBuf buf = Unpooled.wrappedBuffer(message);
        String key = LabyModProtocol.readString(buf, Short.MAX_VALUE);
        String json = LabyModProtocol.readString(buf, Short.MAX_VALUE);
        // LabyMod user joins the server
        if(key.equals("INFO")) {
            System.out.println(json);
        }
        // LabyMod user voicechat settings
        if (key.equals("voicechat")) {
            // Check database to see if a user is muted, and if the time is correct
            // MuteUser.sendMutedPlayerTo(player, player.getUniqueId(), true | false);
        }
    }
}
