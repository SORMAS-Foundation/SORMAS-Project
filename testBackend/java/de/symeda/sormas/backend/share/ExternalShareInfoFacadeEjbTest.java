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

package de.symeda.sormas.backend.share;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.share.ExternalShareInfoCriteria;
import de.symeda.sormas.api.share.ExternalShareInfoDto;
import de.symeda.sormas.api.share.ExternalShareStatus;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class ExternalShareInfoFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetIndexList() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto officer = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference();

		CaseDataDto caze = creator.createCase(officer, rdcf, null);
		ExternalShareInfo caseShareInfoOld = creator.createExternalShareInfo(caze.toReference(), officer, ExternalShareStatus.SHARED, (i) -> {
			i.setCreationDate(new Timestamp(DateHelper.parseDate("12.02.2021", new SimpleDateFormat("dd.MM.yyyy")).getTime()));
		});
		ExternalShareInfo caseShareInfoNew = creator.createExternalShareInfo(caze.toReference(), officer, ExternalShareStatus.SHARED, (i) -> {
			i.setCreationDate(new Timestamp(DateHelper.parseDate("13.02.2021", new SimpleDateFormat("dd.MM.yyyy")).getTime()));
		});

		EventDto event = creator.createEvent(officer);
		ExternalShareInfo eventShareInfo = creator.createExternalShareInfo(event.toReference(), officer, ExternalShareStatus.SHARED);

		List<ExternalShareInfoDto> caseShares =
			getExternalShareInfoFacade().getIndexList(new ExternalShareInfoCriteria().caze(caze.toReference()), 0, 100);

		assertThat(caseShares, hasSize(2));
		assertThat(caseShares.get(0).getUuid(), is(caseShareInfoNew.getUuid()));
		assertThat(caseShares.get(1).getUuid(), is(caseShareInfoOld.getUuid()));

		List<ExternalShareInfoDto> eventShares =
			getExternalShareInfoFacade().getIndexList(new ExternalShareInfoCriteria().event(event.toReference()), 0, 100);

		assertThat(eventShares, hasSize(1));
		assertThat(eventShares.get(0).getUuid(), is(eventShareInfo.getUuid()));

	}
}
