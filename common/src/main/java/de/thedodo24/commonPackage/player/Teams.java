package de.thedodo24.commonPackage.player;

import de.thedodo24.commonPackage.economy.BankAccount;
import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
public enum Teams {

    HELFER("helfer", "helfer-duty","ingi-leiter", "Eventhelfer", "team.mod.bau", "team-helfer", ChatColor.GOLD, 15000 * 100, 3),
    MVA("mva", "mva-duty", "mva-leiter", "MVA", "team.mod.medien", "team-mva", ChatColor.DARK_AQUA, 15000 * 100, 5),
    FBT("fbt", "fbt-duty", "fbt-leiter","FBT", "team.mod.bau", "team-fbt", ChatColor.YELLOW, 7500 * 100, 4),
    POLIZEI("polizist", "polizist-duty", "poli-leiter", "Polizei", "team.mod.justiz", "team-polizist", ChatColor.AQUA, 20000 * 100, 5),
    SUPPORTER("supporter", "supporter-duty","sup-leiter","Supporter", "team.mod.community", "team-supporter", ChatColor.GREEN, 20000 * 100, 6),
    INGENIEUR("ingenieur", "ingenieur-duty", "ingi-leiter", "Ingenieur", "team.mod.bau", "team-ingenieur", ChatColor.BLUE, 40000 * 100, 4),
    DEVELOPER("developer", "developer-duty", "dev-leiter", "Developer", "team.mod.technik", "team-developer", ChatColor.RED, 45000 * 100, 4),
    MODERATOR("moderator", "moderator-duty", "administrator", "Moderator", "team.admin", "team-mod", ChatColor.DARK_GREEN, 0, 0),
    ADMINISTRATOR("administrator", "administrator-duty","administrator","Administrator", "team.admin", "team-sl", ChatColor.DARK_RED, 0, 0);

    String permName;
    String dutyName;
    String leiterGroup;
    String displayName;
    String permission;
    String bankAccount;
    ChatColor color;
    long salary;
    int maxEmployees;

    Teams(String permName, String dutyName, String leiterGroup, String displayName, String permission, String bankAccount, ChatColor color, int salary, int maxEmployees) {
        this.permName = permName;
        this.dutyName = dutyName;
        this.leiterGroup = leiterGroup;
        this.displayName = displayName;
        this.permission = permission;
        this.bankAccount = bankAccount;
        this.color = color;
        this.salary = salary;
        this.maxEmployees = maxEmployees;
    }

}
