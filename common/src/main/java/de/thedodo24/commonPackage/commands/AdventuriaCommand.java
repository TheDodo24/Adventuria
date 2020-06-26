package de.thedodo24.commonPackage.commands;

import de.thedodo24.commonPackage.Common;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.concurrent.Executors;

public class AdventuriaCommand implements CommandExecutor, TabCompleter {

    public AdventuriaCommand() {
        PluginCommand cmd = Common.getInstance().getPlugin().getCommand("adventuria");
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
    }

    private String prefix = "§7§l| §aAdventuria §7» ";

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("reload")) {
                if(s.hasPermission("advi.manage")) {
                        Plugin advi = Common.getInstance().getPlugin();
                        Bukkit.getPluginManager().disablePlugin(advi);
                        Bukkit.getPluginManager().enablePlugin(advi);
                        s.sendMessage(prefix + "§7Das Plugin wurde erfolgreich neu geladen.");
                } else {
                    s.sendMessage(prefix + "§cYou do not have the permission to execute this command. (advi.manage)");
                }
            } else {
                s.sendMessage(prefix + "§a/adventuria reload");
            }
        } else {
            s.sendMessage(prefix + "§a/adventuria reload");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        return null;
    }
}
