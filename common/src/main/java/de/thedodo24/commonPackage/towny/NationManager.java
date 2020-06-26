package de.thedodo24.commonPackage.towny;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.velocypack.VPackSlice;
import de.thedodo24.commonPackage.arango.CollectionManager;
import de.thedodo24.commonPackage.arango.WritableGenerator;
import lombok.Getter;

public class NationManager extends CollectionManager<Nation, String> {

    @Getter
    private ArangoDatabase database;

    public NationManager(ArangoDatabase database) {
        super("nations", Nation::new, database, (key, obj) -> true);

        this.database = database;
        ArangoCursor<VPackSlice> cursor = getDatabase().query("FOR account IN " + this.collection.name() + " RETURN account._key", VPackSlice.class);
        while(cursor.hasNext()) {
            String n = cursor.next().getAsString();
            this.get(n);
        }
        this.closeCursor(cursor);
    }
}
