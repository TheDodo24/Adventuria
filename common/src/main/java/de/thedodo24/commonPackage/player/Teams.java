package de.thedodo24.commonPackage.player;

import de.thedodo24.commonPackage.economy.BankAccount;
import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
public enum Teams {

    HELFER("helfer", "helfer-duty","ingi-leiter", "Eventhelfer", "team.mod.bau", "team-helfer", ChatColor.GOLD),
    MVA("mva", "mva-duty", "mva-leiter", "MVA", "team.mod.medien", "team-mva", ChatColor.DARK_AQUA),
    FBT("fbt", "fbt-duty", "fbt-leiter","FBT", "team.mod.bau", "team-fbt", ChatColor.YELLOW),
    POLIZEI("polizist", "polizist-duty", "poli-leiter", "Polizei", "team.mod.justiz", "team-polizist", ChatColor.AQUA),
    SUPPORTER("supporter", "supporter-duty","sup-leiter","Supporter", "team.mod.community", "team-supporter", ChatColor.GREEN),
    INGENIEUR("ingenieur", "ingenieur-duty", "ingi-leiter", "Ingenieur", "team.mod.bau", "team-ingenieur", ChatColor.BLUE),
    DEVELOPER("developer", "developer-duty", "dev-leiter", "Developer", "team.mod.technik", "team-developer", ChatColor.RED),
    MODERATOR("moderator", "moderator-duty", "administrator", "Moderator", "team.admin", "team-mod", ChatColor.DARK_GREEN),
    ADMINISTRATOR("administrator", "administrator-duty","administrator","Administrator", "team.admin", "team-sl", ChatColor.DARK_RED);

    String permName;
    String dutyName;
    String leiterGroup;
    String displayName;
    String permission;
    String bankAccount;
    ChatColor color;

    Teams(String permName, String dutyName, String leiterGroup, String displayName, String permission, String bankAccount, ChatColor color) {
        this.permName = permName;
        this.dutyName = dutyName;
        this.leiterGroup = leiterGroup;
        this.displayName = displayName;
        this.permission = permission;
        this.bankAccount = bankAccount;
        this.color = color;
    }

}
