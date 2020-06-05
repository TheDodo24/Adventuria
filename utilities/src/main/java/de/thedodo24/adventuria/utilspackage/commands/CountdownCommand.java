package de.thedodo24.adventuria.utilspackage.commands;

import com.google.common.collect.Lists;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.utils.TimeFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class CountdownCommand implements CommandExecutor, TabCompleter {

    private String prefix = "§7§l| §aAdventuria §7» ";

    /*
    -t
    -c

     */

    int scheduler;
    boolean runningTask;

    public CountdownCommand() {
        PluginCommand fullCmd = Common.getInstance().getPlugin().getCommand("countdown");
        PluginCommand cmd = Common.getInstance().getPlugin().getCommand("cd");
        fullCmd.setExecutor(this);
        fullCmd.setTabCompleter(this);
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(s.hasPermission("advi.countdown")) {
            if(args.length > 0) {
                if(args.length == 1) {
                    if(args[0].equalsIgnoreCase("options")) {
                        s.sendMessage(prefix + "§a-t §7|| §aZeitangabe in Sekunden §7(Standard: 60 Sekunden)\n" +
                                prefix + "§a-c §7|| §aCommand ausführen am Ende des Countdowns §7(Standard: keiner)\n" +
                                prefix + "§a-cmsg §7|| §aEine Custom-Message beim Ende des Countdowns anzeigen §7(Standard: \"§4Los!§7\")");
                        return true;
                    }
                }
                if(runningTask) {
                    s.sendMessage(prefix + "Es läuft bereits ein §aCountdown§7.");
                    return false;
                }
                List<String> argList = Arrays.asList(args);
                List<String> reversedList = new ArrayList<>(Arrays.asList(args));
                Collections.reverse(reversedList);
                AtomicBoolean command = new AtomicBoolean(false);
                AtomicReference<String> commandString = new AtomicReference<>("");
                AtomicReference<String> customMessage = new AtomicReference<>("§4Los!");
                int time = 60;
                AtomicBoolean stop = new AtomicBoolean(false);
                for (String string : argList) {
                    if (string.toLowerCase().equalsIgnoreCase("-c")) {
                        int index = argList.indexOf(string);
                        String next = argList.get(index + 1);
                        if (next.startsWith("-") || next.startsWith("/")) {
                            s.sendMessage(prefix + "Ein Command kann nicht mit einem §a- §7oder §a/ §7beginnen.");
                            stop.set(true);
                            break;
                        }
                        command.set(true);
                        commandString.set(next.replace("_", " "));
                    } else if (string.toLowerCase().equalsIgnoreCase("-t")) {
                        int index = argList.indexOf(string);
                        String next = argList.get(index + 1);
                        int i;
                        try {
                            i = Integer.parseInt(next);
                        } catch (NumberFormatException e) {
                            s.sendMessage(prefix + "Die Zeit muss ganzzahlig und positiv sein.");
                            stop.set(true);
                            break;
                        }
                        if (i > 0) {
                            time = i;
                        } else {
                            s.sendMessage(prefix + "Die Zeit muss ganzzahlig und positiv sein.");
                            stop.set(true);
                            break;
                        }
                    } else if(string.toLowerCase().equalsIgnoreCase("-cmsg")) {
                        int index = argList.indexOf(string);
                        String next = argList.get(index + 1);
                        customMessage.set(ChatColor.translateAlternateColorCodes('&', next.replace("_", " ")));
                    }
                }
                if(!stop.get()) {
                    if(time >= 60 && (time % 60) != 0) {
                        s.sendMessage(prefix + "§7Bitte benutze für Zeitangaben, über einer Minute, nur Zahlen die durch 60 teilbar sind.");
                        return false;
                    }
                    if(time < 60 && (time % 10) != 0) {
                        s.sendMessage(prefix + "§7Bitte benutze für Zeitangaben, unter einer Minute, nur Zahlen die durch 10 teilbar sind.");
                        return false;
                    }
                    s.sendMessage(prefix + "§7Der Countdown wurde gestartet.");
                    String msg;
                    if(reversedList.stream().anyMatch(arg -> arg.startsWith("-")))
                        msg = argList.stream().skip(argList.indexOf(reversedList.stream().filter(arg -> arg.startsWith("-")).findFirst().get()) + 2).collect(Collectors.joining(" "));
                    else
                        msg = String.join(" ", argList);
                    msg = ChatColor.translateAlternateColorCodes('&', msg);
                    if(time >= 60)
                        Bukkit.broadcastMessage(prefix + msg + "§7: In §a" + TimeFormat.getOutOfSeconds(time));
                    else
                        Bukkit.broadcastMessage(prefix + msg + "§7: In §a" + time + " Sekunden");
                    runningTask = true;
                    AtomicInteger finalTime = new AtomicInteger(time);
                    scheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(Common.getInstance().getPlugin(), () -> {
                        if(finalTime.get() >= 60) {
                            if((finalTime.get() % 60) == 0) {
                                Bukkit.broadcastMessage(prefix + "Noch §a" + TimeFormat.getOutOfSeconds(finalTime.get()) + "§7...");
                            }
                        } else {
                            if(finalTime.get() > 10) {
                                if((finalTime.get() % 10) == 0) {
                                    Bukkit.broadcastMessage(prefix + "Noch §c" + finalTime.get() + " Sekunden§7...");
                                }
                            } else {
                                if(finalTime.get() > 0) {
                                    Bukkit.broadcastMessage(prefix + "Noch §4" + (finalTime.get() == 1 ? finalTime.get() + " Sekunde" : finalTime.get() + " Sekunden") + "§7...");
                                } else if(finalTime.get() == 0) {
                                    Bukkit.broadcastMessage(prefix + customMessage.get());
                                    if(command.get()) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandString.get());
                                    }
                                    runningTask = false;
                                    Bukkit.getScheduler().cancelTask(scheduler);
                                }
                            }
                        }
                        finalTime.set(finalTime.decrementAndGet());
                    }, 20, 20);
                }
            } else {
                s.sendMessage(prefix + "§a/" + label + " [Optionen] [Nachricht]");
            }
        } else {
            s.sendMessage("§cYou do not have the permission to execute this command. (advi.countdown)");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        if(args.length == 1) {
            return Common.getInstance().removeAutoComplete(Lists.newArrayList("-t", "-c", "-cmsg"), args[0]);
        } else if(args.length == 2) {
            switch(args[0].toLowerCase()) {
                case "-t":
                    return Common.getInstance().removeAutoComplete(Lists.newArrayList("30", "60", "120", "180", "240", "300"), args[1]);
                case "-c":
                case "-cmsg":
                    return Lists.newArrayList("");
            }
        } else if(args.length == 3) {
            return Common.getInstance().removeAutoComplete(Lists.newArrayList("-t", "-c", "-cmsg"), args[2]);
        } else if(args.length == 4) {
            switch(args[2].toLowerCase()) {
                case "-t":
                    return Common.getInstance().removeAutoComplete(Lists.newArrayList("30", "60", "120", "180", "240", "300"), args[3]);
                case "-c":
                case "-cmsg":
                    return Lists.newArrayList("");
            }
        }
        return Lists.newArrayList();
    }
}
