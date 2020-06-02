package de.thedodo24.adventuria.jail;

import com.google.common.collect.Lists;
import de.thedodo24.adventuria.jail.commands.JailCommand;
import de.thedodo24.adventuria.jail.listener.PlayerListener;
import de.thedodo24.adventuria.jail.listener.WorldEvents;
import de.thedodo24.commonPackage.arango.WritableGenerator;
import de.thedodo24.commonPackage.jail.JailLocation;
import de.thedodo24.commonPackage.module.Module;
import de.thedodo24.commonPackage.module.ModuleManager;
import de.thedodo24.commonPackage.module.ModuleSettings;
import de.thedodo24.commonPackage.utils.ConfigFile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@Getter
@ModuleSettings
public class Jail extends Module {

    @Getter
    private static Jail instance;

    public Jail(ModuleSettings settings, ModuleManager manager, JavaPlugin plugin) {
        super(settings, manager, plugin);
        instance = this;
    }

    private ItemStack diamondPickaxe;
    private ItemMeta diamondPickaxeMeta;
    private NamespacedKey namespacedKey;
    private final Map<UUID, BossBar> bossBarMap = new HashMap<>();
    private Map<UUID, List<Block>> destroyedBlocks = new HashMap<>();

    @Override
    public void onEnable() {
        diamondPickaxe = new ItemStack(Material.DIAMOND_PICKAXE, 1);
        diamondPickaxeMeta = diamondPickaxe.getItemMeta();
        diamondPickaxeMeta.setUnbreakable(true);
        diamondPickaxe.setItemMeta(diamondPickaxeMeta);

        namespacedKey = new NamespacedKey(getPlugin(), "item-identifier");

        Bukkit.getOnlinePlayers().forEach(all -> {
            if(this.getManager().getPlayerManager().get(all.getUniqueId()).isJailed()) {
                int blocks = this.getManager().getPlayerManager().get(all.getUniqueId()).getDestroyedJailBlocks();
                double maxBlocks = this.getManager().getPlayerManager().get(all.getUniqueId()).getMaxJailBlocks();
                BossBar bossBar = Bukkit.createBossBar(blocks + (blocks == 1 ? "Block" : "Blöcke"), BarColor.PURPLE, BarStyle.SEGMENTED_20);
                bossBar.setProgress(blocks / maxBlocks);
                bossBar.addPlayer(all);
                bossBarMap.put(all.getUniqueId(), bossBar);
            }
        });

        getManager().getJailManager().getOrGenerate("location");
        getManager().getJailManager().getOrGenerate("teleportLocation");

        registerCommands();
        registerListener(new PlayerListener(), new WorldEvents());
    }

    private void registerCommands() {
        new JailCommand();
    }
}
