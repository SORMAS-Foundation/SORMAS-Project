package de.symeda.sormas.app.component;

import de.symeda.sormas.api.I18nProperties;

/**
 * Created by Orson on 24/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public enum VisualStateControl {

    EDIT_TEXT,
    CHECKBOX,
    SPINNER,
    SWITCH;

    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }

}
