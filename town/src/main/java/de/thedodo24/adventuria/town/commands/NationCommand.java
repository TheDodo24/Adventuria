package de.thedodo24.adventuria.town.commands;

import com.google.common.collect.Lists;
import de.thedodo24.adventuria.town.Towny;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.player.User;
import de.thedodo24.commonPackage.towny.Nation;
import de.thedodo24.commonPackage.towny.Town;
import de.thedodo24.commonPackage.towny.TownRank;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NationCommand implements CommandExecutor, TabCompleter {

    public NationCommand() {
        PluginCommand longCmd = Towny.getInstance().getPlugin().getCommand("nation");
        PluginCommand shortCmd = Towny.getInstance().getPlugin().getCommand("n");
        longCmd.setExecutor(this);
        longCmd.setTabCompleter(this);
        shortCmd.setExecutor(this);
        shortCmd.setTabCompleter(this);
    }

    /*
        nation
        nation help
        nation [name]
        nation online
        nation join [nation]
        nation leave
        nation deposit
        nation withdraw
        nation list

     */

    private String prefix = "§7§l| §eNation §7» ";
    private String formatValue(double v) { return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(v).split(" ")[0] + " A"; }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(args.length == 0) {
            if(s instanceof Player) {
                Player p = (Player) s;
                User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                if(user.checkTownMember() && user.getTown().checkNation()) {
                    Nation nation = user.getTown().getNation();
                    p.sendMessage("§7|----------| §e" + nation.getName() + " " + (nation.isPublic() ? "§7(§aÖffentlich§7)" : "§7(§cGeschlossen§7)") + " §7|----------|");
                    p.sendMessage("§7» König: §e" + nation.getKing().getName());
                    p.sendMessage("§7» Hauptstadt: §e" + nation.getCapital().getName());
                    p.sendMessage("§7» Geld: §e" + formatValue(((Long) nation.getMoney()).doubleValue() / 100) + " §7|| Steuern: §e" + ((Long) nation.getTaxes()).doubleValue() / 100 + "%");
                    p.sendMessage("§7» Städte §8["+nation.getTowns().size()+"]§7: §e" + nation.getTowns().stream().map(Town::getName).collect(Collectors.joining("§7, §e")));
                } else {
                    p.sendMessage(prefix + "§7Du bist in keiner Nation.");
                }
            } else {
                s.sendMessage("Du musst ein Spieler sein.");
            }
        } else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("help")) {
                sendHelpMessage(s, label);
            } else if(args[0].equalsIgnoreCase("online")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                    User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                    if(user.checkTownMember()) {
                        Town t = user.getTown();
                        if(t.checkNation()) {
                            Nation n = t.getNation();
                            Stream<List<User>> nationUserStream = n.getTowns().stream()
                                                                            .map(town -> Towny.getInstance().getManager().getPlayerManager().getResidents(town));
                            int online = nationUserStream.map(nus -> nus.stream().filter(u -> Bukkit.getPlayer(u.getKey()) != null).collect(Collectors.toList()))
                                    .mapToInt(List::size)
                                    .sum();
                            int max = nationUserStream.mapToInt(List::size).sum();
                            p.sendMessage("§7|-----| §eSpieler online §8("+online+"/"+max+") §7|-----|");
                            for(Town towns : n.getTowns()) {
                                List<User> townUsers = Towny.getInstance().getManager().getPlayerManager().getResidents(towns);
                                p.sendMessage("§7» §e" + towns.getName() + "§7: " +
                                        townUsers.stream()
                                                .filter(u -> Bukkit.getPlayer(u.getKey()) != null)
                                                .map(User::getName)
                                                .collect(Collectors.joining(", ")));
                            }
                        } else {
                            p.sendMessage(prefix + "§7Deine Stadt ist in keiner Nation.");
                        }
                    } else {
                        p.sendMessage(prefix + "§7Du bist kein Mitglied einer Stadt.");
                    }
                } else {
                    s.sendMessage("Du musst ein Spieler sein.");
                }
            } else if(args[0].equalsIgnoreCase("leave")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                    User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                    if(user.checkTownMember() && user.getTownRank().equals(TownRank.MAYOR)) {
                        Town town = user.getTown();
                        if(town.checkNation()) {
                            Nation n = town.getNation();
                            n.removeTown(town);
                            n.getTowns().stream()
                                    .map(t -> Towny.getInstance().getManager().getPlayerManager().getResidents(town).stream().filter(u -> Bukkit.getPlayer(u.getKey()) != null))
                                    .forEach(ul -> ul.forEach(u -> Bukkit.getPlayer(u.getKey()).sendMessage("§7§l| §e" + n.getName() + " §7» §e" + town.getName() + " §7ist aus der Nation ausgetreten.")));
                        } else {
                            p.sendMessage(prefix + "§7Deine Stadt ist in keiner Nation.");
                        }
                    } else {
                        p.sendMessage(prefix + "§7Du bist kein Bürgermeister.");
                    }
                } else {
                    s.sendMessage("Du musst ein Spieler sein.");
                }
            } else if(args[0].equalsIgnoreCase("list")) {
                s.sendMessage("§7|-----| §eNationen §7|-----|");
                Executors.newSingleThreadExecutor().execute(() -> Towny.getInstance().getManager().getNationManager().getNations().forEach(nation -> {
                    int residents = nation.getTowns().stream()
                                .mapToInt(town -> Towny.getInstance().getManager().getPlayerManager().getResidents(town).size()).sum();
                    int towns = nation.getTowns().size();
                    s.sendMessage("§7» §e" + nation.getName() + " §7|| " + towns + " Städte || " + residents + " Einwohner");
                }));
            } else {
                Nation nation = Towny.getInstance().getManager().getNationManager().get(args[0].toLowerCase());
                if(nation != null) {
                    s.sendMessage("§7|----------| §e" + nation.getName() + " " + (nation.isPublic() ? "§7(§aÖffentlich§7)" : "§7(§cGeschlossen§7)") + " §7|----------|");
                    s.sendMessage("§7» König: §e" + nation.getKing().getName());
                    s.sendMessage("§7» Hauptstadt: §e" + nation.getCapital().getName());
                    s.sendMessage("§7» Geld: §e" + formatValue(((Long) nation.getMoney()).doubleValue() / 100) + " §7|| Steuern: §e" + ((Long) nation.getTaxes()).doubleValue() / 100 + "%");
                    s.sendMessage("§7» Städte §8["+nation.getTowns().size()+"]§7: §e" + nation.getTowns().stream().map(Town::getName).collect(Collectors.joining("§7, §e")));
                } else {
                    s.sendMessage(prefix + "§7Die Nation §e" + args[0].toLowerCase() + " §7existiert nicht.");
                }
            }
        } else if(args.length == 2) {
            if(args[0].equalsIgnoreCase("join")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                    User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                    if(user.checkTownMember() && user.getTownRank().equals(TownRank.MAYOR)) {
                        Town town = user.getTown();
                        if(!town.checkNation()) {
                            Nation nation = Towny.getInstance().getManager().getNationManager().get(args[1].toLowerCase());
                            if(nation != null) {
                                nation.addTown(town);
                                nation.getTowns().stream()
                                        .map(t -> Towny.getInstance().getManager().getPlayerManager().getResidents(town).stream().filter(u -> Bukkit.getPlayer(u.getKey()) != null))
                                        .forEach(ul -> ul.forEach(u -> Bukkit.getPlayer(u.getKey()).sendMessage("§7§l| §e" + nation.getName() + " §7» §e" + town.getName() + " §7ist der Nation beigetreten.")));
                            } else {
                                p.sendMessage(prefix + "§7Die Nation §e" + args[1].toLowerCase() + " §7existiert nicht.");
                            }
                        } else {
                            p.sendMessage(prefix + "§7Deine Stadt ist bereits Mitglied einer Nation.");
                        }
                    } else {
                        p.sendMessage(prefix + "§7Du bist kein Bürgermeister.");
                    }
                } else {
                    s.sendMessage("Du musst ein Spieler sein.");
                }
            } else if(args[0].equalsIgnoreCase("deposit") || args[0].equalsIgnoreCase("dep")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                    User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                    if(user.checkTownMember()) {
                        Town town = user.getTown();
                        if(town.checkNation()) {
                            Nation nation = town.getNation();
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
                                p.sendMessage(prefix + "§eArgument 2 §7muss eine positive Zahl sein.");
                                return true;
                            }
                            if(value > 0) {
                                if((user.getBalance() - value) >= 0) {
                                    if((user.getBalance() + value) < 0) {
                                        p.sendMessage(prefix + "§7Dein Kontostand kann nicht ins §cMinus §7gehen.");
                                        return true;
                                    }
                                    if((nation.getMoney() + value) < 0) {
                                        p.sendMessage(prefix + "§7Dein Kontostand kann nicht in §cMinus §7gehen.");
                                        return true;
                                    }
                                    long max = 9200000000000000000L;
                                    if(value >  max) {
                                        p.sendMessage(prefix + "§7Der Betrag ist zu hoch.");
                                        return true;
                                    }
                                    nation.depositMoney(value);
                                    user.withdrawMoney(value);
                                    Towny.getInstance().getManager().getPlayerManager().save(user);
                                    Towny.getInstance().getManager().getNationManager().save(nation);
                                    p.sendMessage(prefix + "§7Du hast §e" + formatValue(((Long) value).doubleValue() / 100) + " §7auf das Stadtkonto überwiesen.");
                                    nation.getTowns().stream()
                                            .map(t -> Towny.getInstance().getManager().getPlayerManager().getResidents(town).stream().filter(u -> Bukkit.getPlayer(u.getKey()) != null))
                                            .forEach(ul -> ul.forEach(u -> Bukkit.getPlayer(u.getKey()).sendMessage("§7§l| §e" + nation.getName() + " §7» §e" + p.getName() + " §7hat §e" + formatValue(((Long) value).doubleValue() / 100) + " §7auf das Nationskonto überwiesen.")));
                                } else {
                                    p.sendMessage(prefix + "§7Du hast nicht genügend Geld.");
                                }
                            } else {
                                p.sendMessage(prefix + "§2Argument 2 §7muss eine positive Zahl sein.");
                            }
                        } else {
                            p.sendMessage(prefix + "§7Deine Stadt ist in keiner Nation.");
                        }
                    } else {
                        p.sendMessage(prefix + "§7Du bist in keiner Stadt.");
                    }
                } else {
                    s.sendMessage("Du musst ein Spieler sein.");
                }
            } else if(args[0].equalsIgnoreCase("withdraw") || args[0].equalsIgnoreCase("with")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                    User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                    if(user.checkTownMember()) {
                        Town town = user.getTown();
                        if(town.checkNation()) {
                            Nation nation = town.getNation();
                            if(nation.getKing().getKey().equals(user.getKey())) {
                                String arg = args[1];
                                if(arg.contains(","))
                                    arg = arg.replace(",", ".");
                                long value;
                                try {
                                    if(arg.equalsIgnoreCase("all"))
                                        value = nation.getMoney();
                                    else
                                    if(!arg.equalsIgnoreCase("Infinity") && !arg.equalsIgnoreCase("-Infinity")) {
                                        value = (long) (Double.parseDouble(arg) * 100);
                                    } else {
                                        p.sendMessage(prefix + "§7Ist nicht verfügbar.");
                                        return true;
                                    }
                                } catch(NumberFormatException ignored) {
                                    p.sendMessage(prefix + "§eArgument 2 §7muss eine positive Zahl sein.");
                                    return true;
                                }
                                if(value > 0) {
                                    if((user.getBalance() - value) >= 0) {
                                        if((user.getBalance() + value) < 0) {
                                            p.sendMessage(prefix + "§7Dein Kontostand kann nicht ins §cMinus §7gehen.");
                                            return true;
                                        }
                                        if((nation.getMoney() + value) < 0) {
                                            p.sendMessage(prefix + "§7Dein Kontostand kann nicht in §cMinus §7gehen.");
                                            return true;
                                        }
                                        long max = 9200000000000000000L;
                                        if(value >  max) {
                                            p.sendMessage(prefix + "§7Der Betrag ist zu hoch.");
                                            return true;
                                        }
                                        nation.withdrawMoney(value);
                                        user.depositMoney(value);
                                        Towny.getInstance().getManager().getPlayerManager().save(user);
                                        Towny.getInstance().getManager().getNationManager().save(nation);
                                        p.sendMessage(prefix + "§7Du hast §e" + formatValue(((Long) value).doubleValue() / 100) + " §7auf das Stadtkonto überwiesen.");
                                        nation.getTowns().stream()
                                                .map(t -> Towny.getInstance().getManager().getPlayerManager().getResidents(town).stream().filter(u -> Bukkit.getPlayer(u.getKey()) != null))
                                                .forEach(ul -> ul.forEach(u -> Bukkit.getPlayer(u.getKey()).sendMessage("§7§l| §e" + nation.getName() + " §7» §e" + p.getName() + " §7hat §e" + formatValue(((Long) value).doubleValue() / 100) + " §7auf das Nationskonto überwiesen.")));
                                    } else {
                                        p.sendMessage(prefix + "§7Du hast nicht genügend Geld.");
                                    }
                                } else {
                                    p.sendMessage(prefix + "§2Argument 2 §7muss eine positive Zahl sein.");
                                }
                            } else {
                                p.sendMessage(prefix + "§7Du bist nicht der König der Nation.");
                            }
                        } else {
                            p.sendMessage(prefix + "§7Deine Stadt ist in keiner Nation.");
                        }
                    } else {
                        p.sendMessage(prefix + "§7Du bist in keiner Stadt.");
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
        if(args.length == 1) {
            List<String> l = Lists.newArrayList("help", "online", "join", "leave", "deposit", "withdraw", "list");
            l.addAll(Towny.getInstance().getManager().getNationManager().getNations().stream().map(Nation::getName).collect(Collectors.toList()));
            return Common.getInstance().removeAutoComplete(l, args[0]);
        } else if(args.length == 2) {
            switch(args[0].toLowerCase()) {
                case "join":
                    return Common.getInstance().removeAutoComplete(Towny.getInstance().getManager().getNationManager().getNations().stream().map(Nation::getName).collect(Collectors.toList()), args[1]);
                case "deposit":
                case "withdraw":
                    return Lists.newArrayList("50", "500", "1000", "5000", "10000", "all");
            }
        }
        return Lists.newArrayList();
    }

    private void sendHelpMessage(CommandSender s, String label) {
        s.sendMessage(prefix + "§e/" + label + " help §7|| Zeigt diese Hilfe an\n" +
                        prefix + "§e/" + label + " <Name> §7|| Informationen über deine oder die angegebene Nation\n" +
                        prefix + "§e/" + label + " online §7|| Zeigt alle Spieler an, die in deiner Nation online sind.\n" +
                        prefix + "§e/" + label + " join [Name] §7|| (Bürgermeister) Tritt einer Nation bei (falls öffentlich)\n" +
                        prefix + "§e/" + label + " leave §7|| (Bürgermeister) Verlässt die momentane Nation\n" +
                        prefix + "§e/" + label + " deposit [Betrag] §7|| Zahlt den Betrag auf das Nationskonto\n" +
                        prefix + "§e/" + label + " withdraw [Betrag] §7|| Hebt den Betrag vom Nationskonto ab\n" +
                        prefix + "§e/" + label + " list §7|| Listet alle Nationen auf");
    }


}
