/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.immunization;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class ImmunizationFacadeEjbTest extends AbstractBeanTest {

    @Test
    public void testSaveAndGetByUuid() {
        TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
        UserDto user = creator
                .createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
        PersonDto person = creator.createPerson("John", "Doe");
        ImmunizationDto immunizationDto = creator.createImmunization(Disease.CORONAVIRUS, person.toReference(), user.toReference(), ImmunizationStatus.ACQUIRED, MeansOfImmunization.VACCINATION, ImmunizationManagementStatus.COMPLETED, rdcf);
        ImmunizationDto actual = getImmunizationFacade().getByUuid(immunizationDto.getUuid());
        assertEquals(immunizationDto.getUuid(), actual.getUuid());
        assertEquals(immunizationDto.getPerson(), actual.getPerson());
    }

}
