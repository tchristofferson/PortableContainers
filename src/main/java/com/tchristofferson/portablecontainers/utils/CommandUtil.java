package com.tchristofferson.portablecontainers.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUtil {

    public static boolean permissionDenied(CommandSender sender, Command command) {
        assert command.getPermission() != null;
        if (!sender.hasPermission(command.getPermission())) {
            sender.sendMessage(ChatColor.RED + "Permission Denied!");
            return true;
        }

        return false;
    }

    public static boolean notPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be in-game to use that command!");
            return true;
        }

        return false;
    }

}
