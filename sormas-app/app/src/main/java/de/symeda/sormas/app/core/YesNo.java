package de.symeda.sormas.app.core;

import de.symeda.sormas.api.I18nProperties;

/**
 * Created by Orson on 16/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public enum YesNo {
    YES,
    NO;

    private YesNo() {
    }

    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
}
