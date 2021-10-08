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

package de.symeda.sormas.app.backend.immunization;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.backend.caze.ResponsibleJurisdictionDto;
import de.symeda.sormas.app.util.BooleanJurisdictionValidator;
import de.symeda.sormas.app.util.UserJurisdiction;

public class ImmunizationJurisdictionBooleanValidator extends BooleanJurisdictionValidator {

    private final ImmunizationJurisdictionDto immunizationJurisdiction;
    private final UserJurisdiction userJurisdiction;

    public static ImmunizationJurisdictionBooleanValidator of(ImmunizationJurisdictionDto immunizationJurisdictionDto, UserJurisdiction userJurisdiction) {
        return new ImmunizationJurisdictionBooleanValidator(immunizationJurisdictionDto, userJurisdiction);
    }

    public ImmunizationJurisdictionBooleanValidator(ImmunizationJurisdictionDto immunizationJurisdiction, UserJurisdiction userJurisdiction) {
        super(null);
        this.immunizationJurisdiction = immunizationJurisdiction;
        this.userJurisdiction = userJurisdiction;
    }

    @Override
    protected Boolean isInJurisdiction() {
        return isInJurisdictionByJurisdictionLevel(userJurisdiction.getJurisdictionLevel());
    }

    @Override
    protected Boolean isInJurisdictionOrOwned() {
        return userJurisdiction.getUuid().equals(immunizationJurisdiction.getReportingUserUuid()) || inJurisdiction();
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
        ResponsibleJurisdictionDto responsibleJurisdiction = immunizationJurisdiction.getResponsibleJurisdiction();
        return responsibleJurisdiction != null && DataHelper.equal(responsibleJurisdiction.getRegionUuid(), userJurisdiction.getRegionUuid());
    }

    @Override
    protected Boolean whenDistrictLevel() {
        ResponsibleJurisdictionDto responsibleJurisdiction = immunizationJurisdiction.getResponsibleJurisdiction();
        return responsibleJurisdiction != null && DataHelper.equal(responsibleJurisdiction.getDistrictUuid(), userJurisdiction.getDistrictUuid());
    }

    @Override
    protected Boolean whenCommunityLevel() {
        ResponsibleJurisdictionDto responsibleJurisdiction = immunizationJurisdiction.getResponsibleJurisdiction();
        return responsibleJurisdiction != null && DataHelper.equal(responsibleJurisdiction.getCommunityUuid(), userJurisdiction.getCommunityUuid());
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
