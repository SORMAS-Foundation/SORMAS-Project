package de.symeda.sormas.app.backend.person;

import java.util.stream.Collectors;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.caze.CaseJurisdictionDto;
import de.symeda.sormas.app.backend.contact.ContactJurisdictionDto;
import de.symeda.sormas.app.backend.event.EventJurisdictionDto;
import de.symeda.sormas.app.util.BooleanJurisdictionValidator;
import de.symeda.sormas.app.util.UserJurisdiction;

public class PersonJurisdictionBooleanValidator extends BooleanJurisdictionValidator {

	private final PersonJurisdictionDto personJurisdiction;
	private final UserJurisdiction userJurisdiction;

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
		return false;
	}

	@Override
	public Boolean isRootInJurisdictionOrOwned() {
		return this.isRootInJurisdiction();
	}

	@Override
	public Boolean isRootInJurisdictionForRestrictedAccess() {

		boolean currentUserIsSurveillanceOfficer = personJurisdiction.getCaseJurisdiction()
			.stream()
			.map(CaseJurisdictionDto::getSurveillanceOfficerUuid)
			.collect(Collectors.toList())
			.contains(userJurisdiction.getUuid());

		boolean currentUserIsContactOfficer = personJurisdiction.getContactJurisdiction()
			.stream()
			.map(ContactJurisdictionDto::getContactOfficerUuid)
			.collect(Collectors.toList())
			.contains(userJurisdiction.getUuid());

		boolean currentUserIsResponsibleUser = personJurisdiction.getEventJurisdiction()
			.stream()
			.map(EventJurisdictionDto::getResponsibleUserUuid)
			.collect(Collectors.toList())
			.contains(userJurisdiction.getUuid());

		return currentUserIsSurveillanceOfficer || currentUserIsContactOfficer || currentUserIsResponsibleUser;
	}

	@Override
	protected Boolean whenNotAllowed() {
		return false;
	}

	@Override
	protected Boolean whenNationalLevel() {
		return false;
	}

	@Override
	protected Boolean whenRegionalLevel() {
		return false;
	}

	@Override
	protected Boolean whenDistrictLevel() {
		return false;
	}

	@Override
	protected Boolean whenCommunityLevel() {
		return false;
	}

	@Override
	protected Boolean whenFacilityLevel() {
		return false;
	}

	@Override
	protected Boolean whenPointOfEntryLevel() {
		return false;
	}

	@Override
	protected Boolean whenLaboratoryLevel() {
		return false;
	}
}
