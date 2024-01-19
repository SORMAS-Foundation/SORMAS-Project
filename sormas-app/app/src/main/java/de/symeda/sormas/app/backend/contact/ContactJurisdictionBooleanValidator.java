/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.contact;

import java.util.Collections;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.backend.caze.CaseJurisdictionBooleanValidator;
import de.symeda.sormas.app.util.BooleanJurisdictionValidator;
import de.symeda.sormas.app.util.UserJurisdiction;

public class ContactJurisdictionBooleanValidator extends BooleanJurisdictionValidator {

    private final ContactJurisdictionDto contactJurisdictionDto;
    private final UserJurisdiction userJurisdiction;

    public static ContactJurisdictionBooleanValidator of(ContactJurisdictionDto contactJurisdictionDto, UserJurisdiction userJurisdiction) {
        return new ContactJurisdictionBooleanValidator(contactJurisdictionDto, userJurisdiction);
    }

    private ContactJurisdictionBooleanValidator(ContactJurisdictionDto contactJurisdictionDto, UserJurisdiction userJurisdiction) {
        super(Collections.singletonList(contactJurisdictionDto.getCaseJurisdiction() != null ? CaseJurisdictionBooleanValidator.of(contactJurisdictionDto.getCaseJurisdiction(), userJurisdiction): null), userJurisdiction);
        this.contactJurisdictionDto = contactJurisdictionDto;
        this.userJurisdiction = userJurisdiction;
    }

    @Override
    public Boolean isRootInJurisdiction() {
        return isInJurisdictionByJurisdictionLevel(userJurisdiction.getJurisdictionLevel());
    }

	@Override
	public Boolean isRootInJurisdictionOrOwned() {
		return getReportedByCurrentUser() || inJurisdiction();
	}

	private boolean getReportedByCurrentUser(){
        return userJurisdiction.getUuid().equals(contactJurisdictionDto.getReportingUserUuid());
    }

	@Override
	public Boolean isRootInJurisdictionForRestrictedAccess() {
		return getReportedByCurrentUser() || userJurisdiction.getUuid().equals(contactJurisdictionDto.getContactOfficerUuid());
	}

    @Override
    protected Disease getDisease() {
        return null;
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
        return DataHelper.equal(contactJurisdictionDto.getRegionUuid(), userJurisdiction.getRegionUuid());
    }

    @Override
    protected Boolean whenDistrictLevel() {
        return DataHelper.equal(contactJurisdictionDto.getDistrictUuid(), userJurisdiction.getDistrictUuid());
    }

    @Override
    protected Boolean whenCommunityLevel() {
        return DataHelper.equal(contactJurisdictionDto.getCommunityUuid(), userJurisdiction.getCommunityUuid());
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
        return contactJurisdictionDto.getSampleLabUuids().stream().anyMatch(s -> DataHelper.equal(s, userJurisdiction.getLabUuid()));
    }
}
