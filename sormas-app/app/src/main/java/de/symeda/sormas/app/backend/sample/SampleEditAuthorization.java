package de.symeda.sormas.app.backend.sample;

import de.symeda.sormas.api.utils.jurisdiction.EventJurisdictionHelper;
import de.symeda.sormas.api.utils.jurisdiction.SampleJurisdictionHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.JurisdictionHelper;

public class SampleEditAuthorization {

    public static boolean isSampleEditAllowed(Sample sample) {
        User user = ConfigProvider.getUser();

        return SampleJurisdictionHelper
                .isInJurisdiction(ConfigProvider::hasRole, JurisdictionHelper.createUserJurisdiction(user), JurisdictionHelper.createSampleJurisdictionDto(sample));
    }
}
