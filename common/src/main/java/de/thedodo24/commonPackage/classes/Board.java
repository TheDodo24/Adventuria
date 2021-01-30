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
        /*Team t;
        if(scoreboard.getTeam("000") != null)
            scoreboard.getTeam("000").unregister();
        t = scoreboard.registerNewTeam("000");
        t.setColor(ChatColor.DARK_PURPLE);

        if(scoreboard.getTeam("001") != null){
            scoreboard.getTeam("001").unregister();
        }
        t = scoreboard.registerNewTeam("001");
        t.setColor(ChatColor.DARK_RED);

        if(scoreboard.getTeam("002") != null){
            scoreboard.getTeam("002").unregister();
        }
        t = scoreboard.registerNewTeam("002");
        t.setColor(ChatColor.DARK_GREEN);

        if(scoreboard.getTeam("003") != null){
            scoreboard.getTeam("003").unregister();
        }
        t = scoreboard.registerNewTeam("003");
        t.setColor(ChatColor.AQUA);

        if(scoreboard.getTeam("004") != null){
            scoreboard.getTeam("004").unregister();
        }
        t = scoreboard.registerNewTeam("004");
        t.setColor(ChatColor.RED);

        if(scoreboard.getTeam("005") != null){
            scoreboard.getTeam("005").unregister();
        }
        t = scoreboard.registerNewTeam("005");
        t.setColor(ChatColor.RED);

        if(scoreboard.getTeam("006") != null){
            scoreboard.getTeam("006").unregister();
        }
        t = scoreboard.registerNewTeam("006");
        t.setColor(ChatColor.AQUA);

        if(scoreboard.getTeam("007") != null){
            scoreboard.getTeam("007").unregister();
        }
        t = scoreboard.registerNewTeam("007");
        t.setColor(ChatColor.AQUA);

        if(scoreboard.getTeam("008") != null){
            scoreboard.getTeam("008").unregister();
        }
        t = scoreboard.registerNewTeam("008");
        t.setColor(ChatColor.GREEN);

        if(scoreboard.getTeam("009") != null){
            scoreboard.getTeam("009").unregister();
        }
        t = scoreboard.registerNewTeam("009");
        t.setColor(ChatColor.GREEN);

        if(scoreboard.getTeam("010") != null){
            scoreboard.getTeam("010").unregister();
        }
        t = scoreboard.registerNewTeam("010");
        t.setColor(ChatColor.BLUE);

        if(scoreboard.getTeam("011") != null){
            scoreboard.getTeam("011").unregister();
        }
        t = scoreboard.registerNewTeam("011");
        t.setColor(ChatColor.BLUE);

        if(scoreboard.getTeam("012") != null){
            scoreboard.getTeam("012").unregister();
        }
        t = scoreboard.registerNewTeam("012");
        t.setColor(ChatColor.YELLOW);

        if(scoreboard.getTeam("013") != null){
            scoreboard.getTeam("013").unregister();
        }
        t = scoreboard.registerNewTeam("013");
        t.setColor(ChatColor.YELLOW);

        if(scoreboard.getTeam("014") != null){
            scoreboard.getTeam("014").unregister();
        }
        t = scoreboard.registerNewTeam("014");
        t.setColor(ChatColor.YELLOW);

        if(scoreboard.getTeam("015") != null){
            scoreboard.getTeam("015").unregister();
        }
        t = scoreboard.registerNewTeam("015");
        t.setColor(ChatColor.DARK_AQUA);

        if(scoreboard.getTeam("016") != null){
            scoreboard.getTeam("016").unregister();
        }
        t = scoreboard.registerNewTeam("016");
        t.setColor(ChatColor.GOLD);

        if(scoreboard.getTeam("017") != null){
            scoreboard.getTeam("017").unregister();
        }
        t = scoreboard.registerNewTeam("017");
        t.setColor(ChatColor.GOLD);

        if(scoreboard.getTeam("018") != null){
            scoreboard.getTeam("018").unregister();
        }
        t = scoreboard.registerNewTeam("018");
        t.setColor(ChatColor.GOLD);

        if(scoreboard.getTeam("019") != null){
            scoreboard.getTeam("019").unregister();
        }
        t = scoreboard.registerNewTeam("019");
        t.setColor(ChatColor.LIGHT_PURPLE);

        if(scoreboard.getTeam("020") != null){
            scoreboard.getTeam("020").unregister();
        }
        t = scoreboard.registerNewTeam("020");
        t.setColor(ChatColor.LIGHT_PURPLE);*/
    }


    public void setPrefix(Player p) {
        String group = Common.getInstance().getPerms().getPrimaryGroup(p);
        int weight = Common.getInstance().getChat().getGroupInfoInteger("", group, "weight", 0);
        String prefix = Common.getInstance().getChat().getGroupPrefix("", group).replaceAll("(&([a-z0-9]))", "§$2");
        String suffix = Common.getInstance().getChat().getGroupSuffix("", group).replaceAll("(&([a-z0-9]))", "§$2");
        int teamWeight = 100 - weight;
        if(group.equalsIgnoreCase("member"))
            teamWeight = 80;
        else if(group.equalsIgnoreCase("neuling"))
            teamWeight = 82;
        Team t;
        if(scoreboard.getTeam(String.valueOf(teamWeight)) == null) {
            t = scoreboard.registerNewTeam(String.valueOf(teamWeight));
            t.setColor(ChatColor.getByChar(suffix.replace("§", "")));
            t.setPrefix((prefix.equalsIgnoreCase("Administrator") ? "Admin" : prefix) + " §8● " + suffix);
        } else
            t = scoreboard.getTeam(String.valueOf(teamWeight));
        scoreboard.getTeams().stream().filter(ts -> ts.getEntries().contains(p.getName())).forEach(ts -> ts.removeEntry(p.getName()));
        t.addEntry(p.getName());
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
