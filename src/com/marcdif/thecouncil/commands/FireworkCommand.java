package com.marcdif.thecouncil.commands;

import com.marcdif.thecouncil.TheCouncil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.*;

public class FireworkCommand implements CommandExecutor {

    private final Random random = new Random();

    private final List<Color> availableColors = Arrays.asList(
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE,
            Color.AQUA, Color.FUCHSIA, Color.LIME, Color.MAROON, Color.NAVY,
            Color.OLIVE, Color.PURPLE, Color.SILVER, Color.TEAL, Color.WHITE
    );

    private final List<FireworkEffect.Type> availableTypes = Arrays.asList(
            FireworkEffect.Type.BALL, FireworkEffect.Type.BALL_LARGE, FireworkEffect.Type.BURST,
            FireworkEffect.Type.CREEPER, FireworkEffect.Type.STAR
    );

    private final List<UUID> runningShows = new ArrayList<>();

    public FireworkCommand() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(TheCouncil.getPlugin(TheCouncil.class), () -> {
            List<UUID> toRemove = new ArrayList<>();
            for (UUID uuid : runningShows) {
                Player p = Bukkit.getPlayer(uuid);
                if (p == null) {
                    // Player logged off
                    toRemove.add(uuid);
                } else if (random.nextDouble() < 0.25) {
                    launchFirework(p.getLocation());
                }
            }
            runningShows.removeAll(toRemove);
        }, 0L, 5L);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatColor.RED + "Only a player can run this command!");
            return true;
        }
        Location loc;
        if (args.length < 1) {
            loc = player.getLocation();
        } else if (args[0].equalsIgnoreCase("looking")) {
            Block b = player.getTargetBlockExact(100);
            if (b == null) {
                player.sendMessage(ChatColor.RED + "That's too far away!");
                return true;
            }
            loc = b.getLocation();
        } else if (args[0].equalsIgnoreCase("show")) {
            if (runningShows.contains(player.getUniqueId())) {
                runningShows.remove(player.getUniqueId());
                player.sendMessage(ChatColor.AQUA + "The show is over!");
                return true;
            } else {
                runningShows.add(player.getUniqueId());
                player.sendMessage(ChatColor.AQUA + "Starting a firework show! Run this command again to stop it.");
                return true;
            }
        } else {
            player.sendMessage(ChatColor.RED + "Huh?");
            return true;
        }

        launchFirework(loc);
        return true;
    }

    private void launchFirework(Location loc) {
        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();

        // Random properties
        boolean flicker = random.nextBoolean();
        boolean trail = random.nextBoolean();
        FireworkEffect.Type type = availableTypes.get(random.nextInt(availableTypes.size()));
        List<Color> colors = getRandomColors(1 + random.nextInt(3));
        List<Color> fadeColors = getRandomColors(random.nextInt(3)); // 0–2 fade colors

        FireworkEffect effect = FireworkEffect.builder()
                .flicker(flicker)
                .trail(trail)
                .with(type)
                .withColor(colors)
                .withFade(fadeColors)
                .build();

        meta.addEffect(effect);
        meta.setPower(1 + random.nextInt(2)); // power 1–2
        firework.setFireworkMeta(meta);
    }

    private List<Color> getRandomColors(int count) {
        return availableColors.stream()
                .distinct()
                .sorted((a, b) -> random.nextInt(3) - 1)
                .limit(count)
                .toList();
    }
}
