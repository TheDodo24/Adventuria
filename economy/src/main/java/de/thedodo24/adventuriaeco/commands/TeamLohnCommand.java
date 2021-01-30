package de.thedodo24.adventuriaeco.commands;

import com.google.common.collect.Lists;
import de.thedodo24.adventuriaeco.Economy;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.economy.BankAccount;
import de.thedodo24.commonPackage.economy.BankLog;
import de.thedodo24.commonPackage.economy.BankLogType;
import de.thedodo24.commonPackage.player.User;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TeamLohnCommand implements CommandExecutor, TabCompleter {

    public TeamLohnCommand() {
        PluginCommand cmd = Economy.getInstance().getPlugin().getCommand("tlohn");
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
    }

    private String prefix = "§7§l| §cTeam §7» ";

    /*
    balance <Team>
    give team betrag
    take team betrag
    set team betrag
    pay spieler betrag
    sethead team spieler
     */

    private String noPerm(String perm) { return "§cYou do not have the permissions to execute this command. (" + perm + ")"; }
    private String formatValue(double v) { return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(v).split(" ")[0] + " A"; }

    private void sendHelpMessage(CommandSender s) {
        if(s instanceof Player) {
            Player p = (Player) s;
            BankAccount teamAccount = Economy.getInstance().getManager().getBankManager().getAccountForPlayer(p);
            if(teamAccount != null || p.hasPermission("tlohn.admin")) {
                p.sendMessage(prefix + "§c/tlohn balance " + (p.hasPermission("tlohn.admin") ? "<Team> " : "") +"§7| Zeigt den Kontostand deines, oder des Teams.");
                if(p.hasPermission("tlohn.admin")) {
                    p.sendMessage("\n");
                    p.sendMessage(prefix + "§c/tlohn list §7| Listet die Teams auf");
                    p.sendMessage(prefix + "§c/tlohn give [Team] [Betrag] §7| Gibt dem Teamkonto den Betrag.");
                    p.sendMessage(prefix + "§c/tlohn take [Team] [Betrag] §7| Nimmt dem Teamkonto den Betrag.");
                    p.sendMessage(prefix + "§c/tlohn set [Team] [Betrag] §7| Setzt das Teamkonto auf den Betrag.");
                    //p.sendMessage(prefix + "§c/tlohn sethead [Team] [Spieler] §7| Setzt den Teamleiter des Teams.");
                }
            } else {
                p.sendMessage(noPerm("no-team"));
            }
        } else {
            s.sendMessage("No help");
        }
    }

    @Override
    public boolean onCommand(CommandSender s, Command command, String label, String[] args) {
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("balance") || args[0].equalsIgnoreCase("bal")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                    BankAccount teamAccount = Economy.getInstance().getManager().getBankManager().getAccountForPlayer(p);
                    if(teamAccount != null) {
                        p.sendMessage(prefix + "§7Auf dem Konto des Teams §c" + teamAccount.getKey().split("-")[1] + " §7sind §c" + formatValue(((Long) teamAccount.getBalance()).doubleValue() / 100));
                    } else {
                        p.sendMessage(prefix + "§7Du bist in keinem Team.");
                    }
                } else {
                    s.sendMessage("Du musst ein Spieler sein.");
                }
            } else if(args[0].equalsIgnoreCase("list")) {
                if(s.hasPermission("tlohn.admin")) {
                    s.sendMessage(prefix + Common.getInstance().getTeamAccounts().stream().map(name -> name.split("-")[1]).collect(Collectors.joining(", ")));
                }
            } else {
                sendHelpMessage(s);
            }
        } else if(args.length == 2) {
            if(args[0].equalsIgnoreCase("balance") || args[0].equalsIgnoreCase("bal")) {
                if(s.hasPermission("tlohn.admin")) {
                    BankAccount teamAccount = Economy.getInstance().getManager().getBankManager().get("team-" + args[1].toLowerCase());
                    if(teamAccount != null) {
                        s.sendMessage(prefix + "§7Auf dem Konto des Teams §c" + teamAccount.getKey().split("-")[1] + " §7sind §c" + formatValue(((Long) teamAccount.getBalance()).doubleValue() / 100));
                    } else {
                        s.sendMessage(prefix + "§7Das Team §c" + args[1].toLowerCase() + " §7existiert nicht.");
                    }
                } else {
                    s.sendMessage(noPerm("tlohn.admin"));
                }
            } else {
                sendHelpMessage(s);
            }
        } else if(args.length == 3) {
                if(args[0].equalsIgnoreCase("give")) {
                    if(s.hasPermission("tlohn.admin")) {
                        BankAccount teamAccount = Economy.getInstance().getManager().getBankManager().get("team-" + args[1].toLowerCase());
                        if (teamAccount != null) {
                            Bukkit.dispatchCommand(s, "bank admin give " + teamAccount.getKey() + " " + args[2]);
                            BankLog log = Economy.getInstance().getManager().getLogHandler().get(teamAccount.getKey());
                            log.addHistory(System.currentTimeMillis(), BankLogType.ADMIN_GIVE, s.getName(), Long.parseLong(args[2]) * 100);
                        } else {
                            s.sendMessage(prefix + "§7Das Teamkonto §c" + args[1].toLowerCase() + " §7existiert nicht.");
                        }
                    } else {
                        s.sendMessage(noPerm("tlohn.admin"));
                    }
                } else if(args[0].equalsIgnoreCase("take")) {
                    if(s.hasPermission("tlohn.admin")) {
                        BankAccount teamAccount = Economy.getInstance().getManager().getBankManager().get("team-" + args[1].toLowerCase());
                        if (teamAccount != null) {
                            Bukkit.dispatchCommand(s, "bank admin take " + teamAccount.getKey() + " " + args[2]);
                            BankLog log = Economy.getInstance().getManager().getLogHandler().get(teamAccount.getKey());
                            log.addHistory(System.currentTimeMillis(), BankLogType.ADMIN_TAKE, s.getName(), Long.parseLong(args[2]) * 100);
                        } else {
                            s.sendMessage(prefix + "§7Das Teamkonto §c" + args[1].toLowerCase() + " §7existiert nicht.");
                        }
                    } else {
                        s.sendMessage(noPerm("tlohn.admin"));
                    }
                } else if(args[0].equalsIgnoreCase("set")) {
                    if(s.hasPermission("tlohn.admin")) {
                        BankAccount teamAccount = Economy.getInstance().getManager().getBankManager().get("team-" + args[1].toLowerCase());
                        if (teamAccount != null) {
                            Bukkit.dispatchCommand(s, "bank admin set " + teamAccount.getKey() + " " + args[2]);
                            BankLog log = Economy.getInstance().getManager().getLogHandler().get(teamAccount.getKey());
                            log.addHistory(System.currentTimeMillis(), BankLogType.ADMIN_SET, s.getName(), Long.parseLong(args[2]) * 100);
                        } else {
                            s.sendMessage(prefix + "§7Das Teamkonto §c" + args[1].toLowerCase() + " §7existiert nicht.");
                        }
                    } else {
                        s.sendMessage(noPerm("tlohn.admin"));
                    }
                } else if(args[0].equalsIgnoreCase("pay453423er2r2")) {
                    if(s instanceof Player) {
                        Player p = (Player) s;
                        BankAccount teamAccount = Economy.getInstance().getManager().getBankManager().getAccountForPlayer(p);
                        if(teamAccount != null) {
                            if (teamAccount.getMembers().contains(p.getUniqueId())) {
                                User user = Economy.getInstance().getManager().getPlayerManager().getByName(args[1]);
                                if (user != null) {
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
                                        p.sendMessage(prefix + "§2Argument 2 §7muss eine positive Zahl sein.");
                                        return false;
                                    }
                                    if (value > 0) {
                                        if ((teamAccount.getBalance() - value) >= 0) {
                                            if ((user.getBalance() + value) < 0) {
                                                p.sendMessage(prefix + "§7Der Kontostand darf nicht ins §cMinus §7geraten.");
                                                return false;
                                            }
                                            if ((teamAccount.getBalance()) < 0) {
                                                p.sendMessage(prefix + "§7Der Kontostand darf nicht ins §cMinus §7geraten.");
                                                return false;
                                            }
                                            user.depositMoney(value);
                                            teamAccount.withdrawMoney(value);
                                            Economy.getInstance().getManager().getBankManager().save(teamAccount);
                                            Economy.getInstance().getManager().getPlayerManager().save(user);
                                            p.sendMessage(prefix + "§7Du hast §c" + user.getName() + " §7einen Lohn von §c" + formatValue(((Long) value).doubleValue() / 100) + " §7überwießen.");
                                            Player to;
                                            if ((to = Bukkit.getPlayer(user.getKey())) != null) {
                                                to.sendMessage(prefix + "§7Dir wurde ein Lohn von §c" + formatValue(((Long) value).doubleValue() / 100) + " §7überwießen.");
                                            }
                                        }
                                    } else {
                                        p.sendMessage(prefix + "§cArgument 2 §7muss eine positive Zahl sein.");
                                    }
                                } else {
                                    p.sendMessage(prefix + "§7Der Spieler §c" + args[1] + " §7existiert nicht.");
                                }
                            } else {
                                p.sendMessage(prefix + "§7Du hast keine Berechtigung dazu.");
                            }
                        } else {
                            p.sendMessage(prefix + "§7Du bist in keinem §cTeam§7.");
                        }
                    } else {
                        s.sendMessage(prefix + "§7Du musst ein Spieler sein.");
                    }
                } else if(args[0].equalsIgnoreCase("sethead")) {
                    if(s.hasPermission("tlohn.admin")) {
                        BankAccount teamAccount = Economy.getInstance().getManager().getBankManager().get("team-" + args[1].toLowerCase());
                        if(teamAccount != null) {
                            User user = Economy.getInstance().getManager().getPlayerManager().getByName(args[2]);
                            if(user != null) {
                                if(teamAccount.getMembers().size() > 0) {
                                    if(teamAccount.getMembers().contains(user.getKey())) {
                                        s.sendMessage(prefix + "§7Der Spieler §c" + user.getName() + " §7ist bereits §cTeamleiter§7.");
                                        return false;
                                    }
                                    teamAccount.getMembers().clear();
                                }
                                teamAccount.getMembers().add(user.getKey());
                                Economy.getInstance().getManager().getBankManager().save(teamAccount);
                                s.sendMessage(prefix + "§c" + user.getName() + " §7wurde als §cTeam-Leiter §7des Teams §c" + args[1].toLowerCase() + " §7eingestellt.");
                                Player to;
                                if((to = Bukkit.getPlayer(user.getKey())) != null) {
                                    to.sendMessage(prefix + "§7Du wurdest als §cTeamleiter §7des Teams §c" + args[1].toLowerCase() + " §7eingestellt.");
                                }
                            } else {
                                s.sendMessage(prefix + "§7Der Spieler §c" + args[2] + " §7existiert nicht.");
                            }
                        } else {
                            s.sendMessage(prefix + "§7Das Teamkonto §c" + args[1] + " §7existiert nicht.");
                        }
                    } else {
                        s.sendMessage(noPerm("tlohn.admin"));
                    }
                } else {
                    sendHelpMessage(s);
                }
        } else {
            sendHelpMessage(s);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command command, String label, String[] args) {
        if(s instanceof Player) {
            Player p = (Player) s;
            BankAccount teamAccount = Economy.getInstance().getManager().getBankManager().getAccountForPlayer(p);
            if(args.length == 1) {
                List<String> returnAble = Lists.newArrayList();
                if(teamAccount != null) {
                    returnAble.add("balance");
                }
                if(p.hasPermission("tlohn.admin")) {
                    returnAble.add("balance");
                    returnAble.add("give");
                    returnAble.add("set");
                    returnAble.add("take");
                    returnAble.add("sethead");
                }
                return Common.getInstance().removeAutoComplete(returnAble, args[0]);
            } else if(args.length == 2) {
                switch(args[0].toLowerCase()) {
                    case "balance":
                    case "bal":
                    case "give":
                    case "set":
                    case "take":
                    case "sethead":
                        return Common.getInstance().removeAutoComplete(Common.getInstance().getTeamAccounts().stream().map(a -> a.split("-")[1]).collect(Collectors.toList()), args[1]);
                }
            } else if(args.length == 3) {
                switch (args[0].toLowerCase()) {
                    case "give":
                    case "set":
                    case "take":
                    case "sethead":
                        return Common.getInstance().removeAutoComplete(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[2]);
                }
            }
        }
        return Lists.newArrayList();
    }
}
