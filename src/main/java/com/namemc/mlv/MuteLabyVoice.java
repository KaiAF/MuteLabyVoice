package com.namemc.mlv;

import com.namemc.mlv.commands.MuteUser;
import com.namemc.mlv.utils.LabyModProtocol;
import com.namemc.mlv.utils.TimeManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.sql.*;

public final class MuteLabyVoice extends JavaPlugin implements PluginMessageListener {
    public static String LABY_CHANNEL_NAME = "labymod3:main";
    public static Connection MySQLConnect;
    private String TableName = "muted_laby_players";

    @Override
    public void onEnable() {
        getServer().getMessenger().registerIncomingPluginChannel(this, LABY_CHANNEL_NAME, this);
        this.getCommand("labymute").setExecutor(new MuteUser());
        try {
            MySQLConnect = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/namemc?characterEncoding=utf8", "root", "test");
            Statement stmt = MySQLConnect.createStatement();
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + TableName + " (\n" +
                    "    uuid  CHAR(36)         NOT NULL,\n" +
                    "    muted_for BIGINT DEFAULT 0 NOT NULL,\n" +
                    "    muted BOOLEAN DEFAULT false NOT NULL,\n" +
                    "    reason TEXT,\n" +
                    "    PRIMARY KEY (uuid)\n" +
                    ");"
            );
            stmt.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        try {
            MySQLConnect.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(LABY_CHANNEL_NAME)) {
            return;
        }

        ByteBuf buf = Unpooled.wrappedBuffer(message);
        String key = LabyModProtocol.readString(buf, Short.MAX_VALUE);
        String json = LabyModProtocol.readString(buf, Short.MAX_VALUE);
        // LabyMod user joins the server
        if(key.equals("INFO")) {
            System.out.println(json);
        }
        // LabyMod user voicechat
        if (key.equals("voicechat")) {
            boolean muted = false;
            try {
                Statement stmt = MySQLConnect.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * from " + TableName + " WHERE uuid = '" + player.getUniqueId() + "'");
                if (rs.next()) {
                    long muteLength = rs.getLong("muted_for");
                    muted = rs.getBoolean("muted");
                    if (muted) {
                        muted = muteLength >= TimeManager.getTime();
                    }
                }
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            MuteUser.sendMutedPlayerTo(player, player.getUniqueId(), muted);
        }
    }
}
