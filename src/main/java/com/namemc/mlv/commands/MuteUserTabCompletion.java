package com.namemc.mlv.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MuteUserTabCompletion implements TabCompleter {
    /**
     * Expected arguments:
     *  - command name
     *  - player name
     *  - mute length
     *  - mute reason
     */
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 1) {
            // set proper checks as to not allow people to see vanished players
            List<String> players = new ArrayList<String>();
            Player sender = (Player) commandSender;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (sender.canSee(player)) {
                    players.add(player.getName());
                }
            }
            return players;
        } else if (args.length == 2) {
            List<String> completions = new ArrayList<>();
            String[] completionsArray = { "30s", "60s", "1m", "3m", "5m" };
            StringUtil.copyPartialMatches(args[1], Arrays.asList(completionsArray), completions);
            return completions;
        }

        return new ArrayList<String>();
    }
}
