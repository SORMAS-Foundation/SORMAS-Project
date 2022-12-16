package de.symeda.sormas.api;

import de.symeda.sormas.api.i18n.I18nProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Whether the visit data was generated directly by a SORMAS user or transcribed from a external journal")
public enum VisitOrigin {

    USER,
    EXTERNAL_JOURNAL;

    public String getName() {
        return this.name();
    }

    @Override
    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
}
