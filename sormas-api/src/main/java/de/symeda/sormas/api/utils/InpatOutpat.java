package de.symeda.sormas.api.utils;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum InpatOutpat {
    INPATIENT,
    OUTPATIENT;

    @Override
    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
}
