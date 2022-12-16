package de.symeda.sormas.api.common;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Enum, reasons why the data was deleted.")
public enum DeletionReason {

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
