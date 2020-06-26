package de.thedodo24.commonPackage.module;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import de.thedodo24.commonPackage.classes.MySQL;
import de.thedodo24.commonPackage.economy.ArmorStandHandler;
import de.thedodo24.commonPackage.economy.ArmorStandManager;
import de.thedodo24.commonPackage.economy.BankLogHandler;
import de.thedodo24.commonPackage.economy.BankManager;
import de.thedodo24.commonPackage.jail.JailManager;
import de.thedodo24.commonPackage.towny.NationManager;
import de.thedodo24.commonPackage.towny.PlotManager;
import de.thedodo24.commonPackage.towny.TownManager;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import de.thedodo24.commonPackage.player.PlayerManager;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ModuleManager {

    private JavaPlugin plugin;
    @Getter(AccessLevel.NONE)
    private ArangoCollection collection;
    private ArangoDatabase database;
    private Map<String, Module> registeredModules;
    private PlayerManager playerManager;
    private BankManager bankManager;
    private BankLogHandler logHandler;
    private ArmorStandManager armorStandManager;
    private JailManager jailManager;
    private TownManager townManager;
    private PlotManager plotManager;
    private NationManager nationManager;
    private MySQL mySQL;




    public ModuleManager(String collection, ArangoDatabase database, JavaPlugin plugin, MySQL mySQL) {
        if(!database.collection(collection).exists()) {
            database.createCollection(collection);
        }
        this.plugin = plugin;
        this.collection = database.collection(collection);
        this.database = database;
        this.registeredModules = new HashMap<>();
        this.playerManager = new PlayerManager(database);
        this.bankManager = new BankManager(database);
        this.armorStandManager = new ArmorStandManager(database);
        this.jailManager = new JailManager(database);
        this.logHandler = new BankLogHandler(database);
        this.townManager = new TownManager(database);
        this.plotManager = new PlotManager(database);
        this.nationManager = new NationManager(database);
        this.mySQL = mySQL;
    }

    @SafeVarargs
    public final void loadModules(Class<? extends Module>... modules) {

        try {

            for(Class<? extends Module> clazz : modules)
                loadModule(clazz);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void loadModule(Class<? extends Module> clazz) throws IllegalArgumentException, IllegalStateException {

        try {

            ModuleSettings settings;
            if ((settings = clazz.getAnnotation(ModuleSettings.class)) == null)
                throw(new IllegalArgumentException("module-settings not set"));


            Module module =
                    clazz.getDeclaredConstructor(ModuleSettings.class, ModuleManager.class, JavaPlugin.class)
                            .newInstance(settings, this, plugin);

            if(!this.readModuleInfo(module)) {
                BaseDocument document = new BaseDocument();

                document.setKey(module.getName());
                module.save(document);
                this.collection.insertDocument(document);

            }

            registeredModules.put(module.getName(), module);
            module.onLoad();

            if(module.isEnabled())
                module.onEnable();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void unloadModule(String moduleName) throws IllegalArgumentException {

        Module module;
        if((module = registeredModules.get(moduleName)) == null)
            throw(new IllegalArgumentException("module not registered"));

        try {

            if (module.isEnabled())
                module.onDisable();

            this.registeredModules.remove(moduleName);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void enableModule(String module) throws IllegalArgumentException, IllegalStateException {

        Module mod;
        if((mod = registeredModules.get(module)) == null)
            throw(new IllegalArgumentException("no module found with the given name"));

        if(mod.isEnabled())
            throw(new IllegalStateException("module already active"));

        mod.setEnabled(true);
        save(mod);

        try {
            mod.onEnable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disableModule(String module) throws IllegalArgumentException, IllegalStateException {

        Module mod;
        if((mod = registeredModules.get(module)) == null)
            throw(new IllegalArgumentException("no module found with the given name"));

        if(!mod.isEnabled())
            throw(new IllegalStateException("module already inactive"));

        mod.setEnabled(false);
        save(mod);

        try {
            mod.onDisable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean readModuleInfo(Module module) {

        BaseDocument baseDocuemnt;
        if ((baseDocuemnt = this.collection.getDocument(module.getKey(), BaseDocument.class)) == null)
            return false;

        module.read(baseDocuemnt);

        return true;
    }

    private void save(Module module) {

        BaseDocument document = new BaseDocument();
        document.setKey(module.getKey());
        module.save(document);

        this.collection.replaceDocument(module.getKey(), document);

    }



}
