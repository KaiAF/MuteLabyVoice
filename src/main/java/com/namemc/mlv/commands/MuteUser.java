package com.namemc.mlv.commands;

import com.google.gson.JsonObject;
import com.namemc.mlv.utils.LabyModProtocol;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static com.namemc.mlv.MuteLabyVoice.MySQLConnect;

public class MuteUser implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandName, String[] args) {
        if (args.length == 0) return true;
        Player player = Bukkit.getPlayer(args[0]);
        String mute_reason = args[1];
        if (player == null) {
            sender.sendMessage("Could not find player");
            return true;
        }
        try {
            Statement stmt = MySQLConnect.createStatement();
            stmt.executeUpdate(String.format("INSERT INTO `namemc`.`muted_laby_players`\n" +
                    "(`uuid`,\n" +
                    "`muted_for`,\n" +
                    "`reason`,\n" +
                    "`muted`)\n" +
                    "VALUES\n" +
                    "('%s',\n" +
                    "%s,\n" +
                    "'%s',\n" +
                    "%s);", player.getUniqueId().toString(), "32", mute_reason, "true"));
            stmt.close();
        } catch (SQLException e) {
            sender.sendMessage(ChatColor.RED + "There was an error while updating the database.");
            e.printStackTrace();
        }
        sendMutedPlayerTo(player, player.getUniqueId(), true);
        sender.sendMessage(String.format("Muted %s", player.getName()));
        return true;
    }


    public static void sendMutedPlayerTo(Player player, UUID mutedPlayer, boolean muted) {
        JsonObject voicechatObject = new JsonObject();
        JsonObject mutePlayerObject = new JsonObject();

        mutePlayerObject.addProperty("mute", muted);
        mutePlayerObject.addProperty("target", String.valueOf(mutedPlayer));

        voicechatObject.add("mute_player", mutePlayerObject);

        try {
            LabyModProtocol.sendLabyModMessage(player, "voicechat", voicechatObject);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
