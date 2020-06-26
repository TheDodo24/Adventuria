package de.thedodo24.commonPackage.towny;

import de.thedodo24.commonPackage.Common;
import de.thedodo24.commonPackage.player.User;
import lombok.Getter;

@Getter
public enum PlotPlayer {


    FRIEND("f", "Freund"),
    RESIDENT("r", "Einwohner"),
    NATION("n", "Nationsmitglied"),
    OUTSIDER("o", "Besucher");

    private String displayNameShort;
    private String displayName;

    PlotPlayer(String displayNameShort, String displayName) {
        this.displayNameShort = displayNameShort;
        this.displayName = displayName;
    }

    public static PlotPlayer getTownPlayer(User user, Plot plot) {
        if(user.checkTownMember()) {
            if(plot.isOwned()) {
                if(Common.getInstance().getManager().getPlayerManager().get(plot.getOwner()).checkFriend(user.getKey())) {
                    return FRIEND;
                }
            }
            if(user.getTown().getKey().equalsIgnoreCase(plot.getTown().getKey())) {
                return RESIDENT;
            }
        }
        return OUTSIDER;
    }

}
