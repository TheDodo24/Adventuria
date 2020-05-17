package de.thedodo24.adventuriaeco;

import com.google.common.collect.Lists;
import de.thedodo24.adventuriaeco.commands.BankCommand;
import de.thedodo24.adventuriaeco.commands.MoneyCommand;
import de.thedodo24.adventuriaeco.commands.TeamLohnCommand;
import de.thedodo24.adventuriaeco.listeners.PlayerListeners;
import de.thedodo24.adventuriaeco.listeners.WorldListeners;
import de.thedodo24.adventuriaeco.vault.EconomyHandler;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.arango.WritableGenerator;
import de.thedodo24.commonPackage.economy.BankAccount;
import de.thedodo24.commonPackage.economy.BankType;
import lombok.Getter;
import de.thedodo24.commonPackage.module.Module;
import de.thedodo24.commonPackage.module.ModuleManager;
import de.thedodo24.commonPackage.module.ModuleSettings;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ModuleSettings
@Getter
public class Economy extends Module {

    @Getter
    private static Economy instance;

    private NamespacedKey namespacedKey;

    private List<UUID> armorStandAddList;
    private List<UUID> armorStandDelList;
    private List<UUID> chatListenerCreate;
    private Map<UUID, String> chatListenerTransfer;

    public Economy(ModuleSettings settings, ModuleManager manager, JavaPlugin plugin) {
        super(settings, manager, plugin);

        instance = this;
    }

    @Override
    public void onLoad() {
        try {
            Class.forName("net.milkbowl.vault.permission.Permission");

            getPlugin().getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, new EconomyHandler(), getPlugin(), ServicePriority.Highest);
        } catch(Exception ex) {
            System.err.println("[Adventuria] Vault is depended to load this plugin");
        }
    }

    @Override
    public void onEnable() {
        armorStandAddList = Lists.newArrayList();
        armorStandDelList = Lists.newArrayList();
        chatListenerCreate = Lists.newArrayList();
        chatListenerTransfer = new HashMap<>();
        registerCommands();
        Bukkit.getPluginManager().registerEvents(new PlayerListeners(), getPlugin());
        registerListener(new WorldListeners());

        this.getManager().getBankManager().getOrGenerate("staatskasse", BankAccount::new);
        Common.getInstance().getTeamAccounts().forEach(account -> this.getManager().getBankManager().getOrGenerate(account, key -> {
            BankAccount bankAccount = new BankAccount(key);
            bankAccount.setBankType(BankType.BANK);
            return bankAccount;
        }));
        namespacedKey = new NamespacedKey(getPlugin(), "emerald");
    }

    @Override
    public void onDisable() {
        System.out.println("[Adventuria] Module economy disabled.");
    }

    private void registerCommands() {
        new MoneyCommand();
        new BankCommand();
        new TeamLohnCommand();
    }
}
