package de.symeda.sormas.api;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum VisitOrigin {
    USER,
    EXTERNAL_JOURNAL;

    public String getName() {
        return this.name();
    }

    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
}
