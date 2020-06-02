package de.thedodo24.adventuriaeco.vault;

import com.google.common.collect.Lists;
import de.thedodo24.adventuriaeco.Economy;
import de.thedodo24.commonPackage.economy.BankAccount;
import de.thedodo24.commonPackage.economy.BankType;
import de.thedodo24.commonPackage.player.User;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public class EconomyHandler extends AbstractEconomy {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "Adventuria-Economy";
    }

    @Override
    public boolean hasBankSupport() {
        return true;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double v) {
        return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(v).split(" ")[0] + " A";
    }

    @Override
    public String currencyNamePlural() {
        return "Adventurios";
    }

    @Override
    public String currencyNameSingular() {
        return "Adventurio";
    }

    @Override
    public boolean hasAccount(String s) {
        return hasAccount(s, null);
    }

    @Override
    public boolean hasAccount(String s, String world) {
        User m = Economy.getInstance().getManager().getPlayerManager().getByName(s.toLowerCase());
        if(m == null) {
            BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(s.toLowerCase());
            return bankAccount != null;
        }
        return true;
    }

    @Override
    public double getBalance(String s) {
        return getBalance(s, null);
    }

    @Override
    public double getBalance(String s, String world) {
        User m = Economy.getInstance().getManager().getPlayerManager().getByName(s.toLowerCase());
        if(m == null) {
            BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(s.toLowerCase());
            if(bankAccount == null) {
                return 0;
            }
            return ((Long) bankAccount.getBalance()).doubleValue() / 100;
        }
        return ((Long) m.getBalance()).doubleValue() / 100;
    }

    @Override
    public boolean has(String s, double v) {
        return has(s, null, v);
    }

    @Override
    public boolean has(String s, String world, double v) {
        User m = Economy.getInstance().getManager().getPlayerManager().getByName(s.toLowerCase());
        if(m == null) {
            BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(s.toLowerCase());
            if(bankAccount == null) {
                return false;
            }
            return (((Long) bankAccount.getBalance()).doubleValue() / 100) >= v;
        }
        return (((Long) m.getBalance()).doubleValue() / 100) >= v;
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        return withdrawPlayer(s, null, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String world, double v) {
        User m = Economy.getInstance().getManager().getPlayerManager().getByName(s.toLowerCase());
        if(v <= 0) {
            v *= -1;
        }
        if(m == null) {
            BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(s.toLowerCase());
            if(bankAccount == null) {
                return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not found a account for the player " + s);
            }
            double va = bankAccount.withdrawMoney(v);
            Economy.getInstance().getManager().getBankManager().save(bankAccount);
            return new EconomyResponse(-v, va, EconomyResponse.ResponseType.SUCCESS, "");
        }
        return new EconomyResponse(-v, ((Long) m.withdrawMoney((long) (v * 100))).doubleValue() / 100, EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        return depositPlayer(s, null, v);
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        User m = Economy.getInstance().getManager().getPlayerManager().getByName(s.toLowerCase());
        if(v <= 0) {
            v *= -1;
        }
        if(m == null) {
            BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(s.toLowerCase());
            if(bankAccount == null) {
                return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not found a account for the player " + s);
            }
            double va = bankAccount.depositMoney(v);
            Economy.getInstance().getManager().getBankManager().save(bankAccount);
            return new EconomyResponse(v, va, EconomyResponse.ResponseType.SUCCESS, "");
        }
        return new EconomyResponse(v, ((Long) m.depositMoney((long) (v * 100))).doubleValue() / 100, EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse createBank(String accountName, String playerName) {
        BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(accountName.toLowerCase());
        if(bankAccount != null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Account already exists");
        }
        User m = Economy.getInstance().getManager().getPlayerManager().getByName(playerName.toLowerCase());
        BankAccount b = Economy.getInstance().getManager().getBankManager().register(accountName.toLowerCase());
        b.setBankType(BankType.BANK);
        b.setOwners(Lists.newArrayList(m.getKey()));
        Economy.getInstance().getManager().getBankManager().save(b);
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse deleteBank(String accountName) {
        BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(accountName.toLowerCase());
        if(bankAccount == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not found bank account " + accountName);
        }
        //beta is lettered for still doesn't work
        Economy.getInstance().getManager().getBankManager().delete(accountName.toLowerCase());
        // wenn gelöscht dann gelöscht
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(s.toLowerCase());
        if(bankAccount == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not found bank account " + s);
        }
        return new EconomyResponse(0, ((Long) bankAccount.getBalance()).doubleValue() / 100, EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(s.toLowerCase());
        if(v <= 0) {
            v *= -1;
        }
        if(bankAccount == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not found bank account " + s);
        }
        long value = (long) (v * 100);
        if(value >= bankAccount.getBalance())
            return new EconomyResponse(0, ((Long) bankAccount.getBalance()).doubleValue() / 100, EconomyResponse.ResponseType.SUCCESS, "");
        return new EconomyResponse(0, ((Long) bankAccount.getBalance()).doubleValue() / 100, EconomyResponse.ResponseType.FAILURE, "Bank account does not have enough money");
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(s.toLowerCase());
        if(v <= 0) {
            v *= -1;
        }
        if(bankAccount == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not found bank account " + s);
        }
        long value = (long) (v * 100);
        long newAmount = bankAccount.withdrawMoney(value);
        Economy.getInstance().getManager().getBankManager().save(bankAccount);
        return new EconomyResponse(v, ((Long) newAmount).doubleValue() / 100, EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(s.toLowerCase());
        if(v <= 0) {
            v *= -1;
        }
        if(bankAccount == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not found bank account " + s);
        }
        long value = (long) (v * 100);
        long newAmount = bankAccount.depositMoney(value);
        Economy.getInstance().getManager().getBankManager().save(bankAccount);
        return new EconomyResponse(v, ((Long) newAmount).doubleValue() / 100, EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse isBankOwner(String accountName, String playerName) {
        BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(accountName.toLowerCase());
        if(bankAccount == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not found bank account " + accountName);
        }
        UUID u = Economy.getInstance().getManager().getPlayerManager().getByName(playerName.toLowerCase()).getKey();
        if(bankAccount.getOwners().contains(u))
            return new EconomyResponse(0, ((Long) bankAccount.getBalance()).doubleValue() / 100, EconomyResponse.ResponseType.SUCCESS, "");
        return new EconomyResponse(0, ((Long) bankAccount.getBalance()).doubleValue() / 100, EconomyResponse.ResponseType.FAILURE, playerName + " is not owner of bank account " + accountName);
    }

    @Override
    public EconomyResponse isBankMember(String accountName, String playerName) {
        BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().get(accountName.toLowerCase());
        if(bankAccount == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not found bank account " + accountName);
        }
        UUID u = Economy.getInstance().getManager().getPlayerManager().getByName(playerName.toLowerCase()).getKey();
        if(bankAccount.getMembers().contains(u))
            return new EconomyResponse(0, ((Long) bankAccount.getBalance()).doubleValue() / 100, EconomyResponse.ResponseType.SUCCESS, "");
        return new EconomyResponse(0, ((Long) bankAccount.getBalance()).doubleValue() / 100, EconomyResponse.ResponseType.FAILURE, playerName + " is not member of bank account " + accountName);
    }

    @Override
    public List<String> getBanks() {
        return Economy.getInstance().getManager().getBankManager().bankAccounts().stream().map(BankAccount::getKey).collect(Collectors.toList());
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return createPlayerAccount(s, null);
    }

    @Override
    public boolean createPlayerAccount(String s, String world) {
        if(hasAccount(s)) {
            return false;
        }
        Economy.getInstance().getManager().getBankManager().save(Economy.getInstance().getManager().getBankManager().register(s.toLowerCase()));
        return true;
    }
}
