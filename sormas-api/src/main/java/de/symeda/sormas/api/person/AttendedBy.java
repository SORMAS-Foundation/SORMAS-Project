package de.symeda.sormas.api.person;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum AttendedBy {
    DOCTOR,
    NURSE,
    UNKNOWN;

    @Override
    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
}
