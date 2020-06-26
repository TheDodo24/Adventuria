package de.thedodo24.adventuria.town.listener;

import com.google.common.collect.Lists;
import de.thedodo24.adventuria.town.Towny;
import de.thedodo24.commonPackage.player.User;
import de.thedodo24.commonPackage.towny.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class PlayerListener implements Listener {

    private String prefix = "§7§l| §6Städte §7» ";
    private List<Material> interactMaterial = Lists.newArrayList(Material.CHEST, Material.HOPPER, Material.HOPPER_MINECART, Material.CHEST_MINECART, Material.NOTE_BLOCK, Material.LEVER,
            Material.STONE_PRESSURE_PLATE, Material.OAK_PRESSURE_PLATE, Material.SPRUCE_PRESSURE_PLATE, Material.BIRCH_PRESSURE_PLATE, Material.JUNGLE_PRESSURE_PLATE, Material.ACACIA_PRESSURE_PLATE,
            Material.DARK_OAK_PRESSURE_PLATE, Material.STONE_BUTTON, Material.OAK_TRAPDOOR, Material.SPRUCE_TRAPDOOR, Material.BIRCH_TRAPDOOR, Material.JUNGLE_TRAPDOOR, Material.ACACIA_TRAPDOOR,
            Material.DARK_OAK_TRAPDOOR, Material.OAK_FENCE_GATE, Material.SPRUCE_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.DARK_OAK_FENCE_GATE,
            Material.BEACON, Material.OAK_BUTTON, Material.SPRUCE_BUTTON, Material.BIRCH_BUTTON, Material.JUNGLE_BUTTON, Material.ACACIA_BUTTON, Material.DARK_OAK_BUTTON, Material.ANVIL, Material.TRAPPED_CHEST,
            Material.LIGHT_WEIGHTED_PRESSURE_PLATE, Material.HEAVY_WEIGHTED_PRESSURE_PLATE, Material.DAYLIGHT_DETECTOR, Material.IRON_TRAPDOOR, Material.REPEATER, Material.COMPARATOR, Material.COMPOSTER,
            Material.FURNACE, Material.FURNACE_MINECART, Material.BREWING_STAND, Material.CAULDRON, Material.FLOWER_POT, Material.ARMOR_STAND, Material.SHULKER_BOX, Material.BARREL, Material.SMOKER, Material.BLAST_FURNACE,
            Material.CARTOGRAPHY_TABLE, Material.FLETCHING_TABLE, Material.GRINDSTONE, Material.LECTERN, Material.SMITHING_TABLE, Material.STONECUTTER, Material.BELL, Material.CAMPFIRE);

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();
        if(from.getChunk().getChunkKey() != to.getChunk().getChunkKey()) {
            Plot plot = Towny.getInstance().getManager().getPlotManager().get(String.valueOf(to.getChunk().getChunkKey()));
            if(plot != null) {
                Town t = plot.getTown();
                if(t.getSpawn().getChunk().getChunkKey() == to.getChunk().getChunkKey()) {
                    p.sendActionBar("§7» "+t.getRank()+": §6" + t.getName() + " §7|| §6Stadtspawn §7|| (§cPvP§7)");
                } else {
                    if(plot.isOutpostPlot()) {
                        Location outpostSpawn = t.getOutpostSpawn(plot.getOutpost());
                        if(outpostSpawn.getChunk().getChunkKey() == Long.parseLong(plot.getKey())) {
                            p.sendActionBar("§7» "+t.getRank()+": §6" + t.getName() + " §7|| Outpost: §6" + plot.getOutpost() + " §7|| §6Outpostspawn §7|| (§cPvP§7)");
                        } else {
                            p.sendActionBar("§7[§6" + t.getName() + " §7| §6" + plot.getOutpost() + "§7] » §6" + plot.getName());
                        }
                    } else {
                        p.sendActionBar("§7[§6" + t.getName() + "§7] » §6" + plot.getName());
                    }
                }
            } else {
                Plot fromChunk = Towny.getInstance().getManager().getPlotManager().get(String.valueOf(from.getChunk().getChunkKey()));
                if(fromChunk != null) {
                    p.sendActionBar("§7» §cWildnis §7(§aPvP§7)");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
        if(!p.hasPermission("towny.admin.plot.bypass") && (user.checkTownMember() && user.getTownRank().equals(TownRank.CITIZEN))) {
            if (e.getClickedBlock() != null) {
                if (interactMaterial.contains(e.getClickedBlock().getType())) {
                    Plot plot = Towny.getInstance().getManager().getPlotManager().get(String.valueOf(e.getClickedBlock().getLocation().getChunk().getChunkKey()));
                    if (plot != null) {
                        if (!(plot.isOwned() && plot.getOwner().equals(p.getUniqueId()))) {
                            PlotPlayer townPlayer = PlotPlayer.getTownPlayer(user, plot);
                            boolean cancel = plot.getPermission(TownPermission.SWITCH, townPlayer);
                            e.setCancelled(!cancel);
                            if (!cancel)
                                p.sendMessage(prefix + "Dir ist es als §6" + townPlayer.getDisplayName() + "§7 nicht erlaubt zu ein Item zu verwenden§7.");
                        }
                    }
                }
            }
        }
    }



    @EventHandler
    public void interactWithEntity(PlayerInteractAtEntityEvent e) {
        Player p = e.getPlayer();
        User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
        if(!p.hasPermission("towny.admin.plot.bypass") && (user.checkTownMember() && user.getTownRank().equals(TownRank.CITIZEN))) {
            Plot plot = Towny.getInstance().getManager().getPlotManager().get(String.valueOf(e.getRightClicked().getLocation().getChunk().getChunkKey()));
            if (plot != null) {
                if (!(plot.isOwned() && plot.getOwner().equals(p.getUniqueId()))) {
                    PlotPlayer townPlayer = PlotPlayer.getTownPlayer(user, plot);
                    boolean cancel = plot.getPermission(TownPermission.SWITCH, townPlayer);
                    e.setCancelled(!cancel);
                    if (!cancel)
                        p.sendMessage(prefix + "Dir ist es als §6" + townPlayer.getDisplayName() + "§7 nicht erlaubt zu ein Item zu verwenden§7.");
                }
            }
        }
    }

    @EventHandler
    public void damageEntityEvent(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
            if (!p.hasPermission("towny.admin.plot.bypass") && (user.checkTownMember() && user.getTownRank().equals(TownRank.CITIZEN))) {
                Plot plot = Towny.getInstance().getManager().getPlotManager().get(String.valueOf(e.getEntity().getLocation().getChunk().getChunkKey()));
                if (plot != null) {
                    if (!(plot.isOwned() && plot.getOwner().equals(p.getUniqueId()))) {
                        PlotPlayer townPlayer = PlotPlayer.getTownPlayer(user, plot);
                        boolean cancel = plot.getPermission(TownPermission.ITEM, townPlayer);
                        e.setCancelled(!cancel);
                        if (!cancel)
                            p.sendMessage(prefix + "Dir ist es als §6" + townPlayer.getDisplayName() + " §7nicht erlaubt zu ein Item zu verwenden§7.");
                    }
                }
            }
        }
    }

    @EventHandler
    public void eggThrowEvent(PlayerEggThrowEvent e) {
        Player p = e.getPlayer();
        User user = Towny.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
        if(!p.hasPermission("towny.admin.plot.bypass") && (user.checkTownMember() && user.getTownRank().equals(TownRank.CITIZEN))) {
            Plot plot = Towny.getInstance().getManager().getPlotManager().get(String.valueOf(e.getEgg().getLocation().getChunk().getChunkKey()));
            if (plot != null) {
                if (!(plot.isOwned() && plot.getOwner().equals(p.getUniqueId()))) {
                    PlotPlayer townPlayer = PlotPlayer.getTownPlayer(user, plot);
                    boolean cancel = plot.getPermission(TownPermission.ITEM, townPlayer);
                    e.setHatching(!cancel);
                    if (!cancel)
                        p.sendMessage(prefix + "Dir ist es als §6" + townPlayer.getDisplayName() + " §7nicht erlaubt zu ein Item zu verwenden§7.");
                }
            }
        }
    }


}
