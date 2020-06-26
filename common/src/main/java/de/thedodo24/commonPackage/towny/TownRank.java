package de.thedodo24.commonPackage.towny;

import lombok.Getter;

public enum TownRank {

    MAYOR("Bürgermeister"),
    ASSISTANT("Stadthalter"),
    HELPER("Stadtbauer"),
    CITIZEN("Bürger");

    @Getter
    private String displayName;

    TownRank(String displayName) {
        this.displayName = displayName;
    }

}
