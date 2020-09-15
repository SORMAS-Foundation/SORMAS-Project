package de.symeda.sormas.backend.contact;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.contact.ContactJurisdictionDto;
import de.symeda.sormas.api.utils.jurisdiction.ContactJurisdictionHelper;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.JurisdictionHelper;

@Stateless(name = "ContactJurisdictionChecker")
@LocalBean
public class ContactJurisdictionChecker {

	@EJB
	private UserService userService;

	public boolean isInJurisdictionOrOwned(Contact contact) {
		return isInJurisdictionOrOwned(JurisdictionHelper.createContactJurisdictionDto(contact));
	}

	public boolean isInJurisdictionOrOwned(ContactJurisdictionDto contactJurisdiction) {
		final User user = userService.getCurrentUser();
		return ContactJurisdictionHelper
			.isInJurisdictionOrOwned(user.getJurisdictionLevel(), JurisdictionHelper.createUserJurisdiction(user), contactJurisdiction);
	}
}
