package com.marcdif.thecouncil.commands;

import com.google.gson.JsonObject;
import com.marcdif.thecouncil.TheCouncil;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatColor.RED + "Only a player can run this command!");
            return true;
        }
        Location loc = TheCouncil.getBackLocation(player.getUniqueId());
        if (loc == null) {
            player.sendMessage(ChatColor.RED + "Nowhere to send you back to!");
            return true;
        }
        player.teleport(loc);
        loc.getWorld().spawnParticle(Particle.FIREWORK, loc.add(0, 1, 0), 15, 0.1, 0.1, 0.1, 0.1);
        return true;
    }
}
