package de.thedodo24.commonPackage.towny;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.velocypack.VPackSlice;
import de.thedodo24.commonPackage.arango.CollectionManager;
import de.thedodo24.commonPackage.arango.WritableGenerator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class PlotManager extends CollectionManager<Plot, String> {

    private ArangoDatabase database;

    public PlotManager(ArangoDatabase database) {
        super("plots", Plot::new, database, ((key, obj) -> true));
        this.database = database;
        ArangoCursor<VPackSlice> cursor = getDatabase().query("FOR account IN " + this.collection.name() + " RETURN account._key", VPackSlice.class);
        while(cursor.hasNext()) {
            String n = cursor.next().getAsString();
            this.get(n);
        }
        this.closeCursor(cursor);
    }

    public List<Plot> getPlots() {
        return new ArrayList<>(this.cache.values());
    }

    public List<Plot> getPlots(Town town) {
        return this.cache.values().stream().filter(plot -> plot.getTown().getKey().equalsIgnoreCase(town.getKey())).collect(Collectors.toList());
    }

    public List<Plot> getPlots(UUID uuid) {
        return this.cache.values().stream().filter(Plot::isOwned).filter(plot -> plot.getOwner().equals(uuid)).collect(Collectors.toList());
    }
}
