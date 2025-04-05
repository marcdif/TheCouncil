package com.marcdif.thecouncil.commands;

import com.google.gson.JsonObject;
import com.marcdif.thecouncil.TheCouncil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class NameColorCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatColor.RED + "Only a player can run this command!");
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/namecolor [color]");
            return true;
        }
        ChatColor color;
        try {
            color = ChatColor.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            sendHelpMessage(player);
            return true;
        }
        JsonObject object = TheCouncil.getPlayerFile(player.getUniqueId());
        object.addProperty("nameColor", color.name());
        try {
            TheCouncil.savePlayerFile(player.getUniqueId(), object);
        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + "There was a problem changing your name color! D:");
            return true;
        }
        player.setDisplayName(color + player.getName());
        player.sendMessage("Name color set to " + color + color.name() + "!");
        return true;
    }

    private void sendHelpMessage(Player p) {
        p.sendMessage("Here are the valid colors:");
        for (ChatColor c : ChatColor.values()) {
            p.sendMessage("- " + c + c.name().toLowerCase());
        }
    }
}
