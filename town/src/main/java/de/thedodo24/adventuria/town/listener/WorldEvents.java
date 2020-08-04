package de.thedodo24.adventuria.town.listener;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import de.thedodo24.adventuria.town.Towny;
import de.thedodo24.commonPackage.player.User;
import de.thedodo24.commonPackage.towny.Plot;
import de.thedodo24.commonPackage.towny.PlotPlayer;
import de.thedodo24.commonPackage.towny.TownPermission;
import de.thedodo24.commonPackage.towny.TownRank;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class WorldEvents implements Listener {

    private String prefix =  "§7§l| §6Städte §7» ";

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if(p.getLocation().getWorld().equals(Bukkit.getWorld("Freebuild"))) {
            User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
            Plot plot = Towny.getInstance().getManager().getPlotManager().get(String.valueOf(e.getBlock().getLocation().getChunk().getChunkKey()));
            if (plot != null) {
                if (p.hasPermission("towny.admin.plot.bypass"))
                    return;
                if (user.checkTownMember() && plot.getTown().getKey().equalsIgnoreCase(user.getTown().getKey()) && !user.getTownRank().equals(TownRank.CITIZEN))
                    return;
                if (!(plot.isOwned() && plot.getOwner().equals(p.getUniqueId()))) {
                    PlotPlayer townPlayer = PlotPlayer.getTownPlayer(user, plot);
                    boolean cancel = plot.getPermission(TownPermission.DESTROY, townPlayer);
                    e.setCancelled(!cancel);
                    if (!cancel && (!Towny.getInstance().getCooldown().containsKey(p.getUniqueId()) ||
                            System.currentTimeMillis() >= Towny.getInstance().getCooldown().get(p.getUniqueId()))) {
                        Towny.getInstance().getCooldown().remove(p.getUniqueId());
                        p.sendMessage(prefix + "Dir ist es als §6" + townPlayer.getDisplayName() + " §7nicht erlaubt zu §6" + TownPermission.BUILD.getDisplayName() + "§7.");
                        Towny.getInstance().getCooldown().put(p.getUniqueId(), System.currentTimeMillis() + 2500);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if(p.getLocation().getWorld().equals(Bukkit.getWorld("Freebuild"))) {
            User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
            Plot plot = Towny.getInstance().getManager().getPlotManager().get(String.valueOf(e.getBlock().getLocation().getChunk().getChunkKey()));
            if (plot != null) {
                if (p.hasPermission("towny.admin.plot.bypass"))
                    return;
                if (user.checkTownMember() && plot.getTown().getKey().equalsIgnoreCase(user.getTown().getKey()) && !user.getTownRank().equals(TownRank.CITIZEN))
                    return;
                if (!(plot.isOwned() && plot.getOwner().equals(p.getUniqueId()))) {
                    PlotPlayer townPlayer = PlotPlayer.getTownPlayer(user, plot);
                    boolean cancel = plot.getPermission(TownPermission.DESTROY, townPlayer);
                    e.setCancelled(!cancel);
                    if (!cancel && (!Towny.getInstance().getCooldown().containsKey(p.getUniqueId()) ||
                            System.currentTimeMillis() >= Towny.getInstance().getCooldown().get(p.getUniqueId()))) {
                        Towny.getInstance().getCooldown().remove(p.getUniqueId());
                        p.sendMessage(prefix + "Dir ist es als §6" + townPlayer.getDisplayName() + " §7nicht erlaubt zu §6" + TownPermission.DESTROY.getDisplayName() + "§7.");
                        Towny.getInstance().getCooldown().put(p.getUniqueId(), System.currentTimeMillis() + 2500);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMobSpawn(EntitySpawnEvent e) {
        if(e.getEntity() instanceof Monster) {
            Location loc = e.getLocation();
            Plot plot = Towny.getInstance().getManager().getPlotManager().get(String.valueOf(loc.getChunk().getChunkKey()));
            if(plot != null) {
                e.setCancelled(!plot.getSetting("mobs"));
            }
        } else if(e.getEntityType().equals(EntityType.ARMOR_STAND) || e.getEntityType().equals(EntityType.PAINTING) || e.getEntityType().equals(EntityType.ITEM_FRAME)) {
            Location loc = e.getLocation();
            Plot plot = Towny.getInstance().getManager().getPlotManager().get(String.valueOf(loc.getChunk().getChunkKey()));
        }
    }
    


}
