package com.marcdif.thecouncil.commands;

import com.google.gson.JsonObject;
import com.marcdif.thecouncil.TheCouncil;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class ChatColorCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatColor.RED + "Only a player can run this command!");
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/chatcolor [color]");
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
        object.addProperty("chatColor", color.name());
        try {
            TheCouncil.savePlayerFile(player.getUniqueId(), object);
        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + "There was a problem changing your chat color! D:");
            return true;
        }
        TheCouncil.setChatColor(player.getUniqueId(), color);
        player.sendMessage("Chat color set to " + color + color.name() + "!");
        return true;
    }

    private void sendHelpMessage(Player p) {
        p.sendMessage("Here are the valid colors:");
        for (ChatColor c : ChatColor.values()) {
            p.sendMessage("- " + c + c.name().toLowerCase());
        }
    }
}
