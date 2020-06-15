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

package de.symeda.sormas.backend.symptoms;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SymptomsPseudonymizationTest extends AbstractBeanTest {
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
	public void testSymptomsInJurisdiction() {
		CaseDataDto caze = createCaseWithSymptoms(user2, rdcf2);

		SymptomsDto symptoms = getCaseFacade().getCaseDataByUuid(caze.getUuid()).getSymptoms();

		assertThat(symptoms.getOnsetSymptom(), is("Test onset"));
		assertThat(symptoms.getOtherHemorrhagicSymptomsText(), is("Hemorrhagic"));
		assertThat(symptoms.getOtherNonHemorrhagicSymptomsText(), is("NonHemorrhagic"));
		assertThat(symptoms.getPatientIllLocation(), is("Test location"));
		assertThat(symptoms.getCongenitalHeartDiseaseDetails(), is("Congenital"));
	}

	@Test
	public void testSymptomsOutsideJurisdiction() {
		CaseDataDto caze = createCaseWithSymptoms(user1, rdcf1);

		SymptomsDto symptoms = getCaseFacade().getCaseDataByUuid(caze.getUuid()).getSymptoms();

		assertThat(symptoms.getOnsetSymptom(), is("Test onset"));
		assertThat(symptoms.getOtherHemorrhagicSymptomsText(), isEmptyString());
		assertThat(symptoms.getOtherNonHemorrhagicSymptomsText(), isEmptyString());
		assertThat(symptoms.getPatientIllLocation(), isEmptyString());
		assertThat(symptoms.getCongenitalHeartDiseaseDetails(), isEmptyString());
	}

	private CaseDataDto createCaseWithSymptoms(UserDto user, TestDataCreator.RDCF rdcf) {
		return creator.createCase(user.toReference(), rdcf, c -> {
			SymptomsDto symptoms = c.getSymptoms();

			symptoms.setOnsetSymptom("Test onset");
			symptoms.setOtherHemorrhagicSymptoms(SymptomState.YES);
			symptoms.setOtherHemorrhagicSymptomsText("Hemorrhagic");
			symptoms.setOtherNonHemorrhagicSymptoms(SymptomState.YES);
			symptoms.setOtherNonHemorrhagicSymptomsText("NonHemorrhagic");
			symptoms.setPatientIllLocation("Test location");
			symptoms.setCongenitalHeartDisease(SymptomState.YES);
			symptoms.setCongenitalHeartDiseaseDetails("Congenital");
		});
	}
}
