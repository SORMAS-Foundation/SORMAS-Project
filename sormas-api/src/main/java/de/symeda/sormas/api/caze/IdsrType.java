package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum IdsrType {
    ANTHRAX,
    DENGUE_FEVER,
    PLAGUE,
    RABIES,
    SMALLPOX,
    SARS,
    OTHER;

    @Override
    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
}
