package de.thedodo24.adventuria.jail.listener;

import com.google.common.collect.Lists;
import de.thedodo24.adventuria.jail.Jail;
import de.thedodo24.commonPackage.player.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class WorldEvents implements Listener {

    private String prefix = "§7§l| §cSozialstunden §7» ";

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        User u = Jail.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
        if(u.isJailed()) {
            e.setCancelled(true);
            Location jail = Jail.getInstance().getManager().getJailManager().getLocation("location");
            if(e.getBlock().getType().equals(Material.OBSIDIAN) && p.getLocation().getWorld().equals(jail.getWorld())) {
                e.getBlock().setType(Material.AIR);
                int blocks = u.getDestroyedJailBlocks();
                blocks -= 1;
                if(blocks < 1) {
                    u.unjail();
                    Jail.getInstance().getManager().getPlayerManager().save(u);
                    p.getInventory().all(Material.DIAMOND_PICKAXE).keySet().stream().filter(key -> p.getInventory().getItem(key).hasItemMeta() && p.getInventory().getItem(key).getItemMeta().isUnbreakable())
                            .forEach(key -> p.getInventory().setItem(key, new ItemStack(Material.AIR)));
                    Jail.getInstance().getBossBarMap().get(p.getUniqueId()).removePlayer(p);
                    Jail.getInstance().getBossBarMap().remove(p.getUniqueId());
                    p.teleport(Jail.getInstance().getManager().getJailManager().getLocation("teleportLocation"));
                    p.sendMessage(prefix + "§7Du hast deine §cSozialstunden §7abgesessen");
                    Bukkit.getOnlinePlayers().forEach(all -> {
                        if(all.hasPermission("jail.notify"))
                            all.sendMessage(prefix + "§c" + p.getName() + " §7hat seine §cSozialstunden §7abgesessen");
                    });
                } else {
                    u.setJailed(blocks);
                    BossBar bossBar;
                    if(Jail.getInstance().getBossBarMap().containsKey(p.getUniqueId())) {
                        bossBar = Jail.getInstance().getBossBarMap().get(p.getUniqueId());
                        bossBar.setTitle(blocks + (blocks == 1 ? " Block" : " Blöcke"));
                    } else {
                        bossBar = Bukkit.createBossBar(blocks + (blocks == 1 ? " Block" : " Blöcke"), BarColor.PURPLE, BarStyle.SEGMENTED_20);
                        bossBar.addPlayer(p);
                        Jail.getInstance().getBossBarMap().put(p.getUniqueId(), bossBar);
                    }
                    bossBar.setProgress(blocks / (double) u.getMaxJailBlocks());
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        User u = Jail.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
        if(u.isJailed()) {
            if(!p.getLocation().getWorld().equals(Jail.getInstance().getManager().getJailManager().getLocation("location").getWorld())) {
                e.setCancelled(true);
            }
        }
    }
}
