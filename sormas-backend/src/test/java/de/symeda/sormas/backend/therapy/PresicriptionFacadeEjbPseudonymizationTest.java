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

package de.symeda.sormas.backend.therapy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.mockito.Mockito.when;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.PrescriptionExportDto;
import de.symeda.sormas.api.therapy.PrescriptionIndexDto;
import de.symeda.sormas.api.therapy.TreatmentRoute;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;

@RunWith(MockitoJUnitRunner.class)
public class PresicriptionFacadeEjbPseudonymizationTest extends AbstractBeanTest {

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
	public void testPrescriptionInJurisdiction() {
		PrescriptionDto prescription = createPrescription(creator.createCase(user2.toReference(), rdcf2, null));

		assertNotPseudonymized(getPrescriptionFacade().getPrescriptionByUuid(prescription.getUuid()));
	}

	@Test
	public void testPrescriptionOutsideJurisdiction() {
		PrescriptionDto prescription = createPrescription(creator.createCase(user1.toReference(), rdcf1, null));

		assertPseudonymized(getPrescriptionFacade().getPrescriptionByUuid(prescription.getUuid()));
	}

	@Test
	public void testGetAllPrescriptionsAfter(){
		PrescriptionDto prescription1 = createPrescription(creator.createCase(user2.toReference(), rdcf2, null));
		CaseDataDto case2 = creator.createCase(user1.toReference(), rdcf1, null);
		creator.createContact(user2.toReference(), creator.createPerson().toReference(), case2);
		PrescriptionDto prescription2 = createPrescription(case2);

		List<PrescriptionDto> prescriptions = getPrescriptionFacade().getAllActivePrescriptionsAfter(DateTime.now().minusYears(1).toDate());

		assertNotPseudonymized(prescriptions.stream().filter(p -> p.getUuid().equals(prescription1.getUuid())).findFirst().get());
		assertPseudonymized(prescriptions.stream().filter(p -> p.getUuid().equals(prescription2.getUuid())).findFirst().get());
	}

	@Test
	public void testPseudonymizeIndexList(){
		CaseDataDto case1 = creator.createCase(user2.toReference(), rdcf2, null);
		PrescriptionDto prescription1 = createPrescription(case1);
		CaseDataDto case2 = creator.createCase(user1.toReference(), rdcf1, null);
		creator.createContact(user2.toReference(), creator.createPerson().toReference(), case2);
		PrescriptionDto prescription2 = createPrescription(case2);

		List<PrescriptionIndexDto> prescriptions = getPrescriptionFacade().getIndexList(null);

		PrescriptionIndexDto export1 = prescriptions.stream().filter(p -> p.getUuid().equals(prescription1.getUuid())).findFirst().get();
		assertThat(export1.getPrescribingClinician(), is("John Smith"));
		assertThat(export1.getPrescriptionType(), is("Blood transfusion - Test details"));
		assertThat(export1.getPrescriptionRoute(), is("Test route details"));

		PrescriptionIndexDto export2 = prescriptions.stream().filter(p -> p.getUuid().equals(prescription2.getUuid())).findFirst().get();
		assertThat(export2.getPrescribingClinician(), isEmptyString());
		assertThat(export2.getPrescriptionType(), is("Blood transfusion"));
		assertThat(export2.getPrescriptionRoute(), is("Other"));
	}


	@Test
	public void testPseudonymizeExportList(){
		CaseDataDto case1 = creator.createCase(user2.toReference(), rdcf2, null);
		createPrescription(case1);
		CaseDataDto case2 = creator.createCase(user1.toReference(), rdcf1, null);
		creator.createContact(user2.toReference(), creator.createPerson().toReference(), case2);
		createPrescription(case2);

		List<PrescriptionExportDto> prescriptions = getPrescriptionFacade().getExportList(new CaseCriteria(), 0, 100);

		PrescriptionExportDto export1 = prescriptions.stream().filter(p -> p.getCaseUuid().equals(case1.getUuid())).findFirst().get();
		assertThat(export1.getCaseName(), is("FirstName LASTNAME"));
		assertThat(export1.getPrescribingClinician(), is("John Smith"));
		assertThat(export1.getPrescriptionDetails(), is("Test details"));
		assertThat(export1.getRouteDetails(), is("Test route details"));
		assertThat(export1.getAdditionalNotes(), is("Test additional notes"));

		PrescriptionExportDto export2 = prescriptions.stream().filter(p -> p.getCaseUuid().equals(case2.getUuid())).findFirst().get();
		assertThat(export2.getCaseName(), isEmptyString());
		assertThat(export2.getPrescribingClinician(), isEmptyString());
		assertThat(export2.getPrescriptionDetails(), isEmptyString());
		assertThat(export2.getRouteDetails(), isEmptyString());
		assertThat(export2.getAdditionalNotes(), isEmptyString());
	}

	@Test
	public void testUpdateOutsideJurisdiction(){
		CaseDataDto caze = creator.createCase(user1.toReference(), rdcf1, null);
		creator.createContact(user2.toReference(), creator.createPerson().toReference(), caze);
		PrescriptionDto prescription = createPrescription(caze);

		prescription.setPrescribingClinician(null);
		prescription.setPrescriptionDetails(null);
		prescription.setRouteDetails(null);
		prescription.setAdditionalNotes(null);

		getPrescriptionFacade().savePrescription(prescription);

		Prescription saved = getPrescriptionService().getByUuid(prescription.getUuid());

		assertThat(saved.getPrescribingClinician(), is("John Smith"));
		assertThat(saved.getPrescriptionDetails(), is("Test details"));
		assertThat(saved.getRouteDetails(), is("Test route details"));
		assertThat(saved.getAdditionalNotes(), is("Test additional notes"));
	}

	private PrescriptionDto createPrescription(CaseDataDto caze) {
		return creator.createPrescription(caze, p -> {
			p.setPrescribingClinician("John Smith");
			p.setPrescriptionDetails("Test details");
			p.setRoute(TreatmentRoute.OTHER);
			p.setRouteDetails("Test route details");
			p.setAdditionalNotes("Test additional notes");
		});
	}

	private void assertNotPseudonymized(PrescriptionDto prescription) {
		assertThat(prescription.getPrescribingClinician(), is("John Smith"));
		assertThat(prescription.getPrescriptionDetails(), is("Test details"));
		assertThat(prescription.getRouteDetails(), is("Test route details"));
		assertThat(prescription.getAdditionalNotes(), is("Test additional notes"));
	}

	private void assertPseudonymized(PrescriptionDto prescription) {
		assertThat(prescription.getPrescribingClinician(), isEmptyString());
		assertThat(prescription.getPrescriptionDetails(), isEmptyString());
		assertThat(prescription.getRouteDetails(), isEmptyString());
		assertThat(prescription.getAdditionalNotes(), isEmptyString());
	}
}
