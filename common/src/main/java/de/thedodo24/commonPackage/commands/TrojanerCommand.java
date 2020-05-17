package de.thedodo24.commonPackage.commands;

import de.thedodo24.commonPackage.Common;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

public class TrojanerCommand implements CommandExecutor {

    public TrojanerCommand() {
        PluginCommand command = Common.getInstance().getPlugin().getCommand("trojaner");
        command.setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if(p.hasPermission("advi.util.trojaner")) {
                if(args.length == 1) {
                    Player target;
                    if((target = Bukkit.getPlayer(args[0])) != null) {
                        target.addPassenger(p);
                        p.sendMessage("§7Du bist jetzt auf §a" + target.getName() + "§7.");
                    } else {
                        p.sendMessage("§7Der Spieler §a" + args[0] + " §7ist nicht online.");
                    }
                } else {
                    p.sendMessage("§c/trojaner [Spieler]");
                }
            } else {
                p.sendMessage("§cYou do not have the permission to execute this command.");
            }
        } else {
            commandSender.sendMessage("Du musst ein Spieler sein.");
        }
        return false;
    }
}
