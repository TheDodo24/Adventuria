package de.thedodo24.commonPackage.listener;

import com.google.common.collect.Lists;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.arango.CollectionManager;
import de.thedodo24.commonPackage.player.User;
import de.thedodo24.commonPackage.utils.ManagerScoreboard;
import de.thedodo24.commonPackage.utils.ScoreboardManager;
import de.thedodo24.commonPackage.utils.SkullItems;
import de.thedodo24.commonPackage.utils.TimeFormat;
import net.ess3.api.Economy;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerListener implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        User u = Common.getInstance().getManager().getPlayerManager().getOrGenerate(p.getUniqueId());
        u.setName(p.getName());
        Common.getInstance().getPlayerOnline().put(p.getUniqueId(), System.currentTimeMillis());
        Common.getInstance().checkTime();
        new ScoreboardManager(p);
        ScoreboardManager.getScoreboardMap().forEach((key, val) -> val.sendScoreboard(Bukkit.getPlayer(key), Bukkit.getOnlinePlayers().size()));
        //new ManagerScoreboard(p);
        //ManagerScoreboard.getScoreboardMap().forEach((key, val) -> p.setScoreboard(val.getBoard().getScoreboard()));
    }

    @EventHandler
    public void onAfk(AfkStatusChangeEvent e) {
        Player p = e.getAffected().getBase();
        boolean afk = e.getValue();
        User u = Common.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
        if(afk) {
            Common.getInstance().getAfkPlayer().put(p.getUniqueId(), System.currentTimeMillis());
        } else {
            if(Common.getInstance().getAfkPlayer().containsKey(p.getUniqueId())) {
                long afkTime = System.currentTimeMillis() - Common.getInstance().getAfkPlayer().get(p.getUniqueId());
                Common.getInstance().getAfkPlayer().remove(p.getUniqueId());
                u.updateAfkTime(afkTime);
                if(Common.getInstance().getPlayerOnline().containsKey(p.getUniqueId()))
                    Common.getInstance().getPlayerOnline().replace(p.getUniqueId(), Common.getInstance().getPlayerOnline().get(p.getUniqueId()) + afkTime);
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();
            ItemStack item = e.getCurrentItem();
            if(item != null) {
                if(item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();
                    if(e.getSlotType() != InventoryType.SlotType.OUTSIDE) {
                        InventoryView inv = p.getOpenInventory();
                        String title = inv.getTitle();
                        switch(title) {
                            case "§aWähle einen Spieler aus:":
                                e.setCancelled(true);
                                if(item.getItemMeta().hasDisplayName()) {
                                    String displayName = item.getItemMeta().getDisplayName();
                                    String playerName = displayName.substring(2);
                                    Inventory inventory = Bukkit.createInventory(null, 9, "§a» " + playerName);
                                    User u = Common.getInstance().getManager().getPlayerManager().getByName(playerName);
                                    Map<String, Long> ontimeHistoryMap = u.getOntimeHistoryMap();
                                    Map<String, Long> afkHistoryMap = u.getAfkTimeHistoryMap();
                                    for(int i = 0; i < 10; i++) {
                                        int week = 9 - i;
                                        if(ontimeHistoryMap.containsKey(String.valueOf(week)) && afkHistoryMap.containsKey(String.valueOf(week))){
                                            String ontime = TimeFormat.getInDays(ontimeHistoryMap.get(String.valueOf(week)));
                                            String afkTime;
                                            if(afkHistoryMap.containsKey(String.valueOf(week + 1))) {
                                                afkTime = TimeFormat.getInDays(afkHistoryMap.get(String.valueOf(week)) - afkHistoryMap.get(String.valueOf(week + 1)));
                                            } else {
                                                afkTime = TimeFormat.getInDays(afkHistoryMap.get(String.valueOf(week)));
                                            }
                                            inventory.setItem(i,
                                                    SkullItems.getNumberSkull(week,
                                                            (week == 1 ? "§aLetzte Woche" : (week == 2 ? "§aVorletzte Woche" : "§aVor " + week + " Wochen")),
                                                            Lists.newArrayList("§7» Ontime: §a" + ontime,
                                                                    "§7» AFK-Zeit: §a" + afkTime)));
                                        }
                                    }
                                    p.openInventory(inventory);
                                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                }
                                return;
                        }
                        if(title.startsWith("§a» ")) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onQuit(PlayerQuitEvent e) {
        Common.getInstance().checkTime();
        Player p = e.getPlayer();
        User u = Common.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
        long ontime = System.currentTimeMillis() - Common.getInstance().getPlayerOnline().get(p.getUniqueId());
        long afkTime = 0;
        if(Common.getInstance().getAfkPlayer().containsKey(p.getUniqueId())) {
            afkTime = System.currentTimeMillis() - Common.getInstance().getAfkPlayer().get(p.getUniqueId());
            ontime -= afkTime;
            Common.getInstance().getAfkPlayer().remove(p.getUniqueId());
        }
        u.updateOntime(ontime);
        u.updateAfkTime(afkTime);
        Common.getInstance().getPlayerOnline().remove(p.getUniqueId());
        Common.getInstance().getManager().getPlayerManager().update(u);
        ScoreboardManager.getScoreboardMap().remove(p.getUniqueId());
        ScoreboardManager.getScoreboardMap().forEach((key, val) -> val.sendScoreboard(Bukkit.getPlayer(key), Bukkit.getOnlinePlayers().size() - 1));
    }

}
