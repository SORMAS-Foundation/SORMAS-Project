package de.symeda.sormas.app.shared;

import de.symeda.sormas.api.I18nProperties;

public enum ShipmentStatus {

    NOT_SHIPPED,
    SHIPPED,
    RECEIVED,
    REFERRED_OTHER_LAB;

    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }

    public String toShortString() {
        return I18nProperties.getEnumCaption(this, "Short");
    }
}
