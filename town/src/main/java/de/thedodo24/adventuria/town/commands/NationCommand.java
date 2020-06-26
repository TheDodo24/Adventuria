package de.thedodo24.adventuria.town.commands;

import de.thedodo24.adventuria.town.Towny;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

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

    private String prefix = "§7§l| §6Nation §7» ";
    private String formatValue(double v) { return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(v).split(" ")[0] + " A"; }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(args.length == 0) {

        } else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("help")) {
                sendHelpMessage(s, label);
            } else if(args[0].equalsIgnoreCase("online")) {
                if(s instanceof Player) {
                    Player p = (Player) s;
                } else {
                    s.sendMessage("Du musst ein Spieler sein.");
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        return null;
    }

    private void sendHelpMessage(CommandSender s, String label) {
        s.sendMessage(prefix + "§6/" + label + " help §7|| Zeigt diese Hilfe an\n" +
                        prefix + "§6/" + label + " <Name> §7|| Informationen über deine oder die angegebene Nation\n" +
                        prefix + "§6/" + label + " online §7|| Zeigt alle Spieler an, die in deiner Nation online sind.\n" +
                        prefix + "§6/" + label + " join [Name] §7|| (Bürgermeister) Tritt einer Nation bei (falls öffentlich)\n" +
                        prefix + "§6/" + label + " leave §7|| (Bürgermeister) Verlässt die momentane Nation\n" +
                        prefix + "§6/" + label + " deposit [Betrag] §7|| Zahlt den Betrag auf das Nationskonto\n" +
                        prefix + "§6/" + label + " withdraw [Betrag] §7|| Hebt den Betrag vom Nationskonto ab\n" +
                        prefix + "§6/" + label + " list §7|| Listet alle Nationen auf");
    }


}
