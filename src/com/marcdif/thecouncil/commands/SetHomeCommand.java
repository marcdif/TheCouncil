package com.marcdif.thecouncil.commands;

import com.google.gson.JsonObject;
import com.marcdif.thecouncil.TheCouncil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SetHomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatColor.RED + "Only a player can run this command!");
            return true;
        }
        try {
            Location loc = player.getLocation();
            player.sendMessage(ChatColor.AQUA + "Setting your home to " + round(loc.getX()) + "," + round(loc.getY()) + "," + round(loc.getZ()) + " in world '" + loc.getWorld().getName() + "'");
            JsonObject home = new JsonObject();
            home.addProperty("x", loc.getX());
            home.addProperty("y", loc.getY());
            home.addProperty("z", loc.getZ());
            home.addProperty("yaw", loc.getYaw());
            home.addProperty("pitch", loc.getPitch());
            home.addProperty("world", loc.getWorld().getName());

            JsonObject object = TheCouncil.getPlayerFile(player.getUniqueId());
            object.add("home", home);
            TheCouncil.savePlayerFile(player.getUniqueId(), object);

            player.sendMessage(ChatColor.GREEN + "Home set!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private String round(double number) {
        return String.format("%.2f", number);
    }
}
