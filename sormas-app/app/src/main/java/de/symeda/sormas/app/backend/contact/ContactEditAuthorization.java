package de.symeda.sormas.app.backend.contact;

import de.symeda.sormas.api.utils.jurisdiction.ContactJurisdictionHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.JurisdictionHelper;

public class ContactEditAuthorization {

	public static boolean isContactEditAllowed(Contact contact) {
		User user = ConfigProvider.getUser();

		return ContactJurisdictionHelper.isInJurisdiction(
			ConfigProvider::hasRole,
			JurisdictionHelper.createUserJurisdiction(user),
			JurisdictionHelper.createContactJurisdictionDto(contact));
	}
}
