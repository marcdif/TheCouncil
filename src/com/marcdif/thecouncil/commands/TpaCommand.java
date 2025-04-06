package com.marcdif.thecouncil.commands;

import com.marcdif.thecouncil.utils.TpaUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatColor.RED + "Only a player can run this command!");
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }
        Player tp = Bukkit.getPlayer(args[0]);
        if (tp == null) {
            player.sendMessage(ChatColor.RED + "Who's '" + args[0] + "'?");
            return true;
        }
        TpaUtil.addTeleport(player, tp);
        return true;
    }
}
