package de.thedodo24.commonPackage.utils;

import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.player.CustomScoreboardType;
import de.thedodo24.commonPackage.player.User;
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
        sendScoreboard(player, Bukkit.getOnlinePlayers().size());
    }

    public void removeScoreboard(Player player) {
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
        sendPacket(player, removePacket);
        scoreboardMap.remove(uuid);
    }

    public void sendScoreboard(Player player, int onlineSize) {
        UUID uuid = player.getUniqueId();
        User user = Common.getInstance().getManager().getPlayerManager().get(uuid);
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


        sendPacket(player, removePacket);
        sendPacket(player, createPacket);
        sendPacket(player, display);

        PacketPlayOutScoreboardScore s1 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8-------------", 9);
        PacketPlayOutScoreboardScore s2 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§a", 8);
        PacketPlayOutScoreboardScore s3 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8§l▰§7▰ §9Online", 7);
        PacketPlayOutScoreboardScore s4 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8§l➜§7 " + onlineSize + " §8/§7 " + Bukkit.getMaxPlayers(), 6);
        PacketPlayOutScoreboardScore s5 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§b", 5);
        PacketPlayOutScoreboardScore s6 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8§l▰§7▰ §aGeld", 4);
        PacketPlayOutScoreboardScore s7 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8§l➜§7 " + formatValue(((Long) Common.getInstance().getManager().getPlayerManager().get(uuid).getBalance()).doubleValue() / 100), 3);
        PacketPlayOutScoreboardScore s8 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§c", 2);
        PacketPlayOutScoreboardScore s9 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8------------", 1);


        if(user.isCustomScoreboard()) {
            if(user.checkCustomScoreboard(CustomScoreboardType.CORP) && user.checkCustomScoreboard(CustomScoreboardType.BANK)) {
                s1 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8-------------", 15);
                s2 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§a", 14);
                s3 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8§l▰§7▰ §9Online", 13);
                s4 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8§l➜§7 " + Bukkit.getOnlinePlayers().size() + " §8/§7 " + Bukkit.getMaxPlayers(), 12);
                s5 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§b", 11);
                s6 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8§l▰§7▰ §aGeld", 10);
                s7 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8§l➜§7 " + formatValue(((Long) Common.getInstance().getManager().getPlayerManager().get(uuid).getBalance()).doubleValue() / 100), 9);

                PacketPlayOutScoreboardScore c1 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§d", 8);
                PacketPlayOutScoreboardScore c2;
                PacketPlayOutScoreboardScore c3;
                PacketPlayOutScoreboardScore c4 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§e", 5);
                PacketPlayOutScoreboardScore c5;
                PacketPlayOutScoreboardScore c6;
                if(Common.getInstance().getManager().getMySQL().checkCorp(user.getCustomScoreboard(CustomScoreboardType.CORP))) {
                    c2 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8§l▰§7▰ §eFirma", 7);
                    c3 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§1§8§l➜§7 " + formatValue(((Long) Common.getInstance().getManager().getMySQL().getBalance(user.getCustomScoreboard(CustomScoreboardType.CORP))).doubleValue() / 100), 6);
                    c5 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8§l▰§7▰ §2Bank", 4);
                    c6 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§2§8§l➜§7 " + formatValue(((Long) Common.getInstance().getManager().getBankManager().get(user.getCustomScoreboard(CustomScoreboardType.BANK)).getBalance()).doubleValue() / 100), 3);
                    sendPacket(player, c1);
                    sendPacket(player, c2);
                    sendPacket(player, c3);
                    sendPacket(player, c4);
                    sendPacket(player, c5);
                    sendPacket(player, c6);
                } else {
                    user.unSetCustomScoreboard(CustomScoreboardType.CORP);
                    player.sendMessage("§7§l| §aAdventuria §7» Deine §aCustom-Scoreboard §7Einstellung Firma wurde gelöscht, da die Firma nicht mehr existiert.");
                    sendScoreboard(player, onlineSize);
                }
            } else {
                s1 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8-------------", 12);
                s2 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§a", 11);
                s3 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8§l▰§7▰ §9Online", 10);
                s4 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8§l➜§7 " + Bukkit.getOnlinePlayers().size() + " §8/§7 " + Bukkit.getMaxPlayers(), 9);
                s5 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§b", 8);
                s6 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8§l▰§7▰ §aGeld", 7);
                s7 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§1§8§l➜§7 " + formatValue(((Long) Common.getInstance().getManager().getPlayerManager().get(uuid).getBalance()).doubleValue() / 100), 6);

                PacketPlayOutScoreboardScore c1 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§d", 5);
                PacketPlayOutScoreboardScore c2;
                PacketPlayOutScoreboardScore c3;

                if(user.checkCustomScoreboard(CustomScoreboardType.CORP)) {
                    c2 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8§l▰§7▰ §eFirma", 4);
                    c3 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8§l➜§7 " + formatValue(((Long) Common.getInstance().getManager().getMySQL().getBalance(user.getCustomScoreboard(CustomScoreboardType.CORP))).doubleValue() / 100), 3);
                } else {
                    c2 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8§l▰§7▰ §2Bank", 4);
                    c3 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "obj", "§8§l➜§7 " + formatValue(((Long) Common.getInstance().getManager().getBankManager().get(user.getCustomScoreboard(CustomScoreboardType.BANK)).getBalance()).doubleValue() / 100), 3);
                }
                sendPacket(player, c1);
                sendPacket(player, c2);
                sendPacket(player, c3);
            }
        }


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