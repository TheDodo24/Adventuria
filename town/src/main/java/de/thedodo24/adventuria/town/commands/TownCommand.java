package de.thedodo24.adventuria.town.commands;

import com.google.common.collect.Lists;
import de.thedodo24.adventuria.town.Towny;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.player.User;
import de.thedodo24.commonPackage.towny.Plot;
import de.thedodo24.commonPackage.towny.Town;
import de.thedodo24.commonPackage.towny.TownPermission;
import de.thedodo24.commonPackage.towny.TownRank;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class TownCommand implements CommandExecutor, TabCompleter {

    public TownCommand() {
        PluginCommand townCmd = Towny.getInstance().getPlugin().getCommand("town");
        PluginCommand tCmd = Towny.getInstance().getPlugin().getCommand("t");
        townCmd.setExecutor(this);
        tCmd.setExecutor(this);
        townCmd.setTabCompleter(this);
        tCmd.setTabCompleter(this);
    }

    /*
    t name
    t
    t join name
    t help
    t spawn <name>
    t leave
    t list
    t online
    t deposit <betrag>
    t withdraw <betrag>
     */

    private String prefix = "§7§l| §6Städte §7» ";
    private String formatValue(double v) { return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(v).split(" ")[0] + " A"; }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(args.length == 0) {
            if(s instanceof Player) {
                Player p = (Player) s;
                User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                if(user.checkTownMember()) {
                    Town town = user.getTown();
                    p.sendMessage("§7|----------| "+town.getRank()+" §6" + town.getName() + " " + (town.isPublic() ? "§7(§aÖffentlich§7)" : "§7(§cGeschlossen§7)") + " §7|----------|");
                    p.sendMessage("§7» Schwarzes Brett: §6" + town.getBlackboard());
                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy");
                    Date d = new Date(town.getCreated());
                    p.sendMessage("§7» Gegründet: §6" + format.format(d));
                    p.sendMessage("§7» Stadtgröße: §6" + Towny.getInstance().getManager().getPlotManager().getPlots(town).size() + " / " + (Towny.getInstance().getManager().getPlayerManager().getResidents(town).size() * 16 + town.getBuyedTownSize()) + " §7[Stadtplatz: "+(int) town.getSpawn().getX()+", "+(int) town.getSpawn().getZ()+"]");
                    List<User> residents = Towny.getInstance().getManager().getPlayerManager().getResidents(town);
                    if(town.getOutposts().size() > 0) {
                        List<Plot> plots = Towny.getInstance().getManager().getPlotManager().getPlots(town);
                        Map<String, Integer> outpostResidents = new HashMap<>();
                        town.getOutposts().keySet()
                                .forEach(o ->
                                        outpostResidents.put(o,
                                                ((Long) plots.stream()
                                                        .filter(pl -> pl.isOutpostPlot() && pl.getOutpost().equalsIgnoreCase(o))
                                                        .filter(Plot::isOwned)
                                                        .map(pl -> pl.getOwner().toString())
                                                        .distinct()
                                                        .count()).intValue()));
                    String text = "§6" + outpostResidents.keySet().stream().map(k -> k + " §7["+outpostResidents.get(k)+"]").collect(Collectors.joining("§7, §6"));
                    p.sendMessage("§7» Outposts: §6" + text);
                }
                    p.sendMessage("§7» Geld: §6" + formatValue(((Long) town.getMoney()).doubleValue() / 100) + " §7| Steuer: §6" + (((Long) town.getTaxes()).doubleValue() / 100) + "%");
                    p.sendMessage("§7» Bürgermeister: §6" + residents.stream().filter(u -> u.getTownRank().equals(TownRank.MAYOR)).findFirst().get().getName());
                    List<User> assistants = residents.stream().filter(u -> u.getTownRank().equals(TownRank.ASSISTANT)).collect(Collectors.toList());
                    if(assistants.size() > 0) {
                        p.sendMessage("§7» Stadthalter [" + assistants.size() + "]: §6" + assistants.stream().map(User::getName).collect(Collectors.joining("§7,§6 ")));
                    }
                    List<User> helper = residents.stream().filter(u -> u.getTownRank().equals(TownRank.HELPER)).collect(Collectors.toList());
                    if(helper.size() > 0) {
                        p.sendMessage("§7» Stadtbauer [" + helper.size() + "]: §6" + helper.stream().map(User::getName).collect(Collectors.joining("§7,§6 ")));
                    }
                    p.sendMessage("§7» Bürger [" + residents.size() + "]: §6" + residents.stream().map(User::getName).collect(Collectors.joining("§7,§6 ")));
                } else {
                    sendHelpMessage(p, label);
                }
            } else {
                s.sendMessage(prefix + "§7Du musst ein Spieler sein.");
            }
        } else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("leave")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                    User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                    if(user.checkTownMember()) {
                        if(!user.getTownRank().equals(TownRank.MAYOR)) {
                            Town t = user.getTown();
                            user.removeTown();
                            Towny.getInstance().getManager().getPlayerManager().save(user);
                            p.sendMessage(prefix + "§7Du hast die Stadt verlassen.");
                            Towny.getInstance().getManager().getPlayerManager().getResidents(t).stream().filter(u -> Bukkit.getPlayer(u.getKey()) != null).forEach(u -> Bukkit.getPlayer(u.getKey()).sendMessage("§7§l| §6" + t.getName() + " §7» §6" + p.getName() + " §7hat die Stadt verlassen."));
                        } else {
                            p.sendMessage(prefix + "§7Du kannst als Bürgermeister die Stadt nicht verlassen. §8(/townmayor set mayor [Name])");
                        }
                    } else {
                        p.sendMessage(prefix + "§7Du bist in keiner Stadt.");
                    }
                } else {
                    s.sendMessage(prefix + "§7Du musst ein Spieler sein.");
                }
            } else if(args[0].equalsIgnoreCase("list")) {
                List<Town> towns = Towny.getInstance().getManager().getTownManager().getTowns();
                s.sendMessage("§7|----------| §6Städte §7|----------|");
                towns.forEach(town -> s.sendMessage("§7» §6" + town.getName() + " §7("+town.getRank()+") §7|| " + Towny.getInstance().getManager().getPlayerManager().getResidents(town).size() + " Einwohner || " + town.getOutposts().size() + " Outposts"));
            } else if(args[0].equalsIgnoreCase("online")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                    User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                    if(user.checkTownMember()) {
                        List<User> residents = Towny.getInstance().getManager().getPlayerManager().getResidents(user.getTown());
                        List<User> online = residents.stream().filter(u -> Bukkit.getPlayer(u.getKey()) != null).collect(Collectors.toList());
                        p.sendMessage(prefix + "§7Einwohner online (" + online.size() + "/" + residents.size() + "):");
                        p.sendMessage("§7" + online.stream().map(User::getName).collect(Collectors.joining(", ")));
                    } else {
                        p.sendMessage(prefix + "§7Du bist in keiner Stadt.");
                    }
                } else {
                    s.sendMessage(prefix + "§7Du musst ein Spieler sein.");
                }
            } else if(args[0].equalsIgnoreCase("spawn")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                    User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                    if(user.checkTownMember() || p.hasPermission("towny.admin.town.spawn")) {
                        Town t = user.getTown();
                        p.teleport(t.getSpawn(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                        p.sendMessage(prefix + "§7Du wurdest zum Stadtspawn teleportiert.");
                    } else {
                        p.sendMessage(prefix + "§7Du bist in keiner Stadt.");
                    }
                } else {
                    s.sendMessage(prefix + "§7Du musst ein Spieler sein.");
                }
            } else if(args[0].equalsIgnoreCase("outposts")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                    User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                    if(user.checkTownMember()) {
                        Town town = user.getTown();
                        if(town.hasOutposts()) {
                            p.sendMessage("§7|----------| §6Outposts von " + town.getName() + " §7|----------|");
                            List<Plot> plots = Towny.getInstance().getManager().getPlotManager().getPlots(town);
                            town.getOutposts().keySet()
                                    .forEach(o -> {
                                        int residents = ((Long) plots.stream().filter(pl -> pl.isOutpostPlot() && pl.getOutpost().equalsIgnoreCase(o))
                                                .filter(Plot::isOwned)
                                                .map(pl -> pl.getOwner().toString())
                                                .distinct()
                                                .count()).intValue();
                                        Location spawn = town.getOutpostSpawn(o);
                                        p.sendMessage("§7» §6" + o + "§7: " + residents + " Einwohner || Spawn: "+(int) spawn.getX()+" | " + (int) spawn.getZ());
                                    });
                        } else {
                            p.sendMessage(prefix + "§7Deine Stadt hat keine Outposts.");
                        }
                    } else {
                        p.sendMessage(prefix + "§7Du bist in keiner Stadt.");
                    }
                } else {
                    s.sendMessage("Du musst ein Spieler sein.");
                }
            } else {
                Town town = Towny.getInstance().getManager().getTownManager().get(args[0].toLowerCase());
                if(town != null) {
                    s.sendMessage("§7|----------| §7"+town.getRank()+" §6" + town.getName() + " " + (town.isPublic() ? "§7(§aÖffentlich§7)" : "§7(§cGeschlossen§7)") + " §7|----------|");
                    s.sendMessage("§7» Schwarzes Brett: §6" + town.getBlackboard());
                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy");
                    Date d = new Date(town.getCreated());
                    s.sendMessage("§7» Gegründet: §6" + format.format(d));
                    s.sendMessage("§7» Stadtgröße: §6" + Towny.getInstance().getManager().getPlotManager().getPlots(town).size() + " / " + (Towny.getInstance().getManager().getPlayerManager().getResidents(town).size() * 16 + town.getBuyedTownSize()) + " §7[Stadtplatz: "+(int) town.getSpawn().getX()+", "+(int) town.getSpawn().getZ()+"]");
                    if(town.getOutposts().size() > 0) {
                        List<Plot> plots = Towny.getInstance().getManager().getPlotManager().getPlots(town);
                        Map<String, Integer> outpostResidents = new HashMap<>();
                        town.getOutposts().keySet()
                                .forEach(o ->
                                        outpostResidents.put(o,
                                                ((Long) plots.stream()
                                                        .filter(pl -> pl.isOutpostPlot() && pl.getOutpost().equalsIgnoreCase(o))
                                                        .filter(Plot::isOwned)
                                                        .map(pl -> pl.getOwner().toString())
                                                        .distinct()
                                                        .count()).intValue()));
                        String text = "§6" + outpostResidents.keySet().stream().map(k -> k + " §7["+outpostResidents.get(k)+"]").collect(Collectors.joining("§7, §6"));
                        s.sendMessage("§7» Outposts: §6" + text);
                    }
                    s.sendMessage("§7» Geld: §6" + formatValue(((Long) town.getMoney()).doubleValue() / 100) + " §7| Steuer: §6" + (((Long) town.getTaxes()).doubleValue() / 100) + "%");
                    List<User> residents = Towny.getInstance().getManager().getPlayerManager().getResidents(town);
                    s.sendMessage("§7» Bürgermeister: §6" + residents.stream().filter(u -> u.getTownRank().equals(TownRank.MAYOR)).findFirst().get().getName());
                    List<User> assistants = residents.stream().filter(u -> u.getTownRank().equals(TownRank.ASSISTANT)).collect(Collectors.toList());
                    if(assistants.size() > 0) {
                        s.sendMessage("§7» Stadthalter [" + assistants.size() + "]: §6" + assistants.stream().map(User::getName).collect(Collectors.joining("§7,§6 ")));
                    }
                    List<User> helper = residents.stream().filter(u -> u.getTownRank().equals(TownRank.HELPER)).collect(Collectors.toList());
                    if(helper.size() > 0) {
                        s.sendMessage("§7» Stadtbauer [" + helper.size() + "]: §6" + helper.stream().map(User::getName).collect(Collectors.joining("§7,§6 ")));
                    }
                    s.sendMessage("§7» Bürger [" + residents.size() + "]: §6" + residents.stream().map(User::getName).collect(Collectors.joining("§7,§6 ")));
                } else {
                    sendHelpMessage(s, label);
                }
            }
        } else if(args.length == 2) {
            if (args[0].equalsIgnoreCase("join")) {
                if (s instanceof Player) {
                    Player p = (Player) s;
                    User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                    if (!user.checkTownMember()) {
                        Town town = Towny.getInstance().getManager().getTownManager().get(args[1].toLowerCase());
                        if (town != null) {
                            if (town.isPublic()) {
                                user.setTown(town.getKey(), TownRank.CITIZEN);
                                p.sendMessage(prefix + "§7Du bist der Stadt §6" + town.getName() + " §7beigetreten");
                                List<User> residents = Towny.getInstance().getManager().getPlayerManager().getResidents(town);
                                residents.stream().filter(u -> Bukkit.getPlayer(u.getKey()) != null).forEach(u -> Bukkit.getPlayer(u.getKey()).sendMessage("§7§l| §6" + town.getName() + " §7» §6" + p.getName() + " §7ist der Stadt beigetreten."));
                                Towny.getInstance().getManager().getPlayerManager().save(user);
                            } else {
                                p.sendMessage(prefix + "§7Die Stadt ist nicht öffentlich.");
                            }
                        } else {
                            p.sendMessage(prefix + "§7Die Stadt §6" + args[1] + " §7existiert nicht.");
                        }
                    } else {
                        p.sendMessage(prefix + "§7Du bist bereits in einer Stadt.");
                    }
                } else {
                    s.sendMessage(prefix + "§7Du musst ein Spieler sein.");
                }
            } else if (args[0].equalsIgnoreCase("spawn")) {
                if (s instanceof Player) {
                    Player p = (Player) s;
                    Town t = Towny.getInstance().getManager().getTownManager().get(args[1].toLowerCase());
                    if (t != null) {
                        if (t.isPublic()) {
                            p.teleport(t.getSpawn(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                            p.sendMessage(prefix + "§7Du wurdest zum Stadtspawn von §6" + t.getName() + " §7teleportiert.");
                        } else {
                            p.sendMessage(prefix + "§7Die Stadt ist nicht öffentlich.");
                        }
                    } else {
                        p.sendMessage(prefix + "§7Die Stadt §6" + args[1] + " §7existiert nicht.");
                    }
                } else {
                    s.sendMessage(prefix + "§7Du musst ein Spieler sein.");
                }
            } else if(args[0].equalsIgnoreCase("outpost")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                    User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                    if (user.checkTownMember()) {
                        Town town = user.getTown();
                        if (town.checkOutpost(args[1])) {
                            p.teleport(town.getOutpostSpawn(args[1]), PlayerTeleportEvent.TeleportCause.PLUGIN);
                            p.sendMessage(prefix + "§7Du wurdest zum Outpostspawn von §6" + args[1] + " §7teleportiert");
                        } else {
                            p.sendMessage(prefix + "§7Der Outpost §6" + args[1] + " §7existiert nicht.");
                        }
                    } else {
                        p.sendMessage(prefix + "§7Du bist in keiner Stadt.");
                    }
                } else {
                    s.sendMessage("Du musst ein Spieler sein.");
                }
            } else if (args[0].equalsIgnoreCase("deposit") || args[0].equalsIgnoreCase("dep")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                    User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                    if(user.checkTownMember()) {
                        Town town = user.getTown();
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
                                p.sendMessage(prefix + "§7Ist nicht verfügbar.");
                                return true;
                            }
                        } catch(NumberFormatException ignored) {
                            p.sendMessage(prefix + "§2Argument 2 §7muss eine positive Zahl sein.");
                            return true;
                        }
                        if(value > 0) {
                            if((user.getBalance() - value) >= 0) {
                                if((user.getBalance() + value) < 0) {
                                    p.sendMessage(prefix + "§7Dein Kontostand kann nicht ins §cMinus §7gehen.");
                                    return true;
                                }
                                if((town.getMoney() + value) < 0) {
                                    p.sendMessage(prefix + "§7Dein Kontostand kann nicht in §cMinus §7gehen.");
                                    return true;
                                }
                                long max = 9200000000000000000L;
                                if(value >  max) {
                                    p.sendMessage(prefix + "§7Der Betrag ist zu hoch.");
                                    return true;
                                }
                                town.depositMoney(value);
                                user.withdrawMoney(value);
                                Towny.getInstance().getManager().getPlayerManager().save(user);
                                Towny.getInstance().getManager().getTownManager().save(town);
                                p.sendMessage(prefix + "§7Du hast §6" + formatValue(((Long) value).doubleValue() / 100) + " §7auf das Stadtkonto überwiesen.");
                                Towny.getInstance().getManager().getPlayerManager().getResidents(town).stream().filter(u -> Bukkit.getPlayer(u.getKey()) != null).forEach(u -> Bukkit.getPlayer(u.getKey()).sendMessage("§7§l| §6" + town.getName() + " §7» §6" + p.getName() + " §7hat §6" + formatValue(((Long) value).doubleValue() / 100) + " §7auf das Stadtkonto überwiesen."));
                            } else {
                                p.sendMessage(prefix + "§7Du hast nicht genügend Geld.");
                            }
                        } else {
                            p.sendMessage(prefix + "§2Argument 2 §7muss eine positive Zahl sein.");
                        }
                    } else {
                        p.sendMessage(prefix + "§7Du bist in keiner Stadt.");
                    }
                } else {
                    s.sendMessage(prefix + "§7Du musst ein Spieler sein.");
                }
            } else if(args[0].equalsIgnoreCase("with") || args[0].equalsIgnoreCase("withdraw")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                    User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                    if(user.checkTownMember()) {
                        Town town = user.getTown();
                        if(user.getTownRank().equals(TownRank.ASSISTANT) || user.getTownRank().equals(TownRank.MAYOR)) {
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
                                    p.sendMessage(prefix + "§7Ist nicht verfügbar.");
                                    return true;
                                }
                            } catch(NumberFormatException ignored) {
                                p.sendMessage(prefix + "§2Argument 2 §7muss eine positive Zahl sein.");
                                return true;
                            }
                            if(value > 0) {
                                if((town.getMoney() - value) >= 0) {
                                    if((user.getBalance() + value) < 0) {
                                        p.sendMessage(prefix + "§7Dein Kontostand kann nicht ins §cMinus §7gehen.");
                                        return true;
                                    }
                                    if((town.getMoney() + value) < 0) {
                                        p.sendMessage(prefix + "§7Dein Kontostand kann nicht in §cMinus §7gehen.");
                                        return true;
                                    }
                                    long max = 9200000000000000000L;
                                    if(value >  max) {
                                        p.sendMessage(prefix + "§7Der Betrag ist zu hoch.");
                                        return true;
                                    }
                                    town.withdrawMoney(value);
                                    user.depositMoney(value);
                                    Towny.getInstance().getManager().getPlayerManager().save(user);
                                    Towny.getInstance().getManager().getTownManager().save(town);
                                    p.sendMessage(prefix + "§7Du hast §6" + formatValue(((Long) value).doubleValue() / 100) + " §7vom Stadtkonto abgehoben.");
                                    Towny.getInstance().getManager().getPlayerManager().getResidents(town).stream().filter(u -> Bukkit.getPlayer(u.getKey()) != null).forEach(u -> Bukkit.getPlayer(u.getKey()).sendMessage("§7§l| §6" + town.getName() + " §7» §6" + p.getName() + " §7hat §6" + formatValue(((Long) value).doubleValue() / 100) + " §7vom Stadtkonto abgehoben."));
                                } else {
                                    p.sendMessage(prefix + "§7Du hast nicht genügend Geld.");
                                }
                            } else {
                                p.sendMessage(prefix + "§2Argument 2 §7muss eine positive Zahl sein.");
                            }
                        } else {
                            p.sendMessage(prefix + "§7Du hast keine Berechtigung dazu.");
                        }
                    } else {
                        p.sendMessage(prefix + "§7Du bist in keiner Stadt.");
                    }
                } else {
                    s.sendMessage(prefix + "§7Du musst ein Spieler sein.");
                }
            } else if(args[0].equalsIgnoreCase("invite")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                    if(Towny.getInstance().getTownInventations().containsKey(p.getUniqueId())) {
                        if(args[1].equalsIgnoreCase("accept")) {
                            Town t = Towny.getInstance().getManager().getTownManager().get(Towny.getInstance().getTownInventations().get(p.getUniqueId()));
                            Towny.getInstance().getTownInventations().remove(p.getUniqueId());
                            Towny.getInstance().getTownInventationsTime().remove(p.getUniqueId());
                            User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                            if(user.checkTownMember()) {
                                p.sendMessage(prefix + "§7Du bist bereits in einer Stadt.");
                                return true;
                            }
                            user.setTown(t.getKey(), TownRank.CITIZEN);
                            p.sendMessage(prefix + "§7Du hast die Einladung angenommen.");
                            Towny.getInstance().getManager().getPlayerManager().getResidents(t).stream()
                                    .filter(u -> Bukkit.getPlayer(u.getKey()) != null)
                                    .forEach(u -> Bukkit.getPlayer(u.getKey()).sendMessage("§7§l| §6" + t.getName() + " §7» §6" + p.getName() + " §7ist der Stadt beigetreten."));
                        } else if(args[1].equalsIgnoreCase("deny")) {
                            Towny.getInstance().getTownInventationsTime().remove(p.getUniqueId());
                            Towny.getInstance().getTownInventations().remove(p.getUniqueId());
                            p.sendMessage(prefix + "§7Du hast die Einladung abgelehnt.");
                        } else {
                            p.sendMessage(prefix + "§6/" + label + " invite [accept/deny]");
                        }
                    } else {
                        p.sendMessage(prefix + "§7Du hast keine Einladung erhalten.");
                    }
                } else {
                    s.sendMessage("Du musst ein Spieler sein.");
                }
            } else {
                sendHelpMessage(s, label);
            }
        } else {
            sendHelpMessage(s, label);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        if(s instanceof Player) {
            Player p = (Player) s;
            if(args.length == 1) {
                List<String> tabComplete = Lists.newArrayList("help", "join", "spawn", "leave", "list", "online", "deposit", "withdraw", "outposts", "outpost");
                Towny.getInstance().getManager().getTownManager().getTowns().stream().map(Town::getName).forEach(tabComplete::add);
                if(Towny.getInstance().getTownInventations().containsKey(p.getUniqueId())) {
                    tabComplete.add("invite");
                }
                return Common.getInstance().removeAutoComplete(tabComplete, args[0]);
            } else if(args.length == 2) {
                switch(args[0].toLowerCase()) {
                    case "join":
                    case "spawn":
                        return Common.getInstance().removeAutoComplete(Towny.getInstance().getManager().getTownManager().getTowns().stream().map(Town::getName).collect(Collectors.toList()), args[1]);
                    case "invite":
                        if(Towny.getInstance().getTownInventations().containsKey(p.getUniqueId())) {
                            return Common.getInstance().removeAutoComplete(Lists.newArrayList("accept", "deny"), args[1]);
                        }
                    case "outpost":
                        if(Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId()).checkTownMember())
                            return Common.getInstance().removeAutoComplete(new ArrayList<>(Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId()).getTown().getOutposts().keySet()), args[1]);
                }
            }
        }
        return Lists.newArrayList();
    }

    private void sendHelpMessage(CommandSender p, String label) {
        p.sendMessage(prefix + "§6/" + label + " help §7|| Zeigt diese Hilfe an\n" +
                        prefix + "§6/" + label + " <Name> §7|| Informationen über deine oder die angegebene Stadt\n" +
                        prefix + "§6/" + label + " outposts §7|| Listet die Outposts deiner Stadt\n" +
                        prefix + "§6/" + label + " join [Name] §7|| Tritt der Stadt bei (falls öffentlich)\n" +
                        prefix + "§6/" + label + " invite [accept/deny] §7|| Einladung einer Stadt ablehnen oder annehmen\n" +
                        prefix + "§6/" + label + " spawn <Name> §7|| Teleportiert zum Stadtspawn\n" +
                        prefix + "§6/" + label + " outpost [Name] §7|| Teleportiert zum Outpostspawn\n" +
                        prefix + "§6/" + label + " leave §7|| Verlässt die momentane Stadt\n" +
                        prefix + "§6/" + label + " list §7|| Zeigt alle Städte an\n" +
                        prefix + "§6/" + label + " online §7|| Listet die Spieler der Stadt auf, die momentan online sind\n" +
                        prefix + "§6/" + label + " deposit [Betrag] §7|| Zahlt den Betrag auf das Stadtkonto ein\n" +
                        prefix + "§6/" + label + " withdraw [Betrag] §7|| Hebt den Betrag vom Stadtkonto ab");
    }
}
