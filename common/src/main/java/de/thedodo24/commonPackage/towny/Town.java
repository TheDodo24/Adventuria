package de.thedodo24.commonPackage.towny;

import com.arangodb.entity.BaseDocument;
import de.thedodo24.commonPackage.arango.ArangoWritable;

import java.util.HashMap;
import java.util.Map;

public class Town implements ArangoWritable<String> {

    private String key;
    private Map<String, Object> values;

    public Town(String key) {
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
}
