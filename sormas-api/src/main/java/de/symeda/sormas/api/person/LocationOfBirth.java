package de.symeda.sormas.api.person;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum LocationOfBirth {
    HOSPITAL,
    HEALTH_CENTER,
    HOME,
    TRAINED_ATTENDANT,
    UNTRAINED_ATTENDANT,
    NO_ATTENDANT,
    UNKOWN;

    @Override
    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
}
