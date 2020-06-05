package de.thedodo24.commonPackage.utils;

import com.google.common.collect.Lists;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.classes.Board;
import de.thedodo24.commonPackage.player.CustomScoreboardType;
import de.thedodo24.commonPackage.player.ScoreboardModule;
import de.thedodo24.commonPackage.player.User;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.awt.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ManagerScoreboard {

    @Getter
    public static Map<UUID, ManagerScoreboard> scoreboardMap = new HashMap<>();
    private String formatValue(double v) { return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(v).split(" ")[0] + " A"; }

    private UUID uuid;
    private Board board;

    public ManagerScoreboard(Player player) {
        this.uuid = player.getUniqueId();
        this.board = new Board("§2§lAdvi§a§lStatistiken");
        player.setScoreboard(setBoardLines(player).getScoreboard());
        board.setPrefix(player);
        scoreboardMap.put(uuid, this);
    }

    public void sendScoreboard(Player player) {
        player.setScoreboard(setBoardLines(player).getScoreboard());
    }

    public void removeScoreboard(Player player) { player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard()); }

    private Board setBoardLines(Player player) {
        User user = Common.getInstance().getManager().getPlayerManager().get(player.getUniqueId());
        Map<String, Map<String, String>> scoreboard = user.getCustomScoreboard();
        ScoreboardModule module = new ScoreboardModule();
        board.setValue(1, module.getPlaceholder(1), "§8--------------");
        board.setValue(2, module.getPlaceholder(2), "");
        int line = 3;
        for(int i = 0; i < scoreboard.keySet().size(); i++) {
            Map<String, String> scoreboardLine = scoreboard.get(String.valueOf(i));
            board.setValue(line, module.getPrefixSuffix(), "§7" + module.getValue(CustomScoreboardType.valueOf(scoreboardLine.get("type")), user, scoreboardLine.get("value")));
            line++;
            board.setValue(line, module.getPrefixPrefix(), module.getPattern(CustomScoreboardType.valueOf(scoreboardLine.get("type"))));
            line++;
            board.setValue(line, module.getPlaceholder(line), "");
            line++;
        }
        for(int i = line; i < 22; i++) {
            board.removeLine(i);
        }
        board.setValue(line, module.getPlaceholder(line), "§8--------------");
        return board;
    }


    /*private Board setBoardLines(Player player) {
    /*private Board setBoardLines(Player player) {
        User user = Common.getInstance().getManager().getPlayerManager().get(uuid);
        if(user.isCustomScoreboard()) {
            if(user.checkCustomScoreboard(CustomScoreboardType.CORP) && user.checkCustomScoreboard(CustomScoreboardType.BANK)) {
                if(Common.getInstance().getManager().getMySQL().checkCorp(user.getCustomScoreboard(CustomScoreboardType.CORP))) {
                    board.setValue(15, "§a", "§8--------------");
                    board.setValue(14, "§b", "");
                    board.setValue(13, "§8§l▰§7▰ ", "§aOnline");
                    board.setValue(12, "§8§l» ", "§7" + Bukkit.getOnlinePlayers().size() + " §8/§7 " + Bukkit.getMaxPlayers());
                    board.setValue(11, "§c", "");
                    board.setValue(10, "§8§l▰§7▰ ", "§bGeld");
                    board.setValue(9, "§8§l» ", "§7" + formatValue(((Long) Common.getInstance().getManager().getPlayerManager().get(uuid).getBalance()).doubleValue() / 100));
                    board.setValue(8, "§d", "");
                    board.setValue(7, "§8§l▰§7▰ ", "§9Firma");
                    board.setValue(6, "§8§l» ", "§7" + formatValue(((Long) Common.getInstance().getManager().getMySQL().getBalance(user.getCustomScoreboard(CustomScoreboardType.CORP))).doubleValue() / 100));
                    board.setValue(5, "§e", "");
                    board.setValue(4, "§8§l▰§7▰ ", "§2Bank");
                    board.setValue(3, "§8§l» ", "§7" + formatValue(((Long) Common.getInstance().getManager().getBankManager().get(user.getCustomScoreboard(CustomScoreboardType.BANK)).getBalance()).doubleValue() / 100));
                    board.setValue(2, "§f", "");
                    board.setValue(1, "§1", "§8--------------");
                } else {
                    user.unSetCustomScoreboard(CustomScoreboardType.CORP);
                    player.sendMessage("§7§l| §aAdventuria §7» Deine §aCustom-Scoreboard §7Einstellung Firma wurde gelöscht, da die Firma nicht mehr existiert.");
                    sendScoreboard(player);
                }
            } else {
                for(int i = 15; i > 12; i--) board.removeLine(i);
                board.setValue(12, "§a", "§8--------------");
                board.setValue(11, "§b", "");
                board.setValue(10, "§8§l▰§7▰ ", "§aOnline");
                board.setValue(9, "§8§l» ", "§7" + Bukkit.getOnlinePlayers().size() + " §8/§7 " + Bukkit.getMaxPlayers());
                board.setValue(8, "§c", "");
                board.setValue(7, "§8§l▰§7▰ ", "§bGeld");
                board.setValue(6, "§8§l» ", "§7" + formatValue(((Long) Common.getInstance().getManager().getPlayerManager().get(uuid).getBalance()).doubleValue() / 100));
                board.setValue(5, "§d", "");
                board.setValue(2, "§f", "");
                board.setValue(1, "§1", "§8--------------");
                if(user.checkCustomScoreboard(CustomScoreboardType.CORP)) {
                    board.setValue(4, "§8§l▰§7▰ ", "§9Firma");
                    board.setValue(3, "§8§l» ", "§7" + formatValue(((Long) Common.getInstance().getManager().getMySQL().getBalance(user.getCustomScoreboard(CustomScoreboardType.CORP))).doubleValue() / 100));
                } else {
                    board.setValue(4, "§8§l▰§7▰ ", "§2Bank");
                    board.setValue(3, "§8§l» ", "§7" + formatValue(((Long) Common.getInstance().getManager().getBankManager().get(user.getCustomScoreboard(CustomScoreboardType.BANK)).getBalance()).doubleValue() / 100));
                }
            }
        } else {
            for(int i = 15; i > 9; i--) board.removeLine(i);
            board.setValue(9, "§a", "§8--------------");
            board.setValue(8, "§b", "");
            board.setValue(7, "§8§l▰§7▰ ", "§aOnline");
            board.setValue(6, "§8§l» ", "§7" + Bukkit.getOnlinePlayers().size() + " §8/§7 " + Bukkit.getMaxPlayers());
            board.setValue(5, "§c", "");
            board.setValue(4, "§8§l▰§7▰ ", "§bGeld");
            board.setValue(3, "§8§l» ", "§7" + formatValue(((Long) Common.getInstance().getManager().getPlayerManager().get(uuid).getBalance()).doubleValue() / 100));
            board.setValue(2, "§d", "");
            board.setValue(1, "§1", "§8--------------");
        }
        return board;
    }*/


}
