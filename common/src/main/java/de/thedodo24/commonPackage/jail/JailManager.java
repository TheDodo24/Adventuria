package de.thedodo24.commonPackage.jail;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.velocypack.VPackSlice;
import de.thedodo24.commonPackage.arango.CollectionManager;
import de.thedodo24.commonPackage.arango.WritableGenerator;
import de.thedodo24.commonPackage.economy.BankAccount;
import lombok.Getter;
import org.bukkit.Location;

@Getter
public class JailManager extends CollectionManager<JailLocation, String> {

    private ArangoDatabase database;

    public JailManager(ArangoDatabase database) {
        super("jail", JailLocation::new, database, (key, obj) -> true);

        this.database = database;

        ArangoCursor<VPackSlice> cursor = getDatabase().query("FOR location IN " + this.collection.name() + " RETURN location._key", VPackSlice.class);
        while(cursor.hasNext()) {
            String n = cursor.next().getAsString();
            this.get(n);
        }
        this.closeCursor(cursor);
    }

    public Location getLocation(String key) {
        return this.cache.get(key).getLoc();
    }

    public void disableSave() {
        this.cache.values().forEach(this::save);
    }

    public void update(JailLocation account) {
        this.save(account);
        this.uncache(account.getKey());
    }
}
