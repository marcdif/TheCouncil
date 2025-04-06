package com.marcdif.thecouncil.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.marcdif.thecouncil.TheCouncil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Marc on 2/6/15
 */
public class TpaUtil {
    private static final HashMap<UUID, UUID> map = new HashMap<>();
    private static final HashMap<UUID, Integer> map2 = new HashMap<>();
    private static final HashMap<UUID, Integer> map3 = new HashMap<>();

    public static void logout(Player player) {
        if (player == null) {
            return;
        }

        if (map.containsKey(player.getUniqueId())) {
            UUID tuuid = map.remove(player.getUniqueId());
            Bukkit.getPlayer(tuuid).sendMessage(ChatColor.RED + player.getName() +
                    " has logged out, TPA cancelled!");
            cancelTimer(player.getUniqueId());
            cancelTimer(tuuid);
            map.remove(player.getUniqueId());
            return;
        }
        if (map.containsValue(player.getUniqueId())) {
            for (Map.Entry<UUID, UUID> entry : map.entrySet()) {
                if (entry.getValue().equals(player.getUniqueId())) {
                    Bukkit.getPlayer(entry.getKey()).sendMessage(ChatColor.RED + player.getName() +
                            " has logged out, TPA cancelled!");
                    cancelTimer(player.getUniqueId());
                    cancelTimer(entry.getKey());
                    map.remove(entry.getKey());
                    return;
                }
            }
        }
    }

    public static void addTeleport(final Player sender, final Player target) {
        if (map.containsValue(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "That player already has a pending teleport request!");
            return;
        }
        map.put(sender.getUniqueId(), target.getUniqueId());
        final String name = sender.getDisplayName();
        final String name2 = target.getDisplayName();
        sender.sendMessage(ChatColor.GREEN + "Teleport Request sent to " + name2);
        target.sendMessage(name + ChatColor.GREEN + " has sent you a Teleport Request. Type /tpaccept to accept, and /tpdeny to deny.");
        map2.put(sender.getUniqueId(), Bukkit.getScheduler().runTaskLater(TheCouncil.getInstance(), () -> {
            if (!map.containsKey(sender.getUniqueId())) {
                return;
            }
            map.remove(sender.getUniqueId());
            sender.sendMessage(ChatColor.RED + "Your Teleport Request to " + name2 + ChatColor.RED +
                    " has timed out!");
            target.sendMessage(name + "'s " + ChatColor.RED + "Teleport Request sent to you has timed out!");
        }, 400L).getTaskId());
        map3.put(sender.getUniqueId(), Bukkit.getScheduler().runTaskTimer(TheCouncil.getInstance(), new Runnable() {
            int i = 20;

            @Override
            public void run() {
                if (i <= 0) {
                    target.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.RED + sender.getName() + "'s TPA Expired!"));
                    sender.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.RED + "Your TPA to " + target.getName() + " Expired!"));
                    cancelTimer(sender.getUniqueId());
                    return;
                }
                target.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.AQUA + sender.getName() + "'s TPA: " + getTimerMessage(i)
                        + " " + ChatColor.AQUA + i + "s"));
                sender.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.GREEN + "Your TPA to " + ChatColor.AQUA + target.getName()
                        + ": " + getTimerMessage(i) + " " + ChatColor.AQUA + i + "s"));
                i--;
            }
        }, 0, 20L).getTaskId());
    }

    public static void acceptTeleport(Player player) {
        if (!map.containsValue(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You don't have any pending Teleport Requests!");
            return;
        }
        Player tp = null;
        for (Map.Entry<UUID, UUID> entry : map.entrySet()) {
            if (entry.getValue().equals(player.getUniqueId())) {
                tp = Bukkit.getPlayer(entry.getKey());
                break;
            }
        }
        if (tp == null) {
            player.sendMessage(ChatColor.RED + "You don't have any pending Teleport Requests!");
            return;
        }
        map.remove(tp.getUniqueId());
        cancelTimer(tp.getUniqueId());
        tp.teleport(player.getLocation());
        final String name = tp.getDisplayName();
        final String name2 = player.getDisplayName();
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.GREEN + "You accepted " + name + "'s " + ChatColor.GREEN +
                "Teleport Request!"));
        tp.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(name2 + ChatColor.GREEN + " accepted your Teleport Request!"));
        player.sendMessage(ChatColor.GREEN + "You accepted " + name + "'s " + ChatColor.GREEN + "Teleport Request!");
        tp.sendMessage(name2 + ChatColor.GREEN + " accepted your Teleport Request!");
    }

    public static void denyTeleport(Player player) {
        if (!map.containsValue(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You don't have any pending Teleport Requests!");
            return;
        }
        Player tp = null;
        for (Map.Entry<UUID, UUID> entry : map.entrySet()) {
            if (entry.getValue().equals(player.getUniqueId())) {
                tp = Bukkit.getPlayer(entry.getKey());
                break;
            }
        }
        if (tp == null) {
            player.sendMessage(ChatColor.RED + "You don't have any pending Teleport Requests!");
            return;
        }
        map.remove(tp.getUniqueId());
        cancelTimer(tp.getUniqueId());
        final String name = tp.getDisplayName();
        final String name2 = player.getDisplayName();
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.RED + "You denied " +
                name + "'s " + ChatColor.RED + "Teleport Request!"));
        tp.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(name2 + ChatColor.RED + " denied your Teleport Request!"));
        player.sendMessage(ChatColor.RED + "You denied " + name + "'s " + ChatColor.RED + "Teleport Request!");
        tp.sendMessage(name2 + ChatColor.RED + " denied your Teleport Request!");
    }

    private static void cancelTimer(UUID uuid) {
        Integer taskID1 = map2.remove(uuid);
        Integer taskID2 = map3.remove(uuid);
        if (taskID1 != null) {
            Bukkit.getScheduler().cancelTask(taskID1);
        }
        if (taskID2 != null) {
            Bukkit.getScheduler().cancelTask(taskID2);
        }
    }

    private static String getTimerMessage(int i) {
        return switch (i) {
            case 20 -> ChatColor.DARK_GREEN + "▉▉▉▉▉▉▉▉▉▉";
            case 19 -> ChatColor.DARK_GREEN + "▉▉▉▉▉▉▉▉▉" + ChatColor.GREEN + "▉";
            case 18 -> ChatColor.DARK_GREEN + "▉▉▉▉▉▉▉▉▉" + ChatColor.RED + "▉";
            case 17 -> ChatColor.DARK_GREEN + "▉▉▉▉▉▉▉▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉";
            case 16 -> ChatColor.DARK_GREEN + "▉▉▉▉▉▉▉▉" + ChatColor.RED + "▉▉";
            case 15 -> ChatColor.DARK_GREEN + "▉▉▉▉▉▉▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉";
            case 14 -> ChatColor.DARK_GREEN + "▉▉▉▉▉▉▉" + ChatColor.RED + "▉▉▉";
            case 13 -> ChatColor.DARK_GREEN + "▉▉▉▉▉▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉▉";
            case 12 -> ChatColor.DARK_GREEN + "▉▉▉▉▉▉" + ChatColor.RED + "▉▉▉▉";
            case 11 -> ChatColor.DARK_GREEN + "▉▉▉▉▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉▉▉";
            case 10 -> ChatColor.DARK_GREEN + "▉▉▉▉▉" + ChatColor.RED + "▉▉▉▉▉";
            case 9 -> ChatColor.DARK_GREEN + "▉▉▉▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉▉▉▉";
            case 8 -> ChatColor.DARK_GREEN + "▉▉▉▉" + ChatColor.RED + "▉▉▉▉▉▉";
            case 7 -> ChatColor.DARK_GREEN + "▉▉▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉▉▉▉▉";
            case 6 -> ChatColor.DARK_GREEN + "▉▉▉" + ChatColor.RED + "▉▉▉▉▉▉▉";
            case 5 -> ChatColor.DARK_GREEN + "▉▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉▉▉▉▉▉";
            case 4 -> ChatColor.DARK_GREEN + "▉▉" + ChatColor.RED + "▉▉▉▉▉▉▉▉";
            case 3 -> ChatColor.DARK_GREEN + "▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉▉▉▉▉▉▉";
            case 2 -> ChatColor.DARK_GREEN + "▉" + ChatColor.RED + "▉▉▉▉▉▉▉▉▉";
            case 1 -> ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉▉▉▉▉▉▉▉";
            case 0 -> ChatColor.RED + "▉▉▉▉▉▉▉▉▉▉";
            default -> "";
        };
    }
}