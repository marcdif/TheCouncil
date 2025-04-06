package com.marcdif.thecouncil.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SortChestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatColor.RED + "Only a player can run this command!");
            return true;
        }
        Block targetBlock = player.getTargetBlockExact(5);

        if (targetBlock == null || !(targetBlock.getState() instanceof Chest)) {
            player.sendMessage("§cYou're not looking at a chest within 5 blocks.");
            return true;
        }

        Chest chest = (Chest) targetBlock.getState();
        Inventory chestInv = chest.getInventory();

        List<ItemStack> items = Arrays.stream(chestInv.getContents())
                .filter(item -> item != null && item.getType() != Material.AIR)
                .collect(Collectors.toList());

        items.sort(Comparator.comparing(item -> item.getType().name()));

        chestInv.clear();
        for (ItemStack item : items) {
            chestInv.addItem(item);
        }

        player.sendMessage(ChatColor.AQUA + "Chest sorted by item type!");
        return true;
    }
}
