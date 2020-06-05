package de.thedodo24.adventuria.utilspackage;

import de.thedodo24.adventuria.utilspackage.commands.CountdownCommand;
import de.thedodo24.adventuria.utilspackage.commands.TPFarmweltCommand;
import de.thedodo24.adventuria.utilspackage.commands.TrojanerCommand;
import de.thedodo24.commonPackage.module.Module;
import de.thedodo24.commonPackage.module.ModuleManager;
import de.thedodo24.commonPackage.module.ModuleSettings;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

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
    }

    private void registerCommands() {
        new TrojanerCommand();
        new CountdownCommand();
        new TPFarmweltCommand();
    }
}
