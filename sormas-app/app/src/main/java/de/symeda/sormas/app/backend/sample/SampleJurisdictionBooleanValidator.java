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

package de.symeda.sormas.app.backend.sample;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.backend.caze.CaseJurisdictionBooleanValidator;
import de.symeda.sormas.app.backend.caze.CaseJurisdictionDto;
import de.symeda.sormas.app.backend.contact.ContactJurisdictionBooleanValidator;
import de.symeda.sormas.app.backend.contact.ContactJurisdictionDto;
import de.symeda.sormas.app.backend.event.EventParticipantJurisdictionBooleanValidator;
import de.symeda.sormas.app.backend.event.EventParticipantJurisdictionDto;
import de.symeda.sormas.app.util.BooleanJurisdictionValidator;
import de.symeda.sormas.app.util.UserJurisdiction;

public class SampleJurisdictionBooleanValidator extends BooleanJurisdictionValidator {

    private final SampleJurisdictionDto sampleJurisdictionDto;
    private final UserJurisdiction userJurisdiction;

    public static SampleJurisdictionBooleanValidator of(SampleJurisdictionDto sampleJurisdictionDto, UserJurisdiction userJurisdiction) {

        final List<BooleanJurisdictionValidator> associatedJurisdictionValidators = new ArrayList<>();

        final CaseJurisdictionDto caseJurisdiction = sampleJurisdictionDto.getCaseJurisdiction();
        if (caseJurisdiction != null) {
            associatedJurisdictionValidators.add(CaseJurisdictionBooleanValidator.of(caseJurisdiction, userJurisdiction));
        }
        final ContactJurisdictionDto contactJurisdiction = sampleJurisdictionDto.getContactJurisdiction();
        if (contactJurisdiction != null) {
            associatedJurisdictionValidators.add(ContactJurisdictionBooleanValidator.of(contactJurisdiction, userJurisdiction));
        }
        final EventParticipantJurisdictionDto eventParticipantJurisdiction = sampleJurisdictionDto.getEventParticipantJurisdiction();
        if (eventParticipantJurisdiction != null) {
            associatedJurisdictionValidators
                    .add(EventParticipantJurisdictionBooleanValidator.of(eventParticipantJurisdiction, userJurisdiction));
        }

        return new SampleJurisdictionBooleanValidator(sampleJurisdictionDto, userJurisdiction, associatedJurisdictionValidators);
    }

    private SampleJurisdictionBooleanValidator(SampleJurisdictionDto sampleJurisdictionDto, UserJurisdiction userJurisdiction, List<BooleanJurisdictionValidator> associatedJurisdictionValidators) {
        super(associatedJurisdictionValidators, userJurisdiction);
        this.sampleJurisdictionDto = sampleJurisdictionDto;
        this.userJurisdiction = userJurisdiction;
    }

    @Override
    public Boolean isRootInJurisdiction() {
        return isInJurisdictionByJurisdictionLevel(userJurisdiction.getJurisdictionLevel());
    }

    @Override
    public Boolean isRootInJurisdictionOrOwned() {
        return userJurisdiction.getUuid().equals(sampleJurisdictionDto.getReportingUserUuid()) || inJurisdiction();
    }

    @Override
    public Boolean isRootInJurisdictionForRestrictedAccess() {

        boolean currentUserIsSurveilanceOfficer = userJurisdiction.getUuid().equals(sampleJurisdictionDto.getCaseJurisdiction().getSurveillanceOfficerUuid());
        boolean currentUserIsContactOfficer = userJurisdiction.getUuid().equals(sampleJurisdictionDto.getContactJurisdiction().getContactOfficerUuid());
        boolean currentUserIsResponsibleOfficer = userJurisdiction.getUuid().equals(sampleJurisdictionDto.getEventParticipantJurisdiction().getEventResponsibleUserUuid());

        return currentUserIsSurveilanceOfficer || currentUserIsContactOfficer || currentUserIsResponsibleOfficer;
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
        return DataHelper.equal(sampleJurisdictionDto.getLabUuid(), userJurisdiction.getLabUuid());
    }
}
