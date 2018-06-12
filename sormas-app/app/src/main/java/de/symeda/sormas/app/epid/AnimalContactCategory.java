package de.symeda.sormas.app.epid;

import de.symeda.sormas.api.I18nProperties;

/**
 * Created by Orson on 20/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public enum AnimalContactCategory {
    GENERAL,
    ENVIRONMENTAL_EXPOSURE;

    private AnimalContactCategory() {
    }

    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }
}