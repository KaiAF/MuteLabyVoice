package com.namemc.mlv.commands;

import com.google.gson.JsonObject;
import com.namemc.mlv.utils.LabyModProtocol;
import com.namemc.mlv.utils.TimeManager;
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
        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "Invalid arguments! Required: <player name> <mute length> <reason>");
            return true;
        }
        Player moderator = (Player) sender;
        Player player = Bukkit.getPlayer(args[0]);
        String mute_length = args[1];
        String mute_reason = args[2];
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Could not find player");
            return true;
        }
        if (mute_length == null) {
            mute_length = "5m";
        }
        Long MuteLengthMS = TimeManager.getTime() + TimeManager.toMS(mute_length);
        try {
            Statement stmt = MySQLConnect.createStatement();
            String query = String.format(
               "INSERT INTO `namemc`.`muted_laby_players` (`uuid`, `muted_for`, `reason`, `muted`, `moderator`) VALUES ('%s', '%s', '%s', '%s', '%s');",
               player.getUniqueId().toString(),
               MuteLengthMS,
               mute_reason,
               "1",
               moderator.getUniqueId()
            );
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            sender.sendMessage(ChatColor.RED + "There was an error while updating the database.");
            e.printStackTrace();
        }
        sendMutedPlayerTo(player, player.getUniqueId(), true);
        sender.sendMessage(String.format("Muted %s for %s", player.getName(), mute_length));
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
