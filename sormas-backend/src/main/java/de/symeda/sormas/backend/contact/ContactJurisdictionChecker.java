package de.symeda.sormas.backend.contact;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.contact.ContactJurisdictionDto;
import de.symeda.sormas.api.utils.jurisdiction.ContactJurisdictionHelper;
import de.symeda.sormas.backend.caze.CaseJurisdictionChecker;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.JurisdictionHelper;

@Stateless(name = "ContactJurisdictionChecker")
@LocalBean
public class ContactJurisdictionChecker {

	@EJB
	private UserService userService;
	@EJB
	private CaseJurisdictionChecker caseJurisdictionChecker;

	public boolean isInJurisdiction(Contact contact) {
		return isInJurisdiction(JurisdictionHelper.createContactJurisdictionDto(contact));
	}

	public boolean isInJurisdiction(ContactJurisdictionDto contactJurisdiction) {

		User user = userService.getCurrentUser();
		return ContactJurisdictionHelper
			.isInJurisdiction(userService::hasAnyRole, JurisdictionHelper.createUserJurisdiction(user), contactJurisdiction);
	}
}
