package de.symeda.sormas.api.common;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum DeleteReason {

    GDPR,
    DELETION_REQUEST,
    CREATED_WITH_NO_LEGAL_REASON,
    TRANSFERRED_RESPONSIBILITY,
    DUPLICATE_ENTRIES,
    OTHER_REASON;

    @Override
    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
}
