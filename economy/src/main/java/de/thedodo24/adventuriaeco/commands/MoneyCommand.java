package de.thedodo24.adventuriaeco.commands;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import de.thedodo24.adventuriaeco.Economy;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.player.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MoneyCommand implements CommandExecutor, TabCompleter {


    private String prefix = "§7§l| §aGeld §7» ";

    private void sendHelpMessage(CommandSender p) {
        TextComponent payHelp = new TextComponent(prefix);
        TextComponent payHelpCommand = new TextComponent("/money pay [Name] [Betrag]");
        TextComponent payHelpInfo = new TextComponent(" | Überweist einem Spieler Geld");
        payHelpCommand.setColor(ChatColor.GREEN);
        payHelpCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/money pay "));
        payHelpCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/money pay ").create()));
        payHelpInfo.setColor(ChatColor.GRAY);
        payHelp.addExtra(payHelpCommand);
        payHelp.addExtra(payHelpInfo);
        TextComponent balanceHelp = new TextComponent(prefix);
        TextComponent balanceHelpCommand = new TextComponent("/money balance <[Name]>");
        TextComponent balanceHelpInfo = new TextComponent(" | Zeigt den aktuellen Kontostand");
        balanceHelpCommand.setColor(ChatColor.GREEN);
        balanceHelpCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/money balance "));
        balanceHelpCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/money balance ").create()));
        balanceHelpInfo.setColor(ChatColor.GRAY);
        balanceHelp.addExtra(balanceHelpCommand);
        balanceHelp.addExtra(balanceHelpInfo);


        TextComponent takeHelp = new TextComponent(prefix);
        TextComponent takeHelpCommmand = new TextComponent("/money take [Name] [Betrag]");
        TextComponent takeHelpInfo = new TextComponent(" | Nimmt einem Spieler einen Betrag");
        takeHelpCommmand.setColor(ChatColor.GREEN);
        takeHelpCommmand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/money take "));
        takeHelpCommmand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/money take ").create()));
        takeHelpInfo.setColor(ChatColor.GRAY);
        takeHelp.addExtra(takeHelpCommmand);
        takeHelp.addExtra(takeHelpInfo);

        TextComponent giveHelp = new TextComponent(prefix);
        TextComponent giveHelpCommand = new TextComponent("/money give [Name] [Betrag]");
        TextComponent giveHelpInfo = new TextComponent(" | Gibt einem Spieler einen Betrag");
        giveHelpCommand.setColor(ChatColor.GREEN);
        giveHelpCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/money give "));
        giveHelpCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/money give ").create()));
        giveHelpInfo.setColor(ChatColor.GRAY);
        giveHelp.addExtra(giveHelpCommand);
        giveHelp.addExtra(giveHelpInfo);

        TextComponent setHelp = new TextComponent(prefix);
        TextComponent setHelpCommand = new TextComponent("/money set [Name] [Betrag]");
        TextComponent setHelpInfo = new TextComponent(" | Setzt einem Spieler einen Betrag");
        setHelpCommand.setColor(ChatColor.GREEN);
        setHelpCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/money set "));
        setHelpCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/money set ").create()));
        setHelpInfo.setColor(ChatColor.GRAY);
        setHelp.addExtra(setHelpCommand);
        setHelp.addExtra(setHelpInfo);
        if(p instanceof Player) {
            Player a = (Player) p;
            a.spigot().sendMessage(payHelp);
            a.spigot().sendMessage(balanceHelp);
            if(a.hasPermission("money.admin")) {
                a.sendMessage("\n");
                a.spigot().sendMessage(giveHelp);
                a.spigot().sendMessage(takeHelp);
                a.spigot().sendMessage(setHelp);
            }
        } else {
            p.sendMessage("/money give [Spieler] [Betrag]\n" +
                    "/money take [Spieler] [Betrag]\n" +
                    "/money set [Spieler] [Betrag]\n" +
                    "/money balance [Spieler]");
        }
    }

    private String noPerm(String perm) { return "§cYou do not have the permissions to execute this command. (" + perm + ")"; }

    private String formatValue(double v) { return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(v).split(" ")[0] + " A"; }

    private String format(long v) { return formatValue(((Long) v).doubleValue() / 100); }

    public MoneyCommand() {
        PluginCommand cmd = Economy.getInstance().getPlugin().getCommand("money");
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
    }


    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(args.length == 1) {
            if(s instanceof Player) {
                Player p = (Player) s;
                if(args[0].equalsIgnoreCase("balance") || args[0].equalsIgnoreCase("bal")) {
                    String money = format(Economy.getInstance().getManager().getPlayerManager().get(p.getUniqueId()).getBalance());
                    p.sendMessage(prefix + "§7Dein Kontostand beträgt §a" + money);
                } else {
                    sendHelpMessage(p);
                }
            } else {
                s.sendMessage("[Adventuria] Du musst ein Spieler sein.");
            }
        } else if(args.length == 2) {
            if(args[0].equalsIgnoreCase("balance") || args[0].equalsIgnoreCase("bal")) {
                if(args[1].equalsIgnoreCase("staatskasse")) {
                    s.sendMessage(prefix + "Der Kontostand der §aStaatskasse §7beträgt §a" + format(Economy.getInstance().getManager().getBankManager().get("staatskasse").getBalance()));
                    return true;
                }
                User m = Economy.getInstance().getManager().getPlayerManager().getByName(args[1]);
                if(m != null) {
                    s.sendMessage(prefix + "Der Kontostand von §a" + m.getName() + " §7beträgt §a" + format(m.getBalance()));
                } else {
                    s.sendMessage(prefix + "Der Spieler §a" + args[1] + " §7existiert nicht.");
                }
            } else {
                sendHelpMessage(s);
            }
        } else if(args.length == 3) {
            if(args[0].equalsIgnoreCase("pay")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                        User pMoney = Economy.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                        String arg = args[2];
                        if(arg.contains(","))
                            arg = arg.replace(",", ".");
                        long value;
                        try {
                            value = (long) (Double.parseDouble(arg) * 100);
                        } catch(NumberFormatException e) {
                            s.sendMessage(prefix + "§aArgument 2 §7muss eine positive Zahl sein. §8(" + args[2] + ")");
                            return false;
                        }
                        if(value > 0) {
                            if((pMoney.getBalance() - value) >= 0) {
                                String name = "";
                                if(args[1].equalsIgnoreCase("staatskasse")) {
                                    Economy.getInstance().getManager().getBankManager().get("staatskasse").depositMoney(value);
                                    name = "Staatskasse";
                                } else {
                                    User oMoney = Economy.getInstance().getManager().getPlayerManager().getByName(args[1]);
                                    if(oMoney != null) {
                                        if(!oMoney.getKey().equals(p.getUniqueId())) {
                                            oMoney.depositMoney(value);
                                            name = oMoney.getName();
                                            Player other;
                                            if((other = Bukkit.getPlayer(oMoney.getKey())) != null) {
                                                other.sendMessage(prefix + "§a" + p.getName() + " §7hat dir §a" + formatValue(((Long) value).doubleValue() / 100) + " §7überwiesen.");
                                            }
                                        } else {
                                            p.sendMessage(prefix + "§7Du kannst dir nicht selbst Geld überweisen.");
                                            return false;
                                        }
                                    } else {
                                        p.sendMessage(prefix + "§7Der Spieler §a" + args[1] + " §7existiert nicht.");
                                        return false;
                                    }
                                }
                                pMoney.withdrawMoney(value);
                                p.sendMessage(prefix + "§7Du hast §a" + name + " " + formatValue(((Long) value).doubleValue() / 100) + " §7überwiesen.");
                            } else {
                                p.sendMessage(prefix + "§7Du hast nicht genügend Geld.");
                            }
                        } else {
                            p.sendMessage(prefix + "§aArgument 2 §7muss eine positive Zahl sein. §8(" + args[2] +")");
                        }
                } else {
                    s.sendMessage("[Adventuria] Du musst ein Spieler sein.");
                }
            } else if(args[0].equalsIgnoreCase("give")) {
                if(s.hasPermission("money.give")) {
                        String arg = args[2];
                        if(arg.contains(","))
                            arg = arg.replace(",", ".");
                        long value;
                        try {
                            value = (long) (Double.parseDouble(arg) * 100);
                        } catch(NumberFormatException e) {
                            s.sendMessage(prefix + "§aArgument 2 §7muss eine positive Zahl sein. §8(" + args[2] + ")");
                            return false;
                        }
                        if(value > 0) {
                            String name = "";
                            if(args[1].equalsIgnoreCase("staatskasse")) {
                                Economy.getInstance().getManager().getBankManager().get("staatskasse").depositMoney(value);
                                name = "Staatskasse";
                            } else {
                                User oMoney = Economy.getInstance().getManager().getPlayerManager().getByName(args[1]);
                                if(oMoney != null) {
                                    oMoney.depositMoney(value);
                                    name = oMoney.getName();
                                    Player other;
                                    if((other = Bukkit.getPlayer(oMoney.getKey())) != null) {
                                        other.sendMessage(prefix + "§a" + s.getName() + " §7hat dir §a" + formatValue(((Long) value).doubleValue() / 100) + " §7gegeben.");
                                    }
                                } else {
                                    s.sendMessage(prefix + "§7Der Spieler §a" + args[1] + " §7existiert nicht.");
                                    return false;
                                }
                            }
                            s.sendMessage(prefix + "Du hast §a" + name + " " + formatValue(((Long) value).doubleValue() / 100) + " §7gegeben.");
                            String finalName = name;
                            Bukkit.getOnlinePlayers().forEach(all -> {
                                if(all.hasPermission("money.notify"))
                                    all.sendMessage(prefix + "§a" + s.getName() + " §7hat §a" + finalName + " §7" + formatValue(((Long) value).doubleValue() / 100) + " §7gegeben.");
                            });
                        } else {
                            s.sendMessage(prefix + "§aArgument 2 §7muss eine positive Zahl sein. §8(" + args[2] +")");
                        }
                } else {
                    s.sendMessage(noPerm("money.give"));
                }
            } else if(args[0].equalsIgnoreCase("set")) {
                if(s.hasPermission("money.set")) {
                        String arg = args[2];
                        if(arg.contains(","))
                            arg = arg.replace(",", ".");
                        long value;
                        try {
                            value = (long) (Double.parseDouble(arg) * 100);
                        } catch(NumberFormatException e) {
                            s.sendMessage(prefix + "§aArgument 2 §7muss eine positive Zahl sein. §8(" + args[2] + ")");
                            return false;
                        }
                        if(value >= 0) {
                            String name = "";
                            if(args[1].equalsIgnoreCase("staatskasse")) {
                                name = "Staatskasse";
                                Economy.getInstance().getManager().getBankManager().get("staatskasse").setBalance(value);
                            } else {
                                User oMoney = Economy.getInstance().getManager().getPlayerManager().getByName(args[1]);
                                if(oMoney != null) {
                                    oMoney.setBalance(value);
                                    name = oMoney.getName();
                                    Player other;
                                    if((other = Bukkit.getPlayer(oMoney.getKey())) != null) {
                                        other.sendMessage(prefix + "§a" + s.getName() + " §7hat deinen Kontostand auf §a" + formatValue(((Long) value).doubleValue() / 100) + " §7gesetzt.");
                                    }
                                } else {
                                    s.sendMessage(prefix + "§7Der Spieler §a" + args[1] + " §7existiert nicht.");
                                }
                            }
                            s.sendMessage(prefix + "Du hast den Kontostand von §a" + name + " §7auf§a " + formatValue(((Long) value).doubleValue() / 100) + " §7gesetzt.");
                            String finalName = name;
                            Bukkit.getOnlinePlayers().forEach(all -> {
                                if(all.hasPermission("money.notify"))
                                    all.sendMessage(prefix + "§a" + s.getName() + " §7hat den Kontostand von §a" + finalName + "§7 auf §a" + formatValue(((Long) value).doubleValue() / 100) + " §7gesetzt.");
                            });
                        } else {
                            s.sendMessage(prefix + "§aArgument 2 §7muss eine positive Zahl sein. §8(" + args[2] +")");
                        }
                } else {
                    s.sendMessage(noPerm("money.set"));
                }
            } else if(args[0].equalsIgnoreCase("take")) {
                if(s.hasPermission("money.take")) {
                        String arg = args[2];
                        if(arg.contains(","))
                            arg = arg.replace(",", ".");
                        long value;
                        try {
                            value = (long) (Double.parseDouble(arg) * 100);
                        } catch(NumberFormatException e) {
                            s.sendMessage(prefix + "§aArgument 2 §7muss eine positive Zahl sein. §8(" + args[2] + ")");
                            return false;
                        }
                        if(value >= 0) {
                            String name = "";
                            if(args[1].equalsIgnoreCase("staatskasse")) {
                                name = "Staatskasse";
                                Economy.getInstance().getManager().getBankManager().get("staatskasse").withdrawMoney(value);
                            } else {
                                User oMoney = Economy.getInstance().getManager().getPlayerManager().getByName(args[1]);
                                if(oMoney != null) {
                                    name = oMoney.getName();
                                    oMoney.withdrawMoney(value);
                                    Player other;
                                    if((other = Bukkit.getPlayer(oMoney.getKey())) != null) {
                                        other.sendMessage(prefix + "§a" + s.getName() + " §7hat dir §a" + formatValue(((Long) value).doubleValue() / 100) + " §7genommen.");
                                    }
                                } else {
                                    s.sendMessage(prefix + "§7Der Spieler §a" + args[1] + " §7existiert nicht.");
                                }

                            }
                            s.sendMessage(prefix + "Du hast §a" + name + " " + formatValue(((Long) value).doubleValue() / 100) + " §7genommen.");
                            String finalName = name;
                            Bukkit.getOnlinePlayers().forEach(all -> {
                                if(all.hasPermission("money.notify"))
                                    all.sendMessage(prefix + "§a" + s.getName() + " §7hat §a" + finalName + " §7" + formatValue(((Long) value).doubleValue() / 100) + " §7genommen.");
                            });
                        } else {
                            s.sendMessage(prefix + "§aArgument 2 §7muss eine positive Zahl sein. §8(" + args[2] +")");
                        }
                } else {
                    s.sendMessage(noPerm("money.take"));
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
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if(args.length == 1) {
                List<String> r = Lists.newArrayList("pay", "balance");
                if(p.hasPermission("money.admin")) {
                    r.add("give");
                    r.add("take");
                    r.add("set");
                }
                return Common.getInstance().removeAutoComplete(r, args[0]);
            } else if(args.length == 2) {
                switch(args[0].toLowerCase()) {
                    case "pay":
                    case "balance":
                    case "bal":
                        return Common.getInstance().removeAutoComplete(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
                    case "give":
                    case "take":
                    case "set":
                        if(p.hasPermission("money.admin")) {
                            return Common.getInstance().removeAutoComplete(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
                        }
                }
            }
        }
        return Collections.emptyList();
    }
}
