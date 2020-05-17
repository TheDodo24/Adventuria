package de.thedodo24.Adventuria;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import de.thedodo24.adventuria.jail.Jail;
import de.thedodo24.adventuriaeco.Economy;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.module.Module;
import lombok.Getter;
import de.thedodo24.commonPackage.module.ModuleManager;
import org.bukkit.plugin.java.JavaPlugin;
import de.thedodo24.commonPackage.utils.ConfigFile;

@Getter
public class Adventuria extends JavaPlugin {

    @Getter
    public static Adventuria instance;

    public ArangoDatabase arangoDatabase;
    public ArangoDB arangoDB;
    public String prefix = "[Adventuria] ";

    private ModuleManager moduleManager;

    @Override
    public void onEnable() {
        instance = this;

        ConfigFile config = new ConfigFile(getDataFolder(), "config.yml");
        config.create("database.host", "localhost");
        config.create("database.user", "user");
        config.create("database.password", "password");
        config.create("database.database", "database");
        config.create("database.port", 8529);
        config.save();

        arangoDB = new ArangoDB.Builder().host(config.getString("database.host"), config.getInt("database.port"))
                .user(config.getString("database.user")).password(config.getString("database.password")).build();
        arangoDatabase = arangoDB.db(config.getString("database.database"));

        moduleManager = new ModuleManager("modules", getArangoDatabase(),this);
        moduleManager.loadModules(Common.class, Economy.class, Jail.class);
    }

    @Override
    public void onDisable() {
        moduleManager.getRegisteredModules().values().forEach(Module::onDisable);
    }
}
