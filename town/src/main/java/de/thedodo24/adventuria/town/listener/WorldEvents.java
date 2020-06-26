package de.thedodo24.adventuria.town.listener;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import de.thedodo24.adventuria.town.Towny;
import de.thedodo24.commonPackage.player.User;
import de.thedodo24.commonPackage.towny.Plot;
import de.thedodo24.commonPackage.towny.PlotPlayer;
import de.thedodo24.commonPackage.towny.TownPermission;
import de.thedodo24.commonPackage.towny.TownRank;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class WorldEvents implements Listener {

    private String prefix =  "§7§l| §6Städte §7» ";

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
        if(!p.hasPermission("towny.admin.plot.bypass") && (user.checkTownMember() && user.getTownRank().equals(TownRank.CITIZEN))) {
            Plot plot = Towny.getInstance().getManager().getPlotManager().get(String.valueOf(e.getBlock().getLocation().getChunk().getChunkKey()));
            if (plot != null) {
                if (!(plot.isOwned() && plot.getOwner().equals(p.getUniqueId()))) {
                    PlotPlayer townPlayer = PlotPlayer.getTownPlayer(user, plot);
                    boolean cancel = plot.getPermission(TownPermission.BUILD, townPlayer);
                    e.setCancelled(!cancel);
                    if (!cancel)
                        p.sendMessage(prefix + "Dir ist es als §6" + townPlayer.getDisplayName() + " §7nicht erlaubt zu §6" + TownPermission.BUILD.getDisplayName() + "§7.");
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
        if(!p.hasPermission("towny.admin.plot.bypass") && (user.checkTownMember() && user.getTownRank().equals(TownRank.CITIZEN))) {
            Plot plot = Towny.getInstance().getManager().getPlotManager().get(String.valueOf(e.getBlock().getLocation().getChunk().getChunkKey()));
            if (plot != null) {
                if (!(plot.isOwned() && plot.getOwner().equals(p.getUniqueId()))) {
                    PlotPlayer townPlayer = PlotPlayer.getTownPlayer(user, plot);
                    boolean cancel = plot.getPermission(TownPermission.DESTROY, townPlayer);
                    e.setCancelled(!cancel);
                    if (!cancel)
                        p.sendMessage(prefix + "Dir ist es als §6" + townPlayer.getDisplayName() + " §7nicht erlaubt zu §6" + TownPermission.DESTROY.getDisplayName() + "§7.");
                }
            }
        }
    }

}
