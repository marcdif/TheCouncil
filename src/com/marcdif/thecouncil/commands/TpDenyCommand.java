package com.marcdif.thecouncil.commands;

import com.marcdif.thecouncil.utils.TpaUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpDenyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatColor.RED + "Only a player can run this command!");
            return true;
        }
        TpaUtil.denyTeleport(player);
        return true;
    }
}
