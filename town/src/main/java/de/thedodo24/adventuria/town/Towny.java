package de.thedodo24.adventuria.town;

import de.thedodo24.commonPackage.module.Module;
import de.thedodo24.commonPackage.module.ModuleManager;
import de.thedodo24.commonPackage.module.ModuleSettings;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@ModuleSettings
public class Towny extends Module {

    @Getter
    public static Towny instance;

    public Towny(ModuleSettings settings, ModuleManager manager, JavaPlugin plugin) {
        super(settings, manager, plugin);
        instance = this;
    }

    @Override
    public void onEnable() {

    }

}
