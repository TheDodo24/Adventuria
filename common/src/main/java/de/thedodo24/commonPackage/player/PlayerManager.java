package de.thedodo24.commonPackage.player;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.util.MapBuilder;
import com.arangodb.velocypack.VPackSlice;
import com.google.common.collect.Lists;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.arango.CollectionManager;
import de.thedodo24.commonPackage.economy.BankAccount;
import de.thedodo24.commonPackage.towny.Town;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class PlayerManager extends CollectionManager<User, UUID> {

    private Map<String, UUID> names = new HashMap<>();
    private ArangoDatabase database;

    public PlayerManager(ArangoDatabase database) {
        super("player", User::new, database, (key, obj) -> Bukkit.getPlayer(key) != null);
        this.database = database;
    }

    public User getByName(String s) {
        if(names.containsKey(s)) {
            return this.getOrGenerate(names.get(s));
        }
        ArangoCursor<VPackSlice> cursor = getDatabase().query("FOR player IN " + this.collection.name() + " FILTER LIKE (player.name, @name, true) RETURN {uniqueId: player._key}",
                new MapBuilder().put("name", s).get(), null, VPackSlice.class);
        UUID uniqueId = null;
        if(cursor.hasNext())
            uniqueId = UUID.fromString(cursor.next().get("uniqueId").getAsString());
        this.closeCursor(cursor);
        if(uniqueId != null) {
            names.put(s, uniqueId);
            return this.getOrGenerate(uniqueId);
        }
        return null;
    }

    public LinkedHashMap<User, Long> getHighestMoney() {
        List<User> users = getUsers();
        HashMap<UUID, Long> totalAmount = new HashMap<>();
        users.forEach(user -> {
            long total = user.getBalance();
            List<BankAccount> bankAccounts = Common.getInstance().getManager().getBankManager().getOwnerBankAccounts(user.getKey());
            total += bankAccounts.stream().mapToLong(BankAccount::getBalance).sum();
            totalAmount.put(user.getKey(), total);
        });
        List<UUID> mapKeys = new ArrayList<>(totalAmount.keySet());
        List<Long> mapValues = new ArrayList<>(totalAmount.values());

        Collections.sort(mapValues);
        Collections.reverse(mapValues);

        LinkedHashMap<User, Long> sortedMap = new LinkedHashMap<>();
        for(Long val : mapValues) {
            Iterator<UUID> keyIt = mapKeys.iterator();
            while(keyIt.hasNext()) {
                UUID key = keyIt.next();
                long comp1 = totalAmount.get(key);
                long comp2 = val;
                if(comp1 == comp2) {
                    keyIt.remove();
                    if(sortedMap.size() < 10)
                        sortedMap.put(get(key), val);
                    else
                        break;
                }
            }
        }
        return sortedMap;
    }

    public LinkedHashMap<User, Long> getHighestOntime() {
        List<User> users = getUsers();
        HashMap<UUID, Long> totalOntime = new HashMap<>();
        users.forEach(user -> {
            if(Common.getInstance().getPlayerOnline().containsKey(user.getKey())) {
                long currentTime = System.currentTimeMillis();
                long ontime = currentTime - Common.getInstance().getPlayerOnline().get(user.getKey());
                if(Common.getInstance().getAfkPlayer().containsKey(user.getKey())) {
                    ontime -= (currentTime - Common.getInstance().getAfkPlayer().get(user.getKey()));
                }
                totalOntime.put(user.getKey(), ontime + user.getTotalOntime());
            } else {
                totalOntime.put(user.getKey(), user.getTotalOntime());
            }
        });
        List<UUID> mapKeys = new ArrayList<>(totalOntime.keySet());
        List<Long> mapValues = new ArrayList<>(totalOntime.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);
        Collections.reverse(mapKeys);
        Collections.reverse(mapValues);

        LinkedHashMap<User, Long> sortedMap = new LinkedHashMap<>();

        for (Long val : mapValues) {
            Iterator<UUID> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                UUID key = keyIt.next();
                long comp1 = totalOntime.get(key);
                long comp2 = val;
                if (comp1 == comp2) {
                    keyIt.remove();
                    if (sortedMap.size() < 10) {
                        sortedMap.put(get(key), val);
                    } else {
                        break;
                    }
                }
            }
        }

        return sortedMap;
    }

    public List<User> getUsers() {
        ArangoCursor<VPackSlice> cursor = getDatabase().query("FOR player IN " + this.collection.name() + " RETURN {uniqueId: player._key}",
                VPackSlice.class);
        List<User> list = Lists.newArrayList();
        while(cursor.hasNext()) {
            UUID uuid = UUID.fromString(cursor.next().get("uniqueId").getAsString());
            list.add(get(uuid));
        }
        this.closeCursor(cursor);
        return list;
    }

    public List<User> getResidents(Town town) {
        List<User> users = getUsers();
        return users.stream().filter(User::checkTownMember).filter(user -> user.getTown().getKey().equalsIgnoreCase(town.getKey())).collect(Collectors.toList());
    }

    @Override
    public User uncache(UUID key) {
        User user = super.uncache(key);
        names.remove(user.getName());
        return user;
    }

    public void disableSave() {
        this.cache.values().forEach(this::save);
    }

    public void update(User user) {
        this.save(user);
        this.uncache(user.getKey());
    }
}
