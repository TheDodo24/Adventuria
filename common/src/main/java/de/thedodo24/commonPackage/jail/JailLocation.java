package de.thedodo24.commonPackage.jail;

import com.arangodb.entity.BaseDocument;
import de.thedodo24.commonPackage.arango.ArangoWritable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Map;


@Getter
public class JailLocation implements ArangoWritable<String> {

    private String key;
    @Setter
    private Location loc;

    public JailLocation(String key) {
        this.key = key;
        loc = Bukkit.getWorlds().get(0).getSpawnLocation();
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void read(BaseDocument document) {
        loc = Location.deserialize((Map<String, Object>) document.getProperties().getOrDefault("location",
                                    Bukkit.getWorld("Freebuild").getSpawnLocation().serialize()));
    }

    @Override
    public void save(BaseDocument document) {
        document.addAttribute("location", loc.serialize());
    }
}
