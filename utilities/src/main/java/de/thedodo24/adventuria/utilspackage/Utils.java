package de.thedodo24.adventuria.utilspackage;

import com.google.common.collect.Lists;
import de.thedodo24.adventuria.utilspackage.commands.CountdownCommand;
import de.thedodo24.adventuria.utilspackage.commands.TPFarmweltCommand;
import de.thedodo24.adventuria.utilspackage.commands.TeamCommand;
import de.thedodo24.adventuria.utilspackage.commands.TrojanerCommand;
import de.thedodo24.adventuria.utilspackage.listener.PlayerListener;
import de.thedodo24.commonPackage.module.Module;
import de.thedodo24.commonPackage.module.ModuleManager;
import de.thedodo24.commonPackage.module.ModuleSettings;
import lombok.Getter;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
@ModuleSettings
public class Utils extends Module {

    @Getter
    public static Utils instance;




    public Utils(ModuleSettings settings, ModuleManager manager, JavaPlugin plugin) {
        super(settings, manager, plugin);
        instance = this;
    }

    @Override
    public void onEnable() {
        registerCommands();
        registerListener(new PlayerListener());
    }

    private void registerCommands() {
        new TrojanerCommand();
        new CountdownCommand();
        new TPFarmweltCommand();
        new TeamCommand();
    }
}
