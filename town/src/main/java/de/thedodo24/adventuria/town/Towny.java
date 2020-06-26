package de.thedodo24.adventuria.town;

import com.google.common.collect.Lists;
import de.thedodo24.adventuria.town.commands.*;
import de.thedodo24.adventuria.town.listener.PlayerListener;
import de.thedodo24.adventuria.town.listener.WorldEvents;
import de.thedodo24.commonPackage.module.Module;
import de.thedodo24.commonPackage.module.ModuleManager;
import de.thedodo24.commonPackage.module.ModuleSettings;
import de.thedodo24.commonPackage.towny.TownRank;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

@Getter
@ModuleSettings
public class Towny extends Module {

    @Getter
    public static Towny instance;

    public Towny(ModuleSettings settings, ModuleManager manager, JavaPlugin plugin) {
        super(settings, manager, plugin);
        instance = this;
    }

    private Map<UUID, String> townInventations;
    private Map<UUID, Long> townInventationsTime;
    private List<String> charList;
    private int scheduler;

    private long residentTax;
    private long townTax;
    private SimpleDateFormat dateFormat;

    private String prefix = "§7§l| §6Städte §7» ";
    private String formatValue(double v) { return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(v).split(" ")[0] + " A"; }

    @Override
    public void onEnable() {
        getPlugin().getLogger().log(Level.INFO, "Enable Towny module");
        townInventations = new HashMap<>();
        townInventationsTime = new HashMap<>();
        charList = Lists.newArrayList("!", "\"", "§", "$", "%", "&", "/",
                "(", ")", "=", "?", "`", "´", "+", "*", "#", "'", "_", "-", ":", ".", ";",
                ",", "<", ">", "~", "\\", "}", "]", "[", "{", "³", "²", "^", "°", "ß", "ü", "ä", "ö", "Ä", "Ö", "Ü");
        registerCommands();
        registerListeners();
        startScheduler();

        dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm:ss.SS");
        setTaxDates();
    }

    private void setTaxDates() {
        getPlugin().getLogger().log(Level.INFO, "Resident tax time: " + dateFormat.format(new Date(setResidentTax())));
        getPlugin().getLogger().log(Level.INFO, "Town tax time: " + dateFormat.format(new Date(setTownTax())));
    }

    private long setResidentTax() {
        Calendar resident = Calendar.getInstance();
        if(resident.get(Calendar.HOUR_OF_DAY) >= 18) {
            resident.add(Calendar.DAY_OF_MONTH, 1);
        }
        resident.set(Calendar.HOUR_OF_DAY, 18);
        resident.set(Calendar.MINUTE, 0);
        resident.set(Calendar.SECOND, 0);
        resident.set(Calendar.MILLISECOND, 0);
        residentTax = resident.getTimeInMillis();
        return residentTax;
    }

    private long setTownTax() {
        Calendar town = Calendar.getInstance();
        town.set(Calendar.DAY_OF_WEEK, 8);
        town.set(Calendar.HOUR_OF_DAY, 18);
        town.set(Calendar.MINUTE, 0);
        town.set(Calendar.SECOND, 0);
        town.set(Calendar.MILLISECOND, 0);
        if(System.currentTimeMillis() > town.getTimeInMillis()) {
            town.add(Calendar.WEEK_OF_YEAR, 1);
        }
        townTax = town.getTimeInMillis();
        return townTax;
    }

    private void startScheduler() {
        scheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), () -> {
            if(getTownInventationsTime().size() > 0) {
                List<UUID> toRemove = Lists.newArrayList();
                getTownInventationsTime().forEach((uuid, l) -> {
                    if(System.currentTimeMillis() > l) {
                        townInventations.remove(uuid);
                        toRemove.add(uuid);
                    }
                });
                if(toRemove.size() > 0) {
                    toRemove.forEach(uuid -> townInventationsTime.remove(uuid));
                }
            }
            if(System.currentTimeMillis() >= residentTax) {
                getPlugin().getLogger().log(Level.INFO, "Resident tax time, set to " + dateFormat.format(setResidentTax()));
                Bukkit.broadcastMessage(prefix + "Die Steuern der §6Stadtbewohner§7 werden jetzt eingesammelt.");
                getManager().getTownManager().getTowns().forEach(t -> {
                    long tax = t.getTaxes();
                    AtomicLong total = new AtomicLong(0);
                    AtomicReference<Player> mayor = new AtomicReference<>(null);
                    getManager().getPlayerManager().getResidents(t).forEach(user -> {
                        long withdrawed = (long) (user.getBalance() * ((double) tax / 10000));
                        if(withdrawed > 5000)
                            withdrawed = 5000;
                        user.withdrawMoney(withdrawed);
                        total.set(total.get() + withdrawed);
                        Player to;
                        if((to = Bukkit.getPlayer(user.getKey())) != null) {
                            if(user.getTownRank().equals(TownRank.MAYOR)) {
                                mayor.set(to);
                            }
                            to.sendMessage(prefix + "§7Dir wurden §6" + ((Long) tax).doubleValue() / 100 + "% §7Steuern abgezogen. (" + formatValue(((Long) withdrawed).doubleValue() / 100) + ")");
                        }
                    });
                    t.depositMoney(total.get());
                    if(mayor.get() != null)
                        mayor.get().sendMessage(prefix + "§7Die Steuern haben dir §6" + formatValue(((Long) total.get()).doubleValue() / 100) + " §7in die Stadtkasse gebracht.");
                });
            }
            if(System.currentTimeMillis() >= townTax) {
                getPlugin().getLogger().log(Level.INFO, "Town tax time, set to " + dateFormat.format(setTownTax()));
                Bukkit.broadcastMessage(prefix + "Die §6Stadtsteuern §7werden jetzt eingesammelt.");
                getManager().getTownManager().getTowns().forEach(t -> {
                    long tax = getManager().getTownManager().getTownTax(t);
                    t.withdrawMoney(tax);
                    Player mayor;
                    if((mayor = Bukkit.getPlayer(getManager().getPlayerManager().getResidents(t).stream().filter(u -> u.checkTownMember() && u.getTownRank().equals(TownRank.MAYOR))
                            .findFirst().get().getKey())) != null) {
                        mayor.sendMessage(prefix + "§7Dir wurden die Stadtsteuern in Höhe von §6" + formatValue(((Long) tax).doubleValue() / 100) + " §7abgezogen.");
                    }
                });
            }
        }, 20*5, 20*5);
    }

    private void registerListeners() {
        registerListener(new PlayerListener(), new WorldEvents());
    }

    private void registerCommands() {
        new TownCommand();
        new TownAdminCommand();
        new TownMayorCommand();
        new PlotCommand();
        new TownChatCommand();
        new TownResidentCommand();
        new NationCommand();
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTask(scheduler);
    }
}
