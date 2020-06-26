package de.thedodo24.adventuria.town.commands;

import com.google.common.collect.Lists;
import de.thedodo24.adventuria.town.Towny;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.economy.BankAccount;
import de.thedodo24.commonPackage.player.User;
import de.thedodo24.commonPackage.towny.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

public class PlotCommand implements CommandExecutor, TabCompleter {

    public PlotCommand() {
        PluginCommand cmd = Towny.getInstance().getPlugin().getCommand("plot");
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
    }

    /*
    plot info
    plot claim
    plot fs [betrag]
    plot nfs [betrag]
    plot set
    plot set
     ... perm permission spielergruppe boolean
     ... name
     */

    private String prefix = "§7§l| §6Städte §7» ";
    private String formatValue(double v) { return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(v).split(" ")[0] + " A"; }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (s instanceof Player) {
            Player p = (Player) s;
            User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("info")) {
                    if(user.checkTownMember() || p.hasPermission("towny.admin.plot.info")) {
                        Plot plot = Towny.getInstance().getManager().getPlotManager().get(String.valueOf(p.getLocation().getChunk().getChunkKey()));
                        if(plot != null) {
                            Town town = user.getTown();
                                p.sendMessage("§7|----------| §6Plot " + plot.getChunk().getX() + "; " + plot.getChunk().getZ()
                                        + (plot.getChunk().getChunkKey() == town.getSpawn().getChunk().getChunkKey() ? " §7(§6Stadtspawn§7)" : "") +
                                        (town.getOutposts().values().stream().map(Location::getChunk).map(Chunk::getChunkKey).anyMatch(cKey -> plot.getChunk().getChunkKey() == cKey) ? " §7(§6Outpostspawn§7)" : "")
                                        +  " §7|----------|\n" +
                                        "§7» Name: §6" + plot.getName() + "\n" +
                                        "§7» Stadt: §6" + plot.getTown().getName() + "\n" +
                                        "§7» Besitzer: §6" + (plot.isOwned() ? Towny.getInstance().getManager().getPlayerManager().get(plot.getOwner()).getName() : "Kein Besitzer"));
                                Map<TownPermission, Map<PlotPlayer, Boolean>> permissionMap = plot.getPermissionMap();
                                StringBuilder permissions = new StringBuilder();
                                for(TownPermission townPermission : permissionMap.keySet()) {
                                    Map<PlotPlayer, Boolean> permission = permissionMap.get(townPermission);
                                    StringBuilder line = new StringBuilder("§6" + townPermission.getDisplayName() + "§7: ");
                                    for(PlotPlayer townPlayer : permission.keySet()) {
                                        line.append(permission.get(townPlayer) ? "§a" : "§c").append(townPlayer.getDisplayNameShort());
                                    }
                                    permissions.append(line).append(" ");
                                }
                                p.sendMessage("§7» Berechtigungen: §6" + permissions.toString());
                        } else {
                            p.sendMessage(prefix + "§7Du stehst auf keinem Plot einer Stadt.");
                        }
                    } else {
                        p.sendMessage(prefix + "§7Du bist in keiner Stadt!");
                    }
                } else if(args[0].equalsIgnoreCase("claim")) {
                    if(user.checkTownMember()) {
                        Plot plot = Towny.getInstance().getManager().getPlotManager().get(String.valueOf(p.getLocation().getChunk().getChunkKey()));
                        if(plot != null) {
                            if(user.checkTownMember()) {
                                if(plot.getTown().getKey().equalsIgnoreCase(user.getTown().getKey())) {
                                    if(plot.isBuyable()) {
                                        if(plot.isOwned()) {
                                            if(plot.getOwner().equals(p.getUniqueId())) {
                                                p.sendMessage(prefix + "§7Du kannst nicht dein eigenes Grundstück kaufen.");
                                                return false;
                                            }
                                        }
                                        long price = plot.getBuyPrice();
                                        if((user.getBalance() - price) >= 0) {
                                            user.withdrawMoney(price);
                                            Town town = user.getTown();
                                            town.depositMoney(price);
                                            plot.setOwner(p.getUniqueId());
                                            plot.unSetBuyable();
                                            plot.setName("Grundstück von " + p.getName());
                                            Map<String, Boolean> townPlayerPermissions = new HashMap<String, Boolean>() {{
                                                put(PlotPlayer.FRIEND.toString(), true);
                                                put(PlotPlayer.NATION.toString(), false);
                                                put(PlotPlayer.OUTSIDER.toString(), false);
                                                put(PlotPlayer.RESIDENT.toString(), false);
                                            }};
                                            Map<String, Map<String, Boolean>> permissionMap = new HashMap<>();
                                            permissionMap.put(TownPermission.BUILD.toString(), townPlayerPermissions);
                                            permissionMap.put(TownPermission.DESTROY.toString(), townPlayerPermissions);
                                            permissionMap.put(TownPermission.ITEM.toString(), townPlayerPermissions);
                                            permissionMap.put(TownPermission.SWITCH.toString(), townPlayerPermissions);
                                            plot.setPermission(permissionMap);
                                            p.sendMessage(prefix + "§7Du hast dir das Plot [" + plot.getChunk().getX() + ";" + plot.getChunk().getZ() + "] gekauft.");
                                            Towny.getInstance().getManager().getPlayerManager().getResidents(town)
                                                    .stream().filter(u -> Bukkit.getPlayer(u.getKey()) != null)
                                                    .forEach(u -> Bukkit.getPlayer(u.getKey()).sendMessage("§7§l| §6" + town.getName() + " §7» §6" + p.getName() + " §7hat sich das Plot ["+plot.getChunk().getX()+";"+plot.getChunk().getZ()+"] gekauft."));
                                        } else {
                                            p.sendMessage(prefix + "§7Du hast nicht genügend Geld um dieses Plot zu kaufen.");
                                        }
                                    } else {
                                        p.sendMessage(prefix + "§7Dieses Plot wird nicht zum Verkauf angeboten.");
                                    }
                                } else {
                                    p.sendMessage(prefix + "§7Du stehst auf keinem Plot deiner Stadt.");
                                }
                            } else {
                                p.sendMessage(prefix + "§7Du bist in keiner Stadt.");
                            }
                        } else {
                            p.sendMessage(prefix + "§7Du stehst auf keinem Plot deiner Stadt.");
                        }
                    } else {
                        p.sendMessage(prefix + "§7Du bist in keiner Stadt!");
                    }
                } else if(args[0].equalsIgnoreCase("nfs")) {
                    if(user.checkTownMember()) {
                        Plot plot = Towny.getInstance().getManager().getPlotManager().get(String.valueOf(p.getLocation().getChunk().getChunkKey()));
                        if(plot != null) {
                            if(!p.hasPermission("towny.admin.plot.nfs")) {
                                if(!user.checkTownMember()) {
                                    p.sendMessage(prefix + "§7Du bist in keiner Stadt.");
                                    return false;
                                } else if (!plot.getTown().getKey().equalsIgnoreCase(user.getTown().getKey())) {
                                    p.sendMessage(prefix + "§7Du stehst auf keinem Plot deiner Stadt.");
                                    return false;
                                }
                            }
                            if(plot.isOwned()) {
                                if(!plot.getOwner().equals(p.getUniqueId())) {
                                    if(!p.hasPermission("towny.admin.plot.nfs")) {
                                        if(!(user.getTownRank().equals(TownRank.MAYOR) || user.getTownRank().equals(TownRank.ASSISTANT))) {
                                            p.sendMessage(prefix + "§7Du bist dazu nicht berechtigt.");
                                            return false;
                                        }
                                    }
                                }
                            } else {
                                if(!p.hasPermission("towny.admin.plot.nfs") && !(user.getTownRank().equals(TownRank.MAYOR) || user.getTownRank().equals(TownRank.ASSISTANT))) {
                                    p.sendMessage(prefix + "§7Du bist dazu nicht berechtigt.");
                                    return false;
                                }
                            }
                            if(!plot.isBuyable()) {
                                p.sendMessage(prefix + "§7Das Plot ist nicht zu verkaufen.");
                                return false;
                            }
                            plot.unSetBuyable();
                            if(plot.isOwned())
                                plot.setName("Grundstück von " + Towny.getInstance().getManager().getPlayerManager().get(plot.getOwner()).getName());
                            else
                                plot.setName("Unbesetzt");
                            Towny.getInstance().getManager().getPlayerManager().getResidents(user.getTown()).stream().filter(u -> Bukkit.getPlayer(u.getKey()) != null)
                                    .forEach(u -> Bukkit.getPlayer(u.getKey()).sendMessage(prefix + "§7Das Plot ["+plot.getChunk().getX()+";"+plot.getChunk().getZ()+"] ist nun nicht mehr zum Verkauf verfügbar."));
                        } else {
                            p.sendMessage(prefix + "§7Du stehst auf keinem Plot einer Stadt.");
                        }
                    } else {
                        p.sendMessage(prefix + "§7Du bist in keiner Stadt!");
                    }
                } else if(args[0].equalsIgnoreCase("set")) {
                    sendSetHelpMessage(s, label);
                } else if(args[0].equalsIgnoreCase("unclaim")) {
                    if(user.checkTownMember()) {
                        Plot plot = Towny.getInstance().getManager().getPlotManager().get(String.valueOf(p.getLocation().getChunk().getChunkKey()));
                        if(plot != null) {
                            if(plot.isOwned() && plot.getOwner().equals(p.getUniqueId())) {
                                plot.removeOwner();
                                plot.setName(user.getTown().getName());
                                p.sendMessage(prefix + "§7Das Plot §8[" + plot.getChunk().getX() + ";" + plot.getChunk().getZ() + "] §7ist nicht mehr in deinem Besitzt.");
                                Towny.getInstance().getManager().getPlayerManager().getResidents(user.getTown()).stream().filter(u -> Bukkit.getPlayer(u.getKey()) != null)
                                        .forEach(u -> Bukkit.getPlayer(u.getKey()).sendMessage("§7§l| §6"+user.getTown().getName()+" §7» Das Plot §8["+plot.getChunk().getX()+";"+plot.getChunk().getZ()+"] §7ist nun nicht mehr im Besitz von §6" + p.getName() + "§7."));
                            } else {
                                p.sendMessage(prefix + "§7Dieses Plot gehört nicht dir.");
                            }
                        } else {
                            p.sendMessage(prefix + "§7Du stehst auf keinem geclaimten Plot.");
                        }
                    } else {
                        p.sendMessage(prefix + "§7Du bist in keiner Stadt.");
                    }
                } else {
                    sendHelpMessage(s, label);
                }
            } else if(args.length == 2) {
                if(args[0].equalsIgnoreCase("fs")) {
                    String arg = args[1];
                    if(arg.contains(","))
                        arg = arg.replace(",", ".");
                    long value;
                    try {
                        if(arg.equalsIgnoreCase("all"))
                            value = user.getBalance();
                        else
                        if(!arg.equalsIgnoreCase("Infinity") && !arg.equalsIgnoreCase("-Infinity")) {
                            value = (long) (Double.parseDouble(arg) * 100);
                        } else {
                            p.sendMessage(prefix + "§7Nicht möglich.");
                            return false;
                        }
                    } catch(NumberFormatException ignored) {
                        p.sendMessage(prefix + "§2Argument 2 §7muss eine positive Zahl sein.");
                        return false;
                    }
                    if(value > 0) {
                        long max = 9200000000000000000L;
                        if(value >  max) {
                            p.sendMessage(prefix + "§7Der Betrag ist zu hoch.");
                            return false;
                        }
                        Plot plot = Towny.getInstance().getManager().getPlotManager().get(String.valueOf(p.getLocation().getChunk().getChunkKey()));
                        if(plot != null) {
                            if(!user.checkTownMember() || (!plot.getTown().getKey().equalsIgnoreCase(user.getTown().getKey()) && !p.hasPermission("towny.admin.plot.fs"))) {
                                p.sendMessage(prefix + "§7Du stehst auf keinem Plot deiner Stadt.");
                                return false;
                            }
                            if(plot.isOwned()) {
                                if(!plot.getOwner().equals(p.getUniqueId())) {
                                    if(!p.hasPermission("towny.admin.plot.fs")) {
                                        if(!(user.getTownRank().equals(TownRank.MAYOR) || user.getTownRank().equals(TownRank.ASSISTANT))) {
                                            p.sendMessage(prefix + "§7Du bist dazu nicht berechtigt.");
                                            return false;
                                        }
                                    }
                                }
                            } else {
                                if(!p.hasPermission("towny.admin.plot.fs") && !(user.getTownRank().equals(TownRank.MAYOR) || user.getTownRank().equals(TownRank.ASSISTANT))) {
                                    p.sendMessage(prefix + "§7Du bist dazu nicht berechtigt.");
                                    return false;
                                }
                            }
                            if(plot.isBuyable()) {
                                p.sendMessage(prefix + "§7Das Grundstück ist bereits zu verkaufen.");
                                return false;
                            }
                            plot.setBuyable(value);
                            if(p.hasPermission("towny.admin.plot.fs") || user.getTownRank().equals(TownRank.MAYOR) || user.getTownRank().equals(TownRank.ASSISTANT))
                                plot.removeOwner();
                            if(plot.isOwned())
                                plot.setName("Grundstück von " + Towny.getInstance().getManager().getPlayerManager().get(plot.getOwner()).getName() + " - Zu Verkaufen: " + formatValue(((Long) value).doubleValue() / 100));
                            else
                                plot.setName("Zu Verkaufen: " + formatValue(((Long) value).doubleValue() / 100));
                            Towny.getInstance().getManager().getPlayerManager().getResidents(user.getTown()).stream()
                                    .filter(u -> Bukkit.getPlayer(u.getKey()) != null)
                                    .forEach(u -> Bukkit.getPlayer(u.getKey()).sendMessage("§7§l| §6" + user.getTown().getName() + " §7» Das Plot ["+plot.getChunk().getX()+";"+plot.getChunk().getZ()+"] ist nun zum Verkauf für §6" + formatValue(((Long) value).doubleValue() / 100) + "§7."));
                        } else {
                            p.sendMessage(prefix + "§7Du stehst auf keinem Plot einer Stadt.");
                        }
                    } else {
                        p.sendMessage(prefix + "§2Argument 2 §7muss eine positive Zahl sein.");
                    }
                } else if(args[0].equalsIgnoreCase("set")) {
                    String name = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
                    if(user.checkTownMember()) {
                        Plot plot = Towny.getInstance().getManager().getPlotManager().get(String.valueOf(p.getLocation().getChunk().getChunkKey()));
                        if(plot != null) {
                            if(plot.getTown().getKey().equalsIgnoreCase(user.getTown().getKey())) {
                                if(user.getTownRank().equals(TownRank.MAYOR) || user.getTownRank().equals(TownRank.ASSISTANT)) {
                                    if(!plot.isOwned()) {
                                        if(!plot.isBuyable()) {
                                            plot.setName(name);
                                            p.sendMessage(prefix + "§7Der Name des Plots ["+plot.getChunk().getX()+";"+plot.getChunk().getZ()+"] wurde auf " + name + " gesetzt");
                                        } else {
                                            p.sendMessage(prefix + "§7Dieses Plot wird verkauft.");
                                        }
                                    } else {
                                        p.sendMessage(prefix + "§7Dieses Plot wurde verkauft.");
                                    }
                                } else {
                                    p.sendMessage(prefix + "§7Du bist dazu nicht berechtigt.");
                                }
                            } else {
                                p.sendMessage(prefix + "§7Du stehst auf keinem Plot deiner Stadt.");
                            }
                        } else {
                            p.sendMessage(prefix + "§7Du stehst auf keinem Plot einer Stadt.");
                        }
                    } else {
                        p.sendMessage(prefix + "§7Du bist in keiner Stadt.");
                    }
                }
            } else if(args.length == 5) {
                if(args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("perm")) {
                    Plot plot = Towny.getInstance().getManager().getPlotManager().get(String.valueOf(p.getLocation().getChunk().getChunkKey()));
                    if(plot != null) {
                        if(user.checkTownMember() || p.hasPermission("towny.admin.plot.set.perm")) {
                                if(plot.getTown().getKey().equalsIgnoreCase(user.getTown().getKey()) || p.hasPermission("towny.admin.plot.set.perm")) {
                                    TownPermission townPermission;
                                    try {
                                        townPermission = TownPermission.valueOf(args[2].toUpperCase());
                                    } catch(Exception e) {
                                        p.sendMessage(prefix + "§7Diese Permission ist nicht verfügbar. " + Arrays.stream(TownPermission.values()).map(Enum::toString).collect(Collectors.joining(", ")));
                                        return false;
                                    }
                                    PlotPlayer townPlayer;
                                    try {
                                        townPlayer = PlotPlayer.valueOf(args[3].toUpperCase());
                                    } catch(Exception e) {
                                        p.sendMessage(prefix + "§7Diese Spielergruppe ist nicht verfügbar. " + Arrays.stream(PlotPlayer.values()).map(Enum::toString).collect(Collectors.joining(", ")));
                                        return false;
                                    }
                                    boolean bool;
                                    try {
                                        bool = Boolean.parseBoolean(args[4]);
                                    } catch (Exception e) {
                                        p.sendMessage(prefix + "§6Argument 5 §7muss true oder false sein.");
                                        return false;
                                    }
                                    plot.updatePermission(townPermission, townPlayer, bool);
                                    p.sendMessage(prefix + "§7Die Permission §6" + townPermission.getDisplayName() + " §7wurde für die Spielergruppe §6" + townPlayer.getDisplayName() + " §7auf §6" + (bool ? "§aan" : "§caus") + " §7gesetzt");
                                } else {
                                    p.sendMessage(prefix + "§7Du stehst auf keinem Plot deiner Stadt.");
                                }
                        } else {
                            p.sendMessage(prefix + "§7Du bist kein Mitglied einer Stadt.");
                        }
                    } else {
                        p.sendMessage(prefix + "§7Du stehst auf keinem Plot einer Stadt.");
                    }
                } else {
                    sendHelpMessage(s, label);
                }
            } else {
                sendHelpMessage(s, label);
            }
        } else {
            s.sendMessage("Du musst ein Spieler sein.");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        if(s instanceof Player) {
            if(args.length == 1) {
                return Common.getInstance().removeAutoComplete(Lists.newArrayList("info", "claim", "fs", "nfs", "set", "unclaim"), args[0]);
            } else if(args.length == 2) {
                if(args[0].equalsIgnoreCase("set"))
                    return Common.getInstance().removeAutoComplete(Lists.newArrayList("perm"), args[1]);
            } else if(args.length == 3) {
                if(args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("perm"))
                    return Common.getInstance().removeAutoComplete(Arrays.stream(TownPermission.values()).map(Enum::toString).collect(Collectors.toList()), args[2]);
            } else if(args.length == 4) {
                if(args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("perm"))
                    return Common.getInstance().removeAutoComplete(Arrays.stream(PlotPlayer.values()).map(Enum::toString).collect(Collectors.toList()), args[3]);
            } else if(args.length == 5) {
                if(args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("perm"))
                    return Common.getInstance().removeAutoComplete(Lists.newArrayList("true", "false"), args[4]);
            }
        }
        return Lists.newArrayList();
    }

    private void sendHelpMessage(CommandSender s, String label) {
        s.sendMessage(prefix + "§6/" + label + " info §7| Zeigt Informationen über das aktuelle Plot\n" +
                prefix + "§6/" + label + " claim §7| Kauft das Plot auf dem du stehst\n" +
                prefix + "§6/" + label + " fs [Betrag] §7| Bietet ein Grundstück zum Verkauf an\n" +
                prefix + "§6/" + label + " nfs §7| Löscht ein Verkaufsangebot vom Grundstück\n" +
                prefix + "§6/" + label + " unclaim §7| Entfernt das Plot, auf dem du stehst, aus deinem Besitz\n" +
                prefix + "§6/" + label + " set §7| Zeigt die Auflistung zum setzen von Attributen");
    }

    private void sendSetHelpMessage(CommandSender s, String label) {
        s.sendMessage(prefix + "§e/" + label + " set §6perm [Permission] [Spielergruppe] [true/false] §7| Setzt die Permission für die Spielergruppe\n" +
                        prefix + "§e/" + label + " set §6[Name] §7| Setzt den Namen des Plots");
    }
}
