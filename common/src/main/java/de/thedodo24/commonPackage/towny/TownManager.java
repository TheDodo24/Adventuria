package de.thedodo24.commonPackage.towny;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.velocypack.VPackSlice;
import com.sun.org.apache.xpath.internal.operations.Bool;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.arango.CollectionManager;
import de.thedodo24.commonPackage.arango.WritableGenerator;
import lombok.Getter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<Town> getTowns() {
        return new ArrayList<>(this.cache.values());
    }

    public long getTownTax(Town t) {
        int points = 0;
        int residents = Common.getInstance().getManager().getPlayerManager().getResidents(t).size();
        int plots = Common.getInstance().getManager().getPlotManager().getPlots(t).size();
        if(plots <= 50)
            points += 50;
        else if(plots <= 150)
            points += 75;
        else if(plots <= 400)
            points += 150;
        else if(plots <= 1000)
            points += 350;
        else if(plots <= 2000)
            points += 500;
        else
            points += 600;

        if(residents <= 10)
            points += 50;
        else if(residents <= 20)
            points += 100;
        else if(residents <= 30)
            points += 150;
        else if(residents <= 40)
            points += 200;
        else
            points += 250;

        if(points <= 100)
            return 10000 * 100;
        else if(points <= 200)
            return  20000 * 100;
        else if(points <= 300)
            return 35000 * 100;
        else if(points <= 550)
            return 50000 * 100;
        else if(points <= 700)
            return 65000 * 100;
        else
            return 85000 * 100;
    }

}
