package de.thedodo24.commonPackage.economy;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.velocypack.VPackSlice;
import de.thedodo24.commonPackage.arango.CollectionManager;
import de.thedodo24.commonPackage.arango.WritableGenerator;
import lombok.Getter;

public class BankLogHandler extends CollectionManager<BankLog, String> {

    @Getter
    private ArangoDatabase database;

    public BankLogHandler(ArangoDatabase database) {
        super("bankAccountLog", BankLog::new, database, (key, obj) -> true);

        this.database = database;

        ArangoCursor<VPackSlice> cursor = getDatabase().query("FOR log IN " + this.collection.name() + " RETURN log._key", VPackSlice.class);
        while(cursor.hasNext()) {
            String n = cursor.next().getAsString();
            this.get(n);
        }
        this.closeCursor(cursor);
    }
}
