package de.thedodo24.adventuriaeco.commands;

import com.google.common.collect.Lists;
import de.thedodo24.adventuriaeco.Economy;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.economy.BankAccount;
import de.thedodo24.commonPackage.economy.BankLog;
import de.thedodo24.commonPackage.economy.BankLogType;
import de.thedodo24.commonPackage.economy.BankType;
import de.thedodo24.commonPackage.player.CustomScoreboardType;
import de.thedodo24.commonPackage.player.User;
import de.thedodo24.commonPackage.utils.ClickableText;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class BankCommand implements CommandExecutor, TabCompleter {

    private String prefix = "§7§l| §2Bank §7» ";

    public BankCommand() {
        PluginCommand cmd = Economy.getInstance().getPlugin().getCommand("bank");
        cmd.setTabCompleter(this);
        cmd.setExecutor(this);
    }

    private void sendAdminHelpMessage(CommandSender s) {
        TextComponent deleteHelp = new ClickableText(prefix).build();
        TextComponent deleteHelpCommand = new ClickableText("/bank admin delete [Kontoname]")
                .setChatColor(ChatColor.DARK_GREEN)
                .setClickMessage("/bank admin delete ")
                .setClickEventAction(ClickEvent.Action.SUGGEST_COMMAND)
                .setHoverMessage("/bank admin delete ")
                .setHoverEventAction(HoverEvent.Action.SHOW_TEXT)
                .build();
        TextComponent deleteHelpInfo = new ClickableText(" | Löscht ein Konto eines Spielers").setChatColor(ChatColor.GRAY).build();
        deleteHelp.addExtra(deleteHelpCommand);
        deleteHelp.addExtra(deleteHelpInfo);

        TextComponent giveHelp = new ClickableText(prefix).build();
        TextComponent giveHelpCommand = new ClickableText("/bank admin give [Kontoname] [Betrag]")
                .setChatColor(ChatColor.DARK_GREEN)
                .setClickMessage("/bank admin give ")
                .setClickEventAction(ClickEvent.Action.SUGGEST_COMMAND)
                .setHoverMessage("/bank admin give ")
                .setHoverEventAction(HoverEvent.Action.SHOW_TEXT)
                .build();
        TextComponent giveHelpInfo = new ClickableText(" | Gibt einem Konto eines Spielers den Betrag an Geld").setChatColor(ChatColor.GRAY).build();
        giveHelp.addExtra(giveHelpCommand);
        giveHelp.addExtra(giveHelpInfo);

        TextComponent takeHelp = new ClickableText(prefix).build();
        TextComponent takeHelpCommand = new ClickableText("/bank admin take [Kontoname] [Betrag]")
                .setChatColor(ChatColor.DARK_GREEN)
                .setClickMessage("/bank admin take ")
                .setClickEventAction(ClickEvent.Action.SUGGEST_COMMAND)
                .setHoverMessage("/bank admin take ")
                .setHoverEventAction(HoverEvent.Action.SHOW_TEXT)
                .build();
        TextComponent takeHelpInfo = new ClickableText(" | Nimmt einem Konto eines Spielers den Betrag an Geld").setChatColor(ChatColor.GRAY).build();
        takeHelp.addExtra(takeHelpCommand);
        takeHelp.addExtra(takeHelpInfo);

        TextComponent setHelp = new ClickableText(prefix).build();
        TextComponent setHelpCommand = new ClickableText("/bank admin set [Kontoname] [Betrag]")
                .setChatColor(ChatColor.DARK_GREEN)
                .setClickMessage("/bank admin set ")
                .setClickEventAction(ClickEvent.Action.SUGGEST_COMMAND)
                .setHoverMessage("/bank admin set ")
                .setHoverEventAction(HoverEvent.Action.SHOW_TEXT)
                .build();
        TextComponent setHelpInfo = new ClickableText(" | Setzt einem Konto eines Spielers den Betrag an Geld").setChatColor(ChatColor.GRAY).build();
        setHelp.addExtra(setHelpCommand);
        setHelp.addExtra(setHelpInfo);

        TextComponent addOHelp = new ClickableText(prefix).build();
        TextComponent addOHelpCommand = new ClickableText("/bank admin addowner [Kontoname] [Spieler]")
                .setChatColor(ChatColor.DARK_GREEN)
                .setClickMessage("/bank admin addowner ")
                .setClickEventAction(ClickEvent.Action.SUGGEST_COMMAND)
                .setHoverMessage("/bank admin addowner ")
                .setHoverEventAction(HoverEvent.Action.SHOW_TEXT)
                .build();
        TextComponent addOHelpInfo = new ClickableText(" | Fügt einen Inhaber zum Konto hinzu").setChatColor(ChatColor.GRAY).build();
        addOHelp.addExtra(addOHelpCommand);
        addOHelp.addExtra(addOHelpInfo);

        TextComponent addMHelp = new ClickableText(prefix).build();
        TextComponent addMCommand = new ClickableText("/bank admin addmember [Kontoname] [Spieler]")
                .setChatColor(ChatColor.DARK_GREEN)
                .setClickMessage("/bank admin addmember ")
                .setClickEventAction(ClickEvent.Action.SUGGEST_COMMAND)
                .setHoverMessage("/bank admin addmember ")
                .setHoverEventAction(HoverEvent.Action.SHOW_TEXT)
                .build();
        TextComponent addMInfo = new ClickableText(" | Fügt einen Teilhaber zum Konto hinzu").setChatColor(ChatColor.GRAY).build();
        addMHelp.addExtra(addMCommand);
        addMHelp.addExtra(addMInfo);

        TextComponent remOHelp = new ClickableText(prefix).build();
        TextComponent remOCommand = new ClickableText("/bank admin removeowner [Kontoname] [Spieler]")
                .setChatColor(ChatColor.DARK_GREEN)
                .setClickMessage("/bank admin removeowner ")
                .setClickEventAction(ClickEvent.Action.SUGGEST_COMMAND)
                .setHoverMessage("/bank admin removeowner ")
                .setHoverEventAction(HoverEvent.Action.SHOW_TEXT)
                .build();
        TextComponent remOInfo = new ClickableText(" | Entfernt einen Inhaber von einem Konto").setChatColor(ChatColor.GRAY).build();
        remOHelp.addExtra(remOCommand);
        remOHelp.addExtra(remOInfo);

        TextComponent remMHelp = new ClickableText(prefix).build();
        TextComponent remMCommand = new ClickableText("/bank admin removemember [Kontoname] [Spieler]")
                .setChatColor(ChatColor.DARK_GREEN)
                .setClickMessage("/bank admin removemember ")
                .setClickEventAction(ClickEvent.Action.SUGGEST_COMMAND)
                .setHoverMessage("/bank admin removemember ")
                .setHoverEventAction(HoverEvent.Action.SHOW_TEXT)
                .build();
        TextComponent remMInfo = new ClickableText(" | Entfernt einen Teilhaber von einem Konto").setChatColor(ChatColor.GRAY).build();
        remMHelp.addExtra(remMCommand);
        remMHelp.addExtra(remMInfo);

        TextComponent addStandHelp = new ClickableText(prefix).build();
        TextComponent addStandCommand = new ClickableText("/bank admin addas")
                .setChatColor(ChatColor.DARK_GREEN)
                .setClickMessage("/bank admin addas")
                .setClickEventAction(ClickEvent.Action.RUN_COMMAND)
                .setHoverMessage("/bank admin addas")
                .setHoverEventAction(HoverEvent.Action.SHOW_TEXT)
                .build();
        TextComponent addStandInfo = new ClickableText(" | Fügt einen Armor Stand hinzu").setChatColor(ChatColor.GRAY).build();
        addStandHelp.addExtra(addStandCommand);
        addStandHelp.addExtra(addStandInfo);

        TextComponent delStandHelp = new ClickableText(prefix).build();
        TextComponent delStandCommand = new ClickableText("/bank admin delas")
                .setChatColor(ChatColor.DARK_GREEN)
                .setClickMessage("/bank admin delas")
                .setClickEventAction(ClickEvent.Action.RUN_COMMAND)
                .setHoverMessage("/bank admin delas")
                .setHoverEventAction(HoverEvent.Action.SHOW_TEXT)
                .build();
        TextComponent delStandInfo = new ClickableText(" | Löscht einen Armor Stand").setChatColor(ChatColor.GRAY).build();
        delStandHelp.addExtra(delStandCommand);
        delStandHelp.addExtra(delStandInfo);
        if(s instanceof Player) {
            Player p = (Player) s;
            p.spigot().sendMessage(deleteHelp);
            p.spigot().sendMessage(giveHelp);
            p.spigot().sendMessage(setHelp);
            p.spigot().sendMessage(takeHelp);
            p.spigot().sendMessage(addOHelp);
            p.spigot().sendMessage(addMHelp);
            p.spigot().sendMessage(remOHelp);
            p.spigot().sendMessage(remMHelp);
            p.spigot().sendMessage(addStandHelp);
            p.spigot().sendMessage(delStandHelp);
        } else {
            s.sendMessage("/bank admin delete [Kontoname]\n" +
                    "/bank admin give [Kontoname] [Betrag]\n" +
                    "/bank admin set [Kontoname] [Betrag]\n" +
                    "/bank admin take [Kontoname] [Betrag]\n" +
                    "/bank admin addowner [Konto] [Spieler]\n" +
                    "/bank admin removeowner [Konto] [Spieler]\n" +
                    "/bank admin addmember [Konto] [Spieler]\n" +
                    "/bank admin removemember [Konto] [Spieler]");
        }
    }

    private void sendHelpMessage(Player p) {
        TextComponent createHelp = new TextComponent(prefix);
        TextComponent createHelpCommand = new TextComponent("/bank create [Kontoname]");
        TextComponent createHelpInfo = new TextComponent(" | Erstellt ein Bankkonto");
        createHelpCommand.setColor(ChatColor.DARK_GREEN);
        createHelpCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bank create "));
        createHelpCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/bank create ").create()));
        createHelpInfo.setColor(ChatColor.GRAY);
        createHelp.addExtra(createHelpCommand);
        createHelp.addExtra(createHelpInfo);

        TextComponent balanceHelp = new TextComponent(prefix);
        TextComponent balanceHelpCommand = new TextComponent("/bank balance [Kontoname]");
        TextComponent balanceHelpInfo = new TextComponent(" | Zeigt den Kontostand");
        balanceHelpCommand.setColor(ChatColor.DARK_GREEN);
        balanceHelpCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bank balance "));
        balanceHelpCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/bank balance ").create()));
        balanceHelpInfo.setColor(ChatColor.GRAY);
        balanceHelp.addExtra(balanceHelpCommand);
        balanceHelp.addExtra(balanceHelpInfo);


        TextComponent listHelp = new TextComponent(prefix);
        TextComponent listHelpCommand = new TextComponent("/bank list [Spieler]");
        TextComponent listHelpInfo = new TextComponent(" | Zeigt deine Konten bzw. die des angegebenen Spielers");
        listHelpCommand.setColor(ChatColor.DARK_GREEN);
        listHelpCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bank list"));
        listHelpCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/bank list").create()));
        listHelpInfo.setColor(ChatColor.GRAY);
        listHelp.addExtra(listHelpCommand);
        listHelp.addExtra(listHelpInfo);

        TextComponent deleteHelp = new TextComponent(prefix);
        TextComponent deleteHelpCommand = new TextComponent("/bank delete [Kontoname]");
        TextComponent deleteHelpInfo = new TextComponent(" | Löscht ein Konto");
        deleteHelpCommand.setColor(ChatColor.DARK_GREEN);
        deleteHelpCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bank delete "));
        deleteHelpCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/bank delete ").create()));
        deleteHelpInfo.setColor(ChatColor.GRAY);
        deleteHelp.addExtra(deleteHelpCommand);
        deleteHelp.addExtra(deleteHelpInfo);

        TextComponent addMemberHelp = new TextComponent(prefix);
        TextComponent addMemberCommand = new TextComponent("/bank addmember [Kontoname] [Spielername]");
        TextComponent addMemberInfo = new TextComponent(" | Fügt einen Benutzer zu dem Konto hinzu");
        addMemberCommand.setColor(ChatColor.DARK_GREEN);
        addMemberCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bank addmember "));
        addMemberCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/bank addmember ").create()));
        addMemberInfo.setColor(ChatColor.GRAY);
        addMemberHelp.addExtra(addMemberCommand);
        addMemberHelp.addExtra(addMemberInfo);

        TextComponent addOwnerHelp = new TextComponent(prefix);
        TextComponent addOwnerCommand = new TextComponent("/bank addowner [Kontoname] [Spielername]");
        TextComponent addOwnerInfo = new TextComponent(" | Fügt einen Inhaber zu dem Konto hinzu");
        addOwnerCommand.setColor(ChatColor.DARK_GREEN);
        addOwnerCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bank addowner "));
        addOwnerCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/bank addowner ").create()));
        addOwnerInfo.setColor(ChatColor.GRAY);
        addOwnerHelp.addExtra(addOwnerCommand);
        addOwnerHelp.addExtra(addOwnerInfo);

        TextComponent removeMemberHelp = new TextComponent(prefix);
        TextComponent removeMemberCommand = new TextComponent("/bank removemember [Kontoname] [Spielername]");
        TextComponent removeMemberInfo = new TextComponent(" | Löscht einen Teilhaber vom Konto");
        removeMemberCommand.setColor(ChatColor.DARK_GREEN);
        removeMemberCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bank removemember "));
        removeMemberCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/bank removemember ").create()));
        removeMemberInfo.setColor(ChatColor.GRAY);
        removeMemberHelp.addExtra(removeMemberCommand);
        removeMemberHelp.addExtra(removeMemberInfo);

        TextComponent removeOwnerHelp = new TextComponent(prefix);
        TextComponent removeOwnerCommand = new TextComponent("/bank removeowner [Kontoname] [Spielername]");
        TextComponent removeOwnerInfo = new TextComponent(" | Löscht einen Inhaber vom Konto");
        removeOwnerCommand.setColor(ChatColor.DARK_GREEN);
        removeOwnerCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bank removeowner "));
        removeOwnerCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/bank removeowner ").create()));
        removeOwnerInfo.setColor(ChatColor.GRAY);
        removeOwnerHelp.addExtra(removeOwnerCommand);
        removeOwnerHelp.addExtra(removeOwnerInfo);

        TextComponent depositHelp = new ClickableText(prefix).build();
        TextComponent depositHelpCommand = new ClickableText("/bank deposit [Kontoname] [Betrag]")
                .setChatColor(ChatColor.DARK_GREEN)
                .setClickMessage("/bank deposit ")
                .setClickEventAction(ClickEvent.Action.SUGGEST_COMMAND)
                .setHoverMessage("/bank deposit ")
                .setHoverEventAction(HoverEvent.Action.SHOW_TEXT)
                .build();
        TextComponent depositHelpInfo = new ClickableText(" | Zahlt Geld auf das Konto ein").setChatColor(ChatColor.GRAY).build();
        depositHelp.addExtra(depositHelpCommand);
        depositHelp.addExtra(depositHelpInfo);

        TextComponent withdrawHelp = new ClickableText(prefix).build();
        TextComponent withdrawHelpCommand = new ClickableText("/bank withdraw [Kontoname] [Betrag]")
                .setChatColor(ChatColor.DARK_GREEN)
                .setClickMessage("/bank withdraw ")
                .setClickEventAction(ClickEvent.Action.SUGGEST_COMMAND)
                .setHoverMessage("/bank withdraw ")
                .setHoverEventAction(HoverEvent.Action.SHOW_TEXT)
                .build();
        TextComponent withdrawHelpInfo = new ClickableText(" | Hebt Geld vom Konto ab").setChatColor(ChatColor.GRAY).build();
        withdrawHelp.addExtra(withdrawHelpCommand);
        withdrawHelp.addExtra(withdrawHelpInfo);

        TextComponent transferHelp = new ClickableText(prefix).build();
        TextComponent transferHelpCommand = new ClickableText("/bank transfer [Kontoname] [Zielkonto] [Betrag]")
                .setChatColor(ChatColor.DARK_GREEN)
                .setClickMessage("/bank transfer ")
                .setClickEventAction(ClickEvent.Action.SUGGEST_COMMAND)
                .setHoverMessage("/bank transfer ")
                .setHoverEventAction(HoverEvent.Action.SHOW_TEXT)
                .build();
        TextComponent transferHelpInfo = new ClickableText(" | Überweißt einen Betrag auf ein anderes Konto").setChatColor(ChatColor.GRAY).build();
        transferHelp.addExtra(transferHelpCommand);
        transferHelp.addExtra(transferHelpInfo);

        TextComponent logHelp = new ClickableText(prefix).build();
        TextComponent logHelpCommand = new ClickableText("/bank log [Kontoname] <Seite>")
                .setChatColor(ChatColor.DARK_GREEN)
                .setClickMessage("/bank log ")
                .setClickEventAction(ClickEvent.Action.SUGGEST_COMMAND)
                .setHoverMessage("/bank log ")
                .setHoverEventAction(HoverEvent.Action.SHOW_TEXT)
                .build();
        TextComponent logHelpInfo = new ClickableText(" | Zeigt die Transaktionen des Kontos an").setChatColor(ChatColor.GRAY).build();
        logHelp.addExtra(logHelpCommand);
        logHelp.addExtra(logHelpInfo);

        p.spigot().sendMessage(createHelp);
        p.spigot().sendMessage(balanceHelp);
        p.spigot().sendMessage(listHelp);
        p.spigot().sendMessage(deleteHelp);
        p.spigot().sendMessage(depositHelp);
        p.spigot().sendMessage(withdrawHelp);
        p.spigot().sendMessage(addMemberHelp);
        p.spigot().sendMessage(addOwnerHelp);
        p.spigot().sendMessage(removeMemberHelp);
        p.spigot().sendMessage(removeOwnerHelp);
        p.spigot().sendMessage(transferHelp);
        p.spigot().sendMessage(logHelp);
    }

    private void sendHelpMsg(CommandSender s) {
        if(s instanceof Player) {
            sendHelpMessage((Player) s);
        }
        if(s.hasPermission("bank.admin")) {
            if(s instanceof Player) {
                s.sendMessage("\n §7|----| §4Admin-Hilfe §7|----|");
            }
            sendAdminHelpMessage(s);
        }
    }
    private String noPerm(String perm) { return "§cYou do not have the permissions to execute this command. (" + perm + ")"; }

    private String formatValue(double v) { return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(v).split(" ")[0] + " A"; }



    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(args.length > 0) {
            if(args[0].equalsIgnoreCase("admin")) {
                if(args.length == 2) {
                    if(args[1].equalsIgnoreCase("addas")) {
                        if(s instanceof Player) {
                            Player p = (Player) s;
                            if(p.hasPermission("bank.addas")) {
                                if(!Economy.getInstance().getArmorStandAddList().contains(p.getUniqueId()) && !Economy.getInstance().getArmorStandDelList().contains(p.getUniqueId())) {
                                    Economy.getInstance().getArmorStandAddList().add(p.getUniqueId());
                                    p.sendMessage(prefix + "§7Um einen §2Armorstand §7hinzuzufügen, recktsklicke einen.");
                                } else {
                                    p.sendMessage(prefix + "§7Bitte rechtsklicke einen §2Armorstand§7, oder gebe §2/bank admin cancel §7ein.");
                                }
                            } else {
                                p.sendMessage(noPerm("bank.addas"));
                            }
                        } else {
                            s.sendMessage(prefix + "§7Du musst ein Spieler sein.");
                        }
                    } else if(args[1].equalsIgnoreCase("delas")) {
                        if(s instanceof Player) {
                            Player p = (Player) s;
                            if(p.hasPermission("bank.delas")) {
                                if(!Economy.getInstance().getArmorStandAddList().contains(p.getUniqueId()) && !Economy.getInstance().getArmorStandDelList().contains(p.getUniqueId())) {
                                    Economy.getInstance().getArmorStandDelList().add(p.getUniqueId());
                                    p.sendMessage(prefix + "§7Um einen §2Armorstand §7zu löschen, recktsklicke einen.");
                                } else {
                                    p.sendMessage(prefix + "§7Bitte rechtsklicke einen §2Armorstand§7, oder gebe §2/bank admin cancel §7sein.");
                                }
                            } else {
                                p.sendMessage(noPerm("bank.delas"));
                            }
                        } else {
                            s.sendMessage(prefix + "§7Du musst ein Spieler sein.");
                        }
                    } else if(args[1].equalsIgnoreCase("cancel")) {
                        if(s instanceof Player) {
                            Player p = (Player) s;
                            if(p.hasPermission("bank.delas") || p.hasPermission("bank.addas")) {
                                if(Economy.getInstance().getArmorStandAddList().contains(p.getUniqueId()) || Economy.getInstance().getArmorStandDelList().contains(p.getUniqueId())) {
                                    if(Economy.getInstance().getArmorStandAddList().contains(p.getUniqueId()))
                                        Economy.getInstance().getArmorStandAddList().remove(p.getUniqueId());
                                    else
                                        Economy.getInstance().getArmorStandDelList().remove(p.getUniqueId());
                                    p.sendMessage(prefix + "§7Dein Vorgang wurde §cabgebrochen§7.");
                                } else {
                                    p.sendMessage(prefix + "§7Du hast keine laufende Aktion.");
                                }
                            } else {
                                if(p.hasPermission("bank.delas"))
                                    p.sendMessage(noPerm("bank.addas"));
                                else
                                    p.sendMessage(noPerm("bank.delas"));
                            }
                        } else {
                            s.sendMessage(prefix + "§7Du musst ein Spieler sein.");
                        }
                    } else {
                        sendHelpMsg(s);
                    }
                } else if(args.length == 3) {
                    if(args[1].equalsIgnoreCase("delete")) {
                        if(s.hasPermission("bank.delete")) {
                            String name = args[2].toLowerCase();
                            BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(name);
                            if(bankAccount != null) {
                                if(!name.equalsIgnoreCase("staatskasse")) {
                                    /*Economy.getInstance().getManager().getPlayerManager().getUsers().stream().filter(User::isCustomScoreboard).filter(user -> user.checkCustomScoreboard(CustomScoreboardType.BANK))
                                            .filter(user -> user.getCustomScoreboard(CustomScoreboardType.BANK).equalsIgnoreCase(name)).forEach(user -> {
                                        user.unSetCustomScoreboard(CustomScoreboardType.BANK);
                                        Player to;
                                        if((to = Bukkit.getPlayer(user.getKey())) != null) {
                                            to.sendMessage(prefix + "§7Dein §aCustom-Scoreboard §7für ein Bankkonto wurde gelöscht, da das Bankkonto gelöscht wurde.");
                                        }
                                        Economy.getInstance().getManager().getPlayerManager().save(user);
                                    });*/
                                    Economy.getInstance().getManager().getBankManager().delete(name);
                                    s.sendMessage(prefix + "§7Das Bankkonto §2" + name + " §7wurde gelöscht.");
                                } else {
                                    s.sendMessage(prefix + "§7Du kannst die §2Staatskasse §7nicht löschen.");
                                }
                            } else {
                                s.sendMessage(prefix + "§7Das Bankkonto §2" + name + " §7existiert nicht.");
                            }
                        } else {
                            s.sendMessage(noPerm("bank.delete"));
                        }
                    } else {
                        sendHelpMsg(s);
                    }
                } else if(args.length == 4) {
                    switch(args[1].toLowerCase()) {
                        case "give":
                            if(s.hasPermission("bank.give")) {
                                String name = args[2].toLowerCase();
                                BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(name);
                                if(bankAccount != null) {
                                    String arg = args[3];
                                    if(arg.contains(","))
                                        arg = arg.replace(",", ".");
                                    long value;
                                    try {
                                        value = (long) (Double.parseDouble(arg) * 100);
                                    } catch(NumberFormatException e) {
                                        s.sendMessage(prefix + "§2Argument 4 §7muss eine positive Zahl sein.");
                                        return false;
                                    }
                                    if(value > 0) {
                                        bankAccount.depositMoney(value);
                                        BankAccount staatskasse = Common.getInstance().getManager().getBankManager().get("staatskasse");
                                        staatskasse.withdrawMoney(value);
                                        Common.getInstance().getManager().getBankManager().save(staatskasse);
                                        Common.getInstance().getManager().getBankManager().save(bankAccount);
                                        BankLog log = Common.getInstance().getManager().getLogHandler().getOrGenerate(bankAccount.getKey());
                                        log.addHistory(System.currentTimeMillis(), BankLogType.ADMIN_GIVE, s.getName(), value);
                                        Common.getInstance().getManager().getLogHandler().save(log);
                                        s.sendMessage(prefix + "§2" + formatValue(((Long) value).doubleValue() / 100) + "§7 wurden auf das Konto §2" + name + " §7überwiesen.");
                                    } else {
                                        s.sendMessage(prefix + "§2Argument 4 §7muss eine positive Zahl sein.");
                                    }
                                } else {
                                    s.sendMessage(prefix + "§7Das Bankkonto §2" + name + " §7existiert nicht.");
                                }
                            } else {
                                s.sendMessage(noPerm("bank.give"));
                            }
                            break;
                        case "take":
                            if(s.hasPermission("bank.take")) {
                                String name = args[2].toLowerCase();
                                BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(name);
                                if(bankAccount != null) {
                                    String arg = args[3];
                                    if(arg.contains(","))
                                        arg = arg.replace(",", ".");
                                    long value;
                                    try {
                                        value = (long) (Double.parseDouble(arg) * 100);
                                    } catch(NumberFormatException e) {
                                        s.sendMessage(prefix + "§2Argument 4 §7muss eine positive Zahl sein.");
                                        return false;
                                    }
                                    if(value > 0) {
                                        if((bankAccount.getBalance() - value) >= 0) {
                                            bankAccount.withdrawMoney(value);
                                            BankAccount staatskasse = Common.getInstance().getManager().getBankManager().get("staatskasse");
                                            staatskasse.depositMoney(value);
                                            Common.getInstance().getManager().getBankManager().save(staatskasse);
                                            Common.getInstance().getManager().getBankManager().save(bankAccount);
                                            BankLog log = Common.getInstance().getManager().getLogHandler().getOrGenerate(bankAccount.getKey());
                                            log.addHistory(System.currentTimeMillis(), BankLogType.ADMIN_TAKE, s.getName(), value);
                                            Common.getInstance().getManager().getLogHandler().save(log);
                                            s.sendMessage(prefix + "§2" + formatValue(((Long) value).doubleValue() / 100) + " §7wurden dem Konto §2" + name + " §7genommen.");
                                        } else {
                                            s.sendMessage(prefix + "§7Der Kontostand darf nicht unter 0 A gehen.");
                                        }
                                    } else {
                                        s.sendMessage(prefix + "§2Argument 4 §7muss eine positive Zahl sein.");
                                    }
                                } else {
                                    s.sendMessage(prefix + "§7Das Bankkonto §2" + name + " §7existiert nicht.");
                                }
                            } else {
                                s.sendMessage(noPerm("bank.take"));
                            }
                            break;
                        case "set":
                            if(s.hasPermission("bank.set")) {
                                String name = args[2].toLowerCase();
                                BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(name);
                                if(bankAccount != null) {
                                    String arg = args[3];
                                    if(arg.contains(","))
                                        arg = arg.replace(",", ".");
                                    long value;
                                    try {
                                        value = (long) (Double.parseDouble(arg) * 100);
                                    } catch(NumberFormatException e) {
                                        s.sendMessage(prefix + "§2Argument 4 §7muss eine positive Zahl sein.");
                                        return false;
                                    }
                                    if(value >= 0) {
                                        long old = bankAccount.getBalance();
                                        bankAccount.setBalance(value);
                                        BankAccount staatskasse = Economy.getInstance().getManager().getBankManager().get("staatskasse");
                                        if((old - value) > 0) {
                                            staatskasse.depositMoney(old-value);
                                        } else {
                                            staatskasse.withdrawMoney(value - old);
                                        }
                                        Common.getInstance().getManager().getBankManager().save(staatskasse);
                                        Common.getInstance().getManager().getBankManager().save(bankAccount);
                                        BankLog log = Common.getInstance().getManager().getLogHandler().getOrGenerate(bankAccount.getKey());
                                        log.addHistory(System.currentTimeMillis(), BankLogType.ADMIN_SET, s.getName(), value);
                                        Common.getInstance().getManager().getLogHandler().save(log);
                                    s.sendMessage(prefix + "§7Der Kontostand vom Konto §2" + name + " §7wurde auf §2" + formatValue(((Long) value).doubleValue() / 100) + " §7gesetzt.");
                                    } else {
                                        s.sendMessage(prefix + "§2Argument 4 §7muss eine positive Zahl sein.");
                                    }
                                } else {
                                    s.sendMessage(prefix + "§7Das Bankkonto §2" + name + " §7existiert nicht.");
                                }
                            } else {
                                s.sendMessage(noPerm("bank.set"));
                            }
                            break;
                        case "addmember":
                        case "addm":
                            if(s.hasPermission("bank.addmember")) {
                                String name = args[2].toLowerCase();
                                String other = args[3];
                                User m = Economy.getInstance().getManager().getPlayerManager().getByName(other);
                                if(m != null) {
                                    BankAccount account = Economy.getInstance().getManager().getBankManager().get(name);
                                    if(account != null) {
                                        if(!account.getMembers().contains(m.getKey())) {
                                            if(!account.getOwners().contains(m.getKey())) {
                                                account.getMembers().add(m.getKey());
                                                Economy.getInstance().getManager().getBankManager().save(account);
                                                s.sendMessage(prefix + "§2" + m.getName() + " §7wurde als §6Teilhaber §7hinzugefügt.");
                                                Player to;
                                                if((to = Bukkit.getPlayer(m.getKey())) != null) {
                                                    to.sendMessage(prefix + "§7Du wurdest zum §6Teilhaber §7des Kontos §2" + account.getKey() + " §7von §2" + s.getName() + " §7ernannt.");
                                                }
                                            } else {
                                                s.sendMessage(prefix + "§2" + m.getName() + " §7ist bereits §4Inhaber §7des Kontos.");
                                            }
                                        } else {
                                            s.sendMessage(prefix + "§2" + m.getName() + " §7ist bereits §6Teilhaber §7des Kontos.");
                                        }
                                    } else {
                                        s.sendMessage(prefix + "§7Es wurde kein Konto mit dem Namen §2" + name + " §7gefunden.");
                                    }
                                } else {
                                    s.sendMessage(prefix + "§7Der Spieler §2" + other + " §7existiert nicht.");
                                }
                            } else {
                                s.sendMessage(noPerm("bank.addmember"));
                            }
                            break;
                        case "removemember":
                        case "remm":
                            if(s.hasPermission("bank.removemember")) {
                                String name = args[2].toLowerCase();
                                BankAccount account = Economy.getInstance().getManager().getBankManager().get(name);
                                if(account != null) {
                                    User m = Economy.getInstance().getManager().getPlayerManager().getByName(args[3]);
                                    if(m != null) {
                                        if(account.getMembers().contains(m.getKey())) {
                                            account.getMembers().remove(m.getKey());
                                            Economy.getInstance().getManager().getBankManager().save(account);
                                            s.sendMessage(prefix + "§7Der Spieler §2" + m.getName() + " §7ist nun kein §6Teilhaber §7mehr.");
                                            Player t;
                                            if((t = Bukkit.getPlayer(m.getKey())) != null) {
                                                t.sendMessage(prefix + "§7Dir wurde die §6Teilhaberschaft §7des Kontos §2" + account.getKey() + " §7entzogen.");
                                            }
                                        } else {
                                            s.sendMessage(prefix + "§7Der Spieler §2" + m.getName() + " §7ist kein §6Teilhaber §7des Kontos.");
                                        }
                                    } else {
                                        s.sendMessage(prefix + "§7Der Spieler §2" + args[2] + " §7existiert nicht.");
                                    }
                                } else {
                                    s.sendMessage(prefix + "Ein Konto mit dem Namen §2" + name + " §7konnte nicht gefunden werden.");
                                }
                            } else {
                                s.sendMessage(noPerm("bank.removemember"));
                            }
                            break;
                        case "addowner":
                        case "addo":
                            if(s.hasPermission("bank.addowner")) {
                                String name = args[2].toLowerCase();
                                String other = args[3];
                                User m = Economy.getInstance().getManager().getPlayerManager().getByName(other);
                                if(m != null) {
                                    BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(name);
                                    if(bankAccount != null) {
                                        if(!bankAccount.getOwners().contains(m.getKey()) && !bankAccount.getMembers().contains(m.getKey())) {
                                            bankAccount.getOwners().add(m.getKey());
                                            Economy.getInstance().getManager().getBankManager().save(bankAccount);
                                            s.sendMessage(prefix + "§2" + m.getName() + " §7wurde als §4Inhaber §7hinzugefügt.");
                                            Player to;
                                            if((to = Bukkit.getPlayer(m.getKey())) != null) {
                                                to.sendMessage(prefix + "§7Du wurdest zum §4Inhaber §7des Kontos §2" + bankAccount.getKey() + " §7von §2" + s.getName() + " §7ernannt.");
                                            }
                                        } else {
                                            s.sendMessage(prefix + "§7Der Spieler §2" + m.getName() + " §7ist bereits §6Teilhaber§7/§4Inhaber §7des Kontos.");
                                        }
                                    } else {
                                        s.sendMessage(prefix + "Ein Konto mit dem Namen §2" + name + " §7konnte nicht gefunden werden.");
                                    }
                                } else {
                                    s.sendMessage(prefix + "§7Der Spieler §2" + other + " §7existiert nicht.");
                                }
                            } else {
                                s.sendMessage(noPerm("bank.addowner"));
                            }
                            break;
                        case "nemo":
                            s.sendMessage("§cHast du ihn bereits gefunden? Ich nicht.");
                            break;
                        case "removeowner":
                        case "remo":
                            if(s.hasPermission("bank.removeowner")) {
                                String name = args[2].toLowerCase();
                                BankAccount account = Economy.getInstance().getManager().getBankManager().get(name);
                                if(account != null) {
                                    User m = Economy.getInstance().getManager().getPlayerManager().getByName(args[3]);
                                    if(m != null) {
                                        if(account.getOwners().contains(m.getKey())) {
                                            account.getOwners().remove(m.getKey());
                                            Economy.getInstance().getManager().getBankManager().save(account);
                                            s.sendMessage(prefix + "§7Der Spieler §2" + m.getName() + " §7ist nun kein §4Inhaber §7mehr.");
                                            Player t;
                                            if((t = Bukkit.getPlayer(m.getKey())) != null) {
                                                t.sendMessage(prefix + "§7Dir wurde die §4Inhaberschaft §7des Kontos §2" + account.getKey() + " §7entzogen.");
                                            }
                                        } else {
                                            s.sendMessage(prefix + "§7Der Spieler §2" + m.getName() + " §7ist kein §4Inhaber §7des Kontos.");
                                        }
                                    } else {
                                        s.sendMessage(prefix + "§7Der Spieler §2" + args[2] + " §7existiert nicht.");
                                    }
                                } else {
                                    s.sendMessage(prefix + "Ein Konto mit dem Namen §2" + name + " §7konnte nicht gefunden werden.");
                                }
                            } else {
                                s.sendMessage(noPerm("bank.removeowner"));
                            }
                            break;
                    }
                } else {
                    sendHelpMsg(s);
                }
            } else {
                if(s instanceof Player) {
                    Player p = (Player) s;
                    if(args.length == 1) {
                        if(args[0].equalsIgnoreCase("list")) {
                            List<BankAccount> bankAccounts = Economy.getInstance().getManager().getBankManager().getBankAccounts(p.getUniqueId());
                            List<BankAccount> owner = Lists.newArrayList();
                            List<BankAccount> member = Lists.newArrayList();
                            if(bankAccounts.size() > 0) {
                                bankAccounts.forEach(bAcc -> {
                                    if(bAcc.getOwners().contains(p.getUniqueId())) {
                                        owner.add(bAcc);
                                    } else {
                                        if(!Common.getInstance().getTeamAccounts().contains(bAcc.getKey()))
                                            member.add(bAcc);
                                    }
                                });
                                p.sendMessage("§7|----| §2Bank-Konten §7|----|");
                                if(owner.size() > 0) {
                                    owner.forEach(account -> p.sendMessage("§7» §2" + account.getKey() + "§7: " + formatValue(((Long) account.getBalance()).doubleValue() / 100) + " §7| §4Inhaber"));
                                }
                                if(member.size() > 0) {
                                    member.forEach(account -> p.sendMessage("§7» §2" + account.getKey() + "§7: " + formatValue(((Long) account.getBalance()).doubleValue() / 100) + " §7| §6Teilhaber"));
                                }
                            } else {
                                p.sendMessage(prefix + "§7Du hast keine Konten.");
                            }
                        } else {
                            BankAccount account = Economy.getInstance().getManager().getBankManager().get(args[0]);
                            if(account != null) {
                                p.sendMessage("§7|----| §2" + account.getKey() + " §7|----|\n" +
                                        "§7» Guthaben: §2" + formatValue(((Long) account.getBalance()).doubleValue() / 100) + "\n" +
                                        "§7» Teilhaber: §2" + account.getMembers().stream().map(u -> Economy.getInstance().getManager().getPlayerManager().get(u).getName()).collect(Collectors.joining(", ")) + "\n" +
                                        "§7» Inhaber: §2" + account.getOwners().stream().map(u -> Economy.getInstance().getManager().getPlayerManager().get(u).getName()).collect(Collectors.joining(", ")));
                            } else {
                                sendHelpMessage(p);
                            }
                        }
                    } else if(args.length == 2) {
                        if (args[0].equalsIgnoreCase("create")) {
                            String name = args[1];
                            if (name.length() <= 16) {
                                if (Economy.getInstance().getCharList().stream().noneMatch(name::contains)) {
                                    int size = Economy.getInstance().getManager().getBankManager().getOwnerBankAccounts(p.getUniqueId()).size();
                                    if (size < 3) {
                                        User user = Economy.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                                        BankAccount testAccount = Economy.getInstance().getManager().getBankManager().get(name.toLowerCase());
                                        if (testAccount == null) {
                                            switch (size) {
                                                case 0:
                                                    p.sendMessage(prefix + "§7Dein erstes Bankkonto §2" + name + " §7wurde §2kostenlos §7erstellt.");
                                                    break;
                                                case 1:
                                                    if ((user.getBalance() - 500000) >= 0) {
                                                        p.sendMessage(prefix + "§7Dein zweites Bankkonto §2" + name + " §7wurde für einen Aufpreis von §25.000 A §7erstellt.");
                                                        user.withdrawMoney(500000);
                                                        Economy.getInstance().getManager().getBankManager().get("staatskasse").depositMoney(500000);
                                                    } else {
                                                        p.sendMessage(prefix + "§7Du hast nicht genügend Geld.");
                                                        return false;
                                                    }
                                                    break;
                                                case 2:
                                                    if ((user.getBalance() - 1000000) >= 0) {
                                                        p.sendMessage(prefix + "§7Dein drittes Bankkonto §2" + name + " §7wurde für einen Aufpreis von §210.000 A §7erstellt.");
                                                        user.withdrawMoney(1000000);
                                                        Economy.getInstance().getManager().getBankManager().get("staatskasse").depositMoney(1000000);
                                                    } else {
                                                        p.sendMessage(prefix + "§7Du hast nicht genügend Geld.");
                                                        return false;
                                                    }
                                                    break;
                                            }
                                            BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().getOrGenerate(name.toLowerCase(), key -> {
                                                BankAccount account = new BankAccount(key);
                                                account.setBankType(BankType.BANK);
                                                account.setOwners(Lists.newArrayList(p.getUniqueId()));
                                                account.setMembers(Lists.newArrayList());
                                                account.setBalance(0);
                                                return account;
                                            });
                                            Economy.getInstance().getManager().getLogHandler().save(Economy.getInstance().getManager().getLogHandler().getOrGenerate(name.toLowerCase()));
                                            Economy.getInstance().getManager().getBankManager().save(bankAccount);
                                        } else {
                                            p.sendMessage(prefix + "§7Ein Bankkonto mit dem Namen §2" + name.toLowerCase() + " §7existiert bereits.");
                                        }
                                    } else {
                                        p.sendMessage(prefix + "§7Du hast bereits §23 Konten§7.");
                                    }
                                } else {
                                    p.sendMessage(prefix + "§7Du darfst in deinem Kontonamen keine §2Sonderzeichen §7oder §2Umlaute §7verwenden.");
                                }
                            } else {
                                p.sendMessage(prefix + "§7Der Name darf maximal §216 Zeichen §7lang sein.");
                            }
                        } else if(args[0].equalsIgnoreCase("list")) {
                            String name = args[1].toLowerCase();
                            User user = Economy.getInstance().getManager().getPlayerManager().getByName(name);
                            if(user != null) {
                                List<BankAccount> bankAccounts = Economy.getInstance().getManager().getBankManager().getBankAccounts(user.getKey());
                                if(bankAccounts.size() > 0) {
                                    List<BankAccount> owner = Lists.newArrayList();
                                    List<BankAccount> member = Lists.newArrayList();
                                    bankAccounts.forEach(bAcc -> {
                                        if(bAcc.getOwners().contains(user.getKey())) {
                                            owner.add(bAcc);
                                        } else {
                                            if(!Common.getInstance().getTeamAccounts().contains(bAcc.getKey()))
                                                member.add(bAcc);
                                        }
                                    });
                                    p.sendMessage("§7|----| §2Bank-Konten von " + user.getName() +" §7|----|");
                                    if(owner.size() > 0) {
                                        owner.forEach(account -> p.sendMessage("§7» §2" + account.getKey() + "§7: " + formatValue(((Long) account.getBalance()).doubleValue() / 100) + " §7| §4Inhaber"));
                                    }
                                    if(member.size() > 0) {
                                        member.forEach(account -> p.sendMessage("§7» §2" + account.getKey() + "§7: " + formatValue(((Long) account.getBalance()).doubleValue() / 100) + " §7| §6Teilhaber"));
                                    }
                                } else {
                                    p.sendMessage(prefix + "§7Der Spieler §2" + name + " §7existiert nicht.");
                                }
                            } else {
                                p.sendMessage(prefix + "§7Der Spieler §2" + name + " §7existiert nicht.");
                            }
                        } else if(args[0].equalsIgnoreCase("balance") || args[0].equalsIgnoreCase("bal")) {
                            String name = args[1].toLowerCase();
                            BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(name);
                            if(bankAccount != null) {
                                p.sendMessage(prefix  + "§7Guthaben des Kontos §2" + name + "§7: " + formatValue(((Long) bankAccount.getBalance()).doubleValue() / 100));
                            } else {
                                p.sendMessage(prefix + "§7Das Bankkonto §2" + name + " §7existiert nicht.");
                            }
                        } else if(args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del")) {
                            String name = args[1].toLowerCase();
                            BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(name);
                            if(bankAccount != null) {
                                if(bankAccount.getOwners().contains(p.getUniqueId())) {
                                    long moneyValue = bankAccount.getBalance();
                                    Economy.getInstance().getManager().getPlayerManager().get(p.getUniqueId()).depositMoney(moneyValue);
                                    /*Economy.getInstance().getManager().getPlayerManager().getUsers().stream().filter(User::isCustomScoreboard).filter(user -> user.checkCustomScoreboard(CustomScoreboardType.BANK))
                                            .filter(user -> user.getCustomScoreboard(CustomScoreboardType.BANK).equalsIgnoreCase(name)).forEach(user -> {
                                            user.unSetCustomScoreboard(CustomScoreboardType.BANK);
                                            Player to;
                                            if((to = Bukkit.getPlayer(user.getKey())) != null) {
                                                to.sendMessage(prefix + "§7Dein §aCustom-Scoreboard §7für ein Bankkonto wurde gelöscht, da das Bankkonto gelöscht wurde.");
                                            }
                                            Economy.getInstance().getManager().getPlayerManager().save(user);
                                    });*/
                                    Economy.getInstance().getManager().getBankManager().delete(name);
                                    p.sendMessage(prefix + "§7Das Bankkonto §2" + name + " §7wurde gelöscht.");
                                } else {
                                    p.sendMessage(prefix + "§7Du bist nicht berechtigt das Konto §2" + name + " §7zu löschen.");
                                }
                            } else {
                                p.sendMessage(prefix + "§7Das Bankkonto §2" + name + " §7existiert nicht.");
                            }
                        } else if(args[0].equalsIgnoreCase("log")) {
                            String name = args[1].toLowerCase();
                            BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(name);
                            if(bankAccount != null) {
                                if(bankAccount.getMembers().contains(p.getUniqueId())  || bankAccount.getOwners().contains(p.getUniqueId())
                                        || (bankAccount.getKey().startsWith("team-") && p.hasPermission("bank.log.teamaccounts"))) {
                                    BankLog bankLog = Economy.getInstance().getManager().getLogHandler().getOrGenerate(bankAccount.getKey());
                                    Map<String, Map<String, Object>> log = bankLog.getHistory();
                                    p.sendMessage("§7|-----| §2Transkationen §7|-----|");
                                    SimpleDateFormat formatDay = new SimpleDateFormat("dd.MM.yy");
                                    SimpleDateFormat formatHour = new SimpleDateFormat("HH:mm:ss");
                                    List<String> keySet = new ArrayList<>(log.keySet());
                                    Collections.sort(keySet);
                                    Collections.reverse(keySet);
                                    if(keySet.size() == 0) {
                                        p.sendMessage("§7Keine bisherigen Transaktionen gefunden.");
                                        return true;
                                    }
                                    keySet.stream().limit(5).forEach(l ->
                                            p.sendMessage("§8» §2" + formatDay.format(new Date(Long.parseLong(l))) + " §8- §2" + formatHour.format(new Date(Long.parseLong(l))) + "§8: " +
                                                    (BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.ADMIN_GIVE) ? "§4Eingezahlt" :
                                                            (BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.ADMIN_SET) ? "§4Gesetzt" :
                                                                    (BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.ADMIN_TAKE) ? "§4Ausgezahlt" :
                                                                            (BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.DEPOSIT) ? "§aEingezahlt" :
                                                                                    (BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.WITHDRAW) ? "§cAusgezahlt" :
                                                                                            (BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.TRANSFER_FROM) ? "§cÜberweisung" :
                                                                                                    (BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.TRANSFER_TO) ? "§aÜberweisung" : "")))))))
                                                    + " §8|| " + (bankAccount.getOwners().contains(Economy.getInstance().getManager().getPlayerManager().getByName((String) log.get(l).get("player")).getKey()) ? "§c" + log.get(l).get("player") :
                                                                    (bankAccount.getMembers().contains(Economy.getInstance().getManager().getPlayerManager().getByName((String) log.get(l).get("player")).getKey()) ? "§6" + log.get(l).get("player") : "§4" + log.get(l).get("player")))
                                            + " §8|| " + (BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.ADMIN_GIVE) ||
                                                            BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.DEPOSIT) ||
                                                            BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.TRANSFER_TO) ? "§a+" + formatValue(((Long) log.get(l).get("value")).doubleValue() / 100) :
                                                    BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.TRANSFER_FROM) ||
                                                            BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.WITHDRAW) ||
                                                            BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.ADMIN_TAKE) ? "§c-" + formatValue(((Long) log.get(l).get("value")).doubleValue() / 100) :
                                                            "§7" + formatValue(((Long) log.get(l).get("value")).doubleValue() / 100) )));
                                } else {
                                    p.sendMessage(prefix + "§7Du bist nicht §6Teil§7- oder §4Inhaber §7des Kontos.");
                                }
                            } else {
                                p.sendMessage(prefix + "§7Das Bankkonto §2" + name + " §7existiert nicht.");
                            }
                        } else {
                            sendHelpMessage(p);
                        }
                    } else if(args.length == 3) {
                        if(args[0].equalsIgnoreCase("addmember") || args[0].equalsIgnoreCase("addm")) {
                            String name = args[1].toLowerCase();
                            String other = args[2];
                            User m = Economy.getInstance().getManager().getPlayerManager().getByName(other);
                            if(m != null) {
                                BankAccount account = Economy.getInstance().getManager().getBankManager().get(name);
                                if(account != null) {
                                    if(account.getOwners().contains(p.getUniqueId())) {
                                        if(!account.getMembers().contains(m.getKey())) {
                                            if(!account.getOwners().contains(m.getKey())) {
                                                if(!m.getKey().equals(p.getUniqueId())) {
                                                    account.getMembers().add(m.getKey());
                                                    Economy.getInstance().getManager().getBankManager().save(account);
                                                    p.sendMessage(prefix + "§2" + m.getName() + " §7wurde als §6Teilhaber §7hinzugefügt.");
                                                    Player to;
                                                    if((to = Bukkit.getPlayer(m.getKey())) != null) {
                                                        to.sendMessage(prefix + "§7Du wurdest zum §6Teilhaber §7des Kontos §2" + account.getKey() + " §7von §2" + p.getName() + " §7ernannt.");
                                                    }
                                                } else {
                                                    p.sendMessage(prefix + "§7Du kannst dich nicht selbst als §6Teilhaber §7eintragen.");
                                                }
                                            } else {
                                                p.sendMessage(prefix + "§2" + m.getName() + " §7ist bereits §4Inhaber §7des Kontos.");
                                            }
                                        } else {
                                            p.sendMessage(prefix + "§2" + m.getName() + " §7ist bereits §6Teilhaber §7des Kontos.");
                                        }
                                    } else {
                                        p.sendMessage(prefix + "§7Du hast keine Rechte auf dieses Konto.");
                                    }
                                } else {
                                    p.sendMessage(prefix + "§7Es wurde kein Konto mit dem Namen §2" + name + " §7gefunden.");
                                }
                            } else {
                                p.sendMessage(prefix + "§7Der Spieler §2" + other + " §7existiert nicht.");
                            }
                        } else if(args[0].equalsIgnoreCase("addowner") || args[0].equalsIgnoreCase("addo")) {
                            String name = args[1].toLowerCase();
                            String other = args[2];
                            User m = Economy.getInstance().getManager().getPlayerManager().getByName(other);
                            if(m != null) {
                                BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(name);
                                if(bankAccount != null) {
                                    if(bankAccount.getOwners().contains(p.getUniqueId())) {
                                        if(!bankAccount.getOwners().contains(m.getKey()) && !bankAccount.getMembers().contains(m.getKey())) {
                                            if(!m.getKey().equals(p.getUniqueId())) {
                                                bankAccount.getOwners().add(m.getKey());
                                                Economy.getInstance().getManager().getBankManager().save(bankAccount);
                                                p.sendMessage(prefix + "§2" + m.getName() + " §7wurde als §4Inhaber §7zu deinem Konto §7hinzugefügt.");
                                                Player to;
                                                if((to = Bukkit.getPlayer(m.getKey())) != null) {
                                                    to.sendMessage(prefix + "§7Du wurdest zum §4Inhaber §7des Kontos §2" + bankAccount.getKey() + " §7von §2" + p.getName() + " §7ernannt.");
                                                }
                                            } else {
                                                p.sendMessage(prefix + "§7Du kannst dich nicht selbst als §4Inhaber §7eintragen.");
                                            }
                                        } else {
                                            p.sendMessage(prefix + "§7Der Spieler §2" + m.getName() + " §7ist bereits §6Teilhaber§7/§4Inhaber §7des Kontos.");
                                        }
                                    } else {
                                        p.sendMessage(prefix + "§7Du hast keine Rechte auf dieses Konto.");
                                    }
                                } else {
                                    p.sendMessage(prefix + "Ein Konto mit dem Namen §2" + name + " §7konnte nicht gefunden werden.");
                                }
                            } else {
                                p.sendMessage(prefix + "§7Der Spieler §2" + other + " §7existiert nicht.");
                            }
                        } else if(args[0].equalsIgnoreCase("dep") || args[0].equalsIgnoreCase("deposit")) {
                                String name = args[1].toLowerCase();
                                BankAccount account = Economy.getInstance().getManager().getBankManager().get(name);
                                if(account != null) {
                                    String arg = args[2];
                                    if(arg.contains(","))
                                        arg = arg.replace(",", ".");
                                    long value;
                                    User user = Economy.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                                    try {
                                        if(arg.equalsIgnoreCase("all"))
                                            value = user.getBalance();
                                        else
                                        if(!arg.equalsIgnoreCase("Infinity") && !arg.equalsIgnoreCase("-Infinity")) {
                                            value = (long) (Double.parseDouble(arg) * 100);
                                        } else {
                                            p.sendMessage(prefix + "§7Du möchtest §2Infinity§7? Ok, du bekommst es:\n\n");
                                            p.sendMessage(prefix + "§7Der Kontostand des Kontos §2" + account.getKey() + " §7wurde durch §2CONSOLE §7auf §20A §7gesetzt.");
                                            return false;
                                        }
                                    } catch(NumberFormatException ignored) {
                                        p.sendMessage(prefix + "§2Argument 2 §7muss eine positive Zahl sein.");
                                        return false;
                                    }
                                    if(value > 0) {
                                        long taxes = (long) (value * 0.0025);
                                        if(taxes == 0) {
                                            taxes = 1;
                                        }
                                        if((user.getBalance() - value) >= 0) {
                                            if((user.getBalance() + value) < 0) {
                                                p.sendMessage(prefix + "§7Dein Kontostand kann nicht ins §cMinus §7gehen.");
                                                return false;
                                            }
                                            if((account.getBalance() + value) < 0) {
                                                p.sendMessage(prefix + "§7Dein Kontostand kann nicht in §cMinus §7gehen.");
                                                return false;
                                            }
                                            long max = 9200000000000000000L;
                                            if(value >  max) {
                                                p.sendMessage(prefix + "§7Der Betrag ist zu hoch.");
                                                return false;
                                            }
                                            account.depositMoney(value - taxes);
                                            user.withdrawMoney(value);
                                            Economy.getInstance().getManager().getBankManager().get("staatskasse").depositMoney(taxes);
                                            Economy.getInstance().getManager().getBankManager().save(account);
                                            Economy.getInstance().getManager().getBankManager().save(Economy.getInstance().getManager().getBankManager().get("staatskasse"));
                                            Economy.getInstance().getManager().getPlayerManager().save(user);
                                            BankLog log = Economy.getInstance().getManager().getLogHandler().getOrGenerate(account.getKey());
                                            log.addHistory(System.currentTimeMillis(), BankLogType.DEPOSIT, p.getName(), value);
                                            Economy.getInstance().getManager().getLogHandler().save(log);
                                            p.sendMessage(prefix + "§7Du hast §2" + formatValue(((Long) value).doubleValue() / 100) + " §7auf das Konto §2" + account.getKey() + " §7eingezahlt und §2" + formatValue(((Long) taxes).doubleValue() / 100) + " §7Steuern gezahlt.");
                                            account.getOwners().forEach(owner -> {
                                                Player t;
                                                if((t = Bukkit.getPlayer(owner)) != null) {
                                                    t.sendMessage(prefix + "§2" + p.getName() + " §7hat §2" + formatValue(((Long) value).doubleValue() / 100) + " §7auf das Konto §2" + account.getKey() + " §7eingezahlt");
                                                }
                                            });
                                            account.getMembers().forEach(owner -> {
                                                Player t;
                                                if((t = Bukkit.getPlayer(owner)) != null) {
                                                    t.sendMessage(prefix + "§2" + p.getName() + " §7hat §2" + formatValue(((Long) value).doubleValue() / 100) + " §7auf das Konto §2" + account.getKey() + " §7eingezahlt");
                                                }
                                            });
                                        } else {
                                            p.sendMessage(prefix + "§7Du hast nicht genügend Geld.");
                                        }
                                    } else {
                                        p.sendMessage(prefix + "§2Argument 2 §7muss eine positive Zahl sein.");
                                    }
                                } else {
                                    p.sendMessage(prefix + "Ein Konto mit dem Namen §2" + name + " §7konnte nicht gefunden werden.");
                                }
                        } else if(args[0].equalsIgnoreCase("with") || args[0].equalsIgnoreCase("withdraw")) {
                                String name = args[1].toLowerCase();
                                BankAccount account = Economy.getInstance().getManager().getBankManager().get(name);
                                if(account != null) {
                                    if(account.getMembers().contains(p.getUniqueId()) || account.getOwners().contains(p.getUniqueId())) {
                                        String arg = args[2];
                                        if(arg.contains(","))
                                            arg = arg.replace(",", ".");
                                        long value;
                                        try {
                                            if(arg.equalsIgnoreCase("all"))
                                                value = account.getBalance();
                                            else {
                                                if(!arg.equalsIgnoreCase("Infinity") && !arg.equalsIgnoreCase("-Infinity")) {
                                                    value = (long) (Double.parseDouble(arg) * 100);
                                                } else {
                                                    p.sendMessage(prefix + "§7Du möchtest §2Infinity§7? Ok, du bekommst es:\n\n");
                                                    p.sendMessage(prefix + "§7Der Kontostand des Kontos §2" + account.getKey() + " §7wurde durch §2CONSOLE §7auf §20A §7gesetzt.");
                                                    return false;
                                                }
                                            }
                                        } catch(NumberFormatException ignored) {
                                            p.sendMessage(prefix + "§2Argument 2 §7muss eine positive Zahl sein.");
                                            return false;
                                        }
                                        if(value > 0) {
                                            long taxes = (long) (value * 0.0025);
                                            if(taxes == 0)
                                                taxes = 1;
                                            User user = Economy.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                                            if(((account.getBalance() - value) - taxes) < 0) {
                                                if(!arg.equalsIgnoreCase("all")) {
                                                    p.sendMessage(prefix + "§7Du hast nicht genügend Geld auf deinem §2Konto§7. §8(Steuern: " + formatValue(((Long) taxes).doubleValue() / 100) + ")");
                                                    return false;
                                                }
                                            }
                                            if((user.getBalance() + value) < 0) {
                                                p.sendMessage(prefix + "§7Dein Kontostand kann nicht ins §cMinus §7gehen.");
                                                return false;
                                            }
                                            if((account.getBalance() + value) < 0) {
                                                p.sendMessage(prefix + "§7Dein Kontostand kann nicht in §cMinus §7gehen.");
                                                return false;
                                            }
                                            long max = 9200000000000000000L;
                                            if(value >  max) {
                                                p.sendMessage(prefix + "§7Der Betrag ist zu hoch.");
                                                return false;
                                            }
                                            if(arg.equalsIgnoreCase("all")) {
                                                account.withdrawMoney(value);
                                                user.depositMoney(value - taxes);
                                            } else {
                                                account.withdrawMoney(value + taxes);
                                                user.depositMoney(value);
                                            }
                                            Economy.getInstance().getManager().getBankManager().get("staatskasse").depositMoney(taxes);
                                            Economy.getInstance().getManager().getBankManager().save(account);
                                            Economy.getInstance().getManager().getBankManager().save(Economy.getInstance().getManager().getBankManager().get("staatskasse"));
                                            Economy.getInstance().getManager().getPlayerManager().save(user);
                                            BankLog log = Economy.getInstance().getManager().getLogHandler().getOrGenerate(account.getKey());
                                            log.addHistory(System.currentTimeMillis(), BankLogType.WITHDRAW, p.getName(), value);
                                            Economy.getInstance().getManager().getLogHandler().save(log);
                                            p.sendMessage(prefix + "§7Du hast §2" + formatValue(((Long) value).doubleValue() / 100) + " §7vom Konto §2" + account.getKey() + " §7ausgezahlt und §2" + formatValue(((Long) taxes).doubleValue() / 100) + " §7Steuern gezahlt.");
                                            account.getOwners().forEach(owner -> {
                                                Player t;
                                                if((t = Bukkit.getPlayer(owner)) != null) {
                                                    t.sendMessage(prefix + "§2" + p.getName() + " §7hat §2" + formatValue(((Long) value).doubleValue() / 100) + " §7vom Konto §2" + account.getKey() + " §7ausgezahlt");
                                                }
                                            });
                                            account.getMembers().forEach(owner -> {
                                                Player t;
                                                if((t = Bukkit.getPlayer(owner)) != null) {
                                                    t.sendMessage(prefix + "§2" + p.getName() + " §7hat §2" + formatValue(((Long) value).doubleValue() / 100) + " §7vom Konto §2" + account.getKey() + " §7ausgezahlt");
                                                }
                                            });
                                        } else {
                                            p.sendMessage(prefix + "§2Argument 2 §7muss eine positive Zahl sein.");
                                        }
                                    } else {
                                        p.sendMessage(prefix + "§7Du hast keine Rechte auf dieses Konto.");
                                    }
                                } else {
                                    p.sendMessage(prefix + "Ein Konto mit dem Namen §2" + name + " §7konnte nicht gefunden werden.");
                                }
                        } else if(args[0].equalsIgnoreCase("removeowner") || args[0].equalsIgnoreCase("remo")) {
                            String name = args[1].toLowerCase();
                            BankAccount account = Economy.getInstance().getManager().getBankManager().get(name);
                            if(account != null) {
                                User m = Economy.getInstance().getManager().getPlayerManager().getByName(args[2]);
                                if(m != null) {
                                    if(account.getOwners().contains(p.getUniqueId())) {
                                        if(account.getOwners().contains(m.getKey())) {
                                            account.getOwners().remove(m.getKey());
                                            Economy.getInstance().getManager().getBankManager().save(account);
                                            p.sendMessage(prefix + "§7Der Spieler §2" + m.getName() + " §7ist nun kein §4Inhaber §7mehr.");
                                            Player t;
                                            if((t = Bukkit.getPlayer(m.getKey())) != null) {
                                                t.sendMessage(prefix + "§7Dir wurde die §4Inhaberschaft §7des Kontos §2" + account.getKey() + " §7entzogen.");
                                            }
                                        } else {
                                            p.sendMessage(prefix + "§7Der Spieler §2" + m.getName() + " §7ist kein §4Inhaber §7des Kontos.");
                                        }
                                    } else {
                                        p.sendMessage(prefix + "§7Du hast keine Rechte auf dieses Konto.");
                                    }
                                } else {
                                    p.sendMessage(prefix + "§7Der Spieler §2" + args[2] + " §7existiert nicht.");
                                }
                            } else {
                                p.sendMessage(prefix + "Ein Konto mit dem Namen §2" + name + " §7konnte nicht gefunden werden.");
                            }
                        } else if(args[0].equalsIgnoreCase("removemember") || args[0].equalsIgnoreCase("remm")) {
                            String name = args[1].toLowerCase();
                            BankAccount account = Economy.getInstance().getManager().getBankManager().get(name);
                            if(account != null) {
                                User m = Economy.getInstance().getManager().getPlayerManager().getByName(args[2]);
                                if(m != null) {
                                    if(account.getOwners().contains(p.getUniqueId())) {
                                        if(account.getMembers().contains(m.getKey())) {
                                            account.getMembers().remove(m.getKey());
                                            Economy.getInstance().getManager().getBankManager().save(account);
                                            p.sendMessage(prefix + "§7Der Spieler §2" + m.getName() + " §7ist nun kein §6Teilhaber §7mehr.");
                                            Player t;
                                            if((t = Bukkit.getPlayer(m.getKey())) != null) {
                                                t.sendMessage(prefix + "§7Dir wurde die §6Teilhaberschaft §7des Kontos §2" + account.getKey() + " §7entzogen.");
                                            }
                                        } else {
                                            p.sendMessage(prefix + "§7Der Spieler §2" + m.getName() + " §7ist kein §6Teilhaber §7des Kontos.");
                                        }
                                    } else {
                                        p.sendMessage(prefix + "§7Du hast keine Rechte auf dieses Konto.");
                                    }
                                } else {
                                    p.sendMessage(prefix + "§7Der Spieler §2" + args[2] + " §7existiert nicht.");
                                }
                            } else {
                                p.sendMessage(prefix + "Ein Konto mit dem Namen §2" + name + " §7konnte nicht gefunden werden.");
                            }
                        } else if(args[0].equalsIgnoreCase("log")) {
                            String name = args[1].toLowerCase();
                            BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(name);
                            if(bankAccount != null) {
                                if(bankAccount.getMembers().contains(p.getUniqueId())  || bankAccount.getOwners().contains(p.getUniqueId())
                                        || (bankAccount.getKey().startsWith("team-") && p.hasPermission("bank.log.teamaccounts"))) {
                                    int site;
                                    try {
                                        site = Integer.parseInt(args[2]);
                                    } catch(NumberFormatException e) {
                                        p.sendMessage(prefix + "§2Argument 3 §7muss eine postivite Ganzzahl sein.");
                                        return false;
                                    }
                                    if(site > 0) {
                                        BankLog bankLog = Economy.getInstance().getManager().getLogHandler().getOrGenerate(bankAccount.getKey());
                                        Map<String, Map<String, Object>> log = bankLog.getHistory();
                                        p.sendMessage("§7|-----| §2Transkationen §7|-----|");
                                        SimpleDateFormat formatDay = new SimpleDateFormat("dd.MM.yy");
                                        SimpleDateFormat formatHour = new SimpleDateFormat("HH:mm:ss");
                                        List<String> keySet = new ArrayList<>(log.keySet());
                                        Collections.sort(keySet);
                                        Collections.reverse(keySet);
                                        if(keySet.size() == 0) {
                                            p.sendMessage("§7Keine bisherigen Transaktionen gefunden.");
                                            return true;
                                        }
                                        if(keySet.stream().skip(5 * (site - 1)).limit(5).count() > 0) {
                                            keySet.stream().skip(5*(site - 1)).limit(5).forEach(l ->
                                                    p.sendMessage("§8» §2" + formatDay.format(new Date(Long.parseLong(l))) + " §8- §2" + formatHour.format(new Date(Long.parseLong(l))) + "§8: " +
                                                            (BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.ADMIN_GIVE) ? "§4Eingezahlt" :
                                                                    (BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.ADMIN_SET) ? "§4Gesetzt" :
                                                                            (BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.ADMIN_TAKE) ? "§4Ausgezahlt" :
                                                                                    (BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.DEPOSIT) ? "§aEingezahlt" :
                                                                                            (BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.WITHDRAW) ? "§cAusgezahlt" :
                                                                                                    (BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.TRANSFER_FROM) ? "§cÜberweisung" :
                                                                                                            (BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.TRANSFER_TO) ? "§aÜberweisung" : "")))))))
                                                            + " §8|| " + (bankAccount.getOwners().contains(Economy.getInstance().getManager().getPlayerManager().getByName((String) log.get(l).get("player")).getKey()) ? "§c" + log.get(l).get("player") :
                                                            (bankAccount.getMembers().contains(Economy.getInstance().getManager().getPlayerManager().getByName((String) log.get(l).get("player")).getKey()) ? "§6" + log.get(l).get("player") : "§4" + log.get(l).get("player")))
                                                            + " §8|| " + (BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.ADMIN_GIVE) ||
                                                            BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.DEPOSIT) ||
                                                            BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.TRANSFER_TO) ? "§a+" + formatValue(((Long) log.get(l).get("value")).doubleValue() / 100) :
                                                            BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.TRANSFER_FROM) ||
                                                                    BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.WITHDRAW) ||
                                                                    BankLogType.valueOf(((String) log.get(l).get("type"))).equals(BankLogType.ADMIN_TAKE) ? "§c-" + formatValue(((Long) log.get(l).get("value")).doubleValue() / 100) :
                                                                    "§7" + formatValue(((Long) log.get(l).get("value")).doubleValue() / 100) )));
                                        } else {
                                            p.sendMessage(prefix + "§7Keine weiteren Transaktionen gefunden.");
                                        }
                                    } else {
                                        p.sendMessage(prefix + "§2Argument 3 §7muss eine positive Ganzzahl sein.");
                                    }
                                } else {
                                    p.sendMessage(prefix + "§7Du bist nicht §6Teil§7- oder §4Inhaber §7des Kontos.");
                                }
                            } else {
                                p.sendMessage(prefix + "§7Das Bankkonto §2" + name + " §7existiert nicht.");
                            }
                        } else {
                            sendHelpMessage(p);
                        }
                    } else if(args.length == 4) {
                        if(args[0].equalsIgnoreCase("transfer")) {
                            String own = args[1].toLowerCase();
                            String to = args[2].toLowerCase();
                            BankAccount ownAccount = Economy.getInstance().getManager().getBankManager().get(own);
                            if(ownAccount != null) {
                                BankAccount toAccount = Economy.getInstance().getManager().getBankManager().get(to);
                                if(toAccount != null) {
                                    if(ownAccount.getMembers().contains(p.getUniqueId()) || ownAccount.getOwners().contains(p.getUniqueId())) {
                                        String arg = args[3];
                                        if(arg.contains(","))
                                            arg = arg.replace(",", ".");
                                        long value;
                                        try {
                                            if(!arg.equalsIgnoreCase("Infinity")) {
                                                if(arg.equalsIgnoreCase("all"))
                                                    value = ownAccount.getBalance();
                                                else
                                                    value = (long) (Double.parseDouble(arg) * 100);
                                            } else {
                                                p.sendMessage(prefix + "§7Du möchtest §2Infinity§7? Ok, du bekommst es:\n\n");
                                                p.sendMessage(prefix + "§7Der Kontostand des Kontos §2" + ownAccount.getKey() + " §7wurde durch §2CONSOLE §7auf §20A §7gesetzt.");
                                                return false;
                                            }
                                        } catch(NumberFormatException e) {
                                            p.sendMessage(prefix + "§2Argument 4 §7muss eine positive Zahl sein.");
                                            return false;
                                        }
                                        if(value > 0) {
                                            long taxes = (long) (value * 0.0025);
                                            if(taxes == 0)
                                                taxes = 1;
                                            long max = 9200000000000000000L;
                                            if(value >  max) {
                                                p.sendMessage(prefix + "§7Der Betrag ist zu hoch.");
                                                return false;
                                            }
                                            if(((ownAccount.getBalance() - value) - taxes) < 0) {
                                                if(!arg.equalsIgnoreCase("all")) {
                                                    p.sendMessage(prefix + "§7Du hast nicht genügend Geld auf deinem §2Konto§7. §8(Steuern: " + formatValue(((Long) taxes).doubleValue() / 100) + ")");
                                                    return false;
                                                }
                                            }
                                            if(arg.equalsIgnoreCase("all")) {
                                                ownAccount.withdrawMoney(value);
                                                toAccount.depositMoney(value - taxes);
                                            } else {
                                                ownAccount.withdrawMoney(value + taxes);
                                                toAccount.depositMoney(value);
                                            }
                                                Economy.getInstance().getManager().getBankManager().get("staatskasse").depositMoney(taxes);
                                                Economy.getInstance().getManager().getBankManager().save(ownAccount);
                                                Economy.getInstance().getManager().getBankManager().save(toAccount);
                                                Economy.getInstance().getManager().getBankManager().save(Economy.getInstance().getManager().getBankManager().get("staatskasse"));
                                                BankLog ownLog = Economy.getInstance().getManager().getLogHandler().getOrGenerate(ownAccount.getKey());
                                                ownLog.addHistory(System.currentTimeMillis(), BankLogType.TRANSFER_FROM, p.getName(), value);
                                                Economy.getInstance().getManager().getLogHandler().save(ownLog);
                                                BankLog toLog = Economy.getInstance().getManager().getLogHandler().getOrGenerate(toAccount.getKey());
                                                toLog.addHistory(System.currentTimeMillis(), BankLogType.TRANSFER_TO, p.getName(), value);
                                                Economy.getInstance().getManager().getLogHandler().save(toLog);
                                                p.sendMessage(prefix + "§7Du hast §2" + formatValue(((Long) value).doubleValue() / 100) + " §7auf das Konto §2" + to + " §7überwiesen und §2" + formatValue(((Long) taxes).doubleValue() / 100) + " §7Steuern gezahlt.");
                                                ownAccount.getOwners().forEach(owner -> {
                                                    Player t;
                                                    if((t = Bukkit.getPlayer(owner)) != null) {
                                                        t.sendMessage(prefix + "§2" + p.getName() + " §7hat §2" + formatValue(((Long) value).doubleValue() / 100) + " §7auf das Konto §2" + to + " §7überwiesen.");
                                                    }
                                                });
                                                ownAccount.getMembers().forEach(owner -> {
                                                    Player t;
                                                    if((t = Bukkit.getPlayer(owner)) != null) {
                                                        t.sendMessage(prefix + "§2" + p.getName() + " §7hat §2" + formatValue(((Long) value).doubleValue() / 100) + " §7auf das Konto §2" + to + " §7überwiesen.");
                                                    }
                                                });
                                                toAccount.getOwners().forEach(owner -> {
                                                    Player t;
                                                    if((t = Bukkit.getPlayer(owner)) != null) {
                                                        t.sendMessage(prefix + "§2" + p.getName() + " §7hat §2" + formatValue(((Long) value).doubleValue() / 100) + " §7auf eines deiner Konten überwiesen.");
                                                    }
                                                });
                                                toAccount.getMembers().forEach(owner -> {
                                                    Player t;
                                                    if((t = Bukkit.getPlayer(owner)) != null) {
                                                        t.sendMessage(prefix + "§2" + p.getName() + " §7hat §2" + formatValue(((Long) value).doubleValue() / 100) + " §7auf eines deiner Konten überwiesen.");
                                                    }
                                                });
                                        } else {
                                            p.sendMessage(prefix + "§2Argument 4 §7muss eine positive Zahl sein.");
                                        }
                                    } else {
                                        p.sendMessage(prefix + "§7Du hast keine Rechte auf dieses Konto.");
                                    }
                                } else {
                                    p.sendMessage(prefix + "Ein Konto mit dem Namen §2" + to + " §7konnte nicht gefunden werden.");
                                }
                            } else {
                                p.sendMessage(prefix + "Ein Konto mit dem Namen §2" + own + " §7konnte nicht gefunden werden.");
                            }
                        } else {
                            sendHelpMessage(p);
                        }
                    } else {
                        sendHelpMessage(p);
                    }
                } else {
                    s.sendMessage(prefix + "§7Du musst ein Spieler sein.");
                }
            }
        } else {
            sendHelpMsg(s);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if(args.length == 1) {
                List<String> list = Lists.newArrayList("create", "balance", "list", "delete", "addmember", "addowner", "removemember", "removeowner", "deposit", "withdraw", "log");
                if(p.hasPermission("bank.admin")) {
                    list.add("admin");
                }
                return Common.getInstance().removeAutoComplete(list, args[0]);
            } else if(args.length == 2) {
                switch (args[0].toLowerCase()) {
                    case "list":
                        return Common.getInstance().removeAutoComplete(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
                    case "balance":
                    case "dep":
                    case "with":
                    case "deposit":
                    case "withdraw":
                    case "transfer":
                    case "log":
                        return Common.getInstance().removeAutoComplete(Economy.getInstance().getManager().getBankManager().getBankAccounts(p.getUniqueId()).stream().map(BankAccount::getKey).collect(Collectors.toList()), args[1]);
                    case "delete":
                    case "addmember":
                    case "addm":
                    case "addowner":
                    case "addo":
                    case "removemember":
                    case "remm":
                    case "removeowner":
                    case "remo":
                        return Common.getInstance().removeAutoComplete(Economy.getInstance().getManager().getBankManager().getBankAccounts(p.getUniqueId()).stream().filter(acc -> acc.getOwners().contains(p.getUniqueId())).map(BankAccount::getKey).collect(Collectors.toList()), args[1]);
                    case "admin":
                        if(p.hasPermission("bank.admin"))
                            return Common.getInstance().removeAutoComplete(Lists.newArrayList("delete", "give", "take", "set", "addmember", "addowner", "removemember", "removeowner", "addas", "delas", "cancel"), args[1]);
                }
            } else if(args.length == 3) {
                switch(args[0].toLowerCase()) {
                    case "addmember":
                    case "addowner":
                    case "addm":
                    case "addo":
                        return Common.getInstance().removeAutoComplete(
                                Bukkit.getOnlinePlayers().stream().filter(all -> !Economy.getInstance().getManager().getBankManager().get(args[1].toLowerCase()).getOwners().contains(all.getUniqueId()) && !Economy.getInstance().getManager().getBankManager().get(args[1].toLowerCase()).getMembers().contains(all.getUniqueId()))
                                        .map(Player::getName).collect(Collectors.toList()),
                                args[2]
                        );
                    case "removemember":
                    case "remm":
                        return Common.getInstance().removeAutoComplete(
                                Bukkit.getOnlinePlayers().stream().filter(all -> Economy.getInstance().getManager().getBankManager().get(args[1].toLowerCase()).getMembers().contains(all.getUniqueId()))
                                        .map(Player::getName).collect(Collectors.toList()),
                                args[2]
                        );
                    case "removeowner":
                    case "remo":
                        return Common.getInstance().removeAutoComplete(
                                Bukkit.getOnlinePlayers().stream().filter(all -> Economy.getInstance().getManager().getBankManager().get(args[1].toLowerCase()).getOwners().contains(all.getUniqueId()))
                                        .map(Player::getName).collect(Collectors.toList()),
                                args[2]
                        );
                    case "transfer":
                        return Common.getInstance().removeAutoComplete(
                                Economy.getInstance().getManager().getBankManager().getBankAccounts(p.getUniqueId()).stream().map(BankAccount::getKey).collect(Collectors.toList()),
                                args[2]
                        );
                    case "dep":
                    case "with":
                    case "deposit":
                    case "withdraw":
                        return Common.getInstance().removeAutoComplete(Lists.newArrayList("all", "1000.0", "2500.0", "5000.0", "10000.0", "25000.0", "50000.0", "100000.0"), args[2]);
                    case "log":
                        return Lists.newArrayList("");
                }
                if(p.hasPermission("bank.admin")) {
                    switch(args[1].toLowerCase()) {
                        case "delete":
                        case "give":
                        case "take":
                        case "set":
                        case "addmember":
                        case "addm":
                        case "removemember":
                        case "remm":
                        case "addowner":
                        case "addo":
                        case "removeowner":
                        case "remo":
                            return Common.getInstance().removeAutoComplete(Economy.getInstance().getManager().getBankManager().getByType(BankType.BANK).stream().map(BankAccount::getKey).collect(Collectors.toList()), args[2]);
                    }
                }
            } else if(args.length == 4) {
                if(p.hasPermission("bank.admin")) {
                    switch(args[1].toLowerCase()) {
                        case "addmember":
                        case "addowner":
                        case "addm":
                        case "addo":
                            return Common.getInstance().removeAutoComplete(Bukkit.getOnlinePlayers().stream().filter(all -> !Economy.getInstance().getManager().getBankManager().get(args[2].toLowerCase()).getOwners().contains(all.getUniqueId()) && !Economy.getInstance().getManager().getBankManager().get(args[2].toLowerCase()).getMembers().contains(all.getUniqueId()))
                                    .map(Player::getName).collect(Collectors.toList()), args[3]);
                        case "removemember":
                        case "remm":
                            return Common.getInstance().removeAutoComplete(Bukkit.getOnlinePlayers().stream().filter(all -> Economy.getInstance().getManager().getBankManager().get(args[2].toLowerCase()).getMembers().contains(all.getUniqueId()))
                                    .map(Player::getName).collect(Collectors.toList()), args[3]);
                        case "removeowner":
                        case "remo":
                            return Common.getInstance().removeAutoComplete(Bukkit.getOnlinePlayers().stream().filter(all -> Economy.getInstance().getManager().getBankManager().get(args[2].toLowerCase()).getOwners().contains(all.getUniqueId()))
                                    .map(Player::getName).collect(Collectors.toList()), args[3]);
                    }
                }
            }
        }
        return Collections.emptyList();
    }
}
