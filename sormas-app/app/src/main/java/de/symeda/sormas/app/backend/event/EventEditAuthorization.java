package de.symeda.sormas.app.backend.event;

import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.JurisdictionHelper;
import de.symeda.sormas.api.utils.jurisdiction.EventJurisdictionHelper;

public class EventEditAuthorization {

    public static boolean isEventEditAllowed(Event event) {
        User user = ConfigProvider.getUser();

        return EventJurisdictionHelper
                .isInJurisdiction(ConfigProvider::hasRole, JurisdictionHelper.createUserJurisdiction(user), JurisdictionHelper.createEventJurisdictionDto(event));
    }
}
