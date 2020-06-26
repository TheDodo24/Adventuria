package de.thedodo24.commonPackage.towny;

import com.arangodb.entity.BaseDocument;
import com.google.common.collect.Lists;
import com.sun.org.apache.xpath.internal.operations.Bool;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.arango.ArangoWritable;
import de.thedodo24.commonPackage.utils.ManagerScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class Town implements ArangoWritable<String> {

    private String key;
    private Map<String, Object> values;

    public Town(String key) {
        this.key = key;
        values = new HashMap<>();
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }

    public Map<String, Object> getValues() {
        return values;
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

    public Object getProperty(java.lang.String key) {
        if (this.values.containsKey(key))
            return this.values.get(key);
        return null;
    }

    public <Type> void updateProperty(java.lang.String property, Type value) {
        this.updateProperty$(property, value);
    }

    public <Type> void updateProperty$(java.lang.String property, Type value) {
        if(this.values.containsKey(property))
            this.values.replace(property, value);
        else
            this.values.put(property, value);
        Common.getInstance().getManager().getTownManager().save(this);
    }

    public void deleteProperty(java.lang.String property) {
        this.values.remove(property);
        Common.getInstance().getManager().getTownManager().save(this);
    }

    public boolean isSetProperty(java.lang.String property) {
        return this.values.containsKey(property);
    }

    public String getRank() {
        int residents = Common.getInstance().getManager().getPlayerManager().getResidents(this).size();
        if(residents == 0)
            return "Ruinen";
        if(residents == 1)
            return "Siedlung";
        if(residents == 2)
            return "Kleines Dorf";
        if(residents <= 6)
            return "Dorf";
        if(residents <= 10)
            return "Kleinstadt";
        if(residents <= 14)
            return "Stadt";
        if(residents <= 24)
            return "Großstadt";
        if(residents <= 28)
            return "Metropole";
        return "Megacity";
    }

    public String getNation() {
        return (String) getProperty("nation");
    }

    public boolean checkNation() {
        return isSetProperty("nation");
    }

    public void setNation(String name) {
        updateProperty("nation", name);
    }

    public String getName() {
        return (String) getProperty("name");
    }

     public String getBlackboard() {
        return (String) getProperty("blackboard");
     }

     public void setBlackboard(String blackboard) {
        updateProperty("blackboard", blackboard);
     }

     public long getCreated() {
        return (long) getProperty("created");
     }

     public boolean isPublic() {
        return (boolean) getProperty("public");
     }

     public void setPublic(boolean publicity) {
        updateProperty("public", publicity);
     }

     public long getMoney() {
        return (long) getProperty("balance");
     }

    public void depositMoney(long v) {
        updateProperty("balance", ((long) getProperty("balance")) + v);
    }
    public void withdrawMoney(long v) {
        updateProperty("balance", ((long) getProperty("balance")) - v);
    }

    public long getTaxes() {
        return (long) getProperty("taxes");
    }

    public void setTaxes(long taxes) {
        updateProperty("taxes", taxes);
    }

    public Location getSpawn() {
        return Location.deserialize((Map<String, Object>) getProperty("spawn"));
    }

    public void setSpawn(Location loc) {
        updateProperty("spawn", loc.serialize());
    }

    public int getBuyedTownSize() {
        return ((Long) getProperty("townsize")).intValue();
    }

    public void addBuyedTownSize(int size) {
        setBuyedTownSize(getBuyedTownSize() + size);
    }

    public void setBuyedTownSize(int size) {
        updateProperty("townsize", (long) size);
    }

    public boolean hasOutposts() {
        return ((Map<String, Map<String, Object>>) getProperty("outposts")).size() > 0;
    }

    public void addOutpost(String name, Location loc) {
        Map<String, Map<String, Object>> outposts = (Map<String, Map<String, Object>>) getProperty("outposts");
        outposts.put(name, loc.serialize());
        values.replace("outposts", outposts);
    }

    public Location getOutpostSpawn(String name) {
        return Location.deserialize(((Map<String, Map<String, Object>>) getProperty("outposts")).get(name));
    }

    public boolean checkOutpost(String name) {
        return ((Map<String, Map<String, Object>>) getProperty("outposts")).keySet().stream().anyMatch(n -> n.equalsIgnoreCase(name));
    }

    public void updateSpawn(String name, Location loc) {
        Map<String, Map<String, Object>> outposts = (Map<String, Map<String, Object>>) getProperty("outposts");
        outposts.replace(name, loc.serialize());
        updateProperty("outposts", outposts);
    }

    public void removeOutpost(String name) {
        List<Plot> plots = Common.getInstance().getManager().getPlotManager().getPlots(this);
        plots.stream().filter(Plot::isOutpostPlot).filter(p -> p.getOutpost().equalsIgnoreCase(name)).forEach(p -> Common.getInstance().getManager().getPlotManager().delete(p.getKey()));
        Map<String, Map<String, Object>> outposts = (Map<String, Map<String, Object>>) getProperty("outposts");
        outposts.remove(name);
        updateProperty("outposts", outposts);
    }

    public void renameOutpost(String oldName, String newName) {
        List<Plot> plots = Common.getInstance().getManager().getPlotManager().getPlots(this);
        plots.stream().filter(Plot::isOutpostPlot).filter(pl -> pl.getOutpost().equalsIgnoreCase(oldName)).forEach(pl -> pl.setOutpost(newName));
        Map<String, Map<String, Object>> outposts = (Map<String, Map<String, Object>>) getProperty("outposts");
        Map<String, Object> location = outposts.get(oldName);
        outposts.remove(oldName);
        outposts.put(newName, location);
        updateProperty("outposts", outposts);
    }

    public Map<String, Location> getOutposts() {
        Map<String, Map<String, Object>> outpostMap = (Map<String, Map<String, Object>>) getProperty("outposts");
        Map<String, Location> map = new HashMap<>();
        outpostMap.forEach((key, val) -> map.put(key, Location.deserialize(val)));
        return map;
    }
}
