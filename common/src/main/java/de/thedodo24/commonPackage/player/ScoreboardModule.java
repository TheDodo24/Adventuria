package de.thedodo24.commonPackage.player;

import com.arangodb.entity.BaseDocument;
import com.google.common.collect.Lists;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.economy.BankAccount;
import de.thedodo24.commonPackage.utils.TimeFormat;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Getter
public class ScoreboardModule {

    private String prefixPrefix = "§8§l▰§7▰ ";
    private String prefixSuffix = "§8§l» ";

    private String formatValue(double v) { return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(v).split(" ")[0] + " A"; }

    public String getPattern(CustomScoreboardType type) {
        switch(type) {
            case CORP:
                return "§9Firma";
            case BANK:
                return "§2Bank";
            case MONEY:
                return "§bGeld";
            case ONLINE:
                return "§aOnline";
            case ONTIME:
                return "§5Ontime";
            default:
                return "";
        }

    }

    public String getValue(CustomScoreboardType type, User user, String value) {
        switch (type) {
            case ONTIME:
                long currentOntime = 0;
                if(Common.getInstance().getPlayerOnline().containsKey(user.getKey()))
                    currentOntime = System.currentTimeMillis() - Common.getInstance().getPlayerOnline().get(user.getKey());
                switch(value.toLowerCase()) {
                    case "day":
                        return TimeFormat.getInDays(user.getDayOntime() + currentOntime);
                    case "week":
                        return TimeFormat.getInDays(user.getWeekOntime() + currentOntime);
                    case "total":
                        return TimeFormat.getInDays(user.getTotalOntime() + currentOntime);
                    default:
                        return TimeFormat.getInDays(user.getTotalOntime() + currentOntime);
                }
            case ONLINE:
                return Bukkit.getOnlinePlayers().size() + " §8/§7 " + Bukkit.getMaxPlayers();
            case MONEY:
                return formatValue(((Long) user.getBalance()).doubleValue() / 100);
            case BANK:
                if(Common.getInstance().getManager().getBankManager().get(value) != null) {
                    BankAccount bankAccount = Common.getInstance().getManager().getBankManager().get(value);
                    return formatValue(((Long) bankAccount.getBalance()).doubleValue() / 100);
                } else {
                    return "0 A";
                }
            case CORP:
                Map<String, Long> corp = Common.getInstance().getManager().getMySQL().getCorp(user.getName());
                if(corp.size() > 0) {
                    return formatValue(corp.get(corp.keySet().stream().findFirst().get()).doubleValue() / 100);
                } else {
                    return "0 A";
                }
            default:
                return "";
        }
    }

    public String getPlaceholder(int index) {
        if(index >= 1 && index <= 9) {
            return "§" + index;
        } else if(index > 9) {
            switch(index) {
                case 10:
                    return "§a";
                case 11:
                    return "§b";
                case 12:
                    return "§c";
                case 13:
                    return "§d";
                case 14:
                    return "§e";
                case 15:
                    return "§f";
                case 16:
                    return "§k";
                case 17:
                    return "§l";
                case 18:
                    return "§m";
                case 19:
                    return "§n";
                case 20:
                    return "§o";
            }
        }
        return "";
    }

}
