package de.symeda.sormas.app.caze;

import de.symeda.sormas.api.I18nProperties;

/**
 * Created by Orson on 16/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public enum CaseOutcome {
    NO_OUTCOME,
    DECEASED,
    RECOVERED,
    UNKNOWN;

    private CaseOutcome() {
    }

    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
}
