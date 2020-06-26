package de.thedodo24.adventuria.town.commands;

import com.google.common.collect.Lists;
import de.thedodo24.adventuria.town.Towny;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.player.User;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TownResidentCommand implements CommandExecutor, TabCompleter {

    public TownResidentCommand() {
        PluginCommand cmdLong = Towny.getInstance().getPlugin().getCommand("townresident");
        PluginCommand cmdShort = Towny.getInstance().getPlugin().getCommand("tr");
        cmdLong.setExecutor(this);
        cmdLong.setTabCompleter(this);
        cmdShort.setExecutor(this);
        cmdShort.setTabCompleter(this);
    }

    private String prefix = "§7§l| §6Städte §7» ";

    /*
    tr info <name>
    tr
    tr friend add name
    tr friend del name
     */

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(s instanceof Player) {
            Player p = (Player) s;
            User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("info")) {
                    p.sendMessage("§7|----------| §6" + p.getName() + " §7|----------|");
                    p.sendMessage("§7» Stadt: §6" + (user.checkTownMember() ? user.getTown().getName() : "Kein Mitglied"));
                    if(user.checkTownMember()) {
                        p.sendMessage("§7» Grundstücke: §6" + Towny.getInstance().getManager().getPlotManager().getPlots(p.getUniqueId()).size() + " Stück");
                    }
                    p.sendMessage("§7» Freunde ["+user.getFriends().size()+"]: §6" + user.getFriends().stream().map(st -> Towny.getInstance().getManager().getPlayerManager().get(UUID.fromString(st)).getName()).collect(Collectors.joining("§7, §6")));
                } else {
                    sendHelpMessage(s, label);
                }
            } else if(args.length == 2) {
                if(args[0].equalsIgnoreCase("info")) {
                    User u = Towny.getInstance().getManager().getPlayerManager().getByName(args[1]);
                    if(u != null) {
                        p.sendMessage("§7|----------| §6" + u.getName() + " §7|----------|");
                        p.sendMessage("§7» Stadt: §6" + (u.checkTownMember() ? u.getTown().getName() : "Kein Mitglied"));
                        if(u.checkTownMember()) {
                            p.sendMessage("§7» Grundstücke: §6" + Towny.getInstance().getManager().getPlotManager().getPlots(u.getKey()).size() + " Stück");
                        }
                        p.sendMessage("§7» Freunde ["+u.getFriends().size()+"]: §6" + u.getFriends().stream().map(st -> Towny.getInstance().getManager().getPlayerManager().get(UUID.fromString(st)).getName()).collect(Collectors.joining("§7,§6 ")));
                    } else {
                        p.sendMessage(prefix + "§7Der Spieler §6" + args[1] + " §7existiert nicht.");
                    }
                } else {
                    sendHelpMessage(s, label);
                }
            } else if(args.length == 3) {
                if(args[0].equalsIgnoreCase("friend")) {
                    String name = args[2];
                    User u = Towny.getInstance().getManager().getPlayerManager().getByName(name);
                    if(u != null) {
                        if (args[1].equalsIgnoreCase("add")) {
                            if(user.checkFriend(u.getKey())) {
                                p.sendMessage(prefix + "§6" + u.getName() + " §7ist bereits dein Freund.");
                                return true;
                            }
                            user.addFriend(u.getKey());
                            p.sendMessage(prefix + "§7Du hast §6" + u.getName() + " §7als Freund hinzugefügt.");
                            Player friend;
                            if((friend = Bukkit.getPlayer(u.getKey())) != null) {
                                friend.sendMessage(prefix + "§6" + p.getName() + " §7hat dich als Freund hinzugefügt.");
                            }
                        } else if (args[1].equalsIgnoreCase("remove")) {
                            if(!user.checkFriend(u.getKey())) {
                                p.sendMessage(prefix + "§6" + u.getName() + " §7ist nicht dein Freund.");
                                return true;
                            }
                            user.removeFriend(u.getKey());
                            p.sendMessage(prefix + "§7Du hast §6" + u.getName() + " §7als Freund entfernt.");
                            Player friend;
                            if((friend = Bukkit.getPlayer(u.getKey())) != null) {
                                friend.sendMessage(prefix + "§6" + p.getName() + " §7hat dich als Freund entfernt.");
                            }
                        } else {
                            sendHelpMessage(s, label);
                        }
                    } else {
                        p.sendMessage(prefix + "§7Der Spieler §6" + name + " §7existiert nicht.");
                    }
                } else {
                    sendHelpMessage(s, label);
                }
            } else {
                sendHelpMessage(s, label);
            }
        } else {
            s.sendMessage("Du bist kein Spieler.");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        if(s instanceof Player) {
            Player p = (Player) s;
            User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
            if (args.length == 1) {
                return Common.getInstance().removeAutoComplete(Lists.newArrayList("info", "friend"), args[0]);
            } else if (args.length == 2) {
                switch (args[0].toLowerCase()) {
                    case "info":
                        return Common.getInstance().removeAutoComplete(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
                    case "friend":
                        return Common.getInstance().removeAutoComplete(Lists.newArrayList("add", "remove"), args[1]);
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("friend")) {
                    switch (args[1].toLowerCase()) {
                        case "add":
                            List<String> friends = user.getFriends().stream().map(st -> Towny.getInstance().getManager().getPlayerManager().get(UUID.fromString(st)).getName()).collect(Collectors.toList());
                            return Common.getInstance().removeAutoComplete(Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(st -> !friends.contains(st)).collect(Collectors.toList()), args[2]);
                        case "remove":
                            return Common.getInstance().removeAutoComplete(user.getFriends().stream().map(st -> Towny.getInstance().getManager().getPlayerManager().get(UUID.fromString(st)).getName()).collect(Collectors.toList()), args[2]);
                    }
                }
            }
        }
        return Lists.newArrayList();
    }

    private void sendHelpMessage(CommandSender s, String label) {
        s.sendMessage(prefix + "§6/" + label + " §7|| Zeigt diese Hilfsnachricht\n" +
                        prefix + "§6/" + label + " info <Name> §7|| Zeigt Informationen über dich oder den angegebenen Spieler\n" +
                        prefix + "§6/" + label + " friend [add/remove] [Name] §7|| Fügt einen neuen Freund hinzu oder entfernt einen");
    }
}
