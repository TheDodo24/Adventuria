package de.thedodo24.adventuria.town.commands;

import com.google.common.collect.Lists;
import de.thedodo24.adventuria.town.Towny;
import de.thedodo24.commonPackage.player.User;
import de.thedodo24.commonPackage.towny.TownRank;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TownChatCommand implements CommandExecutor, TabCompleter {

    public TownChatCommand() {
        PluginCommand cmdLong = Towny.getInstance().getPlugin().getCommand("townchat");
        PluginCommand cmdShort = Towny.getInstance().getPlugin().getCommand("tc");
        cmdLong.setExecutor(this);
        cmdLong.setTabCompleter(this);
        cmdShort.setTabCompleter(this);
        cmdShort.setExecutor(this);
    }

    private String prefix = "§7§l| §6Städte §7» ";

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(s instanceof Player) {
            Player p = (Player) s;
            if(args.length > 0) {
                User u = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                if(u.checkTownMember()) {
                    String msg = String.join(" ", args);
                    String prefix;
                    if(u.getTownRank().equals(TownRank.MAYOR))
                        prefix = "§cBürgermeister §8● §c" + p.getName() + " §8» ";
                    else if(u.getTownRank().equals(TownRank.ASSISTANT))
                        prefix = "§2Stadthalter §8● §2" + p.getName() + " §8» ";
                    else if(u.getTownRank().equals(TownRank.HELPER))
                        prefix = "§9Stadtbauer §8● §9" + p.getName() + " §8» ";
                    else
                        prefix = "§6Bürger §8● §6" + p.getName() + " §8» ";
                    Towny.getInstance().getManager().getPlayerManager().getResidents(u.getTown())
                            .stream().filter(user -> Bukkit.getPlayer(user.getKey()) != null)
                            .forEach(user -> Bukkit.getPlayer(user.getKey()).sendMessage("§6[" + u.getTown().getName() + "] " + prefix + "§7" + msg));
                } else {
                    p.sendMessage(prefix + "§7Du bist in keiner Stadt.");
                }
            } else {
                p.sendMessage(prefix + "§6/" + label + " [Text]");
            }
        } else {
            s.sendMessage("Du musst ein Spieler sein.");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        return Lists.newArrayList();
    }
}
