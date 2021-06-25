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

package de.symeda.sormas.app.backend.task;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.caze.CaseJurisdictionBooleanValidator;
import de.symeda.sormas.app.backend.caze.CaseJurisdictionDto;
import de.symeda.sormas.app.backend.contact.ContactJurisdictionBooleanValidator;
import de.symeda.sormas.app.backend.contact.ContactJurisdictionDto;
import de.symeda.sormas.app.backend.event.EventJurisdictionBooleanValidator;
import de.symeda.sormas.app.backend.event.EventJurisdictionDto;
import de.symeda.sormas.app.util.BooleanJurisdictionValidator;
import de.symeda.sormas.app.util.UserJurisdiction;

public class TaskJurisdictionBooleanValidator extends BooleanJurisdictionValidator {

    private final TaskJurisdictionDto taskJurisdictionDto;
    private final UserJurisdiction userJurisdiction;

    public static TaskJurisdictionBooleanValidator of(TaskJurisdictionDto taskJurisdictionDto, UserJurisdiction userJurisdiction) {

        final List<BooleanJurisdictionValidator> associatedJurisdictionValidators = new ArrayList<>();

        final CaseJurisdictionDto caseJurisdiction = taskJurisdictionDto.getCaseJurisdiction();
        if (caseJurisdiction != null) {
            associatedJurisdictionValidators.add(CaseJurisdictionBooleanValidator.of(caseJurisdiction, userJurisdiction));
        }
        final ContactJurisdictionDto contactJurisdiction = taskJurisdictionDto.getContactJurisdiction();
        if (contactJurisdiction != null) {
            associatedJurisdictionValidators.add(ContactJurisdictionBooleanValidator.of(contactJurisdiction, userJurisdiction));
        }
        final EventJurisdictionDto eventJurisdiction = taskJurisdictionDto.getEventJurisdiction();
        if (eventJurisdiction != null) {
            associatedJurisdictionValidators.add(EventJurisdictionBooleanValidator.of(eventJurisdiction, userJurisdiction));
        }

        return new TaskJurisdictionBooleanValidator(taskJurisdictionDto, userJurisdiction, associatedJurisdictionValidators);
    }

    private TaskJurisdictionBooleanValidator(TaskJurisdictionDto taskJurisdictionDto, UserJurisdiction userJurisdiction, List<BooleanJurisdictionValidator> associatedJurisdictionValidators) {
        super(associatedJurisdictionValidators);
        this.taskJurisdictionDto = taskJurisdictionDto;
        this.userJurisdiction = userJurisdiction;
    }

    @Override
    protected Boolean isInJurisdiction() {
        return isInJurisdictionByJurisdictionLevel(userJurisdiction.getJurisdictionLevel());
    }

    @Override
    protected Boolean isInJurisdictionOrOwned() {
        return userJurisdiction.getUuid().equals(taskJurisdictionDto.getCreatorUserUuid()) || userJurisdiction.getUuid().equals(taskJurisdictionDto.getAssigneeUserUuid()) || inJurisdiction();
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
        return false;
    }
}
