package de.symeda.sormas.app.backend.contact;

import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.JurisdictionHelper;

public class ContactEditAuthorization {

	public static boolean isContactEditAllowed(Contact contact) {

		if (contact.getSormasToSormasOriginInfo() != null) {
			return contact.getSormasToSormasOriginInfo().isOwnershipHandedOver();
		}

		final User user = ConfigProvider.getUser();
		final ContactJurisdictionBooleanValidator contactJurisdictionBooleanValidator =
				ContactJurisdictionBooleanValidator.of(JurisdictionHelper.createContactJurisdictionDto(contact), JurisdictionHelper.createUserJurisdiction(user));
		return !contact.isOwnershipHandedOver()
				&& contactJurisdictionBooleanValidator.inJurisdictionOrOwned();
	}
}
