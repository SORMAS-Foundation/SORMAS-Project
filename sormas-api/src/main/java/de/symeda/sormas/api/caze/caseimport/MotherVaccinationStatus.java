package de.symeda.sormas.api.caze.caseimport;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum MotherVaccinationStatus {

    UP_TO_DATE,
    NOT_UP_TO_DATE,
    UNKOWN;

    @Override
    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
}
