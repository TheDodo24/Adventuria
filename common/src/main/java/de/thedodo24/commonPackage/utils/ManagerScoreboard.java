package de.thedodo24.commonPackage.utils;

import com.google.common.collect.Lists;
import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.classes.Board;
import lombok.Getter;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.IScoreboardCriteria;
import org.bukkit.Bukkit;
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
        /*board.setValue(14, "§1", "§8-------------");
        board.setValue(13, "§2", "");
        board.setValue(12, "§8§l▰§7▰ ", "§aOnline");
        board.setValue(11, "§8§l➜ ", "§7" + Bukkit.getOnlinePlayers().size() + " §8/§7 " + Bukkit.getMaxPlayers());
        board.setValue(10, "§3", "");
        board.setValue(9, "§8§l▰§7▰ ", "§2Geld");
        board.setValue(8, "§8§l➜ ", "§7" + formatValue(((Long) Common.getInstance().getManager().getPlayerManager().get(uuid).getBalance()).doubleValue() / 100));
        board.setValue(7, "§4", "");
        board.setValue(6, "§5", "§8---------------");*/
        board.setValue(15, "§8", "");
        board.setValue(14, "§8» ", player.getName());
        board.setValue(13, "§8» §c♡ ", "§c5");
        board.setValue(12, "§2", "");
        board.setValue(11, "§8» ", "§aKeine Kickzeit");
        board.setValue(10, "§3", "");
        board.setValue(9, "§8» ", "§64 Punkte");
        board.setValue(8, "§4", "");
        board.setValue(7, "§8» ", "§c0 Kills");
        board.setValue(6, "§5", "");
        board.setValue(5, "§7Top-Player:", "");
        board.setValue(4, "§8» ", "TheDodo24");
        board.setValue(3, "§8» ", "§70 Kills");
        scoreboardMap.put(uuid, this);
    }


}
