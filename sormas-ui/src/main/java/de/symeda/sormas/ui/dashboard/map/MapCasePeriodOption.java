package de.symeda.sormas.ui.dashboard.map;


import de.symeda.sormas.api.i18n.I18nProperties;

public enum MapCasePeriodOption {

    NEW_CASES,
    CASES_INCIDENCE;

    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
}
