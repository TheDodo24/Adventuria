package de.thedodo24.commonPackage.economy;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.velocypack.VPackSlice;
import de.thedodo24.commonPackage.arango.CollectionManager;
import de.thedodo24.commonPackage.arango.WritableGenerator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class ArmorStandManager extends CollectionManager<ArmorStandHandler, String> {

    private ArangoDatabase database;

    public ArmorStandManager(ArangoDatabase database) {
        super("armorstands", ArmorStandHandler::new, database, ((key, obj) -> true));

        this.database = database;

        this.getOrGenerate("armorstands");
    }

    public List<UUID> getArmorStands() {
        return this.cache.get("armorstands").getArmorStands();
    }

    public void addArmorStand(UUID uuid) {
        this.cache.get("armorstands").getArmorStands().add(uuid);
    }

    public boolean checkArmorStand(UUID uuid) {
        return this.cache.get("armorstands").getArmorStands().contains(uuid);
    }

    public void removeArmorStand(UUID uuid) {
        this.cache.get("armorstands").getArmorStands().remove(uuid);
    }

    public void disableSave() {
        this.cache.values().forEach(this::save);
    }

    public void update(ArmorStandHandler account) {
        this.save(account);
        this.uncache(account.getKey());
    }


}
