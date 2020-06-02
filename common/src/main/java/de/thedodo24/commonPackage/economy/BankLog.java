package de.thedodo24.commonPackage.economy;

import com.arangodb.entity.BaseDocument;
import de.thedodo24.commonPackage.arango.ArangoWritable;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class BankLog implements ArangoWritable<String> {

    private Map<String, Object> values;

    private String key;

    public BankLog(String key) {
        this.key = key;
        values = new HashMap<String, Object>() {{
            put("history", new HashMap<>());
        }};
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

    public Map<String, Map<String, Object>> getHistory() {
        return (Map<String, Map<String, Object>>) getValues().get("history");
    }

    public Map<String, Object> getHistoryAt(long timestamp) {
        return ((Map<String, Map<String, Object>>) getValues().get("history")).get(String.valueOf(timestamp));
    }

    public void addHistory(long timestamp, BankLogType type, String player, long value) {
        ((Map<String, Map<String, Object>>) getValues().get("history")).put(String.valueOf(timestamp), new HashMap<String, Object>() {{ put("player", player); put("type", type.toString()); put("value", value); }});
    }

}
