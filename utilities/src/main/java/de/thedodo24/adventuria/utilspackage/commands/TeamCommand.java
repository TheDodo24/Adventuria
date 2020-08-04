package de.thedodo24.adventuria.utilspackage.commands;

import com.google.common.collect.Lists;
import de.thedodo24.adventuria.utilspackage.Utils;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.economy.BankAccount;
import de.thedodo24.commonPackage.player.Teams;
import de.thedodo24.commonPackage.player.User;
import de.thedodo24.commonPackage.teams.TeamLog;
import de.thedodo24.commonPackage.utils.TimeFormat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class TeamCommand implements CommandExecutor, TabCompleter {

    private String prefix = "§7§l| §cTeam §7» ";

    public TeamCommand() {
        PluginCommand command = Utils.getInstance().getPlugin().getCommand("team");
        command.setTabCompleter(this);
        command.setExecutor(this);
    }

    private String noPerm(String perm) { return "§cYou do not have the permissions to execute this command. (" + perm + ")"; }
    private String formatValue(double v) { return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(v).split(" ")[0] + " A"; }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("list")) {
                if(s.hasPermission("team.mod")) {
                    s.sendMessage(prefix + "Teams: §c" + Arrays.stream(Teams.values()).map(Teams::toString).collect(Collectors.joining("§7, §c")));
                } else {
                    s.sendMessage(noPerm("team.mod"));
                }
            } else if(args[0].equalsIgnoreCase("duty")) {
                if(s.hasPermission("team.duty")) {
                    if(s instanceof Player) {
                        Player p = (Player) s;
                        if(Common.getInstance().getDutyPlayers().containsKey(p.getUniqueId())) {
                            long start = Common.getInstance().getDutyPlayers().get(p.getUniqueId());
                            Common.getInstance().getDutyPlayers().remove(p.getUniqueId());
                            TeamLog teamLog = Utils.getInstance().getManager().getLogManager().getOrGenerate(p.getUniqueId());
                            teamLog.addEntry(start, System.currentTimeMillis());
                            Teams t = Arrays.stream(Teams.values()).filter(team -> team.getPermName().equalsIgnoreCase(Common.getInstance().getPerms().getPrimaryGroup(p))).findFirst().get();
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " parent add " + t.getPermName());
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " parent remove " + t.getDutyName());
                            Bukkit.getOnlinePlayers().stream()
                                    .collect(Collectors.toCollection(() -> Lists.newArrayList(Bukkit.getConsoleSender())))
                                    .forEach(all -> all.sendMessage(prefix + t.getColor() + p.getName() + " §7ist nicht mehr als " + t.getColor() + t.getDisplayName() + " §7im Dienst."));
                        } else {
                            Common.getInstance().getDutyPlayers().put(p.getUniqueId(), System.currentTimeMillis());
                            Teams t = Arrays.stream(Teams.values()).filter(team -> team.getPermName().equalsIgnoreCase(Common.getInstance().getPerms().getPrimaryGroup(p))).findFirst().get();
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " parent remove " + t.getPermName());
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " parent add " + t.getDutyName());
                            Bukkit.getOnlinePlayers().stream()
                                    .collect(Collectors.toCollection(() -> Lists.newArrayList(Bukkit.getConsoleSender())))
                                    .forEach(all -> all.sendMessage(prefix + t.getColor() + p.getName() + " §7ist nun als " + t.getColor() + t.getDisplayName() + " §7im Dienst."));
                        }
                    } else {
                        s.sendMessage(prefix + "Du musst ein Spieler sein.");
                    }
                } else {
                    s.sendMessage(noPerm("team.duty"));
                }
            } else {
                sendHelpMessage(s);
            }
        } else if(args.length == 2) {
            if(s instanceof Player) {
                Player p = (Player) s;
                User user = Utils.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                if(user.isTeamHead()) {
                    if(user.getTeams().size() == 1) {
                        Teams t = user.getTeams().get(0);
                        if(args[0].equalsIgnoreCase("add")) {
                            Player to;
                            if((to = Bukkit.getPlayer(args[1])) != null) {
                                String primaryGroup = Common.getInstance().getPerms().getPrimaryGroup(to);
                                if(Arrays.stream(Teams.values()).map(Teams::getPermName).anyMatch(g -> g.equalsIgnoreCase(primaryGroup))) {
                                    int wPrimary = Common.getInstance().getChat().getGroupInfoInteger("", primaryGroup, "weight", 0);
                                    int wTeam = Common.getInstance().getChat().getGroupInfoInteger("", t.getPermName(), "weight", 0);
                                    if(wPrimary >= wTeam) {
                                        p.sendMessage(prefix + "§c" + to.getName() + " §7ist bereits in einem Team.");
                                        return true;
                                    } else
                                        Common.getInstance().getPerms().playerRemoveGroup(to, primaryGroup);
                                }
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + to.getName() + " parent set " + t.getPermName());
                                for(int i = 0; i <= 5; i++) {
                                    Bukkit.getOnlinePlayers().forEach(all -> all.sendMessage(" "));
                                }
                                Bukkit.getOnlinePlayers().forEach(all -> all.sendMessage(prefix + "§7Wir begrüßen " + t.getColor() + to.getName() + " §7im " + t.getColor() + t.getDisplayName() + " §7Team. Herzlichen Glückwunsch und viel Erfolg!"));
                            } else {
                                p.sendMessage(prefix + "§7Der Spieler §c" + args[1] + " §7ist nicht online.");
                            }
                        } else if(args[0].equalsIgnoreCase("kick")) {
                            User uTo = Utils.getInstance().getManager().getPlayerManager().getByName(args[1].toLowerCase());
                            if(uTo != null) {
                                OfflinePlayer op = Bukkit.getOfflinePlayer(uTo.getKey());
                                String primaryGroup = Common.getInstance().getPerms().getPrimaryGroup("Freebuild", op);
                                if(primaryGroup.equalsIgnoreCase(t.getPermName())) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + op.getName() + " parent set member");
                                    Bukkit.getOnlinePlayers().forEach(all -> all.sendMessage(prefix + "§7Leider mussten wir von §6" + uTo.getName() + " §7Abschied nehmen. Wir bedanken uns für deine Arbeit im "+t.getColor() + t.getDisplayName() +" §7Team!"));
                                } else {
                                    p.sendMessage(prefix + "§c" + uTo.getName() + " §7ist nicht in deinem Team.");
                                }
                            } else {
                                p.sendMessage(prefix + "§7Der Spieler §c" + args[1] + " §7existiert nicht.");
                            }
                        } else {
                            sendHelpMessage(s);
                        }
                    } else {
                        if(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("kick"))
                            p.sendMessage(prefix + "§7Bitte verwende §c/team " + args[0].toLowerCase() + " " + args[1] + " [Team]§7.");
                        else
                            sendHelpMessage(s);
                    }
                } else {
                    if(args[0].equalsIgnoreCase("log")) {
                        if(p.hasPermission("team.mod") || Utils.getInstance().getManager().getPlayerManager().get(p.getUniqueId()).isTeamHead()) {
                            User teamMember = Utils.getInstance().getManager().getPlayerManager().getByName(args[1].toLowerCase());
                            if(teamMember != null) {
                                TeamLog teamLog = Utils.getInstance().getManager().getLogManager().get(teamMember.getKey());
                                if(teamLog != null) {
                                    List<HashMap<String, Long>> entries = teamLog.getEntries();
                                    if(entries.size() > 0) {
                                        p.sendMessage("§7|-----| §cLogs §7|-----|");
                                        SimpleDateFormat formatDay = new SimpleDateFormat("dd.MM.yy");
                                        SimpleDateFormat formatHour = new SimpleDateFormat("HH:mm:ss");
                                        List<Long> keySet = entries.stream().map(a -> a.get("start")).collect(Collectors.toList());
                                        Collections.sort(keySet);
                                        Collections.reverse(keySet);
                                        keySet.stream().limit(5).forEachOrdered(start -> {
                                            long end = entries.stream().filter(e -> e.get("start").longValue() == start).findAny().get().get("end");
                                            long dur = end - start;
                                            p.sendMessage("§7» §a" + formatDay.format(new Date(start)) + " §7-§a " + formatHour.format(new Date(start)) + " §8|| §c" + formatDay.format(new Date(end)) + " §7-§c " + formatHour.format(new Date(end)) + " §8|| §7" + TimeFormat.getInMinutes(dur) + " Minuten");
                                        });
                                    } else {
                                        p.sendMessage(prefix + "§7Es sind keine Einträge im Log von §c" + teamMember.getName() + " §7verfügbar.");
                                    }
                                } else {
                                    p.sendMessage(prefix + "§7Es ist kein Log über §c" + teamMember.getName() + " §7verfügbar.");
                                }
                            } else {
                                p.sendMessage(prefix + "§7Der Spieler §c" + args[1] + " §7existiert nicht.");
                            }
                        } else {
                            p.sendMessage(prefix + noPerm("team.mod"));
                        }
                    } else {
                        sendHelpMessage(s);
                    }
                }
            } else {
                s.sendMessage("Du musst ein Spieler sein.");
            }
        } else if(args.length == 3) {
            if(args[0].equalsIgnoreCase("sethead")) {
                if(s.hasPermission("team.mod")) {
                    if(Arrays.stream(Teams.values()).map(Enum::toString).anyMatch(t -> t.equalsIgnoreCase(args[1]))) {
                        Teams t = Teams.valueOf(args[1].toUpperCase());
                        Player to;
                        if ((to = Bukkit.getPlayer(args[2])) != null) {
                            User uTo = Utils.getInstance().getManager().getPlayerManager().get(to.getUniqueId());
                            uTo.addTeam(t);
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + to.getName() + " parent add " + t.getLeiterGroup());
                            for(int i = 0; i <= 5; i++) {
                                Bukkit.getOnlinePlayers().forEach(all -> all.sendMessage(" "));
                            }
                            Bukkit.getOnlinePlayers().forEach(all -> all.sendMessage(prefix + "§7Wir dürfen " + t.getColor() + to.getName() + " §7als neuen Leiter des " + t.getColor() + t.getDisplayName() + " Teams §7begrüßen. Herzlichen Glückwunsch und viel Erfolg!"));
                        } else {
                            s.sendMessage(prefix + "§c" + args[2] + " §7ist nicht online.");
                        }
                    } else {
                        s.sendMessage(prefix + "§7Das Team §c" + args[1] + " §7existiert nicht.");
                    }
                } else {
                    s.sendMessage(noPerm("team.mod"));
                }
            } else if(args[0].equalsIgnoreCase("add")) {
                if(s.hasPermission("team.mod") || (s instanceof Player && Utils.getInstance().getManager().getPlayerManager().get(((Player) s).getUniqueId()).getTeams().size() > 1)) {
                    if(Arrays.stream(Teams.values()).map(Enum::toString).anyMatch(t -> t.equalsIgnoreCase(args[2]))) {
                        Teams t = Teams.valueOf(args[2].toUpperCase());
                        Player to;
                        if ((to = Bukkit.getPlayer(args[1])) != null) {
                            String primaryGroup = Common.getInstance().getPerms().getPrimaryGroup(to);
                            if(Arrays.stream(Teams.values()).map(Teams::getPermName).anyMatch(g -> g.equalsIgnoreCase(primaryGroup))) {
                                int wPrimary = Common.getInstance().getChat().getGroupInfoInteger("", primaryGroup, "weight", 0);
                                int wTeam = Common.getInstance().getChat().getGroupInfoInteger("", t.getPermName(), "weight", 0);
                                if(wPrimary >= wTeam) {
                                    s.sendMessage(prefix + "§c" + to.getName() + " §7ist bereits in einem Team.");
                                    return true;
                                } else
                                    Common.getInstance().getPerms().playerRemoveGroup(to, primaryGroup);
                            }
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + to.getName() + " parent set " + t.getPermName());
                            for(int i = 0; i <= 5; i++) {
                                Bukkit.getOnlinePlayers().forEach(all -> all.sendMessage(" "));
                            }
                            Bukkit.getOnlinePlayers().forEach(all -> all.sendMessage(prefix + "§7Wir begrüßen " + t.getColor() + to.getName() + " im " + t.getColor() + t.getDisplayName() + " §7Team. Herzlichen Glückwunsch und viel Erfolg!"));
                        } else {
                            s.sendMessage(prefix + "§7Der Spieler §c" + args[1] + " §7ist nicht online.");
                        }
                    } else {
                        s.sendMessage(prefix + "§7Das Team §c" + args[2] + " §7existiert nicht.");
                    }
                } else {
                    s.sendMessage(noPerm("team.mod"));
                }
            } else if(args[0].equalsIgnoreCase("kick")) {
                if(s.hasPermission("team.mod") || (s instanceof Player && Utils.getInstance().getManager().getPlayerManager().get(((Player) s).getUniqueId()).getTeams().size() > 1)) {
                    if(Arrays.stream(Teams.values()).map(Enum::toString).anyMatch(t -> t.equalsIgnoreCase(args[2]))) {
                        Teams t = Teams.valueOf(args[2].toUpperCase());
                        User uTo = Utils.getInstance().getManager().getPlayerManager().getByName(args[1].toLowerCase());
                        if(uTo != null) {
                            OfflinePlayer op = Bukkit.getOfflinePlayer(uTo.getKey());
                            String primaryGroup = Common.getInstance().getPerms().getPrimaryGroup("Freebuild", op);
                            if(primaryGroup.equalsIgnoreCase(t.getPermName()) || primaryGroup.equalsIgnoreCase(t.getLeiterGroup())) {
                                if(uTo.isTeamHead() && uTo.isTeam(t))
                                    uTo.removeTeam(t);
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + op.getName() + " parent set member");
                                Bukkit.getOnlinePlayers().forEach(all -> all.sendMessage(prefix + "§7Leider mussten wir von §6" + uTo.getName() + " §7Abschied nehmen. Wir bedanken uns für deine Arbeit im "+t.getColor() + t.getDisplayName() +" §7Team!"));
                            } else {
                                s.sendMessage(prefix + "§c" + uTo.getName() + " §7ist nicht in dem Team.");
                            }
                        } else {
                            s.sendMessage(prefix + "§7Der Spieler §c" + args[1] + " §7existiert nicht.");
                        }
                    } else {
                        s.sendMessage(prefix + "§7Das Team §c" + args[2] + " §7existiert nicht.");
                    }
                } else {
                    s.sendMessage(noPerm("team.mod"));
                }
            } else if(args[0].equalsIgnoreCase("pay")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                    User user = Utils.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                    if(user.isTeamHead()) {
                        User uTo = Utils.getInstance().getManager().getPlayerManager().getByName(args[1].toLowerCase());
                        if(uTo != null) {
                            OfflinePlayer op = Bukkit.getOfflinePlayer(uTo.getKey());
                            List<String> filteredTeams = Arrays.stream(Common.getInstance().getPerms().getPlayerGroups("Freebuild", op)).filter(teamTo -> user.getTeams().stream().map(Teams::getPermName).anyMatch(permName -> permName.equalsIgnoreCase(teamTo))).collect(Collectors.toList());
                            if(filteredTeams.size() > 0) {
                                String arg = args[2];
                                if (arg.contains(","))
                                    arg = arg.replace(",", ".");
                                long value;
                                try {
                                    if (arg.equalsIgnoreCase("all"))
                                        value = user.getBalance();
                                    else if (!arg.equalsIgnoreCase("Infinity"))
                                        value = (long) (Double.parseDouble(arg) * 100);
                                    else {
                                        p.sendMessage("§7§l| §aGeld §7» Du möchtest §2Infinity§7? Ok, du bekommst es:\n\n");
                                        p.sendMessage("§7§l| §aGeld §7» Der Kontostand des Spielers §a" + p.getName() + " §7wurde durch §aCONSOLE §7auf §a0A §7gesetzt.");
                                        return false;
                                    }
                                } catch (NumberFormatException ignored) {
                                    p.sendMessage(prefix + "§cArgument 2 §7muss eine positive Zahl sein.");
                                    return false;
                                }
                                if (value > 0) {
                                    if(filteredTeams.size() == 1) {
                                        String permName = filteredTeams.get(0);
                                        Teams t = Arrays.stream(Teams.values()).filter(team -> team.getPermName().equalsIgnoreCase(permName)).findFirst().get();
                                        BankAccount teamAccount = Utils.getInstance().getManager().getBankManager().get(t.getBankAccount());
                                        if ((teamAccount.getBalance() - value) >= 0) {
                                            if ((uTo.getBalance() + value) < 0) {
                                                p.sendMessage(prefix + "§7Der Kontostand darf nicht ins §cMinus §7geraten.");
                                                return false;
                                            }
                                            if ((teamAccount.getBalance()) < 0) {
                                                p.sendMessage(prefix + "§7Der Kontostand darf nicht ins §cMinus §7geraten.");
                                                return false;
                                            }
                                            uTo.depositMoney(value);
                                            teamAccount.withdrawMoney(value);
                                            Utils.getInstance().getManager().getBankManager().save(teamAccount);
                                            Utils.getInstance().getManager().getPlayerManager().save(user);
                                            p.sendMessage(prefix + "§7Du hast §c" + uTo.getName() + " §7einen Lohn von §c" + formatValue(((Long) value).doubleValue() / 100) + " §7überwießen.");
                                            Player to;
                                            if ((to = Bukkit.getPlayer(uTo.getKey())) != null) {
                                                to.sendMessage(prefix + "§7Dir wurde ein Lohn von §c" + formatValue(((Long) value).doubleValue() / 100) + " §7überwießen.");
                                            }
                                        } else {
                                            p.sendMessage(prefix + "§7Das §cTeamkonto §7darf nicht ins §cMinus §7geraten.");
                                        }
                                    } else {
                                        p.sendMessage(prefix + "§7Bitte gebe ein Team an. /team pay [Spieler] [Lohn] [Team]");
                                    }
                                } else {
                                    p.sendMessage(prefix + "§cArgument 2 §7muss eine positive Zahl sein.");
                                }
                            } else {
                                p.sendMessage(prefix + "§7Der Spieler §c" + uTo.getName() + " §7ist nicht in deinem Team.");
                            }
                        } else {
                            p.sendMessage(prefix + "§7Der Spieler §c" + args[1] + " §7existiert nicht.");
                        }
                    } else {
                        s.sendMessage(noPerm("no-team-head"));
                    }
                } else {
                    s.sendMessage("Du musst ein Spieler sein.");
                }
            } if(args[0].equalsIgnoreCase("log")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                    if (p.hasPermission("team.mod") || Utils.getInstance().getManager().getPlayerManager().get(p.getUniqueId()).isTeamHead()) {
                        User teamMember = Utils.getInstance().getManager().getPlayerManager().getByName(args[1].toLowerCase());
                        if (teamMember != null) {
                            TeamLog teamLog = Utils.getInstance().getManager().getLogManager().get(teamMember.getKey());
                            if (teamLog != null) {
                                List<HashMap<String, Long>> entries = teamLog.getEntries();
                                if (entries.size() > 0) {
                                    int site;
                                    try {
                                        site = Integer.parseInt(args[2]);
                                    } catch(NumberFormatException e) {
                                        p.sendMessage(prefix + "§2Argument 3 §7muss eine postivite Ganzzahl sein.");
                                        return false;
                                    }
                                    if(site > 0) {
                                        p.sendMessage("§7|-----| §cLogs §7|-----|");
                                        SimpleDateFormat formatDay = new SimpleDateFormat("dd.MM.yy");
                                        SimpleDateFormat formatHour = new SimpleDateFormat("HH:mm:ss");
                                        List<Long> keySet = entries.stream().map(a -> a.get("start")).collect(Collectors.toList());
                                        Collections.sort(keySet);
                                        Collections.reverse(keySet);
                                        if(keySet.stream().skip(5 * (site - 1)).limit(5).count() > 0) {
                                            keySet.stream().skip(5*(site - 1)).limit(5).forEachOrdered(start -> {
                                                long end = entries.stream().filter(e -> e.get("start").longValue() == start).findAny().get().get("end");
                                                long dur = end - start;
                                                p.sendMessage("§7» §a" + formatDay.format(new Date(start)) + " §7-§a " + formatHour.format(new Date(start)) + " §8|| §c" + formatDay.format(new Date(end)) + " §7-§c " + formatHour.format(new Date(end)) + " §8|| §7" + TimeFormat.getInMinutes(dur) + " Minuten");
                                            });
                                        } else {
                                            p.sendMessage("§7Keine Einträge.");
                                        }
                                    } else {
                                        p.sendMessage(prefix + "§7Die Seitenanzahl muss über 0 sein.");
                                    }
                                } else {
                                    p.sendMessage(prefix + "§7Es sind keine Einträge im Log von §c" + teamMember.getName() + " §7verfügbar.");
                                }
                            } else {
                                p.sendMessage(prefix + "§7Es ist kein Log über §c" + teamMember.getName() + " §7verfügbar.");
                            }
                        } else {
                            p.sendMessage(prefix + "§7Der Spieler §c" + args[1] + " §7existiert nicht.");
                        }
                    } else {
                        p.sendMessage(prefix + noPerm("team.mod"));
                    }
                } else {
                    s.sendMessage(prefix + "Du musst ein Spieler sein");
                }
            } else {
                sendHelpMessage(s);
            }
        } else if(args.length == 4) {
            if(args[0].equalsIgnoreCase("pay")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                    User user = Utils.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                    if(user.isTeamHead()) {
                        User uTo = Utils.getInstance().getManager().getPlayerManager().getByName(args[1].toLowerCase());
                        if(uTo != null) {
                            Teams t = Teams.valueOf(args[3].toUpperCase());
                            if(user.getTeams().stream().map(Teams::getPermName).anyMatch(pName -> t.getPermName().equalsIgnoreCase(pName))) {
                                OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
                                if(Arrays.stream(Common.getInstance().getPerms().getPlayerGroups("Freebuild", op)).anyMatch(groups -> groups.equalsIgnoreCase(t.getPermName()))) {
                                    String arg = args[2];
                                    if (arg.contains(","))
                                        arg = arg.replace(",", ".");
                                    long value;
                                    try {
                                        if (arg.equalsIgnoreCase("all"))
                                            value = user.getBalance();
                                        else if (!arg.equalsIgnoreCase("Infinity"))
                                            value = (long) (Double.parseDouble(arg) * 100);
                                        else {
                                            p.sendMessage("§7§l| §aGeld §7» Du möchtest §2Infinity§7? Ok, du bekommst es:\n\n");
                                            p.sendMessage("§7§l| §aGeld §7» Der Kontostand des Spielers §a" + p.getName() + " §7wurde durch §aCONSOLE §7auf §a0A §7gesetzt.");
                                            return false;
                                        }
                                    } catch (NumberFormatException ignored) {
                                        p.sendMessage(prefix + "§cArgument 2 §7muss eine positive Zahl sein.");
                                        return false;
                                    }
                                    if (value > 0) {
                                        if(Arrays.stream(Teams.values()).map(Enum::toString).anyMatch(a -> a.equalsIgnoreCase(args[3]))) {
                                            BankAccount teamAccount = Utils.getInstance().getManager().getBankManager().get(t.getBankAccount());
                                            if ((teamAccount.getBalance() - value) >= 0) {
                                                if ((uTo.getBalance() + value) < 0) {
                                                    p.sendMessage(prefix + "§7Der Kontostand darf nicht ins §cMinus §7geraten.");
                                                    return false;
                                                }
                                                if ((teamAccount.getBalance()) < 0) {
                                                    p.sendMessage(prefix + "§7Der Kontostand darf nicht ins §cMinus §7geraten.");
                                                    return false;
                                                }
                                                uTo.depositMoney(value);
                                                teamAccount.withdrawMoney(value);
                                                Utils.getInstance().getManager().getBankManager().save(teamAccount);
                                                Utils.getInstance().getManager().getPlayerManager().save(user);
                                                p.sendMessage(prefix + "§7Du hast §c" + uTo.getName() + " §7einen Lohn von §c" + formatValue(((Long) value).doubleValue() / 100) + " §7überwießen.");
                                                Player to;
                                                if ((to = Bukkit.getPlayer(uTo.getKey())) != null) {
                                                    to.sendMessage(prefix + "§7Dir wurde ein Lohn von §c" + formatValue(((Long) value).doubleValue() / 100) + " §7überwießen.");
                                                }
                                            }
                                        } else {
                                            p.sendMessage(prefix + "§7Das Team §c" + args[3] + " §7existiert nicht.");
                                        }
                                    } else {
                                        p.sendMessage(prefix + "§cArgument 2 §7muss eine positive Zahl sein.");
                                    }
                                } else {
                                    p.sendMessage(prefix + "§7Der Spieler §c" + uTo.getName() + " §7ist nicht in deinem Team.");
                                }
                            } else {
                                p.sendMessage(prefix + "§7Du bist nicht der Teamleiter des Teams.");
                            }
                        } else {
                            p.sendMessage(prefix + "§7Der Spieler §c" + args[1] + " §7existiert nicht.");
                        }
                    } else {
                        s.sendMessage(noPerm("no-team-head"));
                    }
                } else {
                    s.sendMessage("Du musst ein Spieler sein.");
                }
            } else {
                sendHelpMessage(s);
            }
        } else {
            sendHelpMessage(s);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        if(args.length == 1) {
            List<String> r = Lists.newArrayList("list");
            if(s.hasPermission("team.duty")) {
                r.add("duty");
            }
            if(s.hasPermission("team.mod"))
                r.addAll(Lists.newArrayList("sethead", "add", "kick"));
            if(s instanceof Player && Utils.getInstance().getManager().getPlayerManager().get(((Player) s).getUniqueId()).isTeamHead())
                r.addAll(Lists.newArrayList("add", "kick", "pay"));
            return Common.getInstance().removeAutoComplete(r, args[0]);
        } else if(args.length == 2) {
            switch(args[0].toLowerCase()) {
                case "sethead":
                    if(s.hasPermission("team.mod"))
                        return Common.getInstance().removeAutoComplete(Arrays.stream(Teams.values()).map(Enum::toString).collect(Collectors.toList()), args[1]);
                case "add":
                case "kick":
                    if(s.hasPermission("team.mod") || (s instanceof Player && Utils.getInstance().getManager().getPlayerManager().get(((Player) s).getUniqueId()).isTeamHead()))
                        return Common.getInstance().removeAutoComplete(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
                case "pay":
                    if(s instanceof Player && Utils.getInstance().getManager().getPlayerManager().get(((Player) s).getUniqueId()).isTeamHead())
                        return Common.getInstance().removeAutoComplete(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
            }
        } else if(args.length == 3) {
            switch(args[0].toLowerCase()) {
                case "sethead":
                    if(s.hasPermission("team.mod"))
                        return Common.getInstance().removeAutoComplete(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[2]);
                case "add":
                case "kick":
                    if(s.hasPermission("team.mod"))
                        return Common.getInstance().removeAutoComplete(Arrays.stream(Teams.values()).map(Enum::toString).collect(Collectors.toList()), args[2]);
            }
        } else if(args.length == 4) {
            if(args[0].equalsIgnoreCase("pay")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                    User u = Utils.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                    if(u.isTeamHead())
                        return Common.getInstance().removeAutoComplete(u.getTeams().stream().map(Enum::toString).collect(Collectors.toList()), args[3]);
                }
            }
        }
        return Lists.newArrayList();
    }

    private void sendHelpMessage(CommandSender s) {
        s.sendMessage(prefix + "§c/team §7| Zeigt diese Hilfe an\n" +
                        prefix + "§c/team list §7| Listet die Namen der Teams auf");
        if(s.hasPermission("team.duty")) {
            s.sendMessage(prefix + "§c/team duty §7| Setzt dich in den Teammodus");
        }
        if(s.hasPermission("team.mod")) {
            s.sendMessage(prefix + "§c/team sethead [Team] [Spieler] §7| Setzt den Teamleiter\n" +
                            prefix + "§c/team add [Spieler] [Team] §7| Fügt einen Spieler zu einem Team hinzu\n" +
                            prefix + "§c/team kick [Spieler] [Team] §7| Entfernt einen Spieler aus einem Team\n" +
                            prefix + "§c/team log [Spieler] <Seite> §7| Zeigt die Zeit, die der Spieler im Dienst war");
        }
        if(s instanceof Player) {
            if(Utils.getInstance().getManager().getPlayerManager().get(((Player) s).getUniqueId()).isTeamHead()) {
                s.sendMessage(prefix + "§c/team add [Spieler] §7| Fügt den Spieler zu deinem Team hinzu\n" +
                                prefix + "§c/team kick [Spieler] §7| Entfernt den Spieler aus deinem Team\n" +
                                prefix + "§c/team pay [Spieler] [Lohn] §7| Bezahlt dem Spieler seinen Lohn\n" +
                                prefix + "§c/team pay [Spieler] [Lohn] [Team] §7| Bezahlt dem Spieler aus dem Team seinen Lohn\n" +
                                prefix + "§c/team log [Spieler] <Seite> §7| Zeigt die Zeit, die der Spieler im Dienst war");
            }
        }
    }
}
