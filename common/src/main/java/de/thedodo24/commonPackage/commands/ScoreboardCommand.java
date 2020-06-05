package de.thedodo24.commonPackage.commands;

import com.google.common.collect.Lists;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.economy.BankAccount;
import de.thedodo24.commonPackage.player.CustomScoreboardType;
import de.thedodo24.commonPackage.player.User;
import de.thedodo24.commonPackage.utils.ManagerScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;
import java.util.stream.Collectors;

public class ScoreboardCommand implements CommandExecutor, TabCompleter {

    public ScoreboardCommand() {
        PluginCommand cmd = Common.getInstance().getPlugin().getCommand("sboard");
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
    }

    private String prefix = "§7§l| §aAdventuria §7» ";

    // /sboard set [line] [module]
    // /sboard off [line]
    // /sboard toggle
    // /sboard modules

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(s instanceof Player) {
            Player p = (Player) s;
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("toggle")) {
                    if(ManagerScoreboard.getScoreboardMap().containsKey(p.getUniqueId())) {
                        ManagerScoreboard.getScoreboardMap().get(p.getUniqueId()).removeScoreboard(p);
                        ManagerScoreboard.getScoreboardMap().remove(p.getUniqueId());
                        p.sendMessage(prefix + "§7Du hast dein §aScoreboard §7ausgeblendet.");
                    } else {
                        new ManagerScoreboard(p);
                        p.sendMessage(prefix + "§7Du hast dein §aScoreboard §7eingeblendet.");
                    }
                } else if(args[0].equalsIgnoreCase("modules")) {
                    p.sendMessage(prefix + "§aonline§7, §amoney§7, §aontime§7, §abank§7, §acorp");
                } else {
                    p.sendMessage(prefix + "§a/sboard set [Zeile] [Modulname] <[Kontoname/Ontime]>\n" +
                            prefix + "§a/sboard off [Zeile]\n" +
                            prefix + "§a/sboard toggle\n" +
                            prefix + "§a/sboard modules");
                }
            } else if(args.length == 2) {
                if(args[0].equalsIgnoreCase("off")) {
                    int line;
                    try {
                        line = Integer.parseInt(args[1]);
                    } catch(NumberFormatException e) {
                        p.sendMessage(prefix + "§aArgument 2 §7muss eine positive ganzzahlige Zahl sein.");
                        return false;
                    }
                    if(line >= 0) {
                        User user = Common.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                        if(user.checkCustomScoreboard(line)) {
                            List<Integer> sortedList = user.getCustomScoreboard().keySet().stream().map(Integer::parseInt).sorted().collect(Collectors.toList());
                            Collections.reverse(sortedList);
                            int highestLine = sortedList.get(0);
                            if(highestLine == 0 && line == 0) {
                                    p.sendMessage(prefix + "§7Du kannst diese Linie nicht löschen.");
                                    return false;
                            }
                            Set<String> strings = user.getCustomScoreboard().keySet();
                            Map<String, Map<String, String>> newMap = new HashMap<>();
                            strings.stream().filter(key -> Integer.parseInt(key) > line).forEach(key -> newMap.put(String.valueOf(Integer.parseInt(key) - 1),
                                    user.getCustomScoreboard(Integer.parseInt(key))));
                            strings.stream().filter(key -> Integer.parseInt(key) < line).forEach(key -> newMap.put(key, user.getCustomScoreboard(Integer.parseInt(key))));
                            user.setCustomScoreboard(newMap);
                            p.sendMessage(prefix + "§aZeile #" + line + " §7wurde entfernt.");
                            ManagerScoreboard.getScoreboardMap().get(user.getKey()).sendScoreboard(p);
                        } else {
                            p.sendMessage(prefix + "§7Du hast den §aPlatz #" + line + " §7nicht besetzt.");
                        }
                    } else {
                        p.sendMessage(prefix + "§aArgument 2 §7muss eine positive ganzzahlige Zahl sein.");
                    }
                } else {
                    p.sendMessage(prefix + "§a/sboard set [Zeile] [Modulname] <[Kontoname/Ontime]>\n" +
                            prefix + "§a/sboard off [Zeile]\n" +
                            prefix + "§a/sboard toggle\n" +
                            prefix + "§a/sboard modules");
                }
            } else if(args.length == 3) {
                if(args[0].equalsIgnoreCase("set")) {
                    int line;
                    try {
                        line = Integer.parseInt(args[1]);
                    } catch(NumberFormatException e) {
                        p.sendMessage(prefix + "§aArgument 2 §7muss eine positive ganzzahlige Zahl sein.");
                        return false;
                    }
                    if(line >= 0) {
                        String module = args[2];
                        CustomScoreboardType type;
                        try {
                            type = CustomScoreboardType.valueOf(module.toUpperCase());
                        } catch(Exception e) {
                            p.sendMessage(prefix + "§7Gebe bitte einen gültigen Modulnamen an.");
                            return false;
                        }
                        if(type.equals(CustomScoreboardType.MONEY) || type.equals(CustomScoreboardType.ONLINE) || type.equals(CustomScoreboardType.CORP)) {
                            User user = Common.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                            List<Integer> sortedList = user.getCustomScoreboard().keySet().stream().map(Integer::parseInt).sorted().collect(Collectors.toList());
                            Collections.reverse(sortedList);
                            int highestLine = sortedList.get(0);
                            if((highestLine + 1) >= line) {
                                if (line <= 3) {
                                    if(!user.checkCustomScoreboard(type)) {
                                        user.setCustomScoreboard(line, new HashMap<String, String>() {{
                                            put("type", type.toString());
                                            put("value", "");
                                        }});
                                        p.sendMessage(prefix + "§7Du hast das Modul §a" + type.toString() + " §7in §aLinie #" + line + " §7aktiviert.");
                                        ManagerScoreboard.getScoreboardMap().get(user.getKey()).sendScoreboard(p);
                                    } else {
                                        p.sendMessage(prefix + "§7Dieses Modul ist schon §aaktiviert§7.");
                                    }
                                } else {
                                    p.sendMessage(prefix + "§7Du kannst maximal §a4 Zeilen §7haben.");
                                }
                            } else {
                                p.sendMessage(prefix + "§7Deine ausgewählte Linie ist zu groß. Dein Maximum liegt bei §a" + (highestLine + 1) + "§7.");
                            }
                        } else {
                            p.sendMessage(prefix + "§7Du musst noch den §aKontonamen §7oder den §cOntime-Abstand §7angeben.");
                        }
                    } else {
                        p.sendMessage(prefix + "§aArgument 2 §7muss eine positive ganzzahlige Zahl sein.");
                    }
                } else {
                    p.sendMessage(prefix + "§a/sboard set [Zeile] [Modulname] <[Kontoname/Ontime]>\n" +
                            prefix + "§a/sboard off [Zeile]\n" +
                            prefix + "§a/sboard toggle\n" +
                            prefix + "§a/sboard modules");
                }
            } else if(args.length == 4) {
                if(args[0].equalsIgnoreCase("set")) {
                    int line;
                    try {
                        line = Integer.parseInt(args[1]);
                    } catch(NumberFormatException e) {
                        p.sendMessage(prefix + "§aArgument 2 §7muss eine positive ganzzahlige Zahl sein.");
                        return false;
                    }
                    if(line >= 0) {
                        String module = args[2];
                        CustomScoreboardType type;
                        try {
                            type = CustomScoreboardType.valueOf(module.toUpperCase());
                        } catch(Exception e) {
                            p.sendMessage(prefix + "§7Gebe bitte einen gültigen Modulnamen an.");
                            return false;
                        }
                        if(type.equals(CustomScoreboardType.BANK) || type.equals(CustomScoreboardType.ONTIME)) {
                            String value = args[3];
                            if(type.equals(CustomScoreboardType.BANK)) {
                                BankAccount account = Common.getInstance().getManager().getBankManager().get(value);
                                if(account == null) {
                                    p.sendMessage(prefix + "§7Das Bankkonto §a" + value + " §7existiert nicht.");
                                    return false;
                                } else {
                                    if(!(account.getMembers().contains(p.getUniqueId()) || account.getOwners().contains(p.getUniqueId()))) {
                                        p.sendMessage(prefix + "§7Du bist nicht §6Teil- §7oder §4Inhaber §7des Kontos.");
                                        return false;
                                    }
                                }
                            } else {
                                if(!(value.equalsIgnoreCase("day") || value.equalsIgnoreCase("week") || value.equalsIgnoreCase("total"))) {
                                    p.sendMessage(prefix + "§7Wähle bitte zwischen §aday§7, §aweek§7 oder §atotal§7.");
                                    return false;
                                }
                            }
                            User user = Common.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                            List<Integer> sortedList = user.getCustomScoreboard().keySet().stream().map(Integer::parseInt).sorted().collect(Collectors.toList());
                            Collections.reverse(sortedList);
                            int highestLine = sortedList.get(0);
                            if((highestLine + 1) >= line) {
                                if(line <= 3) {
                                    if(!user.checkCustomScoreboard(type)) {
                                        user.setCustomScoreboard(line, new HashMap<String, String>() {{
                                            put("type", type.toString());
                                            put("value", value);
                                        }});
                                        ManagerScoreboard.getScoreboardMap().get(user.getKey()).sendScoreboard(p);
                                        p.sendMessage(prefix + "§7Du hast das Modul §a" + type.toString() + " §7in §aLinie #" + line + " §7aktiviert.");
                                    } else {
                                        p.sendMessage(prefix + "§7Dieses Modul ist schon §aaktiviert§7.");
                                    }
                                } else {
                                    p.sendMessage(prefix + "§7Du kannst maximal §a4 Zeilen §7haben.");
                                }
                            } else {
                                p.sendMessage(prefix + "§7Deine ausgewählte Linie ist zu groß. Dein Maximum liegt bei §a" + (highestLine + 1) + "§7.");
                            }
                        } else {
                            p.sendMessage(prefix + "§7Du musst noch den §aKontonamen §7oder den §cOntime-Abstand §7angeben.");
                        }
                    } else {
                        p.sendMessage(prefix + "§aArgument 2 §7muss eine positive ganzzahlige Zahl sein.");
                    }
                } else {
                    p.sendMessage(prefix + "§a/sboard set [Zeile] [Modulname] <[Kontoname/Ontime]>\n" +
                            prefix + "§a/sboard off [Zeile]\n" +
                            prefix + "§a/sboard toggle\n" +
                            prefix + "§a/sboard modules");
                }
            } else {
                p.sendMessage(prefix + "§a/sboard set [Zeile] [Modulname] <[Kontoname/Ontime]>\n" +
                        prefix + "§a/sboard off [Zeile]\n" +
                        prefix + "§a/sboard toggle\n" +
                        prefix + "§a/sboard modules");
            }
        } else {
            s.sendMessage("Du musst ein Spieler sein.");
        }
        return false;
    }

    /*@Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(s instanceof Player) {
            Player p = (Player) s;
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("corp")) {
                    Map<String, Long> corp = Common.getInstance().getManager().getMySQL().getCorp(p.getName());
                    User user = Common.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                    user.setCustomScoreboard(CustomScoreboardType.CORP, corp.keySet().stream().findFirst().get());
                    p.sendMessage(prefix + "§7Das §aFirmenkonto§7 wird nun im Scoreboard angezeigt");
                    ManagerScoreboard.getScoreboardMap().get(p.getUniqueId()).sendScoreboard(p);
                } else if(args[0].equalsIgnoreCase("off")) {
                    User user = Common.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                    if(user.isCustomScoreboard()) {
                        user.deleteCustomScoreboard();
                        p.sendMessage(prefix + "§7Dein §aCustom-Scoreboard §7wurde entfernt.");
                        ManagerScoreboard.getScoreboardMap().get(p.getUniqueId()).sendScoreboard(p);
                    } else {
                        p.sendMessage(prefix + "§7Du hast kein §aCustom-Scoreboard§7.");
                    }
                } else if(args[0].equalsIgnoreCase("toggle")) {
                    if(ManagerScoreboard.getScoreboardMap().containsKey(p.getUniqueId())) {
                        ManagerScoreboard.getScoreboardMap().get(p.getUniqueId()).removeScoreboard(p);
                        p.sendMessage(prefix + "§7Du hast dein §aScoreboard §7ausgeblendet.");
                    } else {
                        new ManagerScoreboard(p);
                        p.sendMessage(prefix + "§7Du hast dein §aScoreboard §7eingeblendet.");
                    }
                } else {
                    p.sendMessage(prefix + "§a/sboard corp\n" +
                            prefix + "§a/sboard bank [Kontoname]\n" +
                            prefix + "§a/sboard off <corp/bank>\n" +
                            prefix + "§a/sboard toggle");
                }
            } else if(args.length == 2) {
                if(args[0].equalsIgnoreCase("bank")) {
                    String bankName = args[1].toLowerCase();
                    if(Common.getInstance().getManager().getBankManager().getBankAccounts(p.getUniqueId()).stream().map(BankAccount::getKey).anyMatch(name -> name.equalsIgnoreCase(bankName))) {
                        User user = Common.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                        user.setCustomScoreboard(CustomScoreboardType.BANK, bankName);
                        p.sendMessage(prefix + "§7Das Bankkonto §a" + bankName + " §7wird nun im Scoreboard angezeigt.");
                        ManagerScoreboard.getScoreboardMap().get(p.getUniqueId()).sendScoreboard(p);
                    } else {
                        p.sendMessage(prefix + "§7Du bist nicht §6Teilhaber §7oder §4Inhaber §7des Kontos §a" + bankName + "§7.");
                    }
                } else if(args[0].equalsIgnoreCase("off")) {
                    if(args[1].equalsIgnoreCase("corp")) {
                        User user = Common.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                        if(user.isCustomScoreboard()) {
                            if(user.checkCustomScoreboard(CustomScoreboardType.CORP)) {
                                user.unSetCustomScoreboard(CustomScoreboardType.CORP);
                                ManagerScoreboard.getScoreboardMap().get(p.getUniqueId()).sendScoreboard(p);
                                p.sendMessage(prefix + "§7Deine Firma wird nun nicht mehr im Scoreboard angezeigt.");
                            } else {
                                p.sendMessage(prefix + "§7Deine Firma wird nicht im §aScoreboard §7angezeigt.");
                            }
                        } else {
                            p.sendMessage(prefix + "§7Du hast kein §aCustom-Scoreboard§7.");
                        }
                    } else if(args[1].equalsIgnoreCase("bank")) {
                        User user = Common.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                        if(user.isCustomScoreboard()) {
                            if(user.checkCustomScoreboard(CustomScoreboardType.BANK)) {
                                user.unSetCustomScoreboard(CustomScoreboardType.BANK);
                                ManagerScoreboard.getScoreboardMap().get(p.getUniqueId()).sendScoreboard(p);
                                p.sendMessage(prefix + "§7Dein Bankkonto wird nun nicht mehr im Scoreboard angezeigt.");
                            } else {
                                p.sendMessage(prefix + "§7Deine Bankkonto wird nicht im §aScoreboard §7angezeigt.");
                            }
                        } else {
                            p.sendMessage(prefix + "§7Du hast kein §aCustom-Scoreboard§7.");
                        }
                    } else {
                        p.sendMessage(prefix + "§a/sboard corp\n" +
                                prefix + "§a/sboard bank [Kontoname]\n" +
                                prefix + "§a/sboard off <corp/bank>\n" +
                                prefix + "§a/sboard toggle");
                    }
                } else {
                    p.sendMessage(prefix + "§a/sboard corp\n" +
                            prefix + "§a/sboard bank [Kontoname]\n" +
                            prefix + "§a/sboard off <corp/bank>\n" +
                            prefix + "§a/sboard toggle");
                }
            } else {
                p.sendMessage(prefix + "§a/sboard corp\n" +
                        prefix + "§a/sboard bank [Kontoname]\n" +
                        prefix + "§a/sboard off <corp/bank>\n" +
                        prefix + "§a/sboard toggle");
            }
        } else {
            s.sendMessage("Du musst ein Spieler sein.");
        }
        return false;
    }*/

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        if(s instanceof Player) {
            Player p = (Player) s;
            if(args.length == 1) {
                List<String> returnList = Lists.newArrayList("set", "off", "toggle", "modules");
                return Common.getInstance().removeAutoComplete(returnList, args[0]);
            } else if(args.length == 2) {
                if(args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("off"))
                    return Common.getInstance().removeAutoComplete(Lists.newArrayList("0", "1", "2", "3"), args[1]);
            } else if(args.length == 3) {
                if(args[0].equalsIgnoreCase("set")) {
                    List<String> returnList = Lists.newArrayList("online", "money", "ontime", "bank", "corp");
                    return Common.getInstance().removeAutoComplete(returnList, args[2]);
                }
            } else if(args.length == 4) {
                if(args[0].equalsIgnoreCase("set")) {
                    if(args[2].equalsIgnoreCase("bank")) {
                        List<String> returnList = Common.getInstance().getManager().getBankManager().getBankAccounts(p.getUniqueId()).stream().map(BankAccount::getKey).collect(Collectors.toList());
                        return Common.getInstance().removeAutoComplete(returnList, args[3]);
                    } else if(args[2].equalsIgnoreCase("ontime")) {
                        List<String> returnList = Lists.newArrayList("day", "week", "total");
                        return Common.getInstance().removeAutoComplete(returnList, args[3]);
                    }
                }
            }
        }
        return Lists.newArrayList();
    }


}
