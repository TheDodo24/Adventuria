package de.thedodo24.commonPackage.commands;

import com.google.common.collect.Lists;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.economy.BankAccount;
import de.thedodo24.commonPackage.player.CustomScoreboardType;
import de.thedodo24.commonPackage.player.User;
import de.thedodo24.commonPackage.utils.ScoreboardManager;
import net.minecraft.server.v1_15_R1.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScoreboardCommand implements CommandExecutor, TabCompleter {

    public ScoreboardCommand() {
        PluginCommand cmd = Common.getInstance().getPlugin().getCommand("sboard");
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
    }

    private String prefix = "§7§l| §aAdventuria §7» ";

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(s instanceof Player) {
            Player p = (Player) s;
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("corp")) {
                    Map<String, Long> corp = Common.getInstance().getManager().getMySQL().getCorp(p.getName());
                    User user = Common.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                    user.setCustomScoreboard(CustomScoreboardType.CORP, corp.keySet().stream().findFirst().get());
                    p.sendMessage(prefix + "§7Das §aFirmenkonto§7 wird nun im Scoreboard angezeigt");
                    ScoreboardManager.getScoreboardMap().get(p.getUniqueId()).sendScoreboard(p, Bukkit.getOnlinePlayers().size());
                } else if(args[0].equalsIgnoreCase("off")) {
                    User user = Common.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                    if(user.isCustomScoreboard()) {
                        user.deleteCustomScoreboard();
                        p.sendMessage(prefix + "§7Dein §aCustom-Scoreboard §7wurde entfernt.");
                        ScoreboardManager.getScoreboardMap().get(p.getUniqueId()).sendScoreboard(p, Bukkit.getOnlinePlayers().size());
                    } else {
                        p.sendMessage(prefix + "§7Du hast kein §aCustom-Scoreboard§7.");
                    }
                } else if(args[0].equalsIgnoreCase("toggle")) {
                    if(ScoreboardManager.getScoreboardMap().containsKey(p.getUniqueId())) {
                        ScoreboardManager.getScoreboardMap().get(p.getUniqueId()).removeScoreboard(p);
                        p.sendMessage(prefix + "§7Du hast dein §aScoreboard §7ausgeblendet.");
                    } else {
                        new ScoreboardManager(p);
                        ScoreboardManager.getScoreboardMap().get(p.getUniqueId()).sendScoreboard(p, Bukkit.getOnlinePlayers().size());
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
                        ScoreboardManager.getScoreboardMap().get(p.getUniqueId()).sendScoreboard(p, Bukkit.getOnlinePlayers().size());
                    } else {
                        p.sendMessage(prefix + "§7Du bist nicht §6Teilhaber §7oder §4Inhaber §7des Kontos §a" + bankName + "§7.");
                    }
                } else if(args[0].equalsIgnoreCase("off")) {
                    if(args[1].equalsIgnoreCase("corp")) {
                        User user = Common.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                        if(user.isCustomScoreboard()) {
                            if(user.checkCustomScoreboard(CustomScoreboardType.CORP)) {
                                user.unSetCustomScoreboard(CustomScoreboardType.CORP);
                                ScoreboardManager.getScoreboardMap().get(p.getUniqueId()).sendScoreboard(p, Bukkit.getOnlinePlayers().size());
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
                                ScoreboardManager.getScoreboardMap().get(p.getUniqueId()).sendScoreboard(p, Bukkit.getOnlinePlayers().size());
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
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        if(s instanceof Player) {
            Player p = (Player) s;
            if(args.length == 1) {
                return Lists.newArrayList("corp", "bank", "off", "toggle");
            } else if(args.length == 2) {
                switch(args[0].toLowerCase()) {
                    case "bank":
                        return Common.getInstance().getManager().getBankManager().getBankAccounts(p.getUniqueId()).stream().map(BankAccount::getKey).collect(Collectors.toList());
                    case "off":
                        return Lists.newArrayList("corp", "bank");
                }
            }
        }
        return null;
    }
}
