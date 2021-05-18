package de.symeda.sormas.backend.dashboard;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.dashboard.DashboardCaseDto;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.dashboard.DashboardEventDto;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class DashboardFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetCasesForDashboard() {

		TestDataCreator.RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		TestDataCreator.RDCFEntities rdcf2 = creator.createRDCFEntities("Region2", "District2", "Community2", "Facility2");
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
		CaseDataDto caze2 = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf2);
		caze2.setSharedToCountry(true);
		getCaseFacade().saveCase(caze2);

		DashboardCriteria dashboardCriteria = new DashboardCriteria().region(caze.getRegion())
			.district(caze.getDistrict())
			.disease(caze.getDisease())
			.newCaseDateType(NewCaseDateType.MOST_RELEVANT)
			.dateBetween(DateHelper.subtractDays(new Date(), 1), DateHelper.addDays(new Date(), 1));

		List<DashboardCaseDto> dashboardCaseDtos = getDashboardFacade().getCases(dashboardCriteria);

		// List should have only one entry; shared case should not appear
		assertEquals(1, dashboardCaseDtos.size());
	}

	@Test
	public void testDashboardEventListCreation() {

		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		EventDto event = creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"Title",
			"Description",
			"First",
			"Name",
			"12345",
			TypeOfPlace.PUBLIC_PLACE,
			new Date(),
			new Date(),
			user.toReference(),
			user.toReference(),
			Disease.EVD,
			rdcf.district);

		List<DashboardEventDto> dashboardEventDtos = getDashboardFacade().getNewEvents(
			new DashboardCriteria().region(event.getEventLocation().getRegion())
				.district(event.getEventLocation().getDistrict())
				.disease(event.getDisease())
				.dateBetween(DateHelper.subtractDays(new Date(), 1), DateHelper.addDays(new Date(), 1)));

		// List should have one entry
		assertEquals(1, dashboardEventDtos.size());
	}

	@Test
	public void testDiseaseBurdenForDashboard() {

		Date referenceDate = new Date();

		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		CommunityDto community2 = creator.createCommunity("Community2", rdcf.district);
		TestDataCreator.RDCF rdcf2 = new TestDataCreator.RDCF(
			rdcf.region,
			rdcf.district,
			community2.toReference(),
			creator.createFacility("Facility2", rdcf.region, rdcf.district, community2.toReference()).toReference());

		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);

		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			DateHelper.subtractDays(referenceDate, 2),
			rdcf);

		PersonDto cazePerson2 = creator.createPerson("Case", "Person2");
		CaseDataDto caze2 = creator.createCase(
			user.toReference(),
			cazePerson2.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			DateHelper.addDays(referenceDate, 1),
			rdcf2);

		PersonDto cazePerson3 = creator.createPerson("Case", "Person3");
		CaseDataDto caze3 = creator.createCase(
			user.toReference(),
			cazePerson3.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			DateHelper.addDays(referenceDate, 2),
			rdcf);

		PersonDto cazePerson4 = creator.createPerson("Case", "Person4");
		CaseDataDto caze4 = creator.createCase(
			user.toReference(),
			cazePerson4.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			referenceDate,
			rdcf2);

		List<DiseaseBurdenDto> diseaseBurdenForDashboard = getDashboardFacade().getDiseaseBurden(
			rdcf.region,
			rdcf.district,
			DateHelper.getStartOfDay(referenceDate),
			DateHelper.getEndOfDay(DateHelper.addDays(referenceDate, 10)),
			DateHelper.getStartOfDay(DateHelper.subtractDays(referenceDate, 10)),
			DateHelper.getEndOfDay(DateHelper.subtractDays(referenceDate, 1)),
			NewCaseDateType.MOST_RELEVANT);

		DiseaseBurdenDto evdBurden = diseaseBurdenForDashboard.stream().filter(dto -> dto.getDisease() == Disease.EVD).findFirst().get();
		assertEquals(new Long(3), evdBurden.getCaseCount());
		assertEquals(new Long(1), evdBurden.getPreviousCaseCount());
		assertEquals(rdcf.district.getCaption(), evdBurden.getLastReportedDistrictName());
	}
}
