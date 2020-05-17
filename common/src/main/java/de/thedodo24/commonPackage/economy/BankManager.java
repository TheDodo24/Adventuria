package de.thedodo24.commonPackage.economy;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.velocypack.VPackSlice;
import com.google.common.collect.Lists;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.arango.CollectionManager;
import de.thedodo24.commonPackage.arango.WritableGenerator;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class BankManager extends CollectionManager<BankAccount, String> {

    @Getter
    private ArangoDatabase database;


    public BankManager(ArangoDatabase database) {
        super("bankAccounts", BankAccount::new, database, (key, obj) -> true);

        this.database = database;

        ArangoCursor<VPackSlice> cursor = getDatabase().query("FOR account IN " + this.collection.name() + " RETURN account._key", VPackSlice.class);
        while(cursor.hasNext()) {
            String n = cursor.next().getAsString();
            this.get(n);
        }
        this.closeCursor(cursor);
    }

    public List<BankAccount> getByType(BankType type) {
        return this.cache.values().stream().filter(bankAccount -> bankAccount.getBankType().equals(type)).collect(Collectors.toList());
    }

    public List<BankAccount> getBankAccounts(UUID uuid) {
        return this.cache.values().stream().filter(acc -> acc.getOwners().contains(uuid) || acc.getMembers().contains(uuid)).collect(Collectors.toList());
    }

    public List<BankAccount> getOwnerBankAccounts(UUID uuid) {
        return this.cache.values().stream().filter(acc -> acc.getOwners().contains(uuid)).collect(Collectors.toList());
    }

    public BankAccount getAccountForPlayer(OfflinePlayer p) {
        String primaryGroup = Common.getInstance().getPerms().getPrimaryGroup("Freebuild", p);
        switch(primaryGroup.toLowerCase()) {
            case "hoster":
            case "administrator":
            case "moderator":
                return get("team-sl");
            default:
                return get("team-" + primaryGroup.toLowerCase());
        }
    }

    public List<BankAccount> bankAccounts() {
        return new ArrayList<>(this.cache.values());
    }

    public void disableSave() {
        this.cache.values().forEach(this::save);
    }

    public void update(BankAccount account) {
        this.save(account);
        this.uncache(account.getKey());
    }
}
