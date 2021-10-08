package de.symeda.sormas.api.hospitalization;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum HospitalizationReasonType {

    REPORTED_DISEASE,
    OTHER,
    UNKNOWN;

    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
}
