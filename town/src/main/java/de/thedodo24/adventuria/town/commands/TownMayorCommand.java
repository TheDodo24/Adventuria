package de.thedodo24.adventuria.town.commands;

import com.google.common.collect.Lists;
import com.sun.org.apache.xpath.internal.operations.Bool;
import de.thedodo24.adventuria.town.Towny;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.player.User;
import de.thedodo24.commonPackage.towny.*;
import de.thedodo24.commonPackage.utils.ClickableText;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class TownMayorCommand implements CommandExecutor, TabCompleter {

    public TownMayorCommand() {
        PluginCommand cmdFull = Towny.getInstance().getPlugin().getCommand("townmayor");
        PluginCommand cmdShort = Towny.getInstance().getPlugin().getCommand("tnm");
        cmdFull.setExecutor(this);
        cmdFull.setTabCompleter(this);
        cmdShort.setTabCompleter(this);
        cmdShort.setExecutor(this);
    }

    private String prefix = "§7§l| §6Städte §7» ";

    /*
    set ... (spawn, tax, perm, blackboard)
    invite
    kick
    setrank
    claim


     */

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(s instanceof Player) {
            Player p = (Player) s;
            User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
            if(user.checkTownMember() && user.getTownRank().equals(TownRank.MAYOR)) {
                Town town = user.getTown();
                if(args.length == 1) {
                    if(args[0].equalsIgnoreCase("set")) {
                        sendSetHelpMessage(s, label);
                    } else if(args[0].equalsIgnoreCase("outpost")) {
                        sendOutpostHelpMessaage(s, label);
                    }else if(args[0].equalsIgnoreCase("claim")) {
                        Location loc = p.getLocation();
                        if(loc.getWorld().equals(Bukkit.getWorld("Freebuild"))) {
                            Chunk chunk = loc.getChunk();
                            AtomicBoolean near = new AtomicBoolean(false);
                            int townSize = Towny.getInstance().getManager().getPlayerManager().getResidents(town).size();
                            List<Plot> plots = Towny.getInstance().getManager().getPlotManager().getPlots(town);
                            AtomicReference<Plot> original = new AtomicReference<>();
                            if(plots.size() <= ((townSize * 16) + user.getTown().getBuyedTownSize())) {
                                plots.forEach(c -> {
                                    if((c.getChunk().getX() - chunk.getX()) == 0) {
                                        if(((c.getChunk().getZ() - chunk.getZ()) == -1) || ((c.getChunk().getZ() - chunk.getZ()) == 1)) {
                                            near.set(true);
                                            original.set(c);
                                        }
                                    } else if((c.getChunk().getZ() - chunk.getZ()) == 0) {
                                        if(((c.getChunk().getX() - chunk.getX()) == -1) || ((c.getChunk().getX() - chunk.getX()) == 1)) {
                                            near.set(true);
                                            original.set(c);
                                        }
                                    }
                                });
                                if(near.get()) {
                                    if(Towny.getInstance().getManager().getPlotManager().get(String.valueOf(chunk.getChunkKey())) != null) {
                                        p.sendMessage(prefix + "§7Dieser Chunk wurde bereits beansprucht!");
                                        return false;
                                    }
                                    if((town.getMoney() - 600000) < 0) {
                                        p.sendMessage(prefix + "§7Du hast nicht genügend Geld für diese Aktion auf dem Stadtkonto. §8(Kosten: 6000 A)");
                                        return false;
                                    }
                                    Plot plot = Towny.getInstance().getManager().getPlotManager().getOrGenerate(String.valueOf(chunk.getChunkKey()), key -> {
                                        Plot a = new Plot(key);
                                        Map<String, Object> values = new HashMap<>();
                                        values.put("name", town.getName());
                                        values.put("town", town.getKey());
                                        Map<String, Boolean> townPlayerPermissions = new HashMap<String, Boolean>() {{
                                            put(PlotPlayer.NATION.toString(), false);
                                            put(PlotPlayer.OUTSIDER.toString(), false);
                                            put(PlotPlayer.RESIDENT.toString(), false);
                                            put(PlotPlayer.FRIEND.toString(), true);
                                        }};
                                        values.put("permissions", new HashMap<String, Map<String, Boolean>>() {{
                                            put(TownPermission.BUILD.toString(), townPlayerPermissions);
                                            put(TownPermission.DESTROY.toString(), townPlayerPermissions);
                                            put(TownPermission.ITEM.toString(), townPlayerPermissions);
                                            put(TownPermission.SWITCH.toString(), townPlayerPermissions);
                                        }});
                                        values.put("settings", new HashMap<String, Boolean>() {{
                                            put("pvp", false);
                                            put("mobs", false);
                                        }});
                                        if(original.get() != null && original.get().isOutpostPlot()) {
                                            values.put("outpost", original.get().getOutpost());
                                        }
                                        a.setValues(values);
                                        return a;
                                    });
                                    Towny.getInstance().getManager().getPlotManager().save(plot);
                                    town.withdrawMoney(6000 * 100);
                                    p.sendMessage(prefix + "Dieser Chunk §8[" + chunk.getX() + ";" + chunk.getZ() + "] §7wurde für die Stadt beansprucht." + (plot.isOutpostPlot() ? " (Outpost: " + plot.getOutpost() + ")" : ""));
                                } else {
                                    p.sendMessage(prefix + "§7Der Chunk grenzt an keinem Stadtchunk.");
                                }
                            } else {
                                p.sendMessage(prefix + "§7Du hast die maximale Stadtgröße erreicht.");
                            }
                        } else {
                            p.sendMessage(prefix + "§7Du kannst diesen Befehl nur im §6Freebuild §7ausführen.");
                        }
                    } else if(args[0].equalsIgnoreCase("unclaim")) {
                        Location loc = p.getLocation();
                        Chunk chunk = loc.getChunk();
                        Plot plot = Towny.getInstance().getManager().getPlotManager().get(String.valueOf(chunk.getChunkKey()));
                        if(plot != null) {
                            if(plot.getTown().getKey().equalsIgnoreCase(town.getKey())) {
                                if(plot.isOutpostPlot()) {
                                    if(town.getOutpostSpawn(plot.getOutpost()).getChunk().getChunkKey() == chunk.getChunkKey()) {
                                        p.sendMessage(prefix + "Dieser Chunk ist der Outpost-Spawn von §6" + plot.getOutpost() + "§7. Versetze zuerst den Spawn.");
                                        return true;
                                    }
                                }
                                if(town.getSpawn().getChunk().getChunkKey() == chunk.getChunkKey()) {
                                    p.sendMessage(prefix + "Dieser Chunk ist der Stadtspaawn. Versetze zuerst den Spawn.");
                                    return true;
                                }
                                Towny.getInstance().getManager().getPlotManager().delete(plot.getKey());
                                p.sendMessage(prefix + "Das Plot wurde unclaimt.");
                            } else {
                                p.sendMessage(prefix + "§7Dieser Chunk gehört nicht deiner Stadt.");
                            }
                        } else {
                            p.sendMessage(prefix + "§7Dieser Chunk ist nicht geclaimt.");
                        }
                    } else if(args[0].equalsIgnoreCase("border")) {
                        if(Towny.getInstance().getBorderList().contains(p.getUniqueId())) {
                            Towny.getInstance().getBorderList().remove(p.getUniqueId());
                            p.sendMessage(prefix + "§7Du hast die Grenzen §cdeaktiviert§7.");
                        } else {
                            Towny.getInstance().getBorderList().add(p.getUniqueId());
                            p.sendMessage(prefix + "§7Du hast die Grenzen §aaktiviert§7.");
                        }
                    } else {
                        sendHelpMessage(s, label);
                    }
                } else if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("spawn")) {
                        Chunk c = Bukkit.getWorld("Freebuild").getChunkAt(p.getLocation());
                        if(Towny.getInstance().getManager().getPlotManager().getPlots(town).stream().map(plot -> plot.getChunk().getChunkKey()).anyMatch(chunkKey -> chunkKey == c.getChunkKey())) {
                            town.setSpawn(p.getLocation());
                            p.sendMessage(prefix + "§7Der Spawn deiner Stadt wurde auf deine Position gesetzt.");
                        } else {
                            p.sendMessage(prefix + "§7Der Stadtspawn kann nur innerhalb deiner Stadt gesetzt werden.");
                        }
                    } else {
                        switch(args[0].toLowerCase()) {
                            case "invite":
                                Player invite;
                                if((invite = Bukkit.getPlayer(args[1])) != null) {
                                    User inviteUser = Towny.getInstance().getManager().getPlayerManager().get(invite.getUniqueId());
                                    if(inviteUser.checkTownMember()) {
                                        p.sendMessage(prefix + "§6" + invite.getName() + " §7ist bereits in einer Stadt.");
                                        return false;
                                    }
                                    if(Towny.getInstance().getTownInventations().containsKey(invite.getUniqueId())) {
                                        p.sendMessage(prefix + "§6" + invite.getName() + " §7hat bereits eine Einladung erhalten.");
                                        return false;
                                    }
                                    Towny.getInstance().getTownInventations().put(invite.getUniqueId(), town.getKey());
                                    Towny.getInstance().getTownInventationsTime().put(invite.getUniqueId(), System.currentTimeMillis() + 60000);
                                    p.sendMessage(prefix + "§7Du hast §6" + invite.getName() + " §7in deine Stadt eingeladen.");
                                    TextComponent accept = new ClickableText("§7» §a/town invite accept §7|| Um anzunehmen")
                                            .setChatColor(ChatColor.GREEN)
                                            .setClickEventAction(ClickEvent.Action.RUN_COMMAND)
                                            .setClickMessage("/town invite accept")
                                            .setHoverEventAction(HoverEvent.Action.SHOW_TEXT)
                                            .setHoverMessage("Klicke um anzunehmen")
                                            .build();
                                    TextComponent deny = new ClickableText("§7» §c/town invite deny §7|| Um abzulehnen")
                                            .setChatColor(ChatColor.RED)
                                            .setClickEventAction(ClickEvent.Action.RUN_COMMAND)
                                            .setClickMessage("/town invite deny")
                                            .setHoverEventAction(HoverEvent.Action.SHOW_TEXT)
                                            .setHoverMessage("Klicke um abzulehnen")
                                            .build();
                                    invite.sendMessage(prefix + "§7Du wurdest von §6" + p.getName() + " §7in die Stadt §6" + town.getName() + " §7eingeladen.");
                                    invite.spigot().sendMessage(accept);
                                    invite.spigot().sendMessage(deny);
                                    invite.sendMessage("§7» Die Einladung verfällt in §660 Sekunden§7.");
                                } else {
                                    p.sendMessage(prefix + "§7Der Spieler §6" + args[1] + " §7ist nicht online.");
                                }
                                break;
                            case "kick":
                                User toKick = Towny.getInstance().getManager().getPlayerManager().getByName(args[1]);
                                if(toKick != null) {
                                    if(toKick.checkTownMember()) {
                                        if(toKick.getTown().getKey().equalsIgnoreCase(town.getKey())) {
                                            if(toKick.getTownRank().equals(TownRank.MAYOR)) {
                                                p.sendMessage(prefix + "§7Du kannst den Bürgermeister nicht kicken.");
                                                return false;
                                            }
                                            Towny.getInstance().getManager().getPlayerManager().getResidents(town).stream()
                                                    .filter(all -> Bukkit.getPlayer(all.getKey()) != null)
                                                    .forEach(all -> Bukkit.getPlayer(all.getKey()).sendMessage("§7§l| §6" + town.getName() + " §7» §6" + toKick.getName() + " §7wurde aus der Stadt entfernt."));
                                            toKick.removeTown();
                                        } else {
                                            p.sendMessage(prefix + "§6" + toKick.getName() + " §7ist nicht in deiner Stadt.");
                                        }
                                    } else {
                                        p.sendMessage(prefix + "§6" + toKick.getName() + " §7ist in keiner Stadt.");
                                    }
                                } else {
                                    p.sendMessage(prefix + "§7Der Spieler §6" + args[1] + " §7existiert nicht.");
                                }
                                break;
                            default:
                                sendHelpMessage(s, label);
                        }
                    }
                } else if(args.length >= 3) {
                    if(args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("blackboard")) {
                        List<String> blackboardList = Arrays.asList(args);
                        String blackboard = blackboardList.stream().skip(2).collect(Collectors.joining(" "));
                        town.setBlackboard(blackboard);
                        p.sendMessage(prefix + "§7Die Nachricht wurde erfolgreich geändert.");
                    } else {
                        if(args.length == 3) {
                            if(args[0].equalsIgnoreCase("set")) {
                                switch(args[1].toLowerCase()) {
                                    case "mayor":
                                        if(town != null) {
                                            Player newMayor;
                                            if((newMayor = Bukkit.getPlayer(args[2])) != null) {
                                                User newMayorUser = Towny.getInstance().getManager().getPlayerManager().get(newMayor.getUniqueId());
                                                if(newMayorUser.checkTownMember()) {
                                                    if(newMayorUser.getTown().getKey().equalsIgnoreCase(town.getKey())) {
                                                        if(newMayorUser.getTownRank().equals(TownRank.MAYOR)) {
                                                            s.sendMessage(prefix + "§6" + newMayor.getName() + " §7ist bereits Bürgermeister der Stadt.");
                                                            return false;
                                                        }
                                                        List<User> residents = Towny.getInstance().getManager().getPlayerManager().getResidents(town);
                                                        user.updateTownRank(TownRank.CITIZEN);
                                                        newMayorUser.updateTownRank(TownRank.MAYOR);
                                                        s.sendMessage(prefix + "§7Der neue Bürgermeister von §6" + town.getName() + " §7ist §6" + newMayor.getName() + "§7.");
                                                        residents.stream().filter(u -> Bukkit.getPlayer(u.getKey()) != null).forEach(u -> Bukkit.getPlayer(u.getKey()).sendMessage("§7§l| §6" + town.getName() + " §7» §6" + p.getName() +" §7hat abgedankt. Der neue Bürgermeister der Stadt ist §6" + newMayor.getName() + "§7!"));
                                                    } else {
                                                        s.sendMessage(prefix + "§6" + newMayor.getName() + " §7ist nicht in der Stadt.");
                                                    }
                                                } else {
                                                    s.sendMessage(prefix + "§6" + newMayor.getName() + " §7ist in keiner Stadt.");
                                                }
                                            } else {
                                                s.sendMessage(prefix + "§7Der Spieler §6" + args[2] + " §7ist nicht online.");
                                            }
                                        } else {
                                            s.sendMessage(prefix + "§7Die Stadt §6" + args[1].toLowerCase() + " §7existiert nicht.");
                                        }
                                        break;
                                    case "taxes":
                                        long taxes;
                                        try {
                                            taxes = (long) (Double.parseDouble(args[2]) * 100);
                                        } catch (NumberFormatException e) {
                                            p.sendMessage(prefix + "§6Argument 3 §7muss eine positive Kommazahl sein.");
                                            return false;
                                        }
                                        if(taxes >= 100 && taxes <= 500) {
                                            town.setTaxes(taxes);
                                            long finalTaxes = taxes;
                                            Towny.getInstance().getManager().getPlayerManager().getResidents(town).stream().filter(u -> Bukkit.getPlayer(u.getKey()) != null)
                                                    .forEach(u -> Bukkit.getPlayer(u.getKey()).sendMessage("§7§l| §6" + town.getName() + " §7» Die Steuern wurden auf §6" + (((Long) finalTaxes).doubleValue() / 100) + "% §7gesetzt."));
                                        } else {
                                            p.sendMessage(prefix + "§7Die Steuern müssen zwischen §61%-5% §7liegen.");
                                        }
                                        break;
                                    case "public":
                                        boolean pub;
                                        try {
                                            pub = Boolean.parseBoolean(args[2]);
                                        } catch (Exception e) {
                                            p.sendMessage(prefix + "§6Argument 3 §7muss entweder true oder false sein.");
                                            return false;
                                        }
                                        town.setPublic(pub);
                                        p.sendMessage(prefix + "§7Die Stadt ist nun " + (pub ? "§aöffentlich" : "§cgeschlossen") + "§7.");
                                        break;
                                    default:
                                        sendSetHelpMessage(s, label);
                                }
                            } else if(args[0].equalsIgnoreCase("buy")) {
                                if(args[1].equalsIgnoreCase("townsize")) {
                                    int size;
                                    try {
                                        size = Integer.parseInt(args[2]);
                                    } catch (NumberFormatException e) {
                                        p.sendMessage(prefix + "§6Argument 3 §7muss eine ganzzahlige positve Zahl sein.");
                                        return false;
                                    }
                                    if(size > 0) {
                                        if((town.getMoney() - (200000 * size)) < 0) {
                                            p.sendMessage(prefix + "Du hast nicht genügend Geld.");
                                            return true;
                                        }
                                        town.addBuyedTownSize(size);
                                        town.withdrawMoney(200000 * size);
                                        p.sendMessage(prefix + "§7Du hast deine maximale Stadtgröße um §6" + size + " §7vergrößert.");
                                    } else {
                                        p.sendMessage(prefix + "§6Argument 3 §7muss eine ganzzahlige positve Zahl sein.");
                                    }
                                } else {
                                    sendHelpMessage(s, label);
                                }
                            } else if(args[0].equalsIgnoreCase("outpost")) {
                                if(args[1].equalsIgnoreCase("create")) {
                                    if(p.getLocation().getWorld().equals(Bukkit.getWorld("Freebuild"))) {
                                        String name = args[2];
                                        if(town.checkOutpost(name)) {
                                            p.sendMessage(prefix + "Deine Stadt besitzt bereits einen Outpost mit den Namen §6" + name + "§7.");
                                            return true;
                                        }
                                        long chunkKey = p.getLocation().getChunk().getChunkKey();
                                        if(Towny.getInstance().getManager().getPlotManager().get(String.valueOf(chunkKey)) != null) {
                                            p.sendMessage(prefix + "§7Dieses Plot ist bereits besetzt.");
                                            return true;
                                        }
                                        if((town.getMoney() - 1500000) < 0) {
                                            p.sendMessage(prefix + "Du hast nicht genug Geld um einen §6Outpost §7zu gründen.");
                                            return true;
                                        }
                                        town.addOutpost(name, p.getLocation());
                                        Plot plot = Towny.getInstance().getManager().getPlotManager().getOrGenerate(String.valueOf(chunkKey), key -> {
                                            Plot a = new Plot(key);
                                            Map<String, Object> values = new HashMap<>();
                                            values.put("name", town.getName());
                                            values.put("town", town.getKey());
                                            Map<String, Boolean> townPlayerPermissions = new HashMap<String, Boolean>() {{
                                                put(PlotPlayer.NATION.toString(), false);
                                                put(PlotPlayer.OUTSIDER.toString(), false);
                                                put(PlotPlayer.RESIDENT.toString(), false);
                                                put(PlotPlayer.FRIEND.toString(), true);
                                            }};
                                            values.put("permissions", new HashMap<String, Map<String, Boolean>>() {{
                                                put(TownPermission.BUILD.toString(), townPlayerPermissions);
                                                put(TownPermission.DESTROY.toString(), townPlayerPermissions);
                                                put(TownPermission.ITEM.toString(), townPlayerPermissions);
                                                put(TownPermission.SWITCH.toString(), townPlayerPermissions);
                                            }});
                                            values.put("outpost", name);
                                            a.setValues(values);
                                            return a;
                                        });
                                        Towny.getInstance().getManager().getPlotManager().save(plot);
                                        town.withdrawMoney(1500000);
                                        Towny.getInstance().getManager().getPlayerManager().getResidents(town).stream().filter(u -> Bukkit.getPlayer(u.getKey()) != null).forEach(u ->
                                                Bukkit.getPlayer(u.getKey()).sendMessage("§7§l| §6" + town.getName() + " §7» Es wurde ein neuer Outpost §8(" + name + ") §7erstellt."));
                                    } else {
                                        p.sendMessage(prefix + "§7Du kannst diesen Befehl nur im §6Freebuild §7ausführen.");
                                    }
                                } else {
                                    sendOutpostHelpMessaage(s, label);
                                }
                            } else {
                                sendHelpMessage(s, label);
                            }
                        } else if(args.length == 4) {
                            if(args[0].equalsIgnoreCase("set")) {
                                if (args[1].equalsIgnoreCase("rank")) {
                                    User toRank = Towny.getInstance().getManager().getPlayerManager().getByName(args[2]);
                                    if(toRank != null) {
                                        if(toRank.checkTownMember() && toRank.getTown().getKey().equalsIgnoreCase(town.getKey())) {
                                            TownRank newRank;
                                            try {
                                                newRank = TownRank.valueOf(args[3].toUpperCase());
                                            } catch (Exception e) {
                                                p.sendMessage(prefix + "§7Dieser Rang ist nicht verfügbar. " + Arrays.stream(TownRank.values()).map(Enum::toString).collect(Collectors.joining(", ")));
                                                return false;
                                            }
                                            if(newRank.equals(TownRank.MAYOR)) {
                                                p.sendMessage(prefix + "§7Um jemanden Bürgermeister zu setzen, benutze bitten den Befehl §6/townmayor set mayor [Spieler]§7.");
                                                return false;
                                            }
                                            toRank.updateTownRank(newRank);
                                            Towny.getInstance().getManager().getPlayerManager().getResidents(town).stream().filter(u -> Bukkit.getPlayer(u.getKey()) != null)
                                                    .forEach(u -> Bukkit.getPlayer(u.getKey()).sendMessage("§7§l| §6" + town.getName() + " §7» §6" + toRank.getName() + " §7ist nun §6" + toRank.getTownRank().getDisplayName() + "§7."));
                                        } else {
                                            p.sendMessage(prefix + "§6" + toRank.getName() + " §7ist nicht in deiner Stadt");
                                        }
                                    } else {
                                        p.sendMessage(prefix + "§7Der Spieler §6" + args[2] + " §7existiert nicht.");
                                    }
                                } else {
                                    sendSetHelpMessage(s, label);
                                }
                            } else if(args[0].equalsIgnoreCase("outpost")) {
                                if(args[1].equalsIgnoreCase("rename")) {
                                    if(town.hasOutposts()) {
                                        if(town.checkOutpost(args[2])) {
                                            if(town.checkOutpost(args[3])) {
                                                p.sendMessage(prefix + "§7Ein Outpost mit dem Namen §6" + args[3] + " §7existiert bereits.");
                                                return true;
                                            }
                                            town.renameOutpost(args[2], args[3]);
                                            Towny.getInstance().getManager().getPlayerManager().getResidents(town)
                                                    .stream().filter(u -> Bukkit.getPlayer(u.getKey()) != null)
                                                    .filter(u -> u.getTown().getKey().equalsIgnoreCase(town.getKey()))
                                                    .forEach(u -> Bukkit.getPlayer(u.getKey()).sendMessage("§7§l| §6" + town.getName() + " §7» Das Outpost §6" + args[2] + " §7wurde in §6" + args[3] + " §7umbenannt."));
                                        } else {
                                            p.sendMessage(prefix + "§7Es gibt keinen Outpost mit diesem Namen.");
                                        }
                                    } else {
                                        p.sendMessage(prefix + "§7Deine Stadt hat keine Outposts.");
                                    }
                                } else if(args[1].equalsIgnoreCase("set")) {
                                    if(args[2].equalsIgnoreCase("spawn")) {
                                        if(town.hasOutposts()) {
                                            if(town.checkOutpost(args[3])) {
                                                long chunkKey = p.getLocation().getChunk().getChunkKey();
                                                Plot plot = Towny.getInstance().getManager().getPlotManager().get(String.valueOf(chunkKey));
                                                if(plot != null) {
                                                    if(plot.isOutpostPlot() && plot.getOutpost().equalsIgnoreCase(args[3])) {
                                                        town.updateSpawn(args[3], p.getLocation());
                                                        p.sendMessage(prefix + "§7Du hast den Outpostspawn auf deine Position gesetzt.");
                                                    } else {
                                                        p.sendMessage(prefix + "§7Dieses Plot ist nicht in dem Outpost.");
                                                    }
                                                } else {
                                                    p.sendMessage(prefix + "§7Du stehst auf keinem §6Stadtgrundstück§7.");
                                                }
                                            } else {
                                                p.sendMessage(prefix + "§7Das Outpost §6" + args[3] + " §7existiert nicht.");
                                            }
                                        } else {
                                            p.sendMessage(prefix + "§7Deine Stadt hat keine Outposts.");
                                        }
                                    } else {
                                        sendOutpostHelpMessaage(s, label);
                                    }
                                } else {
                                    sendOutpostHelpMessaage(s, label);
                                }
                            } else {
                                sendHelpMessage(s, label);
                            }
                        } else {
                            sendHelpMessage(s, label);
                        }
                    }
                } else {
                    sendHelpMessage(s, label);
                }
            } else {
                s.sendMessage(prefix + "§7Du bist kein Bürgermeister.");
            }
        } else {
            s.sendMessage("Du musst ein Spieler sein.");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        if(s instanceof Player) {
            Player p = (Player) s;
            User u = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
            if(u.checkTownMember() && u.getTownRank().equals(TownRank.MAYOR)) {
                Town town = u.getTown();
                if(args.length == 1) {
                    return Common.getInstance().removeAutoComplete(Lists.newArrayList("set", "invite", "kick", "claim", "unclaim", "buy", "outpost", "border"), args[0]);
                } else if(args.length == 2) {
                    switch (args[0].toLowerCase()) {
                        case "invite":
                        case "kick":
                            return Common.getInstance().removeAutoComplete(Towny.getInstance().getManager().getPlayerManager().getResidents(town).stream().map(User::getName).collect(Collectors.toList()), args[1]);
                        case "buy":
                            return Common.getInstance().removeAutoComplete(Lists.newArrayList("townsize"), args[1]);
                        case "set":
                            return Common.getInstance().removeAutoComplete(Lists.newArrayList("mayor", "blackboard", "rank", "spawn", "taxes", "perm", "public"), args[1]);
                        case "outpost":
                            return Common.getInstance().removeAutoComplete(Lists.newArrayList("create", "rename", "set"), args[1]);
                    }
                } else if(args.length == 3) {
                    if (args[0].equalsIgnoreCase("set")) {
                        switch (args[1].toLowerCase()) {
                            case "mayor":
                            case "rank":
                                return Common.getInstance().removeAutoComplete(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[2]);
                            case "perm":
                                return Common.getInstance().removeAutoComplete(Arrays.stream(TownPermission.values()).map(Enum::toString).collect(Collectors.toList()), args[2]);
                            case "public":
                                return Common.getInstance().removeAutoComplete(Lists.newArrayList("true", "false"), args[2]);
                        }
                    } else if(args[0].equalsIgnoreCase("outpost")) {
                        if(args[1].equalsIgnoreCase("set"))
                            return Common.getInstance().removeAutoComplete(Lists.newArrayList("spawn"), args[2]);
                        else if(args[1].equalsIgnoreCase("rename")) {
                            return Common.getInstance().removeAutoComplete(new ArrayList<>(town.getOutposts().keySet()), args[2]);
                        }
                    }
                } else if(args.length == 4) {
                    if(args[0].equalsIgnoreCase("outpost")) {
                        if (args[1].equalsIgnoreCase("set")) {
                            if(args[2].equalsIgnoreCase("spawn")) {
                                return Common.getInstance().removeAutoComplete(new ArrayList<>(town.getOutposts().keySet()), args[3]);
                            }
                        }
                    } else if(args[0].equalsIgnoreCase("set")) {
                        if(args[1].equalsIgnoreCase("rank")) {
                            return Common.getInstance().removeAutoComplete(Arrays.stream(TownRank.values()).map(Enum::toString).collect(Collectors.toList()), args[3]);
                        }
                    }
                }
            }
        }
        return Lists.newArrayList();
    }

    private void sendHelpMessage(CommandSender s, String label) {
        s.sendMessage(prefix + "§6/" + label + " §7| Zeigt diese Hilfsnachricht\n" +
                        prefix + "§6/" + label + " set §7| Listet die Möglichkeit zum Attribute setzen auf\n" +
                        prefix + "§6/" + label + " invite [Spieler] §7| Lädt Spieler in die Stadt ein\n" +
                        prefix + "§6/" + label + " kick [Spieler] §7| Kickt den Spieler aus der Stadt\n" +
                        prefix + "§6/" + label + " claim §7| Claimt den Chunk in dem du stehst für die Stadt\n" +
                        prefix + "§6/" + label + " unclaim §7| Unclaimt den Chunk in dem du stehst\n" +
                        prefix + "§6/" + label + " buy townsize [Anzahl] §7| Erhöhere deine Stadtgröße\n" +
                        prefix + "§6/" + label + " outpost §7| Listet die Möglichkeiten für Outposts auf\n" +
                        prefix + "§6/" + label + " border §7| Schaltet die Grenzen der Stadt ein/aus");
    }

    private void sendSetHelpMessage(CommandSender s, String label) {
        s.sendMessage(prefix + "§e/" + label + " set §6mayor [Spieler] §7| Setzt einen neuen Bürgermeister\n" +
                        prefix + "§e/" + label + " set §6blackboard [Nachricht] §7| Setzt das schwarze Brett der Stadt\n" +
                        prefix + "§e/" + label + " set §6rank [Spieler] [Rang] §7| Setzt dem Spieler den Rang\n" +
                        prefix + "§e/" + label + " set §6spawn §7| Setzt den Spawn der Stadt\n" +
                        prefix + "§e/" + label + " set §6taxes [Steuern] §7| Setzt die Stadtsteuern\n" +
                        prefix + "§e/" + label + " set §6perm [Permission] [Spielergruppe] [true/false] §7| Setzt die Permission für die Spielergruppe\n" +
                        prefix + "§e/" + label + " set §6public [true/false] §7| Setzt den Öffentlichkeitsstatus.\n" +
                        prefix + "§e/" + label + " set §6pvp [true/false] §7| Erlaubt/Verbietet PvP in der Stadt\n" +
                        prefix + "§e/" + label + " set §6mobs [true/false] §7| Erlaubt/Verbietet Mobspawn in der Stadt");
    }

    private void sendOutpostHelpMessaage(CommandSender s, String label) {
        s.sendMessage(prefix + "§e/" + label + " outpost §6create [Name] §7| Setzt ein Outpostspawn auf deine Position\n" +
                        prefix + "§e/" + label + " outpost §6rename [Name] [Neuer Name] §7| Benennt ein Outpost neu\n" +
                        prefix + "§e/" + label + " outpost §6set spawn [Name] §7| Setzt den Outpostspawn auf deine Position");
    }
}
