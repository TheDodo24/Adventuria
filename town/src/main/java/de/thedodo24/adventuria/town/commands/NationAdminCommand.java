package de.thedodo24.adventuria.town.commands;

import de.thedodo24.adventuria.town.Towny;
import de.thedodo24.commonPackage.towny.Nation;
import org.bukkit.Bukkit;
import org.bukkit.command.*;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

public class NationAdminCommand implements CommandExecutor, TabCompleter {

    public NationAdminCommand() {
        PluginCommand longCmd = Towny.getInstance().getPlugin().getCommand("nationadmin");
        PluginCommand shortCmd = Towny.getInstance().getPlugin().getCommand("na");
        longCmd.setExecutor(this);
        longCmd.setTabCompleter(this);
        shortCmd.setExecutor(this);
        shortCmd.setTabCompleter(this);
    }

    /*
    nation
        new
        rename
        delete
        deposit
        withdraw
     set
        king
        public
     */

    private String prefix = "§7§l| §cNationen §7» ";
    private String noPerm(String perm) { return "§cYou do not have the permissions to execute this command. (" + perm + ")"; }
    private String formatValue(double v) { return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(v).split(" ")[0] + " A"; }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("nation"))
                sendNationHelpMessage(s, label);
            else if(args[0].equalsIgnoreCase("set"))
                sendSetHelpMessage(s, label);
            else
                sendHelpMessage(s, label);
        } else if(args.length == 2) {
            if(args[0].equalsIgnoreCase("nation"))
                sendNationHelpMessage(s, label);
            else if(args[0].equalsIgnoreCase("set"))
                sendSetHelpMessage(s, label);
            else
                sendHelpMessage(s, label);
        } else if(args.length == 3) {
            if(args[0].equalsIgnoreCase("nation") && args[1].equalsIgnoreCase("delete")) {
                if(s.hasPermission("nation.admin.nation.delete")) {
                    String name = args[1].toLowerCase();
                    Nation nation = Towny.getInstance().getManager().getNationManager().get(name);
                    if(nation != null) {
                        Towny.getInstance().getManager().getNationManager().delete(name);
                        Towny.getInstance().getPlugin().getLogger().log(Level.INFO, "Nation " + name + " was deleted");
                        String finalName = nation.getName();
                        Bukkit.getOnlinePlayers().forEach(all -> all.sendMessage(prefix + "§7Die Nation §c" + finalName + " §7wurde aufgelöst."));
                    } else {
                        s.sendMessage(prefix + "§7Die Nation §c" + name + " §7existiert nicht.");
                    }
                } else {
                    s.sendMessage(noPerm("nation.admin.nation.delete"));
                }
            } else {
                if(args[0].equalsIgnoreCase("nation"))
                    sendNationHelpMessage(s, label);
                else if(args[0].equalsIgnoreCase("set"))
                    sendSetHelpMessage(s, label);
                else
                    sendHelpMessage(s, label);
            }
        } else if(args.length == 4) {
            if(args[0].equalsIgnoreCase("nation")) {
                if(args[1].equalsIgnoreCase("new")) {
                    if(s.hasPermission("nation.admin.nation.new")) {
                        String name = args[2];
                        if(Towny.getInstance().getManager().getNationManager().get(name.toLowerCase()) == null) {
                            Nation nation = Towny.getInstance().getManager().getNationManager().getOrGenerate(name.toLowerCase());
                        } else {
                            s.sendMessage(prefix + "§7Die Nation §c" + name + " §7existiert bereits.");
                        }
                    } else {
                        s.sendMessage(noPerm("nation.admin.nation.new"));
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        return null;
    }

    private void sendHelpMessage(CommandSender s, String label) {
        s.sendMessage(prefix + "§c/" + label + " §7|| Zeigt diese Hilfe\n" +
                        prefix + "§c/" + label + " nation §7|| Nation Admin Befehlsübersicht\n" +
                        prefix + "§c/" + label + " set §7|| Listet die Möglichkeiten Attribute zu setzen auf");
    }

    private void sendNationHelpMessage(CommandSender s, String label) {
        s.sendMessage(prefix + "§4/" + label + " nation §cnew [Name] [König] §7|| Erstellt eine neue Nation\n" +
                        prefix + "§4/" + label + " nation §crename [Nation] [Neuer Name] §7|| Benennt eine Nation um\n" +
                        prefix + "§4/" + label + " nation §cdelete [Nation] §7|| Löscht eine Nation\n" +
                        prefix + "§4/" + label + " nation §cdeposit [Nation] [Betrag] §7|| Überweist Geld auf die Nationskasse\n" +
                        prefix + "§4/" + label + " nation §cwithdraw [Nation] [Betrag] §7|| Hebt Geld von der Nationskasse ab");
    }

    private void sendSetHelpMessage(CommandSender s, String label) {
        s.sendMessage(prefix + "§4/" + label + " set §cking [Nation] [Spieler] §7|| Setzt einen neuen König\n" +
                        prefix + "§4/" + label + " set §cpublic [Nation] [true/false] §7|| Setzt den Öffentlichkeitsstatus der Nation");
    }
}
