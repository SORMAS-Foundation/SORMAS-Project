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

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.util.BooleanJurisdictionValidator;
import de.symeda.sormas.app.util.UserJurisdiction;

public class EventParticipantJurisdictionBooleanValidator extends BooleanJurisdictionValidator {

    // todo - sync this with validator from backend

    private final EventParticipantJurisdictionDto eventParticipantJurisdictionDto;
    private final UserJurisdiction userJurisdiction;

    public static EventParticipantJurisdictionBooleanValidator of(EventParticipantJurisdictionDto eventParticipantJurisdictionDto, UserJurisdiction userJurisdiction) {
        return new EventParticipantJurisdictionBooleanValidator(eventParticipantJurisdictionDto, userJurisdiction);
    }

    private EventParticipantJurisdictionBooleanValidator(EventParticipantJurisdictionDto eventParticipantJurisdictionDto, UserJurisdiction userJurisdiction) {
        super(null);
        this.eventParticipantJurisdictionDto = eventParticipantJurisdictionDto;
        this.userJurisdiction = userJurisdiction;
    }

    @Override
    protected Boolean isInJurisdiction() {
        return isInJurisdictionByJurisdictionLevel(userJurisdiction.getJurisdictionLevel());
    }

    @Override
    protected Boolean isInJurisdictionOrOwned() {
        return userJurisdiction.getUuid().equals(eventParticipantJurisdictionDto.getReportingUserUuid()) || inJurisdiction();
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
        return DataHelper.equal(eventParticipantJurisdictionDto.getRegionUuid(), userJurisdiction.getRegionUuid());
    }

    @Override
    protected Boolean whenDistrictLevel() {
        return DataHelper.equal(eventParticipantJurisdictionDto.getDistrictUuid(), userJurisdiction.getDistrictUuid());
    }

    @Override
    protected Boolean whenCommunityLevel() {
        return false;
        // todo community of address https://github.com/hzi-braunschweig/SORMAS-Project/issues/5903
        // return DataHelper.equal(eventParticipantJurisdictionDto.getCommunityUuid(), userJurisdiction.getCommunityUuid());
    }

    @Override
    protected Boolean whenFacilityLevel() {
        return false; // todo  address facility https://github.com/hzi-braunschweig/SORMAS-Project/issues/5903
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
