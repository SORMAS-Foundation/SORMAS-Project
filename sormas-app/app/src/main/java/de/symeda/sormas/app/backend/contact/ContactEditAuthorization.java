package de.symeda.sormas.app.backend.contact;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.jurisdiction.ContactJurisdictionHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.JurisdictionHelper;

public class ContactEditAuthorization {

    public static boolean isContactEditAllowed(Contact contact) {
        User user = ConfigProvider.getUser();

        return ContactJurisdictionHelper
                .isInJurisdiction(UserRole.getJurisdictionLevel(user.getUserRoles()), JurisdictionHelper.createUserJurisdiction(user), JurisdictionHelper.createContactJurisdictionDto(contact));
    }
}
