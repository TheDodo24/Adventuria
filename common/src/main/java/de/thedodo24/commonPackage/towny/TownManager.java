package de.thedodo24.commonPackage.towny;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.velocypack.VPackSlice;
import de.thedodo24.commonPackage.arango.CollectionManager;
import de.thedodo24.commonPackage.arango.WritableGenerator;
import lombok.Getter;

@Getter
public class TownManager extends CollectionManager<Town, String> {

    private ArangoDatabase database;

    public TownManager(ArangoDatabase database) {
        super("towns", Town::new, database, (key, obj) -> true);
        this.database = database;

        ArangoCursor<VPackSlice> cursor = getDatabase().query("FOR account IN " + this.collection.name() + " RETURN account._key", VPackSlice.class);
        while(cursor.hasNext()) {
            String n = cursor.next().getAsString();
            this.get(n);
        }
        this.closeCursor(cursor);
    }
}
