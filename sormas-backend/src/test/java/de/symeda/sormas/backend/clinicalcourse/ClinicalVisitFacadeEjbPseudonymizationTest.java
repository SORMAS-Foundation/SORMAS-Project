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

package de.symeda.sormas.backend.clinicalcourse;

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitCriteria;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitExportDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitIndexDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.symptoms.Symptoms;
import info.novatec.beantest.transactions.Transactional;
import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClinicalVisitFacadeEjbPseudonymizationTest extends AbstractBeanTest {

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
	public void testClinicalVisitInJurisdiction() {
		CaseDataDto caze = createCase(user2, rdcf2);
		ClinicalVisitDto visit = createClinicalVisit(caze);

		assertNotPseudonymized(getClinicalVisitFacade().getClinicalVisitByUuid(visit.getUuid()));
	}

	@Test
	public void testClinicalVisitOutsideJurisdiction() {
		CaseDataDto caze = createCase(user1, rdcf1);
		// create contact ro have access to @case1
		creator.createContact(user2.toReference(), caze.getPerson(), caze);
		ClinicalVisitDto visit = createClinicalVisit(caze);

		assertPseudonymized(getClinicalVisitFacade().getClinicalVisitByUuid(visit.getUuid()));
	}

	@Test
	public void testGetVisitsAfter(){
		CaseDataDto case1 = createCase(user1, rdcf1);
		// create contact ro have access to @case1
		creator.createContact(user2.toReference(), case1.getPerson(), case1);
		ClinicalVisitDto visit1 = createClinicalVisit(case1);

		CaseDataDto case2 = createCase(user2, rdcf2);
		ClinicalVisitDto visit2 = createClinicalVisit(case2);

		List<ClinicalVisitDto> visitsAfter = getClinicalVisitFacade().getAllActiveClinicalVisitsAfter(DateTime.now().withYear(2019).toDate());

		assertPseudonymized(visitsAfter.stream().filter(v -> v.getUuid().equals(visit1.getUuid())).findFirst().get());
		assertNotPseudonymized(visitsAfter.stream().filter(v -> v.getUuid().equals(visit2.getUuid())).findFirst().get());
	}

	@Test
	public void testPseudonymizeIndexList() {
		CaseDataDto case1 = createCase(user1, rdcf1);
		// create contact ro have access to @case1
		creator.createContact(user2.toReference(), case1.getPerson(), case1);
		ClinicalVisitDto visit1 = createClinicalVisit(case1);

		CaseDataDto case2 = createCase(user2, rdcf2);
		ClinicalVisitDto visit2 = createClinicalVisit(case2);

		List<ClinicalVisitIndexDto> indexList = getClinicalVisitFacade().getIndexList(null);

		ClinicalVisitIndexDto export1 = indexList.stream().filter(v -> v.getUuid().equals(visit1.getUuid())).findFirst().get();
		assertThat(export1.getVisitingPerson(), isEmptyString());
		assertThat(export1.getVisitRemarks(), isEmptyString());

		ClinicalVisitIndexDto export2 = indexList.stream().filter(v -> v.getUuid().equals(visit2.getUuid())).findFirst().get();
		assertThat(export2.getVisitingPerson(), is("John Smith"));
		assertThat(export2.getVisitRemarks(), is("Test remarks"));
	}

	@Test
	public void testPseudonymizeExportList() {
		CaseDataDto case1 = createCase(user1, rdcf1);
		// create contact ro have access to @case1
		creator.createContact(user2.toReference(), case1.getPerson(), case1);
		createClinicalVisit(case1);

		CaseDataDto case2 = createCase(user2, rdcf2);
		createClinicalVisit(case2);

		List<ClinicalVisitExportDto> exportList = getClinicalVisitFacade().getExportList(new CaseCriteria(), 0, 100);

		ClinicalVisitExportDto export1 = exportList.stream().filter(v -> v.getCaseUuid().equals(case1.getUuid())).findFirst().get();
		assertThat(export1.getCaseName(), isEmptyString());
		assertThat(export1.getVisitingPerson(), isEmptyString());
		assertThat(export1.getVisitRemarks(), isEmptyString());

		ClinicalVisitExportDto export2 = exportList.stream().filter(v -> v.getCaseUuid().equals(case2.getUuid())).findFirst().get();
		assertThat(export2.getCaseName(), is("John SMITH"));
		assertThat(export2.getVisitingPerson(), is("John Smith"));
		assertThat(export2.getVisitRemarks(), is("Test remarks"));
	}

	@Test
	public void testUpdateOutsideJurisdiction(){
		CaseDataDto caze = createCase(user1, rdcf1);
		// create contact ro have access to @case1
		creator.createContact(user2.toReference(), caze.getPerson(), caze);
		ClinicalVisitDto visit = createClinicalVisit(caze);

		visit.setVisitRemarks(null);
		visit.setVisitingPerson(null);
		visit.getSymptoms().setPatientIllLocation(null);
		visit.getSymptoms().setOtherHemorrhagicSymptomsText(null);

		getClinicalVisitFacade().saveClinicalVisit(visit);

		ClinicalVisit saved = getClinicalVisitService().getByUuid(visit.getUuid());

		assertThat(saved.getVisitRemarks(), is("Test remarks"));
		assertThat(saved.getVisitingPerson(), is("John Smith"));

		Symptoms symptoms = getSymptomsService().getByUuid(visit.getSymptoms().getUuid());
		assertThat(symptoms.getPatientIllLocation(), is("Test ill location"));
		assertThat(symptoms.getOtherHemorrhagicSymptomsText(), is("OtherHemorrhagic"));
	}

	private ClinicalVisitDto createClinicalVisit(CaseDataDto caseDataDto) {
		return creator.createClinicalVisit(caseDataDto, v -> {
			v.setVisitingPerson("John Smith");
			v.setVisitRemarks("Test remarks");

			SymptomsDto symptoms = new SymptomsDto();
			symptoms.setPatientIllLocation("Test ill location");
			symptoms.setOtherHemorrhagicSymptoms(SymptomState.YES);
			symptoms.setOtherHemorrhagicSymptomsText("OtherHemorrhagic");
			v.setSymptoms(symptoms);
		});
	}

	private CaseDataDto createCase(UserDto reportingUser, TestDataCreator.RDCF rdcf) {
		return creator.createCase(reportingUser.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf);
	}

	private void assertPseudonymized(ClinicalVisitDto visit) {
		assertThat(visit.getVisitingPerson(), isEmptyString());
		assertThat(visit.getVisitRemarks(), isEmptyString());
	}

	private void assertNotPseudonymized(ClinicalVisitDto visit) {
		assertThat(visit.getVisitingPerson(), is("John Smith"));
		assertThat(visit.getVisitRemarks(), is("Test remarks"));
	}
}
