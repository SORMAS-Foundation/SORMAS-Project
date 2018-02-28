package de.symeda.sormas.app.temp;

import de.symeda.sormas.api.I18nProperties;

/**
 * Created by Orson on 10/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public enum CauseOfDeath {
    EPIDEMIC_DISEASE,
    OTHER_CAUSE;

    private CauseOfDeath() {
    }

    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
}
