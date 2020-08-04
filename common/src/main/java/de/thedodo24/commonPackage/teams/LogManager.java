package de.thedodo24.commonPackage.teams;

import com.arangodb.ArangoDatabase;
import de.thedodo24.commonPackage.arango.CollectionManager;
import de.thedodo24.commonPackage.arango.WritableGenerator;

import java.util.UUID;

public class LogManager extends CollectionManager<TeamLog, UUID> {


    public LogManager(ArangoDatabase database) {
        super("teamlog", TeamLog::new, database, (key, obj) -> true);
    }
}
