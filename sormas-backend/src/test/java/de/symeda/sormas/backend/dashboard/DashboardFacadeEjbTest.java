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
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.person.PersonDto;
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
			.newCaseDateBetween(DateHelper.subtractDays(new Date(), 1), DateHelper.addDays(new Date(), 1));

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
			new EventCriteria().region(event.getEventLocation().getRegion())
				.district(event.getEventLocation().getDistrict())
				.disease(event.getDisease())
				.eventDateType(null)
				.eventDateBetween(DateHelper.subtractDays(new Date(), 1), DateHelper.addDays(new Date(), 1)));

		// List should have one entry
		assertEquals(1, dashboardEventDtos.size());
	}
}
