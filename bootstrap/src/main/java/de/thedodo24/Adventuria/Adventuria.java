package de.thedodo24.Adventuria;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import de.thedodo24.adventuria.jail.Jail;
import de.thedodo24.adventuria.town.Towny;
import de.thedodo24.adventuria.utilspackage.Utils;
import de.thedodo24.adventuriaeco.Economy;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.classes.MySQL;
import de.thedodo24.commonPackage.module.Module;
import de.thedodo24.commonPackage.towny.Town;
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
    public MySQL mySQL;

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

        config.create("mysql.host", "localhost");
        config.create("mysql.user", "user");
        config.create("mysql.password", "password");
        config.create("mysql.database", "database");
        config.create("mysql.port", 3306);
        config.save();

        arangoDB = new ArangoDB.Builder().host(config.getString("database.host"), config.getInt("database.port"))
                .user(config.getString("database.user")).password(config.getString("database.password")).build();
        arangoDatabase = arangoDB.db(config.getString("database.database"));

        mySQL = new MySQL(config.getString("mysql.host"), config.getString("mysql.user"), config.getString("mysql.password"), config.getString("mysql.database"),
                config.getInt("mysql.port"));
        mySQL.createInstance();

        moduleManager = new ModuleManager("modules", getArangoDatabase(),this, mySQL);
        moduleManager.loadModules(Common.class, Economy.class, Jail.class, Utils.class, Towny.class);
    }

    @Override
    public void onDisable() {
        moduleManager.getRegisteredModules().values().forEach(Module::onDisable);
        Common.getInstance().getManager().getPlayerManager().disableSave();
        Common.getInstance().getManager().getBankManager().disableSave();
        Common.getInstance().getManager().getArmorStandManager().disableSave();
        Common.getInstance().getManager().getJailManager().disableSave();
        mySQL.killInstance();
    }
}
