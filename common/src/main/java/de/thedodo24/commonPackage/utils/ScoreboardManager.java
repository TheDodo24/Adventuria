package de.thedodo24.commonPackage.utils;

import de.thedodo24.commonPackage.Common;
import lombok.Getter;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Getter
public class ScoreboardManager {

    @Getter
    public static Map<UUID, ScoreboardManager> scoreboardMap = new HashMap<>();

    private String formatValue(double v) { return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(v).split(" ")[0] + " A"; }

    private UUID uuid;
    private Scoreboard scoreboard;

    public ScoreboardManager(Player player) {
        this.uuid = player.getUniqueId();
        this.scoreboard = new Scoreboard();
        scoreboard.registerObjective("obj", IScoreboardCriteria.DUMMY, IChatBaseComponent.ChatSerializer.b("§2§lAdvi§a§lStatistiken"), IScoreboardCriteria.EnumScoreboardHealthDisplay.a(""));
        scoreboardMap.put(uuid, this);
        sendScoreboard(player);
    }

    public void sendScoreboard(Player player) {
        UUID uuid = player.getUniqueId();
        Scoreboard scoreboard;
        if (scoreboardMap.containsKey(player.getUniqueId()))
            scoreboard = scoreboardMap.get(player.getUniqueId()).getScoreboard();
        else {
            scoreboard = new Scoreboard();
            scoreboard.registerObjective("obj", IScoreboardCriteria.DUMMY, IChatBaseComponent.ChatSerializer.b("§2§lAdvi§a§lStatistiken"), IScoreboardCriteria.EnumScoreboardHealthDisplay.a(""));
            scoreboardMap.put(uuid, this);
        }
        ScoreboardObjective obj = scoreboard.getObjective("obj");
        PacketPlayOutScoreboardObjective removePacket = new PacketPlayOutScoreboardObjective(obj, 1);
        PacketPlayOutScoreboardObjective createPacket = new PacketPlayOutScoreboardObjective(obj, 0);
        PacketPlayOutScoreboardDisplayObjective display = new PacketPlayOutScoreboardDisplayObjective(1, obj);

        obj.setDisplayName(IChatBaseComponent.ChatSerializer.b("§2§lAdvi§a§lStatistiken"));

        PacketPlayOutScoreboardScore s1 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8-------------", 9);
        PacketPlayOutScoreboardScore s2 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§a", 8);
        PacketPlayOutScoreboardScore s3 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8§l▰§7▰ §aOnline", 7);
        PacketPlayOutScoreboardScore s4 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8§l➜§7 " + Bukkit.getOnlinePlayers().size() + " §8/§7 " + Bukkit.getMaxPlayers(), 6);
        PacketPlayOutScoreboardScore s5 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§b", 5);
        PacketPlayOutScoreboardScore s6 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8§l▰§7▰ §2Geld", 4);
        PacketPlayOutScoreboardScore s7 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8§l➜§7 " + formatValue(((Long) Common.getInstance().getManager().getPlayerManager().get(uuid).getBalance()).doubleValue() / 100), 3);
        PacketPlayOutScoreboardScore s8 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§c", 2);
        PacketPlayOutScoreboardScore s9 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8------------", 1);

        sendPacket(player, removePacket);
        sendPacket(player, createPacket);
        sendPacket(player, display);

        sendPacket(player, s1);
        sendPacket(player, s2);
        sendPacket(player, s3);
        sendPacket(player, s4);
        sendPacket(player, s5);
        sendPacket(player, s6);
        sendPacket(player, s7);
        sendPacket(player, s8);
        sendPacket(player, s9);
    }

    private void sendPacket(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

}