package de.symeda.sormas.api.person;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum PersonContactDetailType {
    PHONE,
    EMAIL,
    OTHER;

    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
}
