package de.thedodo24.adventuria.jail.commands;

import com.google.common.collect.Lists;
import de.thedodo24.adventuria.jail.Jail;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.jail.JailLocation;
import de.thedodo24.commonPackage.player.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JailCommand implements CommandExecutor, TabCompleter {

    private String prefix = "§7§l| §cSozialstunden §7» ";

    public JailCommand() {
        PluginCommand cmd = Jail.getInstance().getPlugin().getCommand("jail");
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
    }

    private String noperm(String perm) {
        return "§cYou do not have the permission §4" + perm + " §cto execute this command!";
    }

    private String helpMessage() {
        return prefix +  "§c/jail [Name] [Blöcke] §7| Jailt einen Spieler\n" +
                prefix +  "§c/jail free [Name] §7| Entlässt einen Spieler\n" +
                prefix +  "§c/jail set §7| Setzt den Jail-Punkt\n" +
                prefix +  "§c/jail info [Name] §7| Status über den Spieler\n" +
                prefix +  "§c/jail setteleport §7| Setzt den Teleport-Punkt";
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(args.length == 2) {
            if (args[0].equalsIgnoreCase("free")) {
                if (s.hasPermission("jail.free")) {
                    String name = args[1];
                    UUID uuid = Bukkit.getOfflinePlayer(name).getUniqueId();
                    User u = Jail.getInstance().getManager().getPlayerManager().getByName(name);
                    if (u.isJailed()) {
                        u.unjail();
                        Jail.getInstance().getManager().getPlayerManager().save(u);
                        Player p;
                        if ((p = Bukkit.getPlayer(uuid)) != null) {
                            name = p.getName();
                            p.teleport(Jail.getInstance().getManager().getJailManager().getLocation("teleportLocation"));
                            p.sendMessage(prefix + "§7Du wurdest durch §c" + s.getName() + " §7befreit.");
                            Jail.getInstance().getBossBarMap().get(p.getUniqueId()).removePlayer(p);
                            Jail.getInstance().getBossBarMap().remove(p.getUniqueId());
                            p.getInventory().all(Material.DIAMOND_PICKAXE).keySet().stream().filter(key -> p.getInventory().getItem(key).hasItemMeta() && p.getInventory().getItem(key).getItemMeta().isUnbreakable())
                                    .forEach(key -> p.getInventory().setItem(key, new ItemStack(Material.AIR)));
                        }
                        s.sendMessage(prefix + "§c" + name + " §7wurde freigelassen.");
                        String finalName = name;
                        Bukkit.getOnlinePlayers().forEach(all -> {
                            if (all.hasPermission("jail.notify"))
                                all.sendMessage(prefix + "§c" + finalName + " §7wurde durch §c" + s.getName() + " §7aus den Sozialstunden befreit.");
                        });
                        if(Jail.getInstance().getDestroyedBlocks().containsKey(u.getKey())) {
                            List<Block> blockList = Jail.getInstance().getDestroyedBlocks().get(u.getKey());
                            blockList.forEach(block -> block.setType(Material.OBSIDIAN));
                            Jail.getInstance().getDestroyedBlocks().remove(u.getKey());
                        }
                    } else {
                        s.sendMessage(prefix + "§7Der Spieler §c" + args[1] + " §7ist nicht eingesperrt.");
                    }
                } else {
                    s.sendMessage(noperm("jail.free"));
                }
            } else if(args[0].equalsIgnoreCase("info")) {
                if(s.hasPermission("jail.info")) {
                    String name = args[1];
                    User u = Jail.getInstance().getManager().getPlayerManager().getByName(name);
                    if(u.isJailed()) {
                        s.sendMessage(prefix + "§c" + name + " §7ist noch für §5" + u.getDestroyedJailBlocks() + "§7/§5" + u.getMaxJailBlocks() + " Blöcken §7eingesperrt.");
                    } else {
                        s.sendMessage(prefix + "§7Der Spieler §c" + name + " §7ist nicht eingesperrt.");
                    }
                } else {
                    s.sendMessage(noperm("jail.info"));
                }
            } else {
                if(s.hasPermission("jail.jail")) {
                    String name = args[0];
                    int blocks;
                    try {
                        blocks = Integer.parseInt(args[1]);
                    } catch(NumberFormatException e) {
                        s.sendMessage(prefix + "§7Argument 2 muss eine §cpositive ganzzahlige Zahl §7sein.");
                        return false;
                    }
                    User u = Jail.getInstance().getManager().getPlayerManager().getByName(name);
                    if(u != null) {
                        if(!u.isJailed()) {
                            if(blocks > 0) {
                                u.setJailed(blocks);
                                Jail.getInstance().getManager().getPlayerManager().save(u);
                                Player p;
                                if((p = Bukkit.getPlayer(u.getKey())) != null) {
                                    name = p.getName();
                                    p.teleport(Jail.getInstance().getManager().getJailManager().getLocation("location"));
                                    p.getInventory().addItem(Jail.getInstance().getDiamondPickaxe());
                                    p.sendMessage(prefix + "§7Du wurdest zu §cSozialstunden §7eingesperrt. §7Du musst §5" + blocks + (blocks == 1? " Obsidian-Block" : " Obsidian-Blöcke") + " §7abbauen.");
                                    BossBar bossBar = Bukkit.createBossBar(blocks + (blocks == 1 ? " Block" : " Blöcke"), BarColor.PURPLE, BarStyle.SEGMENTED_20);
                                    bossBar.setProgress(1);
                                    bossBar.addPlayer(p);
                                    Jail.getInstance().getBossBarMap().put(p.getUniqueId(), bossBar);
                                }
                                s.sendMessage(prefix + "§c" + name + " §7wurde für §5" + blocks + (blocks == 1? " Obsidian-Block" : " Obsidian-Blöcke") +" §7eingesperrt.");
                                String finalName = name;
                                Bukkit.getOnlinePlayers().forEach(all -> {
                                    if(all.hasPermission("jail.notify"))
                                        all.sendMessage(prefix + "§c" + finalName + " §7wurde durch §c" + s.getName() + " §7zu §5" + blocks + (blocks == 1 ? " Block" : " Blöcke") + " §7Sozialstunden bestraft.");
                                });
                            } else {
                                s.sendMessage(prefix + "§7Argument 2 muss eine §cpositive ganzzahlige Zahl §7sein.");
                            }
                        } else {
                            s.sendMessage(prefix + "§7Der Spieler ist bereits eingesperrt.");
                        }
                    } else {
                        s.sendMessage(prefix + "Dieser Spieler existiert nicht");
                    }
                } else {
                    s.sendMessage(noperm("jail.jail"));
                }
            }
        } else if(args.length == 1) {
            if(s instanceof Player) {
                Player p = (Player) s;
                if(args[0].equalsIgnoreCase("set")) {
                    if(p.hasPermission("jail.set")) {
                        JailLocation loc = Jail.getInstance().getManager().getJailManager().get("location");
                        loc.setLoc(p.getLocation());
                        Jail.getInstance().getManager().getJailManager().save(loc);
                        p.sendMessage(prefix + "§7Deine Position wurde gespeichert.");
                    } else {
                        p.sendMessage(noperm("jail.set"));
                    }
                } else if(args[0].equalsIgnoreCase("setteleport")) {
                    if(p.hasPermission("jail.set")) {
                        JailLocation loc = Jail.getInstance().getManager().getJailManager().get("teleportLocation");
                        loc.setLoc(p.getLocation());
                        Jail.getInstance().getManager().getJailManager().save(loc);
                        p.sendMessage(prefix + "§7Deine Position wurde gespeichert.");
                    } else {
                        p.sendMessage(noperm("jail.set"));
                    }
                } else {
                    if(s.hasPermission("jail.set")) {
                        s.sendMessage(helpMessage());
                    } else {
                        s.sendMessage(noperm("jail.set"));
                        s.sendMessage(noperm("jail.set"));
                    }
                }
            } else {
                s.sendMessage("Du musst ein Spieler sein, um diesen Befehl ausführen zu können.");
            }
        } else {
            if(s.hasPermission("jail.jail")) {
                s.sendMessage(helpMessage());
            } else {
                s.sendMessage(noperm("jail.jail"));
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        if(s instanceof Player) {
            Player p = (Player) s;
            if(p.hasPermission("jail.set") || p.hasPermission("jail.jail") || p.hasPermission("jail.info") || p.hasPermission("jail.free")) {
                if(args.length == 1) {
                    List<String> list = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
                    list.add("setteleport");
                    list.add("set");
                    list.add("info");
                    list.add("free");
                    return Common.getInstance().removeAutoComplete(list, args[0]);
                } else if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("free"))
                        return Common.getInstance().removeAutoComplete(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
                }
            }
        }
        return Lists.newArrayList();
    }
}
