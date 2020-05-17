package de.thedodo24.commonPackage;

import com.arangodb.ArangoDatabase;
import com.google.common.collect.Lists;
import de.thedodo24.commonPackage.commands.CacheCommand;
import de.thedodo24.commonPackage.commands.OntimeCommand;
import de.thedodo24.commonPackage.commands.TrojanerCommand;
import de.thedodo24.commonPackage.listener.PlayerListener;
import de.thedodo24.commonPackage.module.ModuleSettings;
import de.thedodo24.commonPackage.player.User;
import de.thedodo24.commonPackage.utils.ScoreboardManager;
import lombok.Getter;
import de.thedodo24.commonPackage.module.Module;
import de.thedodo24.commonPackage.module.ModuleManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.permission.Permission;

import java.util.*;

@Getter
@ModuleSettings
public class Common extends Module {

    @Getter
    private static Common instance;
    private final Map<UUID, Long> playerOnline = new HashMap<>();
    private final Map<UUID, Long> afkPlayer = new HashMap<>();
    private List<String> teamAccounts = Lists.newArrayList("team-sl", "team-ingenieur", "team-developer", "team-supporter", "team-polizist", "team-mva", "team-fbt", "team-helfer");
    private Permission perms = null;



    private long nextDay;
    private long nextWeek;

    public Common(ModuleSettings settings, ModuleManager manager, JavaPlugin plugin) {
        super(settings, manager, plugin);
        instance = this;
    }


    public void checkTime() {
        final long currentTime = System.currentTimeMillis();
        if(currentTime >= getNextWeek()) {
            getManager().getPlayerManager().getUsers().forEach(user -> {
                if(getPlayerOnline().containsKey(user.getKey())) {
                    long ontime = currentTime - getPlayerOnline().get(user.getKey());
                    long sinceMidnight = currentTime - getNextWeek();
                    long afkTime = 0;
                    if(Common.getInstance().getAfkPlayer().containsKey(user.getKey())){
                        afkTime = currentTime - getAfkPlayer().get(user.getKey());
                        getAfkPlayer().replace(user.getKey(), currentTime);
                    }
                    long beforeMidnightAFK = 0;
                    if((afkTime - sinceMidnight) > 0)
                        beforeMidnightAFK = afkTime - sinceMidnight;
                    else
                        sinceMidnight -= afkTime;
                    user.updateOntime(ontime - afkTime);
                    user.addOntimeHistory(user.getWeekOntime(), user.getAfkTime() + beforeMidnightAFK);
                    user.setWeekOntime(sinceMidnight);
                    user.setDayOntime(sinceMidnight);
                    user.updateAfkTime(afkTime);
                    getPlayerOnline().replace(user.getKey(), currentTime);
                } else {
                    user.addOntimeHistory(user.getWeekOntime(), user.getAfkTime());
                    user.setWeekOntime(0);
                    user.setDayOntime(0);
                }
            });
            setNextWeek();
            setNextDay();
        } else if(currentTime >= getNextDay()) {
            getManager().getPlayerManager().getUsers().forEach(user -> {
                if(getPlayerOnline().containsKey(user.getKey())) {
                    long ontime = currentTime - getPlayerOnline().get(user.getKey());
                    long sinceMidnight = currentTime - getNextDay();
                    long afkTime = 0;
                    if(Common.getInstance().getAfkPlayer().containsKey(user.getKey())){
                        afkTime = currentTime - getAfkPlayer().get(user.getKey());
                        getAfkPlayer().replace(user.getKey(), currentTime);
                    }
                    if((sinceMidnight - afkTime) > 0)
                        sinceMidnight -= afkTime;
                    user.updateOntime(ontime - afkTime);
                    user.setDayOntime(sinceMidnight);
                    user.updateAfkTime(afkTime);
                    getPlayerOnline().replace(user.getKey(), currentTime);
                } else {
                    user.setDayOntime(0);
                }
            });
            setNextDay();
        }
    }

    @Override
    public void onEnable() {
        try {
            Class.forName("net.milkbowl.vault.permission.Permission");

            RegisteredServiceProvider<Permission> rsp = getPlugin().getServer().getServicesManager().getRegistration(Permission.class);
            perms = rsp.getProvider();
        } catch (Exception e) {
            System.err.println("[Adventuria] Vault is depended to load this plugin");
        }
        registerListener(new PlayerListener());
        new OntimeCommand();
        new TrojanerCommand();
        new CacheCommand();
        if(Bukkit.getOnlinePlayers().size() > 0) {
            Bukkit.getOnlinePlayers().forEach(all -> {
                new ScoreboardManager(all);
                getPlayerOnline().put(all.getUniqueId(), System.currentTimeMillis());
            });
        }
        setNextDay();
        setNextWeek();
    }

    private void setNextDay() {
        Calendar calendar = Calendar.getInstance(Locale.GERMANY);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        nextDay = calendar.getTimeInMillis();
    }

    private void setNextWeek() {
        Calendar calendar = Calendar.getInstance(Locale.GERMANY);
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        nextWeek = calendar.getTimeInMillis();
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(all -> {
            User u = Common.getInstance().getManager().getPlayerManager().get(all.getUniqueId());
            long ontime = System.currentTimeMillis() - Common.getInstance().getPlayerOnline().get(all.getUniqueId());
            long afkTime = 0;
            if(Common.getInstance().getAfkPlayer().containsKey(all.getUniqueId())) {
                afkTime = Common.getInstance().getAfkPlayer().get(all.getUniqueId());
                ontime -= (System.currentTimeMillis() - afkTime);
                Common.getInstance().getAfkPlayer().remove(all.getUniqueId());
            }
            u.updateOntime(ontime);
            u.updateAfkTime(afkTime);
            Common.getInstance().getPlayerOnline().remove(all.getUniqueId());
        });
        getManager().getPlayerManager().disableSave();
        getManager().getBankManager().disableSave();
        getManager().getArmorStandManager().disableSave();
        getManager().getJailManager().disableSave();
    }
}
