/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.specialcaseaccess;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.specialcaseaccess.SpecialCaseAccessDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class SpecialCaseAccessFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetByCase() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf, "officer", DefaultUserRole.SURVEILLANCE_OFFICER).toReference();
		CaseDataDto caze = creator.createCase(user, rdcf, null);

		TestDataCreator.RDCF otherRdcf = creator.createRDCF();
		UserReferenceDto otherUser = creator.createUser(otherRdcf, "otherOfficer", DefaultUserRole.SURVEILLANCE_OFFICER).toReference();

		creator.createSpecialCaseAccess(caze.toReference(), user, otherUser, DateHelper.addDays(new Date(), 1));

		// expired ones should not be returned
		creator.createSpecialCaseAccess(caze.toReference(), user, otherUser, DateHelper.subtractDays(new Date(), 1));
		// ones linked to other cases should not be returned
		CaseDataDto caze2 = creator.createCase(user, rdcf, null);
		creator.createSpecialCaseAccess(caze2.toReference(), user, otherUser, DateHelper.addDays(new Date(), 1));

		List<SpecialCaseAccessDto> specialAccesses = getSpecialCaseAccessFacade().getAllActiveByCase(caze.toReference());
		assertThat(specialAccesses, hasSize(1));
		assertThat(specialAccesses.get(0).getCaze(), is(caze.toReference()));
		assertThat(specialAccesses.get(0).getAssignedBy(), is(user));
		assertThat(specialAccesses.get(0).getAssignedTo(), is(otherUser));
	}

	@Test
	public void testDeleteExpiredSpecialAccesses() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf, "officer", DefaultUserRole.SURVEILLANCE_OFFICER).toReference();
		CaseDataDto caze = creator.createCase(user, rdcf, null);

		TestDataCreator.RDCF otherRdcf = creator.createRDCF();
		UserReferenceDto otherUser = creator.createUser(otherRdcf, "otherOfficer", DefaultUserRole.SURVEILLANCE_OFFICER).toReference();

		creator.createSpecialCaseAccess(caze.toReference(), user, otherUser, DateHelper.addDays(new Date(), 1));
		// expired ones should be deleted
		creator.createSpecialCaseAccess(caze.toReference(), user, otherUser, DateHelper.subtractDays(new Date(), 1));

		getSpecialCaseAccessFacade().deleteExpiredSpecialCaseAccesses();

		long specialAccessCount = getSpecialCaseAccessService().count();
		assertThat(specialAccessCount, is(1L));
	}
}
