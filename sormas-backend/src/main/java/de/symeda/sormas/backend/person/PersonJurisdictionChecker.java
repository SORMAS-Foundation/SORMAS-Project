package de.symeda.sormas.backend.person;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.jurisdiction.CaseJurisdictionHelper;
import de.symeda.sormas.api.utils.jurisdiction.ContactJurisdictionHelper;
import de.symeda.sormas.api.utils.jurisdiction.EventParticipantJurisdictionHelper;
import de.symeda.sormas.api.utils.jurisdiction.UserJurisdiction;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.JurisdictionHelper;

@Stateless(name = "PersonJurisdictionChecker")
@LocalBean
public class PersonJurisdictionChecker {

	@EJB
	private UserService userService;
	@EJB
	private PersonService personService;

	public Boolean isInJurisdiction(String uuid) {
		final Person person = personService.getByUuid(uuid);
		final User user = userService.getCurrentUser();
		final JurisdictionLevel userJurisdictionLevel = user.getJurisdictionLevel();
		final UserJurisdiction userJurisdiction = JurisdictionHelper.createUserJurisdiction(user);

		if (!person.getCases().isEmpty()) {
			for (Case aCase : person.getCases()) {
				if (CaseJurisdictionHelper
					.isInJurisdictionOrOwned(userJurisdictionLevel, userJurisdiction, JurisdictionHelper.createCaseJurisdictionDto(aCase)))
					return true;
			}
		} else if (!person.getContacts().isEmpty()) {
			for (Contact contact : person.getContacts()) {
				if (ContactJurisdictionHelper
					.isInJurisdictionOrOwned(userJurisdictionLevel, userJurisdiction, JurisdictionHelper.createContactJurisdictionDto(contact)))
					return true;
			}
		} else if (!person.getEventParticipants().isEmpty()) {
			for (EventParticipant eventParticipant : person.getEventParticipants()) {
				if (EventParticipantJurisdictionHelper.isInJurisdictionOrOwned(
					userJurisdictionLevel,
					userJurisdiction,
					JurisdictionHelper.createEventParticipantJurisdictionDto(eventParticipant)))
					return true;
			}
		}

		return false;
	}
}
