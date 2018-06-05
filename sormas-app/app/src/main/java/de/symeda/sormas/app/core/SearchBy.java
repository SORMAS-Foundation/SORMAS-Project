package de.symeda.sormas.app.core;

import de.symeda.sormas.api.I18nProperties;

/**
 * Created by Orson on 09/01/2018.
 */

public enum SearchBy {

    BY_FILTER_STATUS,
    BY_CASE_ID,
    BY_CONTACT_ID,
    BY_EVENT_ID;

    public String toString() {
        return I18nProperties.getEnumCaption(this);
    }

}
