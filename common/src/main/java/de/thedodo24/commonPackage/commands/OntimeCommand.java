package de.thedodo24.commonPackage.commands;

import com.google.common.collect.Lists;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.player.User;
import de.thedodo24.commonPackage.utils.SkullItems;
import de.thedodo24.commonPackage.utils.TimeFormat;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OntimeCommand implements CommandExecutor, TabCompleter {

    public OntimeCommand() {
        PluginCommand cmd = Common.getInstance().getPlugin().getCommand("ontime");
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
    }

    private String prefix = "§7§l| §aOntime §7» ";

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(args.length == 0) {
            if(s instanceof Player) {
                Player p = (Player) s;
                User u = Common.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                Common.getInstance().checkTime();
                long currentOntime = System.currentTimeMillis() - Common.getInstance().getPlayerOnline().get(p.getUniqueId());
                long afkTime = 0;
                if(Common.getInstance().getAfkPlayer().containsKey(p.getUniqueId())) {
                    afkTime = Common.getInstance().getAfkPlayer().get(p.getUniqueId());
                    currentOntime -= afkTime;
                }
                long total = u.getTotalOntime() + currentOntime;
                long week = u.getWeekOntime() + currentOntime;
                long day = u.getDayOntime() + currentOntime;
                long totalAfk = u.getAfkTime() + afkTime;
                p.sendMessage(prefix + "Du hast folgende §aOntime§7: \n" +
                        "§7» Insgesamt: §a" + TimeFormat.getString(total) + "\n" +
                        "§7» Diese Woche: §a" + TimeFormat.getString(week) + "\n" +
                        "§7» Heute: §a" + TimeFormat.getString(day) + "\n" +
                        "§7» AFK-Zeit: §a" + TimeFormat.getString(totalAfk));
            } else {
                s.sendMessage("Du musst ein Spieler sein.");
            }
        } else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("history")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                    int inventorySize = (Bukkit.getOnlinePlayers().size() / 9) + 1;
                    Inventory inv = Bukkit.createInventory(null, inventorySize * 9, "§aWähle einen Spieler aus:");
                    Bukkit.getOnlinePlayers().stream().map(SkullItems::getPlayerHead).forEach(inv::addItem);
                    p.openInventory(inv);
                } else {
                    s.sendMessage("Du musst ein Spieler sein");
                }
            } else {
                User u = Common.getInstance().getManager().getPlayerManager().getByName(args[0]);
                if(u != null) {
                    Common.getInstance().checkTime();
                    long total = u.getTotalOntime();
                    long week = u.getWeekOntime();
                    long day = u.getDayOntime();
                    long totalAfk = u.getAfkTime();
                    s.sendMessage(prefix + "§a" + u.getName() + " §7hat folgende §aOntime§7:\n" +
                            "§7» Insgesamt: §a" + TimeFormat.getString(total) + "\n" +
                            "§7» Diese Woche: §a" + TimeFormat.getString(week) + "\n" +
                            "§7» Heute: §a" + TimeFormat.getString(day) + "\n" +
                            "§7» AFK-Zeit: §a" + TimeFormat.getString(totalAfk));
                } else {
                    s.sendMessage(prefix + "§7Der Spieler §a" + args[0] + " §7existiert nicht.");
                }
            }
        } else if(args.length == 2) {
            if(args[0].equalsIgnoreCase("history")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                    User u = Common.getInstance().getManager().getPlayerManager().getByName(args[1]);
                    if(u != null) {
                        Inventory inventory = Bukkit.createInventory(null, 9, "§a» " + u.getName());
                        Map<String, Long> ontimeHistoryMap = u.getOntimeHistoryMap();
                        Map<String, Long> afkHistoryMap = u.getAfkTimeHistoryMap();
                        for(int i = 0; i < 10; i++) {
                            int week = 9 - i;
                            if(ontimeHistoryMap.containsKey(String.valueOf(week)) && afkHistoryMap.containsKey(String.valueOf(week))){
                                String ontime = TimeFormat.getInDays(ontimeHistoryMap.get(String.valueOf(week)));
                                String afkTime;
                                if(afkHistoryMap.containsKey(String.valueOf(week + 1))) {
                                    afkTime = TimeFormat.getInDays(afkHistoryMap.get(String.valueOf(week)) - afkHistoryMap.get(String.valueOf(week + 1)));
                                } else {
                                    afkTime = TimeFormat.getInDays(afkHistoryMap.get(String.valueOf(week)));
                                }
                                inventory.setItem(i,
                                        SkullItems.getNumberSkull(week,
                                                (week == 1 ? "§aLetzte Woche" : (week == 2 ? "§aVorletzte Woche" : "§aVor " + week + " Wochen")),
                                                Lists.newArrayList("§7» Ontime: §a" + ontime,
                                                        "§7» AFK-Zeit: §a" + afkTime)));
                            }
                        }
                        p.openInventory(inventory);
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    } else {
                        p.sendMessage(prefix + "§7Der Spieler §a" + args[1] + " §7existiert nicht.");
                    }
                } else {
                    s.sendMessage(prefix + "§7Du musst ein Spieler sein.");
                }
            } else {
                s.sendMessage(prefix + "§a/ontime <[Spieler]/history> <Spieler>");
            }
        } else {
            s.sendMessage(prefix + "§a/ontime <[Spieler]/history> <Spieler>");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        if(s instanceof Player) {
            if(args.length == 1) {
                List<String> returnAble = Lists.newArrayList("history");
                returnAble.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
                return returnAble;
            } else if(args.length == 2) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            }
        }
        return Lists.newArrayList();
    }
}
