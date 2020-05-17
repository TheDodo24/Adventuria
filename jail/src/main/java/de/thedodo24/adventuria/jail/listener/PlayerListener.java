package de.thedodo24.adventuria.jail.listener;

import com.google.gson.internal.$Gson$Preconditions;
import de.thedodo24.adventuria.jail.Jail;
import de.thedodo24.commonPackage.player.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerListener implements Listener {

    private String prefix = "§7§l| §cSozialstunden §7» ";

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        Location jail = Jail.getInstance().getManager().getJailManager().getLocation("location");
        Location teleport = Jail.getInstance().getManager().getJailManager().getLocation("teleportLocation");
        User u = Jail.getInstance().getManager().getPlayerManager().getOrGenerate(p.getUniqueId());
        u.setName(p.getName());
        if(u.isJailed()) {
            int blocks = u.getDestroyedJailBlocks();
            if (blocks < 1) {
                u.unjail();
                Jail.getInstance().getManager().getPlayerManager().save(u);
                p.teleport(teleport);
                p.sendMessage(prefix + "§7Du hast deine §cSozialstunden §7abgesessen");
                Bukkit.getOnlinePlayers().forEach(all -> {
                    if (all.hasPermission("jail.notify")) {
                        all.sendMessage(prefix + "§c" + p.getName() + " §7hat seine Sozialstunden abgesessen.");
                    }
                });
            } else {
                if (!p.getLocation().getWorld().equals(jail.getWorld())) {
                    p.teleport(jail);
                    p.sendMessage(prefix + "§7Du wurdest zu §cSozialstunden §7eingesperrt!");

                    p.getInventory().addItem(Jail.getInstance().getDiamondPickaxe());
                }
                p.sendMessage(prefix + "§7Du musst noch §5" + blocks + " Obsidian-Blöcke §7abbauen.");

                BossBar bossBar = Bukkit.createBossBar(blocks + (blocks == 1 ? " Block" : " Blöcke"), BarColor.PURPLE, BarStyle.SEGMENTED_20);
                double maxBlocks = u.getMaxJailBlocks();
                bossBar.setProgress(blocks / maxBlocks);
                bossBar.addPlayer(p);
                Jail.getInstance().getBossBarMap().put(p.getUniqueId(), bossBar);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(Jail.getInstance().getBossBarMap().containsKey(p.getUniqueId())) {
            Jail.getInstance().getBossBarMap().get(p.getUniqueId()).removePlayer(p);
            Jail.getInstance().getBossBarMap().remove(p.getUniqueId());
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        User u = Jail.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
        if(u.isJailed()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e)  {
        Player p = e.getPlayer();
        User u = Jail.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
        if(u.isJailed()) {
            if(!p.hasPermission("jail.bypass")) {
                e.setCancelled(true);
                p.sendMessage(prefix + "§7Du darfst während der §cSozialstunden §7keine Befehle ausführen.");
            }
        }
    }

}
