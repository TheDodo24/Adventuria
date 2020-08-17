package de.thedodo24.commonPackage.player;

import com.google.common.collect.Lists;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.arango.ArangoWritable;
import com.arangodb.entity.BaseDocument;
import de.thedodo24.commonPackage.towny.Town;
import de.thedodo24.commonPackage.towny.TownRank;
import de.thedodo24.commonPackage.utils.ManagerScoreboard;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Getter
public class User implements ArangoWritable<UUID> {

    private UUID key;

    public Map<String, Object> values = new HashMap<>();


    public User(UUID key) {
        this.key = key;
        values.put("moneyBalance", (long) 25000);
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
        values.put("scoreboard", new HashMap<String, HashMap<String, String>>() {{
            put("0", new HashMap<String, String>() {{
                put("type", CustomScoreboardType.MONEY.toString());
                put("value", "");
            }});
            put("1", new HashMap<String, String>() {{
                put("type", CustomScoreboardType.ONLINE.toString());
                put("value", "");
            }});
        }});
        values.put("friends", Lists.newArrayList());
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

    // ECONOMY

    public long getBalance() {
        return (long) getProperty("moneyBalance");
    }

    public long depositMoney(long v) {
        long finalBalance = v + (long) getProperty("moneyBalance");
        updateProperty("moneyBalance", finalBalance);
        Executors.newSingleThreadExecutor().execute(() -> ManagerScoreboard.getScoreboardMap().forEach((key, val) -> val.sendScoreboard(Bukkit.getPlayer(key))));
        return finalBalance;
    }

    public long withdrawMoney(long v) {
        long finalBalance = (long) getProperty("moneyBalance") - v;
        updateProperty("moneyBalance", finalBalance);
        Executors.newSingleThreadExecutor().execute(() -> ManagerScoreboard.getScoreboardMap().forEach((key, val) -> val.sendScoreboard(Bukkit.getPlayer(key))));
        return finalBalance;
    }

    public long setBalance(long v) {
        updateProperty("moneyBalance", v);
        Executors.newSingleThreadExecutor().execute(() -> ManagerScoreboard.getScoreboardMap().forEach((key, val) -> val.sendScoreboard(Bukkit.getPlayer(key))));
        return v;
    }

    // JAIL

    public void setJailed(int blocks) {
        Map<String, Long> jailedParameters = new HashMap<>();
        if(isJailed()) {
            jailedParameters.put("max", (long) getMaxJailBlocks());
        } else {
            jailedParameters.put("max", (long) blocks);
        }

        jailedParameters.put("destroyed", (long) blocks);
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
            return ((Map<String, Long>) getProperty("jail")).get("destroyed").intValue();
        return 0;
    }

    public int getMaxJailBlocks() {
        if(this.values.containsKey("jail"))
            return ((Map<String, Long>) getProperty("jail")).get("max").intValue();
        return 0;
    }

    // CUSTOM SCOREBOARD

    public void setCustomScoreboard(Map<String, Map<String, String>> customScoreboard) {
        updateProperty("scoreboard", customScoreboard);
    }

    public void unsetScoreboard(CustomScoreboardType type) {
        Map<String, Map<String, String>> map = ((Map<String, Map<String, String>>) getProperty("scoreboard"));
        map.keySet().stream().filter(key -> map.get(key).get("type").equalsIgnoreCase(type.toString())).forEach(map::remove);
        setCustomScoreboard(map);
    }

    public void setCustomScoreboard(int place, Map<String, String> customScoreboard) {
        Map<String, Map<String, String>> scoreboard = ((Map<String, Map<String, String>>) getProperty("scoreboard"));
        if(scoreboard.containsKey(place))
            scoreboard.replace(String.valueOf(place), customScoreboard);
        else
            scoreboard.put(String.valueOf(place), customScoreboard);
    }

    public void unsetCustomScoreboard(int place) {

        ((Map<String, Map<String, String>>) getProperty("scoreboard")).remove(String.valueOf(place));
    }

    public boolean checkCustomScoreboard(int place) {
        return ((Map<String, Map<String, String>>) getProperty("scoreboard")).containsKey(String.valueOf(place));
    }

    public boolean checkCustomScoreboard(CustomScoreboardType type) {
        return ((Map<String, Map<String, String>>) getProperty("scoreboard")).values().stream().map(map -> map.get("type")).anyMatch(string -> type.toString().equalsIgnoreCase(string));
    }

    public Integer getCustomScoreboardLine(CustomScoreboardType type) {
        Map<String, Map<String, String>> map = ((Map<String, Map<String, String>>) getProperty("scoreboard"));
        return Integer.parseInt(map.keySet().stream().filter(key -> map.get(key).get("type").equalsIgnoreCase(type.toString())).findFirst().get());
    }

    public Map<String, String> getCustomScoreboard(int place) {
        Map<String, Map<String, String>> scoreboard = ((Map<String, Map<String, String>>) getProperty("scoreboard"));
        if(scoreboard.containsKey(String.valueOf(place)))
            return scoreboard.get(String.valueOf(place));
        return null;
    }

    public Map<String, Map<String, String>> getCustomScoreboard() {
        return ((Map<String, Map<String, String>>) getProperty("scoreboard"));
    }

    // ONTIME

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

    // TOWN

    public boolean checkTownMember() {
        return isSetProperty("town");
    }

    public void setTown(String townName, TownRank rank) {
        updateProperty("town", new HashMap<String, Object>() {{ put("name", townName); put("rank", rank.toString()); }});
    }

    public void removeTown() {
        deleteProperty("town");
    }

    public String getTownString() {
        Map<String, Object> townMap = (Map<String, Object>) getProperty("town");
        return (String) townMap.get("name");
    }

    public Town getTown() {
        Map<String, Object> townMap = (Map<String, Object>) getProperty("town");
        return Common.getInstance().getManager().getTownManager().get((String) townMap.get("name"));
    }

    public TownRank getTownRank() {
        Map<String, Object> townMap = (Map<String, Object>) getProperty("town");
        return TownRank.valueOf((String) townMap.get("rank"));
    }

    public void updateTownRank(TownRank rank) {
        Map<String, Object> townMap = (Map<String, Object>) getProperty("town");
        townMap.replace("rank", rank.toString());
        updateProperty("town", townMap);
    }

    public List<String> getFriends() {
        return (List<String>) getProperty("friends");
    }

    public boolean checkFriend(UUID uuid) {
        return ((List<String>) getProperty("friends")).stream().anyMatch(f -> f.equalsIgnoreCase(uuid.toString()));
    }

    public void addFriend(UUID uuid) {
        List<String> friends = (List<String>) getProperty("friends");
        friends.add(uuid.toString());
        updateProperty("friends", friends);
    }

    public void removeFriend(UUID uuid) {
        List<String> friends = (List<String>) getProperty("friends");
        friends.remove(uuid.toString());
        updateProperty("friends", friends);
    }

    // TEAM MANAGEMENT

    public boolean isTeamHead() {
        return isSetProperty("team");
    }

    public void addTeam(Teams team) {
        List<String> teamList = Lists.newArrayList(team.toString());
        if(isSetProperty("team")) {
            List<String> existingTeams = (List<String>) getProperty("team");
            teamList.addAll(existingTeams);
        }
        updateProperty("team", teamList);
    }

    public void removeTeam(Teams team) {
        List<String> teamList = (List<String>) getProperty("team");
        teamList.remove(team.toString());
        updateProperty("team", teamList);
    }

    public List<Teams> getTeams() {
        return ((List<String>) getProperty("team")).stream().map(Teams::valueOf).collect(Collectors.toList());
    }

    public boolean isTeam(Teams team) {
        return ((List<String>) getProperty("team")).stream().anyMatch(s -> team.toString().equalsIgnoreCase(s));
    }

}
