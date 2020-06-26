package de.thedodo24.commonPackage.towny;

import com.arangodb.entity.BaseDocument;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.arango.ArangoWritable;
import de.thedodo24.commonPackage.player.User;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class Nation implements ArangoWritable<String> {

    private String key;
    private Map<String, Object> values;


    public Nation(String key) {
        this.key = key;
        values = new HashMap<>();
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

    public <Type> void updateProperty(String property, Type value) {
        this.updateProperty$(property, value);
    }

    public <Type> void updateProperty$(String property, Type value) {
        if(this.values.containsKey(property))
            this.values.replace(property, value);
        else
            this.values.put(property, value);
        Common.getInstance().getManager().getNationManager().save(this);
    }

    public void deleteProperty(java.lang.String property) {
        this.values.remove(property);
        Common.getInstance().getManager().getNationManager().save(this);
    }

    public boolean isSetProperty(java.lang.String property) {
        return this.values.containsKey(property);
    }


    public String getName() {
        return (String) getProperty("name");
    }

    public List<Town> getTowns() {
        return ((List<String>) getProperty("towns")).stream().map(st -> Common.getInstance().getManager().getTownManager().get(st)).collect(Collectors.toList());
    }

    public boolean checkTowns(String name) {
        return getTowns().stream().map(Town::getKey).anyMatch(st -> st.equalsIgnoreCase(name));
    }

    public void addTown(Town t) {
        List<String> towns = (List<String>) getProperty("towns");
        towns.add(t.getKey());
        updateProperty("towns", towns);
    }

    public void removeTown(Town t) {
        List<String> towns = (List<String>) getProperty("towns");
        towns.remove(t.getKey());
        updateProperty("towns", towns);
    }

    public long getTaxes() {
        return (long) getProperty("taxes");
    }

    public void setTaxes(long taxes) {
        updateProperty("taxes", taxes);
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

    public User getKing() {
        return Common.getInstance().getManager().getPlayerManager().get(UUID.fromString((String) getProperty("king")));
    }

    public void setKing(User user) {
        updateProperty("king", user.getKey().toString());
    }

    public Town getCapital() {
        return Common.getInstance().getManager().getTownManager().get((String) getProperty("capital"));
    }

    public void setCapital(Town town) {
        updateProperty("capital", town.getKey());
    }

}
