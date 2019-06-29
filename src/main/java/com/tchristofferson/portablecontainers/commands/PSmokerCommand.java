package com.tchristofferson.portablecontainers.commands;

import com.tchristofferson.portablecontainers.PortableContainers;
import com.tchristofferson.portablecontainers.utils.CommandUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PSmokerCommand implements CommandExecutor {

    private final PortableContainers plugin;

    public PSmokerCommand(PortableContainers plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtil.permissionDenied(sender, command) || CommandUtil.notPlayer(sender)) return true;

        Player player = (Player) sender;
        plugin.getPlayerContainers().get(player.getUniqueId()).openSmoker();

        return true;
    }
}
