package de.symeda.sormas.app.shared;

import de.symeda.sormas.api.I18nProperties;

/**
 * Created by Mate Strysewske on 10.08.2017.
 */

public enum ShipmentStatus {

    NOT_SHIPPED,
    SHIPPED,
    RECEIVED,
    REFERRED_OTHER_LAB;

    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }

}
