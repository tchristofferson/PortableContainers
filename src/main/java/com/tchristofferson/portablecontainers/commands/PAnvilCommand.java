package com.tchristofferson.portablecontainers.commands;

import com.tchristofferson.portablecontainers.utils.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class PAnvilCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtil.permissionDenied(sender, command) || CommandUtil.notPlayer(sender)) return true;

        Player player = (Player) sender;
        player.openInventory(Bukkit.createInventory(player, InventoryType.ANVIL));

        return true;
    }
}
