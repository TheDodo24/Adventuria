package de.thedodo24.commonPackage.classes;

import de.thedodo24.commonPackage.Common;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {

    private static final List<ChatColor> colors = Arrays.asList(ChatColor.values());

    @Getter
    private final Scoreboard scoreboard;
    private final Objective sidebar;

    private final List<BoardLine> boardLines = new ArrayList<>();
    public Board(String displayName) {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        sidebar = scoreboard.registerNewObjective("sidebar", "dummy", displayName, RenderType.INTEGER);
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        for(int i=0; i < colors.size(); i++) {
            final ChatColor color = colors.get(i);
            Team team;
            team = scoreboard.registerNewTeam("line"+i);
            team.addEntry(color.toString());
            boardLines.add(new BoardLine(color, i, team));
        }
        Team t;
        if(scoreboard.getTeam("000") == null){
            t = scoreboard.registerNewTeam("000");
            t.setPrefix("§5");
        }
        if(scoreboard.getTeam("001") == null){
            t = scoreboard.registerNewTeam("001");
            t.setPrefix("§4");
        }
        if(scoreboard.getTeam("002") == null){
            t = scoreboard.registerNewTeam("002");
            t.setPrefix("§2");
        }
        if(scoreboard.getTeam("003") == null){
            t = scoreboard.registerNewTeam("003");
            t.setPrefix("§b");
        }
        if(scoreboard.getTeam("004") == null){
            t = scoreboard.registerNewTeam("004");
            t.setPrefix("§c");
        }
        if(scoreboard.getTeam("005") == null){
            t = scoreboard.registerNewTeam("005");
            t.setPrefix("§c");
        }
        if(scoreboard.getTeam("006") == null){
            t = scoreboard.registerNewTeam("006");
            t.setPrefix("§9");
        }
        if(scoreboard.getTeam("007") == null){
            t = scoreboard.registerNewTeam("007");
            t.setPrefix("§9");
        }
        if(scoreboard.getTeam("008") == null){
            t = scoreboard.registerNewTeam("008");
            t.setPrefix("§a");
        }
        if(scoreboard.getTeam("009") == null){
            t = scoreboard.registerNewTeam("009");
            t.setPrefix("§a");
        }
        if(scoreboard.getTeam("010") == null){
            t = scoreboard.registerNewTeam("010");
            t.setPrefix("§b");
        }
        if(scoreboard.getTeam("011") == null){
            t = scoreboard.registerNewTeam("011");
            t.setPrefix("§b");
        }
        if(scoreboard.getTeam("012") == null){
            t = scoreboard.registerNewTeam("012");
            t.setPrefix("§e");
        }
        if(scoreboard.getTeam("013") == null){
            t = scoreboard.registerNewTeam("013");
            t.setPrefix("§e");
        }
        if(scoreboard.getTeam("014") == null){
            t = scoreboard.registerNewTeam("014");
            t.setPrefix("§3");
        }
        if(scoreboard.getTeam("015") == null){
            t = scoreboard.registerNewTeam("015");
            t.setPrefix("§3");
        }
        if(scoreboard.getTeam("016") == null){
            t = scoreboard.registerNewTeam("016");
            t.setPrefix("§6");
        }
        if(scoreboard.getTeam("017") == null){
            t = scoreboard.registerNewTeam("017");
            t.setPrefix("§6");
        }
        if(scoreboard.getTeam("018") == null){
            t = scoreboard.registerNewTeam("018");
            t.setPrefix("§6");
        }
        if(scoreboard.getTeam("019") == null){
            t = scoreboard.registerNewTeam("019");
            t.setPrefix("§d");
        }
        if(scoreboard.getTeam("020") == null){
            t = scoreboard.registerNewTeam("020");
            t.setPrefix("§d");
        }
    }


    public void setPrefix(Player p) {
        String group = Common.getInstance().getPerms().getPrimaryGroup(p);
        String team = "";
        switch(group.toLowerCase()) {
            case "hoster":
                team = "000";
                break;
            case "administrator":
                team = "001";
                break;
            case "moderator":
                team = "002";
                break;
            case "comm-manager":
                team = "003";
                break;
            case "dev-leiter":
                team = "004";
                break;
            case "developer":
                team = "005";
                break;
            case "ingi-leiter":
                team = "006";
                break;
            case "ingenieur":
                team = "007";
                break;
            case "sup-leiter":
                team = "008";
                break;
            case "supporter":
                team = "010";
                break;
            case "poli-leiter":
                team = "011";
                break;
            case "polizist":
                team = "012";
                break;
            case "fbt-leiter":
                team = "013";
                break;
            case "fbt":
                team = "014";
                break;
            case "mva-leiter":
                team = "015";
                break;
            case "mva":
                team = "016";
                break;
            case "urgestein":
            case "helfer":
            case "member":
                team = "017";
                break;
            case "neuling":
                team = "018";
                break;
            case "gast":
            case "default":
                team = "019";
                break;
            default:
                team = "020";
                break;
        }
        scoreboard.getTeams().stream().filter(t -> t.getEntries().contains(p.getName())).forEach(t -> t.removeEntry(p.getName()));
        scoreboard.getTeam(team).addEntry(p.getName());
        String groupChat = Common.getInstance().getPerms().getPrimaryGroup(p);
        String prefix = Common.getInstance().getChat().getGroupPrefix("", groupChat).replaceAll("(&([a-z0-9]))", "§$2");
        String suffix = Common.getInstance().getChat().getGroupSuffix("", group).replaceAll("(&([a-z0-9]))", "§$2");
        p.setPlayerListName(suffix + prefix + " §8● " + suffix + p.getName());
        p.setDisplayName(suffix + prefix + " §8● " + suffix + p.getName());
        //Bukkit.getOnlinePlayers().forEach(all -> all.setScoreboard(scoreboard));
    }

    private BoardLine getBoardLine(int line) {
        return boardLines.stream().filter(boardLine -> boardLine.getLine() == line).findFirst().orElse(null);
    }

    public void setValue(int line, String prefix, String suffix) {
        final BoardLine boardLine = getBoardLine(line);
        Validate.notNull(boardLine, "Unable to find BoardLine with index of " + line + ".");
        sidebar.getScore(boardLine.getColor().toString()).setScore(line);
        boardLine.getTeam().setPrefix(prefix);
        boardLine.getTeam().setSuffix(suffix);
    }

    public void removeLine(int line) {
        final BoardLine boardLine = getBoardLine(line);
        Validate.notNull(boardLine, "Unable to find BoardLine with index of " + line + ".");
        scoreboard.resetScores(boardLine.getColor().toString());
    }

}
