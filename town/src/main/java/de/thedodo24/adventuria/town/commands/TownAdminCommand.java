package de.thedodo24.adventuria.town.commands;

import com.google.common.collect.Lists;
import com.sun.org.apache.xpath.internal.operations.Bool;
import de.thedodo24.adventuria.town.Towny;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.arango.WritableGenerator;
import de.thedodo24.commonPackage.economy.BankAccount;
import de.thedodo24.commonPackage.player.User;
import de.thedodo24.commonPackage.towny.*;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class TownAdminCommand implements CommandExecutor, TabCompleter {

    public TownAdminCommand() {
        PluginCommand cmdFull = Towny.getInstance().getPlugin().getCommand("townadmin");
        PluginCommand cmdAlias = Towny.getInstance().getPlugin().getCommand("townadmin");
        cmdFull.setExecutor(this);
        cmdFull.setTabCompleter(this);
        cmdAlias.setTabCompleter(this);
        cmdFull.setExecutor(this);
    }
    /*
        ta
        ta set
        ta town
        ta plot
     */

    private String prefix = "§7§l| §cStädte §7» ";
    private String noPerm(String perm) { return "§cYou do not have the permissions to execute this command. (" + perm + ")"; }
    private String formatValue(double v) { return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(v).split(" ")[0] + " A"; }



    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(args.length == 0) {
            sendHelpMessage(s, label);
        } else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("set")) {
                sendSetHelpMessage(s, label);
            } else if(args[0].equalsIgnoreCase("town")) {
                sendTownHelpMessage(s, label);
            } else {
                sendHelpMessage(s, label);
            }
        } else if(args.length == 3) {
            if(args[0].equalsIgnoreCase("town")) {
                if(args[1].equalsIgnoreCase("delete")) {
                    if(s.hasPermission("towny.admin.town.delete")) {
                        Town town = Towny.getInstance().getManager().getTownManager().get(args[2].toLowerCase());
                        if(town != null) {
                            String name = town.getName();
                            Towny.getInstance().getManager().getPlotManager().getPlots(town).forEach(plot -> Towny.getInstance().getManager().getPlotManager().delete(plot.getKey()));
                            Towny.getInstance().getManager().getPlayerManager().getResidents(town).forEach(User::removeTown);
                            Towny.getInstance().getManager().getTownManager().delete(args[2].toLowerCase());
                            Bukkit.getOnlinePlayers().forEach(all -> all.sendMessage(prefix + "§7Die Stadt §6" + name + " §7wurde aufgelöst."));
                            Bukkit.getConsoleSender().sendMessage("Die Stadt " + name + " wurde aufgelöst");
                        } else {
                            s.sendMessage(prefix + "§7Die Stadt §6" + args[2] + " §7existiert nicht.");
                        }
                    } else {
                        s.sendMessage(noPerm("towny.admin.town.delete"));
                    }
                } else {
                    sendTownHelpMessage(s, label);
                }
            } else {
                sendHelpMessage(s, label);
            }
        } else if(args.length == 4) {
            if(args[0].equalsIgnoreCase("town")) {
                switch(args[1].toLowerCase()) {
                    case "new":
                        if(s instanceof Player) {
                            Player p = (Player) s;
                            if(p.getLocation().getWorld().equals(Bukkit.getWorld("Freebuild"))) {
                                if(p.hasPermission("towny.admin.town.create")) {
                                    String name = args[2];
                                    Town testTown = Towny.getInstance().getManager().getTownManager().get(name.toLowerCase());
                                    if(testTown == null) {
                                        String mayorString = args[3];
                                        Player mayor;
                                        if((mayor = Bukkit.getPlayer(mayorString)) != null) {
                                            if(Common.getInstance().getCharList().stream().noneMatch(name::contains)) {
                                                Town newTown = Towny.getInstance().getManager().getTownManager().getOrGenerate(name.toLowerCase(), key -> {
                                                    Town t = new Town(key);
                                                    Map<String, Object> values = new HashMap<>();
                                                    values.put("name", name);
                                                    values.put("blackboard", "/townmayor set blackboard [Text]");
                                                    values.put("created", System.currentTimeMillis());
                                                    values.put("public", false);
                                                    values.put("balance", (long) 0);
                                                    values.put("taxes", 100L);
                                                    values.put("spawn", p.getLocation().serialize());
                                                    values.put("townsize", 0L);
                                                    values.put("outposts", new HashMap<String, Map<String, Object>>());
                                                    values.put("settings", new HashMap<String, Boolean>() {{
                                                        put("pvp", false);
                                                        put("mobs", false);
                                                    }});
                                                    t.setValues(values);
                                                    return t;
                                                });
                                                Plot plot = Towny.getInstance().getManager().getPlotManager().getOrGenerate(String.valueOf(p.getLocation().getChunk().getChunkKey()), key -> {
                                                    Plot a = new Plot(key);
                                                    Map<String, Object> values = new HashMap<>();
                                                    values.put("name", newTown.getName());
                                                    values.put("town", newTown.getKey());
                                                    Map<String, Boolean> townPlayerPermissions = new HashMap<String, Boolean>() {{
                                                        put(PlotPlayer.NATION.toString(), false);
                                                        put(PlotPlayer.OUTSIDER.toString(), false);
                                                        put(PlotPlayer.RESIDENT.toString(), false);
                                                        put(PlotPlayer.FRIEND.toString(), false);
                                                    }};
                                                    values.put("permissions", new HashMap<String, Map<String, Boolean>>() {{
                                                        put(TownPermission.BUILD.toString(), townPlayerPermissions);
                                                        put(TownPermission.DESTROY.toString(), townPlayerPermissions);
                                                        put(TownPermission.ITEM.toString(), townPlayerPermissions);
                                                        put(TownPermission.SWITCH.toString(), townPlayerPermissions);
                                                    }});
                                                    a.setValues(values);
                                                    return a;
                                                });
                                                Towny.getInstance().getManager().getPlotManager().save(plot);
                                                Towny.getInstance().getManager().getTownManager().save(newTown);
                                                User userMayor = Towny.getInstance().getManager().getPlayerManager().get(mayor.getUniqueId());
                                                userMayor.setTown(newTown.getKey(), TownRank.MAYOR);
                                                Towny.getInstance().getManager().getPlayerManager().save(userMayor);
                                                p.sendMessage(prefix + "§7Die Stadt §6" + name + " §7wurde erstellt.");
                                                Bukkit.getOnlinePlayers().forEach(all -> all.sendMessage(prefix + "§6" + mayor.getName() + " §7hat die Stadt §6" + name + " §7gegründet."));
                                                Bukkit.getConsoleSender().sendMessage("Die Stadt " + name + " wurde gegründet.");
                                            } else {
                                                p.sendMessage(prefix + "§7Der Name enthält verbotene Charakter.");
                                            }
                                        } else {
                                            p.sendMessage(prefix + "§7Der Bürgermeister muss online sein.");
                                        }
                                    } else {
                                        p.sendMessage(prefix + "§7Eine Stadt mit dem Namen §6" + name + " §7existiert bereits.");
                                    }
                                } else {
                                    p.sendMessage(noPerm("towny.admin.town.create"));
                                }
                            } else {
                                p.sendMessage(prefix + "§7Du kannst diesen Befehl nur im §6Freebuild §7ausführen.");
                            }
                        } else {
                            s.sendMessage(prefix + "§7Du musst ein Spieler sein.");
                        }
                        break;
                    case "rename":
                        if(s.hasPermission("towny.admin.town.rename")) {
                            String oldName = args[2];
                            String newName = args[3];
                            Town town = Towny.getInstance().getManager().getTownManager().get(oldName.toLowerCase());
                            if(town != null) {
                                Town checkTown = Towny.getInstance().getManager().getTownManager().get(newName.toLowerCase());
                                if(checkTown == null) {
                                    Town newTown = Towny.getInstance().getManager().getTownManager().getOrGenerate(newName.toLowerCase(), key -> {
                                        Town t = new Town(key);
                                        Map<String, Object> values = town.getValues();
                                        values.replace("name", newName);
                                        t.setValues(values);
                                        return t;
                                    });
                                    Towny.getInstance().getManager().getPlayerManager().getResidents(town).forEach(user -> {
                                        user.setTown(newTown.getKey(), user.getTownRank());
                                        Towny.getInstance().getManager().getPlayerManager().save(user);
                                    });
                                    Towny.getInstance().getManager().getTownManager().delete(oldName.toLowerCase());
                                    Towny.getInstance().getManager().getTownManager().save(newTown);
                                    Bukkit.getOnlinePlayers().forEach(all -> all.sendMessage(prefix + "§6" + oldName.toLowerCase() + " §7wurde in §6" + newName + " §7umbenannt."));
                                } else {
                                    s.sendMessage(prefix + "§7Eine Stadt mit dem Namen §6" + newName + " §7existiert bereits.");
                                }
                            } else {
                                s.sendMessage(prefix + "§7Die Stadt §6" + oldName.toLowerCase() + " §7existiert nicht.");
                            }
                        } else {
                            s.sendMessage(noPerm("towny.admin.town.rename"));
                        }
                        break;
                    case "deposit":
                        if(s.hasPermission("towny.admin.town.deposit")) {
                            Town town = Towny.getInstance().getManager().getTownManager().get(args[2].toLowerCase());
                            if(town != null) {
                                String arg = args[3];
                                if(arg.contains(","))
                                    arg = arg.replace(",", ".");
                                long value;
                                try {
                                    if(!arg.equalsIgnoreCase("Infinity") && !arg.equalsIgnoreCase("-Infinity")) {
                                        value = (long) (Double.parseDouble(arg) * 100);
                                    } else {
                                        s.sendMessage(prefix + "§7Ist nicht verfügbar.");
                                        return false;
                                    }
                                } catch(NumberFormatException ignored) {
                                    s.sendMessage(prefix + "§6Argument 4 §7muss eine positive Zahl sein.");
                                    return false;
                                }
                                if(value > 0) {
                                    if((town.getMoney() + value) < 0) {
                                        s.sendMessage(prefix + "§7Dein Kontostand kann nicht in §cMinus §7gehen.");
                                        return false;
                                    }
                                    long max = 9200000000000000000L;
                                    if(value >  max) {
                                        s.sendMessage(prefix + "§7Der Betrag ist zu hoch.");
                                        return false;
                                    }
                                    BankAccount staatskasse = Towny.getInstance().getManager().getBankManager().get("staatskasse");
                                    staatskasse.withdrawMoney(value);
                                    town.depositMoney(value);
                                    Towny.getInstance().getManager().getBankManager().save(staatskasse);
                                    Towny.getInstance().getManager().getTownManager().save(town);
                                    s.sendMessage(prefix + "§7Du hast §6" + formatValue(((Long) value).doubleValue() / 100) + " §7auf das Stadtkonto von §6" + town.getName() + " §7überwiesen.");
                                    Towny.getInstance().getManager().getPlayerManager().getResidents(town).stream()
                                            .filter(user -> Bukkit.getPlayer(user.getKey()) != null)
                                            .forEach(all -> Bukkit.getPlayer(all.getKey()).sendMessage("§7§l| §6" + town.getName() + " §7» §6" + s.getName() + " §7hat §6" + formatValue(((Long) value).doubleValue() / 100) + " §7auf das Stadtkonto überwiesen."));
                                } else {
                                    s.sendMessage(prefix + "§2Argument 4 §7muss eine positive Zahl sein.");
                                }
                            } else {
                                s.sendMessage(prefix + "§7Die Stadt §6" + args[2].toLowerCase() + " §7existiert nicht.");
                            }
                        } else {
                            s.sendMessage(noPerm("towny.admin.town.deposit"));
                        }
                        break;
                    case "withdraw":
                        if(s.hasPermission("towny.admin.town.withdraw")) {
                            Town town = Towny.getInstance().getManager().getTownManager().get(args[2].toLowerCase());
                            if(town != null) {
                                String arg = args[3];
                                if(arg.contains(","))
                                    arg = arg.replace(",", ".");
                                long value;
                                try {
                                    if(!arg.equalsIgnoreCase("Infinity") && !arg.equalsIgnoreCase("-Infinity")) {
                                        value = (long) (Double.parseDouble(arg) * 100);
                                    } else {
                                        s.sendMessage(prefix + "§7Ist nicht verfügbar.");
                                        return false;
                                    }
                                } catch(NumberFormatException ignored) {
                                    s.sendMessage(prefix + "§6Argument 4 §7muss eine positive Zahl sein.");
                                    return false;
                                }
                                if(value > 0) {
                                    if((town.getMoney() + value) < 0) {
                                        s.sendMessage(prefix + "§7Dein Kontostand kann nicht in §cMinus §7gehen.");
                                        return false;
                                    }
                                    long max = 9200000000000000000L;
                                    if(value >  max) {
                                        s.sendMessage(prefix + "§7Der Betrag ist zu hoch.");
                                        return false;
                                    }
                                    BankAccount staatskasse = Towny.getInstance().getManager().getBankManager().get("staatskasse");
                                    staatskasse.depositMoney(value);
                                    town.withdrawMoney(value);
                                    Towny.getInstance().getManager().getBankManager().save(staatskasse);
                                    Towny.getInstance().getManager().getTownManager().save(town);
                                    s.sendMessage(prefix + "§7Du hast §6" + formatValue(((Long) value).doubleValue() / 100) + " §7vom Stadtkonto von §6" + town.getName() + " §7entfernt.");
                                    Towny.getInstance().getManager().getPlayerManager().getResidents(town).stream()
                                            .filter(user -> Bukkit.getPlayer(user.getKey()) != null)
                                            .forEach(all -> Bukkit.getPlayer(all.getKey()).sendMessage("§7§l| §6" + town.getName() + " §7» §6" + s.getName() + " §7hat §6" + formatValue(((Long) value).doubleValue() / 100) + " §7vom Stadtkonto entfernt."));
                                } else {
                                    s.sendMessage(prefix + "§2Argument 4 §7muss eine positive Zahl sein.");
                                }
                            } else {
                                s.sendMessage(prefix + "§7Die Stadt §6" + args[2].toLowerCase() + " §7existiert nicht.");
                            }
                        } else {
                            s.sendMessage(noPerm("towny.admin.town.withdraw"));
                        }
                        break;
                    default:
                        sendTownHelpMessage(s, label);
                        break;
                }
            } else if(args[0].equalsIgnoreCase("set")) {
                switch(args[1].toLowerCase()) {
                    case "mayor":
                        if(s.hasPermission("towny.admin.town.set.mayor")) {
                            Town town = Towny.getInstance().getManager().getTownManager().get(args[2].toLowerCase());
                            if(town != null) {
                                Player newMayor;
                                if((newMayor = Bukkit.getPlayer(args[3])) != null) {
                                    User newMayorUser = Towny.getInstance().getManager().getPlayerManager().get(newMayor.getUniqueId());
                                    if(newMayorUser.checkTownMember()) {
                                        if(newMayorUser.getTown().getKey().equalsIgnoreCase(town.getKey())) {
                                            if(newMayorUser.getTownRank().equals(TownRank.MAYOR)) {
                                                s.sendMessage(prefix + "§6" + newMayor.getName() + " §7ist bereits Bürgermeister der Stadt.");
                                                return false;
                                            }
                                            List<User> residents = Towny.getInstance().getManager().getPlayerManager().getResidents(town);
                                            User oldMayor = residents.stream().filter(user -> user.getTownRank().equals(TownRank.MAYOR)).findFirst().get();
                                            oldMayor.updateTownRank(TownRank.CITIZEN);
                                            newMayorUser.updateTownRank(TownRank.MAYOR);
                                            s.sendMessage(prefix + "§7Der neue Bürgermeister von §6" + town.getName() + " §7ist §6" + newMayor.getName() + "§7.");
                                            residents.stream().filter(user -> Bukkit.getPlayer(user.getKey()) != null).forEach(user -> Bukkit.getPlayer(user.getKey()).sendMessage("§7§l| §6" + town.getName() + " §7» §6" + oldMayor.getName() +" §7hat abgedankt. Der neue Bürgermeister der Stadt ist §6" + newMayor.getName() + "§7!"));
                                        } else {
                                            s.sendMessage(prefix + "§6" + newMayor.getName() + " §7ist nicht in der Stadt.");
                                        }
                                    } else {
                                        s.sendMessage(prefix + "§6" + newMayor.getName() + " §7ist in keiner Stadt.");
                                    }
                                } else {
                                    s.sendMessage(prefix + "§7Der Spieler §6" + args[3] + " §7ist nicht online.");
                                }
                            } else {
                                s.sendMessage(prefix + "§7Die Stadt §6" + args[2].toLowerCase() + " §7existiert nicht.");
                            }
                        } else {
                            s.sendMessage(noPerm("towny.admin.town.set.mayor"));
                        }
                         break;
                    case "public":
                        if(s.hasPermission("towny.admin.town.set.public")) {
                            Town town = Towny.getInstance().getManager().getTownManager().get(args[2].toLowerCase());
                            if(town != null) {
                                boolean p;
                                try {
                                    p = Boolean.parseBoolean(args[3]);
                                } catch(Exception e) {
                                    s.sendMessage(prefix + "§6Argument 4 §7muss 'true' oder 'false' sein.");
                                    return false;
                                }
                                town.setPublic(p);
                                s.sendMessage(prefix + "§7Der Status der Stadt wurde auf " + (p ? "§aöffentlich" : "§cgeschlossen") + " §7geändert.");
                            } else {
                                s.sendMessage(prefix + "§7Die Stadt §6" + args[2].toLowerCase() + " §7existiert nicht.");
                            }
                        } else {
                            s.sendMessage(noPerm("towny.admin.town.set.public"));
                        }
                        break;
                    default:
                        sendSetHelpMessage(s, label);
                        break;
                }
            } else {
                sendHelpMessage(s, label);
            }
        } else {
            sendHelpMessage(s, label);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        if(s instanceof Player) {
            if(s.hasPermission("towny.admin")) {
                if(args.length == 1) {
                    return Common.getInstance().removeAutoComplete(Lists.newArrayList("set", "town"), args[0]);
                } else if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("set"))
                        return Common.getInstance().removeAutoComplete(Lists.newArrayList("mayor", "public"), args[1]);
                    else if(args[0].equalsIgnoreCase("town"))
                        return Common.getInstance().removeAutoComplete(Lists.newArrayList("new", "rename", "delete", "deposit", "withdraw"), args[1]);
                } else if(args.length == 3) {
                    if(args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("town")) {
                        switch(args[1].toLowerCase()) {
                            case "mayor":
                            case "public":
                            case "rename":
                            case "delete":
                            case "deposit":
                            case "withdraw":
                                return Common.getInstance().removeAutoComplete(Towny.getInstance().getManager().getTownManager().getTowns().stream().map(Town::getName).collect(Collectors.toList()), args[2]);
                        }
                    }
                } else if(args.length == 4) {
                    if(args[0].equalsIgnoreCase("town") && args[1].equalsIgnoreCase("new")) {
                        return Common.getInstance().removeAutoComplete(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[3]);
                    } else if(args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("public")) {
                        return Common.getInstance().removeAutoComplete(Lists.newArrayList("true", "false"), args[3]);
                    }
                }
            }
        }
        return Lists.newArrayList();
    }

    private void sendSetHelpMessage(CommandSender s, String label) {
        if(s.hasPermission("towny.admin")) {
            s.sendMessage(prefix + "§4/" + label + " set §cmayor [Stadt] [Name] §7| Setzt den Bürgermeister der Stadt\n" +
                            prefix + "§4/" + label + " set §cpublic [Stadt] [true/false] §7| Setzt den Öffentlichkeitsstatus der Stadt");
        } else {
            s.sendMessage(noPerm("towny.admin"));
        }
    }

    private void sendTownHelpMessage(CommandSender s, String label) {
        if(s.hasPermission("towny.admin")) {
            s.sendMessage(prefix + "§4/" + label + " town §cnew [Name] [Bürgermeister] §7| Erstellt eine neue Stadt\n" +
                    prefix + "§4/" + label + " town §crename [Stadtname] [Neuer Stadtname] §7| Benennt eine Stadt um\n" +
                    prefix + "§4/" + label + " town §cdelete [Stadtname] §7| Löscht eine Stadt\n" +
                    prefix + "§4/" + label + " town §cdeposit [Stadtname] [Betrag] §7| Überweist Geld auf das Stadtkonto\n" +
                    prefix + "§4/" + label + " town §cwithdraw [Stadtname] [Betrag] §7| Hebt Geld vom Stadtkonto ab");
        } else {
            s.sendMessage(noPerm("towny.admin"));
        }
    }

    private void sendHelpMessage(CommandSender s, String label) {
        if(s.hasPermission("towny.admin")) {
            s.sendMessage(prefix + "§c/" + label + " §7|| Zeigt diese Hilfe\n" +
                    prefix + "§c/" + label + " set §7|| Listet die Möglichkeit zum Attribute setzen auf\n" +
                    prefix + "§c/" + label + " town §7|| Stadt Admin Befehls Übersicht");
        } else {
            s.sendMessage(noPerm("towny.admin"));
        }
    }

}
