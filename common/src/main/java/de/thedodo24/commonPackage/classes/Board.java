package de.thedodo24.commonPackage.classes;

import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {

    private static final List<ChatColor> colors = Arrays.asList(ChatColor.values());

    @Getter
    private final Scoreboard scoreboard;
    private final Objective objective;

    private final List<BoardLine> boardLines = new ArrayList<>();
    public Board(String displayName) {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("obj", "dummy", displayName, RenderType.HEARTS);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        for(int i=0; i < colors.size(); i++) {
            final ChatColor color = colors.get(i);
            final Team team = scoreboard.registerNewTeam("line"+i);
            team.addEntry(color.toString());
            boardLines.add(new BoardLine(color, i, team));
        }
    }

    private BoardLine getBoardLine(int line) {
        return boardLines.stream().filter(boardLine -> boardLine.getLine() == line).findFirst().orElse(null);
    }

    public void setValue(int line, String prefix, String suffix) {
        final BoardLine boardLine = getBoardLine(line);
        Validate.notNull(boardLine, "Unable to find BoardLine with index of " + line + ".");
        objective.getScore(boardLine.getColor().toString()).setScore(line);
        boardLine.getTeam().setPrefix(prefix);
        boardLine.getTeam().setSuffix(suffix);
    }

    public void removeLine(int line) {
        final BoardLine boardLine = getBoardLine(line);
        Validate.notNull(boardLine, "Unable to find BoardLine with index of " + line + ".");
        scoreboard.resetScores(boardLine.getColor().toString());
    }

}
