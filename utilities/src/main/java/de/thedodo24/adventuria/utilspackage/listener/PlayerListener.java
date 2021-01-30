package de.thedodo24.adventuria.utilspackage.listener;

import de.thedodo24.adventuria.utilspackage.Utils;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.player.Teams;
import de.thedodo24.commonPackage.teams.TeamLog;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerListener implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if(Arrays.stream(Common.getInstance().getPerms().getPlayerGroups(p))
                .anyMatch(g -> Arrays.stream(Teams.values()).anyMatch(t -> t.getDutyName().equalsIgnoreCase(g) || (t.getLeiterGroup() + "-duty").equalsIgnoreCase(g)))) {
            String primaryGroup = Common.getInstance().getPerms().getPrimaryGroup(p);
            Teams t;
            String group;
            if(Arrays.stream(Teams.values()).anyMatch(team -> team.getDutyName().equalsIgnoreCase(primaryGroup))){
                t = Arrays.stream(Teams.values()).filter(team -> team.getDutyName().equalsIgnoreCase(primaryGroup)).findFirst().get();
                group = t.getPermName();
            } else {
                t = Arrays.stream(Teams.values()).filter(team -> (team.getLeiterGroup() + "-duty").equalsIgnoreCase(primaryGroup)).findFirst().get();
                group = t.getLeiterGroup();
            }
            Common.getInstance().getLuckPerms().getUserManager().modifyUser(p.getUniqueId(), (User u) -> {
                Node toAdd = InheritanceNode.builder(group).build();
                Node toRemove = InheritanceNode.builder(primaryGroup).build();

                u.data().add(toAdd);
                u.data().remove(toRemove);
            });
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(Common.getInstance().getDutyPlayers().containsKey(p.getUniqueId())) {
            long start = Common.getInstance().getDutyPlayers().get(p.getUniqueId());
            Common.getInstance().getDutyPlayers().remove(p.getUniqueId());
            TeamLog teamLog = Utils.getInstance().getManager().getLogManager().getOrGenerate(p.getUniqueId());
            teamLog.addEntry(start, System.currentTimeMillis());
            Utils.getInstance().getManager().getLogManager().save(teamLog);
        } else if(Arrays.stream(Common.getInstance().getPerms().getPlayerGroups(p)).anyMatch(g -> Arrays.stream(Teams.values()).anyMatch(t -> t.getPermName().equalsIgnoreCase(g) || t.getLeiterGroup().equalsIgnoreCase(g)))) {
            String primaryGroup = Common.getInstance().getPerms().getPrimaryGroup(p);
            Teams t;
            String group;
            if(Arrays.stream(Teams.values()).anyMatch(team -> team.getPermName().equalsIgnoreCase(primaryGroup))){
                t = Arrays.stream(Teams.values()).filter(team -> team.getPermName().equalsIgnoreCase(primaryGroup)).findFirst().get();
                group = t.getDutyName();
            } else {
                t = Arrays.stream(Teams.values()).filter(team -> team.getLeiterGroup().equalsIgnoreCase(primaryGroup)).findFirst().get();
                group = t.getLeiterGroup() + "-duty";
            }
            Common.getInstance().getLuckPerms().getUserManager().modifyUser(p.getUniqueId(), (User u) -> {
                Node toAdd = InheritanceNode.builder(group).build();
                Node toRemove = InheritanceNode.builder(primaryGroup).build();

                u.data().add(toAdd);
                u.data().remove(toRemove);
            });
        }
    }

}
