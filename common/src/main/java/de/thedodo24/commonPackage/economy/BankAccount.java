package de.thedodo24.commonPackage.economy;

import com.arangodb.entity.BaseDocument;
import com.google.common.collect.Lists;
import de.thedodo24.commonPackage.arango.ArangoWritable;
import de.thedodo24.commonPackage.utils.ScoreboardManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class BankAccount implements ArangoWritable<String> {

    private String key;

    public BankAccount(String key) {
        this.key = key;
        this.owners = Lists.newArrayList();
        this.members = Lists.newArrayList();
    }

    private long balance;
    private List<UUID> owners;
    private List<UUID> members;
    private BankType bankType;

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public void read(BaseDocument document) {
        balance = (long) document.getProperties().getOrDefault("balance", 0);
        owners = ((List<String>) document.getProperties().getOrDefault("owners", Lists.newArrayList())).stream().map(UUID::fromString).collect(Collectors.toList());
        members = ((List<String>) document.getProperties().getOrDefault("members", Lists.newArrayList())).stream().map(UUID::fromString).collect(Collectors.toList());
        bankType = BankType.valueOf((String) document.getProperties().getOrDefault("bankType", BankType.EXTERNAL.name()));
    }

    @Override
    public void save(BaseDocument document) {
        document.addAttribute("balance", balance);
        document.addAttribute("owners", (owners == null ? Lists.newArrayList() : owners));
        document.addAttribute("members", (members == null ? Lists.newArrayList() : members));
        document.addAttribute("bankType", (bankType == null ? BankType.EXTERNAL : bankType).name());
    }

    public double withdrawMoney(double v) {
        balance -= ((long) (v * 100));
        ScoreboardManager.getScoreboardMap().forEach((key, val) -> val.sendScoreboard(Bukkit.getPlayer(key), Bukkit.getOnlinePlayers().size()));
        return ((Long) balance).doubleValue() / 100;
    }

    public double depositMoney(double v) {
        balance += ((long) v * 100);
        ScoreboardManager.getScoreboardMap().forEach((key, val) -> val.sendScoreboard(Bukkit.getPlayer(key), Bukkit.getOnlinePlayers().size()));
        return ((Long) balance).doubleValue() / 100;
    }

    public long depositMoney(long v) {
        ScoreboardManager.getScoreboardMap().forEach((key, val) -> val.sendScoreboard(Bukkit.getPlayer(key), Bukkit.getOnlinePlayers().size()));
        return balance += v;
    }
    public long withdrawMoney(long v) {
        ScoreboardManager.getScoreboardMap().forEach((key, val) -> val.sendScoreboard(Bukkit.getPlayer(key), Bukkit.getOnlinePlayers().size()));
        return balance -= v;
    }
}
