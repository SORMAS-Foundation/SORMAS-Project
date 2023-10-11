/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.dashboard.sample;

import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.dashboard.SampleDashboardCriteria;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleDashboardFilterDateType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.AbstractUiBeanTest;

public class SampleDashboardDataProviderTest extends AbstractUiBeanTest {

	private SampleDashboardDataProvider dataProvider;

	@Override
	public void init() {
		super.init();
		dataProvider = new SampleDashboardDataProvider();
	}

	@Test
	public void testBuildDashboardCriteria() {
		Date fromDate = DateHelper.getDateZero(2023, 2, 20);
		Date toDateDate = DateHelper.getDateZero(2023, 2, 25);

		dataProvider.setFromDate(fromDate);
		dataProvider.setToDate(toDateDate);
		dataProvider.setDateType(SampleDashboardFilterDateType.ASSOCIATED_ENTITY_REPORT_DATE);
		dataProvider.setDisease(Disease.CORONAVIRUS);
		dataProvider.setSampleMaterial(SampleMaterial.CRUST);
		dataProvider.setWithNoDisease(true);

		SampleDashboardCriteria criteria = dataProvider.buildDashboardCriteriaWithDates();

		Assertions.assertEquals(dataProvider.getFromDate(), criteria.getDateFrom());
		Assertions.assertEquals(dataProvider.getToDate(), criteria.getDateTo());
		Assertions.assertEquals(dataProvider.getDateType(), criteria.getSampleDateType());
		Assertions.assertEquals(dataProvider.getDisease(), criteria.getDisease());
		Assertions.assertEquals(dataProvider.getSampleMaterial(), criteria.getSampleMaterial());
		Assertions.assertEquals(dataProvider.getWithNoDisease(), criteria.getWithNoDisease());
	}

	@Test
	public void testRefreshData() {
		var rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		PersonDto person = creator.createPerson();
		ContactDto contact = creator.createContact(user.toReference(), person.toReference(), Disease.CORONAVIRUS, rdcf);

		creator.createSample(contact.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setSamplePurpose(SamplePurpose.INTERNAL);
		});

		final String sampleExternalId = "test-sample-ext-id";
		SampleDto sample = creator.createSample(contact.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setSamplePurpose(SamplePurpose.EXTERNAL);
			s.setReceived(true);
		});
		creator.createPathogenTest(sample.toReference(), user.toReference(), rdcf.facility, t -> {
			t.setExternalId(sampleExternalId);
		});

		dataProvider.refreshData();

		Assertions.assertEquals(1, dataProvider.getSampleCountsByResultType().size());
		Assertions.assertEquals(2, dataProvider.getSampleCountsByPurpose().size());
		Assertions.assertEquals(1, dataProvider.getSampleCountsBySpecimenCondition().size());
		Assertions.assertEquals(1, dataProvider.getSampleCountsByShipmentStatus().size());
		Assertions.assertEquals(2, dataProvider.getTestResultCountsByResultType().size());
	}
}
