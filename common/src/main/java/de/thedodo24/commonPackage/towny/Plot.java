package de.thedodo24.commonPackage.towny;

import com.arangodb.entity.BaseDocument;
import com.sun.org.apache.xpath.internal.operations.Bool;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.arango.ArangoWritable;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class Plot implements ArangoWritable<String> {

    private String key;
    private Map<String, Object> values;

    public Plot(String key) {
        this.key = key;
        this.values = new HashMap<>();
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void read(BaseDocument document) {
        values = document.getProperties();
    }

    @Override
    public void save(BaseDocument document) {
        document.setProperties(values);
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }

    public Object getProperty(java.lang.String key) {
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
        Common.getInstance().getManager().getPlotManager().save(this);
    }

    public void deleteProperty(java.lang.String property) {
        this.values.remove(property);
        Common.getInstance().getManager().getPlotManager().save(this);
    }

    public boolean isSetProperty(java.lang.String property) {
        return this.values.containsKey(property);
    }

    public boolean isOwned() {
        return isSetProperty("owner");
    }

    public UUID getOwner() {
        return UUID.fromString((String) getProperty("owner"));
    }

    public void removeOwner() {
        deleteProperty("owner");
    }

    public void setOwner(UUID uuid) {
        updateProperty("owner", uuid.toString());
    }

    public void setName(String name) {
        updateProperty("name", name);
    }

    public String getName() {
        return (String) getProperty("name");
    }

    public Town getTown() {
        return Common.getInstance().getManager().getTownManager().get((String) getProperty("town"));
    }

    public void setTown(Town town) {
        updateProperty("town", town.getKey());
    }

    public Chunk getChunk() {
        return Bukkit.getWorld("Freebuild").getChunkAt(Long.parseLong(getKey()));
    }

    public boolean isBuyable() {
        return isSetProperty("buy");
    }

    public void setBuyable(long price) {
        updateProperty("buy", price);
    }

    public void unSetBuyable() {
        deleteProperty("buy");
    }

    public long getBuyPrice() { return (long) getProperty("buy"); }

    public boolean isOutpostPlot() {
        return isSetProperty("outpost");
    }

    public String getOutpost() {
        return (String) getProperty("outpost");
    }

    public void setOutpost(String name) { updateProperty("outpost", name);}

    public Map<TownPermission, Map<PlotPlayer, Boolean>> getPermissionMap() {
        Map<String, Map<String, Boolean>> permissionProperty = (Map<String, Map<String, Boolean>>) getProperty("permissions");
        Map<TownPermission, Map<PlotPlayer, Boolean>> returnAble = new HashMap<>();
        permissionProperty.keySet().stream().map(TownPermission::valueOf).forEach(permission -> {
            Map<String, Boolean> townPlayerMapString = permissionProperty.get(permission.toString());
            Map<PlotPlayer, Boolean> townPlayerMap = new HashMap<>();
            townPlayerMapString.keySet().stream().map(PlotPlayer::valueOf).forEach(townPlayer -> townPlayerMap.put(townPlayer, townPlayerMapString.get(townPlayer.toString())));
            returnAble.put(permission, townPlayerMap);
        });
        return returnAble;
    }

    public Map<PlotPlayer, Boolean> getPermission(TownPermission permission) {
        Map<String, Map<String, Boolean>> permissionMap = (Map<String, Map<String, Boolean>>) getProperty("permissions");
        Map<PlotPlayer, Boolean> returnAble = new HashMap<>();
        permissionMap.get(permission).keySet().stream().map(PlotPlayer::valueOf).forEach(townPlayer -> returnAble.put(townPlayer, permissionMap.get(permission.toString()).get(townPlayer.toString())));
        return returnAble;
    }

    public boolean getPermission(TownPermission permission, PlotPlayer player) {
        Map<String, Map<String, Boolean>> permissionMap = (Map<String, Map<String, Boolean>>) getProperty("permissions");
        return permissionMap.get(permission.toString()).get(player.toString());
    }

    public void setPermission(Map<String, Map<String, Boolean>> permission) {
        updateProperty("permissions", permission);
    }

    public void updatePermission(TownPermission permission, PlotPlayer player, boolean bool) {
        Map<String, Map<String, Boolean>> permissionMap = (Map<String, Map<String, Boolean>>) getProperty("permissions");
        Map<String, Boolean> playerMap = permissionMap.get(permission.toString());
        playerMap.replace(player.toString(), bool);
        Map<String, Map<String, Boolean>> newPermissionMap = new HashMap<>();
        permissionMap.forEach((key, val) -> {
            if(key.equalsIgnoreCase(permission.toString())) {
                newPermissionMap.put(key, playerMap);
            } else {
                newPermissionMap.put(key, val);
            }
        });
        updateProperty("permissions", newPermissionMap);
    }

    public Map<String, Boolean> getSettings() {
        return (Map<String, Boolean>) getProperty("settings");
    }

    public boolean getSetting(String setting) {
        return ((Map<String, Boolean>) getProperty("settings")).get(setting);
    }

    public void setSetting(String setting, boolean bool) {
        Map<String, Boolean> settingsMap = getSettings();
        settingsMap.replace(setting, bool);
        updateProperty("settings", settingsMap);
    }
}