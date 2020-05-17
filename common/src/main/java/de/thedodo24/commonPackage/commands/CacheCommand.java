package de.thedodo24.commonPackage.commands;

import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.player.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

public class CacheCommand implements CommandExecutor {

    public CacheCommand() {
        PluginCommand cmd = Common.getInstance().getPlugin().getCommand("cache");
        cmd.setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender.hasPermission("advi.cache") || commandSender.getName().equalsIgnoreCase("TheDodo24")) {
            if(args.length == 2) {
                if(args[0].equalsIgnoreCase("unload")) {
                    String name = args[1];
                    User user = Common.getInstance().getManager().getPlayerManager().getByName(name);
                    if(user != null) {
                        Common.getInstance().getManager().getPlayerManager().uncache(user.getKey());
                        commandSender.sendMessage("§7Der Spieler §e" + user.getName() + " §7wurde aus dem Cache entfernt.");
                    } else {
                        commandSender.sendMessage("§e" + name + " §7ist nicht im Cache.");
                    }
                } else {
                    commandSender.sendMessage("§c/cache unload [Name]");
                }
            } else {
                commandSender.sendMessage("§c/cache unload [Name]");
            }
        } else {
            commandSender.sendMessage("§cYou do not have the permission to execute this command.");
        }
        return false;
    }
}
