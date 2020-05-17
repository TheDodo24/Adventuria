package de.thedodo24.commonPackage.player;

import com.google.common.collect.Lists;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.arango.ArangoWritable;
import com.arangodb.entity.BaseDocument;
import de.thedodo24.commonPackage.utils.ScoreboardManager;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class User implements ArangoWritable<UUID> {

    private UUID key;

    private Map<String, Object> values = new HashMap<>();


    public User(UUID key) {
        this.key = key;
        values.put("moneyBalance", (long) 0);
        values.put("ontime", new HashMap<String, Object>() {{
            put("totalOntime", (long) 0);
            put("weekOntime", (long) 0);
            put("dayOntime", (long) 0);
            put("afkTime", (long) 0);
            put("ontimeHistory", new HashMap<String, HashMap<String, Long>>() {{
                put("afkTime", new HashMap<>());
                put("ontime", new HashMap<>());
            }});
        }});
    }

    @Override
    public UUID getKey() {
        return this.key;
    }

    @Override
    public void read(BaseDocument document) {
        this.values = document.getProperties();
    }

    @Override
    public void save(BaseDocument document) {
        document.setProperties(values);
    }

    public Object getProperty(String key) {
        if (this.values.containsKey(key))
            return this.values.get(key);
        return null;
    }

    public <Type> void updateProperty(String property, Type value) {
        this.updateProperty$(property, value);
    }

    public <Type> void updateProperty$(String property, Type value) {
        if(this.values.containsKey(property))
            this.values.replace(property, value);
        else
            this.values.put(property, value);
        Common.getInstance().getManager().getPlayerManager().save(this);
    }

    public void deleteProperty(String property) {
        this.values.remove(property);
        Common.getInstance().getManager().getPlayerManager().save(this);
    }

    public boolean isSetProperty(String property) {
        return this.values.containsKey(property);
    }

    public String getName() {
        return (String) getProperty("name");
    }

    public void setName(String name) { updateProperty("name", name); }

    public long getBalance() {
        return (long) getProperty("moneyBalance");
    }

    public long depositMoney(long v) {
        long finalBalance = v + (long) getProperty("moneyBalance");
        updateProperty("moneyBalance", finalBalance);
        ScoreboardManager.getScoreboardMap().forEach((key, val) -> val.sendScoreboard(Bukkit.getPlayer(key)));
        return finalBalance;
    }

    public long withdrawMoney(long v) {
        long finalBalance = (long) getProperty("moneyBalance") - v;
        updateProperty("moneyBalance", finalBalance);
        ScoreboardManager.getScoreboardMap().forEach((key, val) -> val.sendScoreboard(Bukkit.getPlayer(key)));
        return finalBalance;
    }

    public long setBalance(long v) {
        updateProperty("moneyBalance", v);
        ScoreboardManager.getScoreboardMap().forEach((key, val) -> val.sendScoreboard(Bukkit.getPlayer(key)));
        return v;
    }

    public void setJailed(int blocks) {
        Map<String, Integer> jailedParameters = new HashMap<>();
        if(isJailed()) {
            jailedParameters.put("max", getMaxJailBlocks());
        } else {
            jailedParameters.put("max", blocks);
        }

        jailedParameters.put("destroyed", blocks);
        updateProperty("jail", jailedParameters);
    }

    public boolean isJailed() {
        return isSetProperty("jail");
    }

    public void unjail() {
        deleteProperty("jail");
    }

    public int getDestroyedJailBlocks() {
        if(this.values.containsKey("jail"))
            return ((Map<String, Integer>) getProperty("jail")).get("destroyed");
        return 0;
    }

    public int getMaxJailBlocks() {
        if(this.values.containsKey("jail"))
            return ((Map<String, Integer>) getProperty("jail")).get("max");
        return 0;
    }

    public Map<String, Object> getOntimeMap() {
        Map<String, Object> ontimeMap = (Map<String, Object>) getProperty("ontime");
        ontimeMap.remove("ontimeHistory");
        return ontimeMap;
    }

    public long getTotalOntime() {
        return (Long) ((Map<String, Object>) getProperty("ontime")).get("totalOntime");
    }

    public long getWeekOntime() {
        return (Long) ((Map<String, Object>) getProperty("ontime")).get("weekOntime");
    }

    public long getDayOntime() {
        return (Long) ((Map<String, Object>) getProperty("ontime")).get("dayOntime");
    }

    public long getAfkTime() {
        return (Long) ((Map<String, Object>) getProperty("ontime")).get("afkTime");
    }

    public long updateTotalOntime(long ontime) {
        Map<String, Object> ontimeMap = (Map<String, Object>) getProperty("ontime");
        ontimeMap.replace("totalOntime", (long) ontimeMap.get("totalOntime") + ontime);
        updateProperty("ontime", ontimeMap);
        return (long) ontimeMap.get("totalOntime");
    }

    public long updateWeekOntime(long ontime) {
        Map<String, Object> ontimeMap = (Map<String, Object>) getProperty("ontime");
        ontimeMap.replace("weekOntime", (long) ontimeMap.get("weekOntime") + ontime);
        updateProperty("ontime", ontimeMap);
        return (long) ontimeMap.get("weekOntime");
    }

    public long updateDayOntime(long ontime) {
        Map<String, Object> ontimeMap = (Map<String, Object>) getProperty("ontime");
        ontimeMap.replace("dayOntime", (long) ontimeMap.get("dayOntime") + ontime);
        updateProperty("ontime", ontimeMap);
        return (long) ontimeMap.get("dayOntime");
    }

    public long updateAfkTime(long ontime) {
        Map<String, Object> ontimeMap = (Map<String, Object>) getProperty("ontime");
        ontimeMap.replace("afkTime", (long) ontimeMap.get("afkTime") + ontime);
        updateProperty("ontime", ontimeMap);
        return (long) ontimeMap.get("afkTime");
    }

    public void updateOntime(long ontime) {
        updateTotalOntime(ontime);
        updateWeekOntime(ontime);
        updateDayOntime(ontime);
    }

    public void setTotalOntime(long ontime) {
        Map<String, Object> ontimeMap = (Map<String, Object>) getProperty("ontime");
        ontimeMap.replace("totalOntime", ontime);
        updateProperty("ontime", ontimeMap);
    }
    public void setWeekOntime(long ontime) {
        Map<String, Object> ontimeMap = (Map<String, Object>) getProperty("ontime");
        ontimeMap.replace("weekOntime", ontime);
        updateProperty("ontime", ontimeMap);
    }
    public void setDayOntime(long ontime) {
        Map<String, Object> ontimeMap = (Map<String, Object>) getProperty("ontime");
        ontimeMap.replace("dayOntime", ontime);
        updateProperty("ontime", ontimeMap);
    }
    public void setAfkTime(long ontime) {
        Map<String, Object> ontimeMap = (Map<String, Object>) getProperty("ontime");
        ontimeMap.replace("afkTime", ontime);
        updateProperty("ontime", ontimeMap);
    }

    public Map<String, Long> getOntimeHistoryMap() {
        return ((Map<String, HashMap<String, Long>>) ((Map<String, Object>) getProperty("ontime")).get("ontimeHistory")).get("ontime");
    }

    public Map<String, Long> getAfkTimeHistoryMap() {
        return ((Map<String, HashMap<String, Long>>) ((Map<String, Object>) getProperty("ontime")).get("ontimeHistory")).get("afkTime");
    }

    public long getOntimeHistory(int week) {
        if(week >= 1 && week <= 9) {
            return ((Map<String, Map<String, Long>>) ((Map<String, Object>) getProperty("ontime")).get("ontimeHistory")).get("ontime").get(week);
        }
        return 0;
    }

    public long getAfkHistory(int week) {
        if(week >= 1 && week <= 9) {
            return ((Map<String, Map<String, Long>>) ((Map<String, Object>) getProperty("ontime")).get("ontimeHistory")).get("afkTime").get(week);
        }
        return 0;
    }

    public long getAfkOfTheWeekHistory(int week) {
        if(week >= 1 && week <= 9) {
            Map<String, Object> ontimeMap = (Map<String, Object>) getProperty("ontime");
            Map<String, Map<String, Long>> ontimeHistoryMap = (Map<String, Map<String, Long>>) ontimeMap.get("ontimeHistory");
            Map<String, Long> afkTimeHistoryMap = ontimeHistoryMap.get("afkTime");
            return afkTimeHistoryMap.get(week) - afkTimeHistoryMap.get(week + 1);
        }
        return 0;
    }

    public void addOntimeHistory(long ontime, long afkTime) {
        Map<String, Object> ontimeMap = (Map<String, Object>) getProperty("ontime");
        Map<String, Map<String, Long>> ontimeHistoryMap = (Map<String, Map<String, Long>>) ontimeMap.get("ontimeHistory");
        Map<String, Long> historyOntimeMap = ontimeHistoryMap.get("ontime");
        Map<String, Long> afkTimeHistoryMap = ontimeHistoryMap.get("afkTime");

        Map<String, Long> newHistoryOntimeMap = new HashMap<>();
        historyOntimeMap.forEach((key, value) -> {
            if(Integer.parseInt(key) < 9)
                newHistoryOntimeMap.put(String.valueOf(Integer.parseInt(key) + 1), value);
        });
        Map<String, Long> newAfkTimeHistoryMap = new HashMap<>();
        afkTimeHistoryMap.forEach((key, value) -> {
            if(Integer.parseInt(key) < 10)
                newAfkTimeHistoryMap.put(String.valueOf(Integer.parseInt(key) + 1), value);
        });
        newHistoryOntimeMap.put("1", ontime);
        newAfkTimeHistoryMap.put("1", afkTime);
        ontimeHistoryMap.replace("ontime", newHistoryOntimeMap);
        ontimeHistoryMap.replace("afkTime", newAfkTimeHistoryMap);
    }

}
