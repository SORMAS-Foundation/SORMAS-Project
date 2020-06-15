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

package de.symeda.sormas.backend.hospitalization;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HospitalizationFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;
	private UserDto user1;
	private UserDto user2;

	@Override
	public void init() {
		super.init();

		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		user1 = creator
			.createUser(rdcf1.region.getUuid(), rdcf1.district.getUuid(), rdcf1.facility.getUuid(), "Surv", "Off1", UserRole.SURVEILLANCE_OFFICER);

		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
		user2 = creator
			.createUser(rdcf2.region.getUuid(), rdcf2.district.getUuid(), rdcf2.facility.getUuid(), "Surv", "Off2", UserRole.SURVEILLANCE_OFFICER);

		when(MockProducer.getPrincipal().getName()).thenReturn("SurvOff2");
	}

	@Test
	public void testHospitalizationInJurisdiction() {
		CaseDataDto caseWithHospitalization = createCaseWithHospitalization(user2, rdcf2);

		CaseDataDto saved = getCaseFacade().getCaseDataByUuid(caseWithHospitalization.getUuid());

		assertNotPseudonymized(saved.getHospitalization());
	}

	@Test
	public void testHospitalizationOutsideJurisdiction() {
		CaseDataDto caseWithHospitalization = createCaseWithHospitalization(user1, rdcf1);

		CaseDataDto saved = getCaseFacade().getCaseDataByUuid(caseWithHospitalization.getUuid());

		assertPseudonymized(saved.getHospitalization());
	}

	public CaseDataDto createCaseWithHospitalization(UserDto user, TestDataCreator.RDCF rdcf) {
		return creator.createCase(user.toReference(), rdcf, c -> {
			PreviousHospitalizationDto prevHospitalization = new PreviousHospitalizationDto();
			prevHospitalization.setRegion(rdcf.region);
			prevHospitalization.setDistrict(rdcf.district);
			prevHospitalization.setCommunity(rdcf.community);
			prevHospitalization.setHealthFacility(rdcf.facility);
			prevHospitalization.setHealthFacilityDetails("Test facility details");
			prevHospitalization.setDescription("Test description");
			c.getHospitalization().getPreviousHospitalizations().add(prevHospitalization);
		});
	}

	private void assertNotPseudonymized(HospitalizationDto hospitalization){
		PreviousHospitalizationDto prevHospitalization = hospitalization.getPreviousHospitalizations().get(0);

		assertThat(prevHospitalization.getCommunity(), is(rdcf2.community));
		assertThat(prevHospitalization.getHealthFacility(), is(rdcf2.facility));
		assertThat(prevHospitalization.getHealthFacilityDetails(), is("Test facility details"));
		assertThat(prevHospitalization.getDescription(), is("Test description"));
	}

	private void assertPseudonymized(HospitalizationDto hospitalization){
		PreviousHospitalizationDto prevHospitalization = hospitalization.getPreviousHospitalizations().get(0);

		assertThat(prevHospitalization.getCommunity(), is(nullValue()));
		assertThat(prevHospitalization.getHealthFacility(), is(nullValue()));
		assertThat(prevHospitalization.getHealthFacilityDetails(), isEmptyString());
		assertThat(prevHospitalization.getDescription(), isEmptyString());
	}
}
