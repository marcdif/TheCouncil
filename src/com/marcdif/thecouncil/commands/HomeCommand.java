package com.marcdif.thecouncil.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.marcdif.thecouncil.TheCouncil;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class HomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatColor.RED + "Only a player can run this command!");
            return true;
        }
        JsonObject object = TheCouncil.getPlayerFile(player.getUniqueId());
        if (!object.has("home")) {
            player.sendMessage(ChatColor.RED + "You don't have a home set! Run /sethome to set one.");
            return true;
        }
        JsonObject homeCoords = object.getAsJsonObject("home");
        double x = homeCoords.get("x").getAsDouble();
        double y = homeCoords.get("y").getAsDouble();
        double z = homeCoords.get("z").getAsDouble();
        float yaw = homeCoords.get("yaw").getAsFloat();
        float pitch = homeCoords.get("pitch").getAsFloat();
        String world = homeCoords.get("world").getAsString();
        World w = Bukkit.getWorld(world);
        Location loc = new Location(w, x,y,z,yaw,pitch);
        player.sendMessage(ChatColor.AQUA + "Teleporting you home!");
        player.teleport(loc);
        w.spawnParticle(Particle.FIREWORK, loc.add(0, 1, 0), 15, 0.1, 0.1, 0.1, 0.1);
        return true;
    }
}
