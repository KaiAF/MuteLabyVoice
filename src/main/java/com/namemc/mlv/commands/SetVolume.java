package com.namemc.mlv.commands;

import com.google.gson.JsonObject;
import com.namemc.mlv.utils.LabyModProtocol;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class SetVolume implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandName, String[] args) {
        if (args.length == 0) return true;
        Player player = Bukkit.getPlayer(args[0]);
        if (player != null) {
            Integer volume = Integer.parseInt(args[1]);
            if (volume > 10 || volume < 0) volume = 10;
            sendSettings(player, true, true, volume);
        } else {
            sender.sendMessage("Could not find player");
        }
        return true;
    }

    // I think there is a bug with laby!! This is required, but, the player can just press cancel. The returned settings also says "accepted_settings": true.
    // Another thing to mention: A user can just change the volume back, so I guess this could be considered a warning??
    // Not sure how well this command will work, so it can be ignored.
    public void sendSettings(Player player, boolean keepSettings, boolean required, Integer volume) {
        JsonObject voicechatObject = new JsonObject();
        voicechatObject.addProperty( "keep_settings_on_server_switch", keepSettings);

        JsonObject requestSettingsObject = new JsonObject();
        requestSettingsObject.addProperty("required", required);

        JsonObject settingsObject = new JsonObject();
        settingsObject.addProperty("microphoneVolume", volume); // Own microphone volume. (0 - 10, Default 10)

        requestSettingsObject.add("settings", settingsObject);
        voicechatObject.add("request_settings", requestSettingsObject);

        try {
            LabyModProtocol.sendLabyModMessage(player, "voicechat", voicechatObject);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
