package com.marcdif.thecouncil;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.marcdif.thecouncil.commands.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.UUID;

public class TheCouncil extends JavaPlugin implements Listener {
    private static HashMap<UUID, ChatColor> chatColors = new HashMap<>();
    private static HashMap<UUID, Location> backLocations = new HashMap<>();
    private long lastTime = -1;

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("THECOUNCIL PLUGIN STARTING UP!!!");

        File pluginFolder = new File("plugins/TheCouncil");
        if (!pluginFolder.exists()) pluginFolder.mkdir();
        File playerFolder = new File("plugins/TheCouncil/players");
        if (!playerFolder.exists()) playerFolder.mkdir();

        getCommand("home").setExecutor(new HomeCommand());
        getCommand("sethome").setExecutor(new SetHomeCommand());
        getCommand("namecolor").setExecutor(new NameColorCommand());
        getCommand("chatcolor").setExecutor(new ChatColorCommand());
        getCommand("back").setExecutor(new BackCommand());
        getCommand("fw").setExecutor(new FireworkCommand());
        getCommand("sortchest").setExecutor(new SortChestCommand());

        Bukkit.getPluginManager().registerEvents(this, this);

        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            Bukkit.getLogger().warning("Players are online so it looks like the plugin was reloaded - loading player data from files!");
            for (Player p : Bukkit.getOnlinePlayers()) {
                loadPlayerChatColor(p);
                loadPlayerNameColor(p);
            }
        }

        Location healthHungerResetLoc = new Location(Bukkit.getWorlds().get(0), 447, 74, -585);

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            // Day/night messages
            World world = Bukkit.getWorlds().get(0);
            long time = world.getTime();
            // Day starts at 0, night at 13000
            if (lastTime != -1) {
                if (lastTime < 13000 && time >= 13000) {
                    Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "The sun sets and night begins...");
                } else if (lastTime >= 13000 && time < 13000) {
                    Bukkit.broadcastMessage(ChatColor.GOLD + "The sun rises on a new day...");
                }
            }
            lastTime = time;

            // Health/hunger reset
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getHealth() >= 10 && p.getFoodLevel() >= 10) continue;
                if (p.getLocation().distance(healthHungerResetLoc) <= 3) {
                    // If player is within 3 blocks of the health/hunger reset loc, set health and hunger to full
                    p.setHealth(20);
                    p.setFoodLevel(20);
                    p.getWorld().spawnParticle(Particle.HEART, p.getLocation().add(0,1,0), 5, 0.1, 0.1, 0.1);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 1f, 1f);
                }
            }
        }, 0L, 20L);
    }

    /*
    Player stuff
     */

    public static JsonObject getPlayerFile(UUID uuid) {
        File playerFile = new File("plugins/TheCouncil/players/" + uuid + ".json");
        if (!playerFile.exists()) {
            return new JsonObject();
        }
        try {
            String content = Files.readString(playerFile.toPath());
            JsonElement element = JsonParser.parseString(content);
            if (!element.isJsonObject()) {
                return new JsonObject();
            }
            return element.getAsJsonObject();
        } catch (Exception e) {
            return new JsonObject();
        }
    }

    public static void savePlayerFile(UUID uuid, JsonObject object) throws IOException {
        File playerFile = new File("plugins/TheCouncil/players/" + uuid + ".json");
        if (!playerFile.exists()) playerFile.createNewFile();
        Files.writeString(playerFile.toPath(), object.toString());
    }

    public static Location getBackLocation(UUID uuid) {
        return backLocations.get(uuid);
    }

    /*
    Chat stuff
     */

    private void loadPlayerNameColor(Player p) {
        JsonObject object = getPlayerFile(p.getUniqueId());
        if (object.has("nameColor")) {
            ChatColor color;
            try {
                color = ChatColor.valueOf(object.get("nameColor").getAsString());
            } catch (IllegalArgumentException e) {
                return;
            }
            p.setDisplayName(color + p.getName());
        }
    }

    private void loadPlayerChatColor(Player p) {
        JsonObject object = getPlayerFile(p.getUniqueId());
        if (!object.has("chatColor")) {
            chatColors.put(p.getUniqueId(), ChatColor.GRAY);
        } else {
            chatColors.put(p.getUniqueId(), ChatColor.valueOf(object.get("chatColor").getAsString()));
        }
    }

    public static void setChatColor(UUID uuid, ChatColor color) {
        chatColors.put(uuid, color);
    }

    /*
    Events
     */

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player p = event.getPlayer();
        loadPlayerChatColor(p);
        loadPlayerNameColor(p);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Player p = event.getPlayer();
        Bukkit.broadcastMessage(p.getDisplayName() + ": " + chatColors.get(p.getUniqueId()) + event.getMessage());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player p = event.getPlayer();
        Location from = event.getFrom();
        if (!backLocations.containsKey(p.getUniqueId())) {
            p.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "You teleported - run " + ChatColor.BLUE + "" + ChatColor.ITALIC + "/back " + ChatColor.AQUA + "" + ChatColor.ITALIC + "to go back to where you were");
        }
        backLocations.put(p.getUniqueId(), from);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        Location to = event.getTo();
        Block b = to.getBlock().getRelative(BlockFace.DOWN);
        if (b == null || b.getType() != Material.GOLD_BLOCK) return;
        Vector direction = p.getLocation().getDirection().normalize().multiply(1.5).setY(1.2);
        p.setVelocity(direction);
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 60, 0));
        to.getWorld().playSound(to, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1.5f);
    }
}
