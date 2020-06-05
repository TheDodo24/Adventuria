package de.thedodo24.adventuria.utilspackage.commands;

import de.thedodo24.adventuria.utilspackage.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public class TPFarmweltCommand implements CommandExecutor {

    private String prefix = "§7§l| §aAdventuria §7» ";

    public TPFarmweltCommand() {
        PluginCommand cmd = Utils.getInstance().getPlugin().getCommand("tpfarmwelt");
        cmd.setExecutor(this);
    }


    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(s instanceof ConsoleCommandSender) {
            if(args.length == 1) {
                String name = args[0];
                Player toTeleport;
                if((toTeleport = Bukkit.getPlayer(name)) != null) {
                    if(toTeleport.getWorld().getName().startsWith("Farmwelt")) {
                        int x = ThreadLocalRandom.current().nextInt(-10000, 10000);
                        int z = ThreadLocalRandom.current().nextInt(-10000, 10000);
                        int highestBlock = toTeleport.getWorld().getHighestBlockYAt(x, z) + 1;
                        toTeleport.teleport(new Location(toTeleport.getWorld(), x, highestBlock, z));
                    }
                } else {
                    s.sendMessage(prefix + "§7Dieser Spieler ist nicht online.");
                }
            } else {
                s.sendMessage(prefix + "§a/tpfarmwelt [Spieler]");
            }
        } else {
            s.sendMessage(prefix + "§cYou do not have the permission to execute this command.");
        }
        return false;
    }
}
