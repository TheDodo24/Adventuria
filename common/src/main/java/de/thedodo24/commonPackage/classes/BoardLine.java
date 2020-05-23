package de.thedodo24.commonPackage.classes;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

public class BoardLine {

    @Getter
    private final ChatColor color;
    @Getter
    private final int line;
    @Getter
    private final org.bukkit.scoreboard.Team team;

    public BoardLine(ChatColor color, int line, Team team) {
        this.color = color;
        this.line = line;
        this.team = team;
    }

}
