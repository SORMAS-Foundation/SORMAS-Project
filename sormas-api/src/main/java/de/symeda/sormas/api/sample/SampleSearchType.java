package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum SampleSearchType {
    ALL,
    CASE,
    CONTACT;

    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
}
