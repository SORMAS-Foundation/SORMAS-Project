package de.symeda.sormas.app.backend.environment;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.util.BooleanJurisdictionValidator;
import de.symeda.sormas.app.util.UserJurisdiction;

public class EnvironmentJurisdictionBooleanValidator extends BooleanJurisdictionValidator {

	private final EnvironmentJurisdictionDto environmentJurisdiction;
	private final UserJurisdiction userJurisdiction;

	public static EnvironmentJurisdictionBooleanValidator of(EnvironmentJurisdictionDto environmentJurisdiction, UserJurisdiction userJurisdiction) {
		return new EnvironmentJurisdictionBooleanValidator(environmentJurisdiction, userJurisdiction);
	}

	private EnvironmentJurisdictionBooleanValidator(EnvironmentJurisdictionDto environmentJurisdiction, UserJurisdiction userJurisdiction) {
		super(null, userJurisdiction);
		this.environmentJurisdiction = environmentJurisdiction;
		this.userJurisdiction = userJurisdiction;
	}

	@Override
	protected Disease getDisease() {
		return null;
	}

	@Override
	public Boolean isRootInJurisdiction() {
		return isInJurisdictionByJurisdictionLevel(userJurisdiction.getJurisdictionLevel());
	}

	@Override
	public Boolean isRootInJurisdictionOrOwned() {
		return isReportedByCurrentUser() || isResponsibleByCurrentUser() || inJurisdiction();
	}

	private boolean isReportedByCurrentUser() {
		return userJurisdiction.getUuid().equals(environmentJurisdiction.getReportingUserUuid());
	}

	private boolean isResponsibleByCurrentUser() {
		return userJurisdiction.getUuid().equals(environmentJurisdiction.getResponsibleUserUuid());
	}

	@Override
	public Boolean isRootInJurisdictionForRestrictedAccess() {
		return isReportedByCurrentUser() || isResponsibleByCurrentUser();
	}

	@Override
	protected Boolean whenNotAllowed() {
		return false;
	}

	@Override
	protected Boolean whenNationalLevel() {
		return true;
	}

	@Override
	protected Boolean whenRegionalLevel() {
		return DataHelper.equal(environmentJurisdiction.getRegionUuid(), userJurisdiction.getRegionUuid());
	}

	@Override
	protected Boolean whenDistrictLevel() {
		return DataHelper.equal(environmentJurisdiction.getDistrictUuid(), userJurisdiction.getDistrictUuid());
	}

	@Override
	protected Boolean whenCommunityLevel() {
		return DataHelper.equal(environmentJurisdiction.getCommunityUuid(), userJurisdiction.getCommunityUuid());
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
