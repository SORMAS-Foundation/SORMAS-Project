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

package de.symeda.sormas.app.backend.event;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.util.BooleanJurisdictionValidator;
import de.symeda.sormas.app.util.UserJurisdiction;

public class EventJurisdictionBooleanValidator extends BooleanJurisdictionValidator {

    private final EventJurisdictionDto eventJurisdictionDto;
    private final UserJurisdiction userJurisdiction;

    public static EventJurisdictionBooleanValidator of(EventJurisdictionDto eventJurisdictionDto, UserJurisdiction userJurisdiction) {
        return new EventJurisdictionBooleanValidator(eventJurisdictionDto, userJurisdiction);
    }

    private EventJurisdictionBooleanValidator(EventJurisdictionDto eventJurisdictionDto, UserJurisdiction userJurisdiction) {
        super(null, userJurisdiction);
        this.eventJurisdictionDto = eventJurisdictionDto;
        this.userJurisdiction = userJurisdiction;
    }

    @Override
    public Boolean isRootInJurisdiction() {
        return isInJurisdictionByJurisdictionLevel(userJurisdiction.getJurisdictionLevel());
    }

	@Override
	public Boolean isRootInJurisdictionOrOwned() {
		return getReportedByCurrentUser() || isCurrentUserResponsible() || inJurisdiction();
	}

	private boolean getReportedByCurrentUser() {
		return userJurisdiction.getUuid().equals(eventJurisdictionDto.getReportingUserUuid());
	}

	public boolean isCurrentUserResponsible() {
		return userJurisdiction.getUuid().equals(eventJurisdictionDto.getResponsibleUserUuid());
	}

	@Override
	public Boolean isRootInJurisdictionForRestrictedAccess() {
		return getReportedByCurrentUser() || isCurrentUserResponsible();
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
        return DataHelper.equal(eventJurisdictionDto.getRegionUuid(), userJurisdiction.getRegionUuid());
    }

    @Override
    protected Boolean whenDistrictLevel() {
        return DataHelper.equal(eventJurisdictionDto.getDistrictUuid(), userJurisdiction.getDistrictUuid());
    }

    @Override
    protected Boolean whenCommunityLevel() {
        return DataHelper.equal(eventJurisdictionDto.getCommunityUuid(), userJurisdiction.getCommunityUuid());
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
