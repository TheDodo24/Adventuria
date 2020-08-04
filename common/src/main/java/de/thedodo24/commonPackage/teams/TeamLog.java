package de.thedodo24.commonPackage.teams;

import com.arangodb.entity.BaseDocument;
import com.google.common.collect.Lists;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.arango.ArangoWritable;

import java.util.*;
import java.util.stream.Collectors;

public class TeamLog implements ArangoWritable<UUID> {

    private UUID uuid;
    private Map<String, Object> values;

    public TeamLog(UUID key) {
        this.uuid = key;
        this.values = new HashMap<>();
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
        Common.getInstance().getManager().getLogManager().save(this);
    }

    public void deleteProperty(String property) {
        this.values.remove(property);
        Common.getInstance().getManager().getLogManager().save(this);
    }

    public boolean isSetProperty(String property) {
        return this.values.containsKey(property);
    }

    @Override
    public UUID getKey() {
        return uuid;
    }

    @Override
    public void read(BaseDocument document) {
        document.setProperties(values);
    }

    @Override
    public void save(BaseDocument document) {
        values = document.getProperties();
    }

    public void addEntry(long start, long end) {
        updateProperty(String.valueOf(start), end);
    }


    public boolean hasEntry(long s) {
        return isSetProperty(s + "");
    }

    public HashMap<String, Long> getEntry(long start) {
        long end = (long) getProperty(start + "");
        return new HashMap<String, Long>() {{
            put("start", start);
            put("end", end);
        }};
    }

    public List<HashMap<String, Long>> getEntries() {
        List<HashMap<String, Long>> v = Lists.newArrayList();
        List<String> keySet = new ArrayList<>(this.values.keySet());
        Collections.sort(keySet);
        for(int i = keySet.size() - 1; i >= 0; i--) {
            long start = Long.parseLong(keySet.get(i));
            v.add(new HashMap<String, Long>() {{ put("start", start); put("end", (long) getProperty(start + ""));}});
        }
        return v;
    }


}
