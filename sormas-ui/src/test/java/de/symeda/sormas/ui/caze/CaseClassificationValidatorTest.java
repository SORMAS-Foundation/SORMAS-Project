/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.caze;

import java.io.IOException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator;

public class CaseClassificationValidatorTest extends AbstractBeanTest {

	public static final String INVALID_CASE_CLASSIFICATION = "invalid case classification";

	@Test
    public void testCaseClassificationValidator() throws IOException, InvalidColumnException, InterruptedException {

        TestDataCreator creator = new TestDataCreator();

        TestDataCreator.RDCF rdcf = creator.createRDCF("region", "district", "community", "facility");

        UserDto user = creator
                .createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
        PersonDto cazePerson = creator.createPerson("Case", "Person");
        CaseDataDto caze = creator.createCase(
                user.toReference(),
                cazePerson.toReference(),
                Disease.EVD,
                CaseClassification.PROBABLE,
                InvestigationStatus.PENDING,
                new Date(),
                rdcf);

        final CaseClassificationValidator caseClassificationValidator = new CaseClassificationValidator(caze.getUuid(), INVALID_CASE_CLASSIFICATION);
        Assert.assertTrue(caseClassificationValidator.isValidValue(CaseClassification.PROBABLE));

        fail on purpose this test needs to be extended for other cases
    }
}
