package com.namemc.mlv.commands;

import com.google.gson.JsonObject;
import com.namemc.mlv.utils.LabyModProtocol;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class MuteUser implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandName, String[] args) {
        if (args.length == 0) return true;
        Player player = Bukkit.getPlayer(args[0]);
        if (player != null) {
            sendMutedPlayerTo(player, player.getUniqueId(), true);
            sender.sendMessage(String.format("Muted %s", player.getName()));
            // Up to you to add this to your database
        } else {
            sender.sendMessage("Could not find player");
        }
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
