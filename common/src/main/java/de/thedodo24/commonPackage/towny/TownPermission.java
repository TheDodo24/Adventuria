package de.thedodo24.commonPackage.towny;

import lombok.Getter;

@Getter
public enum TownPermission {

    BUILD("Bauen"),
    DESTROY("Zerstören"),
    SWITCH("Interagieren"),
    ITEM("Verwenden");

    private String displayName;

    TownPermission(String displayName) {
        this.displayName = displayName;
    }


}
