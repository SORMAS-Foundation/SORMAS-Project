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

	public boolean isInJurisdiction(Contact contact) {
		return isInJurisdiction(JurisdictionHelper.createContactJurisdictionDto(contact));
	}

	public boolean isInJurisdiction(ContactJurisdictionDto contactJurisdiction) {
		final User user = userService.getCurrentUser();
		return ContactJurisdictionHelper.isInJurisdiction(user.getJurisdictionLevel(), JurisdictionHelper.createUserJurisdiction(user), contactJurisdiction);
	}
}
