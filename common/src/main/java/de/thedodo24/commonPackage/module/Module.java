package de.thedodo24.commonPackage.module;

import de.thedodo24.commonPackage.arango.ArangoWritable;
import com.arangodb.entity.BaseDocument;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import de.thedodo24.commonPackage.utils.ConfigFile;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Module implements ArangoWritable<String> {

    protected String name;
    protected String commandGroup;

    @Setter
    protected boolean enabled;
    protected ConfigFile config;
    protected ModuleManager manager;
    protected JavaPlugin plugin;
    protected List<Listener> activeListeners;
    protected List<Listener> passiveListeners;


    public Module(ModuleSettings settings, ModuleManager manager, JavaPlugin plugin) {
        this.name = settings.name().replace("module", getClass().getSimpleName()
                .replace("Module", "")).toLowerCase();
        this.commandGroup = settings.commandGroup().replace("[module]", this.name);

        this.manager = manager;
        this.plugin = plugin;

        this.activeListeners = new ArrayList<>();
        this.passiveListeners = new ArrayList<>();
        this.config = new ConfigFile(plugin.getDataFolder(), this.name);
    }


    @Override
    public String getKey() {
        return this.name;
    }

    @Override
    public void read(BaseDocument document) {
        this.enabled = (boolean) document.getProperties().getOrDefault("isEnabled", true);
    }

    @Override
    public void save(BaseDocument document) {
        document.addAttribute("isEnabled", this.enabled);
    }

    protected final void registerListener(Listener listener, boolean active) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);

        if(active) activeListeners.add(listener);
        else passiveListeners.add(listener);
    }

    protected final void registerListener(Listener... listeners) {
        for(Listener listener : listeners)
            registerListener(listener, true);
    }

    protected final void unregisterListeners() {

        try {

            Field handlerListList = HandlerList.class.getField("allLists");
            handlerListList.setAccessible(true);

            List<HandlerList> handlerLists = (List<HandlerList>) handlerListList.get(null);
            for(HandlerList handlerList : handlerLists)
                for(Listener listener : activeListeners)
                    handlerList.unregister(listener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onLoad() {
        plugin.getLogger().info("Loaded " + name + " module");
    }

    public void onEnable() {
        plugin.getLogger().info("Enabling " + name + " module");
    }

    public void onDisable() {
        plugin.getLogger().info("Disabling " + name + " module");
    }

}
