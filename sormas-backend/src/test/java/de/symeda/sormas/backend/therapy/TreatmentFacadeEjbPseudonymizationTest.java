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

package de.symeda.sormas.backend.therapy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.mockito.Mockito.when;

import java.util.List;

import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.TreatmentExportDto;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.therapy.PrescriptionExportDto;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.therapy.TreatmentIndexDto;
import de.symeda.sormas.api.therapy.TreatmentRoute;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;

@RunWith(MockitoJUnitRunner.class)
public class TreatmentFacadeEjbPseudonymizationTest extends AbstractBeanTest {
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
	public void testTreatmentInJurisdiction() {
		TreatmentDto treatment = createTreatment(creator.createCase(user2.toReference(), rdcf2, null));

		assertNotPseudonymized(getTreatmentFacade().getTreatmentByUuid(treatment.getUuid()));
	}

	@Test
	public void testTreatmentOutsideJurisdiction() {
		TreatmentDto treatment = createTreatment(creator.createCase(user1.toReference(), rdcf1, null));

		assertPseudonymized(getTreatmentFacade().getTreatmentByUuid(treatment.getUuid()));
	}

	@Test
	public void testGetAllTreatmentsAfter(){
		TreatmentDto treatment1 = createTreatment(creator.createCase(user2.toReference(), rdcf2, null));
		CaseDataDto case2 = creator.createCase(user1.toReference(), rdcf1, null);
		creator.createContact(user2.toReference(), creator.createPerson().toReference(), case2);
		TreatmentDto treatment2 = createTreatment(case2);

		List<TreatmentDto> treatments = getTreatmentFacade().getAllActiveTreatmentsAfter(DateTime.now().minusYears(1).toDate());

		assertNotPseudonymized(treatments.stream().filter(p -> p.getUuid().equals(treatment1.getUuid())).findFirst().get());
		assertPseudonymized(treatments.stream().filter(p -> p.getUuid().equals(treatment2.getUuid())).findFirst().get());
	}

	@Test
	public void testPseudonymizeIndexList(){
		CaseDataDto case1 = creator.createCase(user2.toReference(), rdcf2, null);
		TreatmentDto treatment1 = createTreatment(case1);
		CaseDataDto case2 = creator.createCase(user1.toReference(), rdcf1, null);
		creator.createContact(user2.toReference(), creator.createPerson().toReference(), case2);
		TreatmentDto treatment2 = createTreatment(case2);

		List<TreatmentIndexDto> treatments = getTreatmentFacade().getIndexList(null);

		TreatmentIndexDto export1 = treatments.stream().filter(p -> p.getUuid().equals(treatment1.getUuid())).findFirst().get();
		assertThat(export1.getExecutingClinician(), is("John Smith"));
		assertThat(export1.getTreatmentType(), is("Blood transfusion - Test details"));
		assertThat(export1.getTreatmentRoute(), is("Test route details"));

		TreatmentIndexDto export2 = treatments.stream().filter(p -> p.getUuid().equals(treatment2.getUuid())).findFirst().get();
		assertThat(export2.getExecutingClinician(), isEmptyString());
		assertThat(export2.getTreatmentType(), is("Blood transfusion"));
		assertThat(export2.getTreatmentRoute(), is("Other"));
	}

	@Test
	public void testPseudonymizeExportList(){
		CaseDataDto case1 = creator.createCase(user2.toReference(), rdcf2, null);
		createTreatment(case1);
		CaseDataDto case2 = creator.createCase(user1.toReference(), rdcf1, null);
		creator.createContact(user2.toReference(), creator.createPerson().toReference(), case2);
		createTreatment(case2);

		List<TreatmentExportDto> exportList = getTreatmentFacade().getExportList(new CaseCriteria(), 0, 100);

		TreatmentExportDto export1 = exportList.stream().filter(p -> p.getCaseUuid().equals(case1.getUuid())).findFirst().get();
		assertThat(export1.getCaseName(), is("FirstName LASTNAME"));
		assertThat(export1.getExecutingClinician(), is("John Smith"));
		assertThat(export1.getTreatmentDetails(), is("Test details"));
		assertThat(export1.getRouteDetails(), is("Test route details"));
		assertThat(export1.getAdditionalNotes(), is("Test additional notes"));

		TreatmentExportDto export2 = exportList.stream().filter(p -> p.getCaseUuid().equals(case2.getUuid())).findFirst().get();
		assertThat(export2.getCaseName(), isEmptyString());
		assertThat(export2.getExecutingClinician(), isEmptyString());
		assertThat(export2.getTreatmentDetails(), isEmptyString());
		assertThat(export2.getRouteDetails(), isEmptyString());
		assertThat(export2.getAdditionalNotes(), isEmptyString());
	}

	@Test
	public void testUpdateOutsideJurisdiction(){
		CaseDataDto caze = creator.createCase(user1.toReference(), rdcf1, null);
		creator.createContact(user2.toReference(), creator.createPerson().toReference(), caze);
		TreatmentDto treatment = createTreatment(caze);

		treatment.setExecutingClinician(null);
		treatment.setTreatmentDetails(null);
		treatment.setRouteDetails(null);
		treatment.setAdditionalNotes(null);

		getTreatmentFacade().saveTreatment(treatment);

		Treatment saved = getTreatmentService().getByUuid(treatment.getUuid());

		assertThat(saved.getExecutingClinician(), is("John Smith"));
		assertThat(saved.getTreatmentDetails(), is("Test details"));
		assertThat(saved.getRouteDetails(), is("Test route details"));
		assertThat(saved.getAdditionalNotes(), is("Test additional notes"));
	}


	private TreatmentDto createTreatment(CaseDataDto caze) {
		return creator.createTreatment(caze, t -> {
			t.setExecutingClinician("John Smith");
			t.setTreatmentDetails("Test details");
			t.setRoute(TreatmentRoute.OTHER);
			t.setRouteDetails("Test route details");
			t.setAdditionalNotes("Test additional notes");
		});
	}

	private void assertNotPseudonymized(TreatmentDto treatment) {
		assertThat(treatment.getExecutingClinician(), is("John Smith"));
		assertThat(treatment.getTreatmentDetails(), is("Test details"));
		assertThat(treatment.getRouteDetails(), is("Test route details"));
		assertThat(treatment.getAdditionalNotes(), is("Test additional notes"));
	}

	private void assertPseudonymized(TreatmentDto treatment) {
		assertThat(treatment.getExecutingClinician(), isEmptyString());
		assertThat(treatment.getTreatmentDetails(), isEmptyString());
		assertThat(treatment.getRouteDetails(), isEmptyString());
		assertThat(treatment.getAdditionalNotes(), isEmptyString());
	}
}
