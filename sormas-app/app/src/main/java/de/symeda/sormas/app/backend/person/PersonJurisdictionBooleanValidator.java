package de.symeda.sormas.app.backend.person;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.caze.CaseJurisdictionDto;
import de.symeda.sormas.app.backend.contact.ContactJurisdictionDto;
import de.symeda.sormas.app.backend.event.EventJurisdictionDto;
import de.symeda.sormas.app.core.NotImplementedException;
import de.symeda.sormas.app.util.BooleanJurisdictionValidator;
import de.symeda.sormas.app.util.UserJurisdiction;

public class PersonJurisdictionBooleanValidator extends BooleanJurisdictionValidator {

	private final PersonJurisdictionDto personJurisdiction;
	protected final UserJurisdiction userJurisdiction;

	public PersonJurisdictionBooleanValidator(UserJurisdiction userJurisdiction, PersonJurisdictionDto personJurisdiction) {
		super(null, userJurisdiction);
		this.personJurisdiction = personJurisdiction;
		this.userJurisdiction = userJurisdiction;
	}

	@Override
	protected Disease getDisease() {
		return null;
	}

	@Override
	public Boolean isRootInJurisdiction() {
		throw new NotImplementedException("Person jurisdiction depends on linked core entity jurisdiction");
	}

	@Override
	public Boolean isRootInJurisdictionOrOwned() {
		throw new NotImplementedException("Person jurisdiction depends on linked core entity jurisdiction");
	}

	@Override
	public Boolean isRootInJurisdictionForRestrictedAccess() {

		boolean currentUserIsSurveillanceOfficer = personJurisdiction.getCaseJurisdictions()
			.stream()
			.map(CaseJurisdictionDto::getSurveillanceOfficerUuid)
			.anyMatch(surveillanceOfficer -> surveillanceOfficer.equals(userJurisdiction.getUuid()));

		if (currentUserIsSurveillanceOfficer) {
			return true;
		}

		boolean currentUserIsContactOfficer = personJurisdiction.getContactJurisdictions()
			.stream()
			.map(ContactJurisdictionDto::getContactOfficerUuid)
			.anyMatch(contactOfficer -> contactOfficer.equals(userJurisdiction.getUuid()));

		if (currentUserIsContactOfficer) {
			return true;
		}

		return personJurisdiction.getEventJurisdictions()
			.stream()
			.map(EventJurisdictionDto::getResponsibleUserUuid)
			.anyMatch(responsibleUser -> responsibleUser.equals(userJurisdiction.getUuid()));
	}

	@Override
	protected Boolean whenNotAllowed() {
		throw new NotImplementedException("Person jurisdiction depends on linked core entity jurisdiction");
	}

	@Override
	protected Boolean whenNationalLevel() {
		throw new NotImplementedException("Person jurisdiction depends on linked core entity jurisdiction");
	}

	@Override
	protected Boolean whenRegionalLevel() {
		throw new NotImplementedException("Person jurisdiction depends on linked core entity jurisdiction");
	}

	@Override
	protected Boolean whenDistrictLevel() {
		throw new NotImplementedException("Person jurisdiction depends on linked core entity jurisdiction");
	}

	@Override
	protected Boolean whenCommunityLevel() {
		throw new NotImplementedException("Person jurisdiction depends on linked core entity jurisdiction");
	}

	@Override
	protected Boolean whenFacilityLevel() {
		throw new NotImplementedException("Person jurisdiction depends on linked core entity jurisdiction");
	}

	@Override
	protected Boolean whenPointOfEntryLevel() {
		throw new NotImplementedException("Person jurisdiction depends on linked core entity jurisdiction");
	}

	@Override
	protected Boolean whenLaboratoryLevel() {
		throw new NotImplementedException("Person jurisdiction depends on linked core entity jurisdiction");
	}
}
