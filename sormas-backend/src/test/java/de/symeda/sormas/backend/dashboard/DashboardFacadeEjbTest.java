package de.symeda.sormas.backend.dashboard;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.dashboard.*;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import org.testcontainers.shaded.org.apache.commons.lang3.time.DateUtils;

import static de.symeda.sormas.api.event.eventimport.EventImportEntities.createEvent;
import static org.junit.jupiter.api.Assertions.*;

public class DashboardFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetCasesForDashboard() {

		RDCF rdcf = creator.createRDCF();
		RDCF rdcf2 = creator.createRDCF("Region2", "District2", "Community2", "Facility2");

		UserDto user = creator.createSurveillanceSupervisor(rdcf);

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
		getCaseFacade().save(caze2);

		DashboardCriteria dashboardCriteria = new DashboardCriteria().region(caze.getResponsibleRegion())
			.district(caze.getDistrict())
			.disease(caze.getDisease())
			.newCaseDateType(NewCaseDateType.MOST_RELEVANT)
			.dateBetween(DateHelper.subtractDays(new Date(), 1), DateHelper.addDays(new Date(), 1));

		List<DashboardCaseDto> dashboardCaseDtos = getDashboardFacade().getCases(dashboardCriteria);

		// List should have only one entry; shared case should not appear
		assertEquals(1, dashboardCaseDtos.size());
	}

	@Test
	public void testGetTestResultCountByResultType() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceOfficer(rdcf);

		PersonReferenceDto person1 = creator.createPerson("Heinz", "First").toReference();
		PersonReferenceDto person2 = creator.createPerson("Heinz", "Second").toReference();
		CaseDataDto case1 = creator.createCase(user.toReference(), person1, rdcf);
		CaseDataDto case2 = creator.createCase(user.toReference(), person2, rdcf);

		Date date = new Date();
		DashboardCriteria dashboardCriteria = new DashboardCriteria().region(case1.getResponsibleRegion())
			.district(case1.getDistrict())
			.disease(case1.getDisease())
			.newCaseDateType(NewCaseDateType.REPORT)
			.dateBetween(DateHelper.subtractDays(date, 1), DateHelper.addDays(date, 1));

		DashboardFacade dashboardFacade = getDashboardFacade();
		// no existing samples
		Map<PathogenTestResultType, Long> resultMap = dashboardFacade.getNewCasesFinalLabResultCountByResultType(dashboardCriteria);
		assertEquals(new Long(0), resultMap.values().stream().collect(Collectors.summingLong(Long::longValue)));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.INDETERMINATE, null));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.NEGATIVE, null));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.PENDING, null));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.POSITIVE, null));

		// one pending sample with in one case
		FacilityDto lab = creator.createFacility("lab", rdcf.region, rdcf.district, rdcf.community);
		creator.createSample(case1.toReference(), user.toReference(), lab.toReference());

		resultMap = dashboardFacade.getNewCasesFinalLabResultCountByResultType(dashboardCriteria);
		assertEquals(new Long(1), resultMap.values().stream().collect(Collectors.summingLong(Long::longValue)));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.INDETERMINATE, null));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.NEGATIVE, null));
		assertEquals(new Long(1), resultMap.getOrDefault(PathogenTestResultType.PENDING, null));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.POSITIVE, null));

		// one pending sample in each of two cases
		creator.createSample(case2.toReference(), user.toReference(), lab.toReference());

		resultMap = dashboardFacade.getNewCasesFinalLabResultCountByResultType(dashboardCriteria);
		assertEquals(new Long(2), resultMap.values().stream().collect(Collectors.summingLong(Long::longValue)));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.INDETERMINATE, null));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.NEGATIVE, null));
		assertEquals(new Long(2), resultMap.getOrDefault(PathogenTestResultType.PENDING, null));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.POSITIVE, null));

		// one pending sample in each of two cases
		// and one positive sample in one of the two cases
		SampleDto sample = creator.createSample(case1.toReference(), user.toReference(), lab.toReference());
		sample.setPathogenTestResult(PathogenTestResultType.POSITIVE);
		getSampleFacade().saveSample(sample);

		resultMap = dashboardFacade.getNewCasesFinalLabResultCountByResultType(dashboardCriteria);
		assertEquals(new Long(2), resultMap.values().stream().collect(Collectors.summingLong(Long::longValue)));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.INDETERMINATE, null));
		assertNull(resultMap.getOrDefault(PathogenTestResultType.NEGATIVE, null));
		assertEquals(new Long(1), resultMap.getOrDefault(PathogenTestResultType.PENDING, null));
		assertEquals(new Long(1), resultMap.getOrDefault(PathogenTestResultType.POSITIVE, null));
	}

	@Test
	public void testDashboardEventListCreation() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

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
			rdcf);

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

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		CommunityDto community2 = creator.createCommunity("Community2", rdcf.district);
		RDCF rdcf2 = new RDCF(
			rdcf.region,
			rdcf.district,
			community2.toReference(),
			creator.createFacility("Facility2", rdcf.region, rdcf.district, community2.toReference()).toReference());

		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));

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

	@Test
	public void testGetCasesForDashboardPerPerson() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();

		PersonDto undefinedPerson = creator.createPerson();
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			undefinedPerson.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		creator.createCase(
			user.toReference(),
			undefinedPerson.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		createCasesForPersonWithCondition(PresentCondition.ALIVE, user.toReference(), rdcf, 2);
		createCasesForPersonWithCondition(PresentCondition.DEAD, user.toReference(), rdcf, 3);
		createCasesForPersonWithCondition(PresentCondition.BURIED, user.toReference(), rdcf, 2);
		createCasesForPersonWithCondition(PresentCondition.UNKNOWN, user.toReference(), rdcf, 4);

		DashboardCriteria dashboardCriteria = new DashboardCriteria().region(caze.getResponsibleRegion())
			.district(caze.getDistrict())
			.disease(caze.getDisease())
			.newCaseDateType(NewCaseDateType.MOST_RELEVANT)
			.dateBetween(DateHelper.subtractDays(new Date(), 1), DateHelper.addDays(new Date(), 1));

		Map<PresentCondition, Integer> dashboardCaseDtos = getDashboardFacade().getCasesCountPerPersonCondition(dashboardCriteria);
		assertEquals(4, dashboardCaseDtos.size());
		assertEquals(2, dashboardCaseDtos.get(PresentCondition.ALIVE).intValue());
		assertEquals(3, dashboardCaseDtos.get(PresentCondition.DEAD).intValue());
		assertEquals(2, dashboardCaseDtos.get(PresentCondition.BURIED).intValue());
		assertEquals(6, dashboardCaseDtos.get(PresentCondition.UNKNOWN).intValue());
	}

	private void createCasesForPersonWithCondition(PresentCondition presentCondition, UserReferenceDto userReferenceDto, RDCF rdcf, int nrOfCases) {
		PersonDto personDto = creator.createPerson("James Smith", presentCondition.name(), p -> {
			p.setPresentCondition(presentCondition);
		});

		for (int i = 0; i < nrOfCases; i++) {
			creator.createCase(
				userReferenceDto,
				personDto.toReference(),
				Disease.CORONAVIRUS,
				CaseClassification.NOT_CLASSIFIED,
				InvestigationStatus.PENDING,
				new Date(),
				rdcf);
		}
	}


	@Test
	public void testGetCasesCountByClassification() {

		// Create necessary data for testing
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		Date currentDate = new Date();

		// Create cases with different classifications
		creator.createCase(
				user.toReference(),
				creator.createPerson("Case", "Person1").toReference(),
				Disease.EVD,
				CaseClassification.CONFIRMED,
				InvestigationStatus.PENDING,
				currentDate,
				rdcf);

		creator.createCase(
				user.toReference(),
				creator.createPerson("Case", "Person2").toReference(),
				Disease.EVD,
				CaseClassification.PROBABLE,
				InvestigationStatus.PENDING,
				currentDate,
				rdcf);

		creator.createCase(
				user.toReference(),
				creator.createPerson("Case", "Person3").toReference(),
				Disease.EVD,
				CaseClassification.SUSPECT,
				InvestigationStatus.PENDING,
				currentDate,
				rdcf);

		// Define dashboard criteria
		DashboardCriteria dashboardCriteria = new DashboardCriteria()
				.region(rdcf.region)
				.district(rdcf.district)
				.disease(Disease.EVD)
				.newCaseDateType(NewCaseDateType.MOST_RELEVANT)
				.dateBetween(DateHelper.subtractDays(currentDate, 1), DateHelper.addDays(currentDate, 1));

		// Get counts by classification
		Map<CaseClassification, Integer> casesCountByClassification = getDashboardFacade().getCasesCountByClassification(dashboardCriteria);

		// Verify the results
		assertEquals(1, casesCountByClassification.get(CaseClassification.CONFIRMED));
		assertEquals(1, casesCountByClassification.get(CaseClassification.PROBABLE));
		assertEquals(1, casesCountByClassification.get(CaseClassification.SUSPECT));
	}


	@Test
	public void testGetEpiCurveSeriesElementsPerCaseClassification() {
		// Create necessary data for testing
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		Date currentDate = new Date();

		// Create cases with different classifications
		creator.createCase(
				user.toReference(),
				creator.createPerson("Case", "Person1").toReference(),
				Disease.EVD,
				CaseClassification.CONFIRMED,
				InvestigationStatus.PENDING,
				currentDate,
				rdcf);

		creator.createCase(
				user.toReference(),
				creator.createPerson("Case", "Person2").toReference(),
				Disease.EVD,
				CaseClassification.PROBABLE,
				InvestigationStatus.PENDING,
				currentDate,
				rdcf);

		// Define dashboard criteria
		DashboardCriteria dashboardCriteria = new DashboardCriteria()
				.region(rdcf.region)
				.district(rdcf.district)
				.disease(Disease.EVD)
				.newCaseDateType(NewCaseDateType.MOST_RELEVANT)
				.setEpiCurveGrouping(EpiCurveGrouping.DAY)
				.dateBetween(DateHelper.subtractDays(currentDate, 1), DateHelper.addDays(currentDate, 1));

		// Call the method under test
		Map<Date, Map<CaseClassification, Integer>> result = getDashboardFacade().getEpiCurveSeriesElementsPerCaseClassification(dashboardCriteria);

		// Assertions
		assertEquals(3, result.size()); // Ensure three date entries within dateBetween range

		// Validate the inner maps for each date
		for (Map.Entry<Date, Map<CaseClassification, Integer>> entry : result.entrySet()) {
			Date date = entry.getKey();
			Map<CaseClassification, Integer> classifications = entry.getValue();

			// Ensure the inner map is not null
			assertNotNull(classifications);

			// Validate the expected counts for each classification
			if (DateHelper.isSameDay(date, currentDate)) {
				assertEquals(2, classifications.size()); // Expecting two classifications for the current date
				assertEquals(1, classifications.get(CaseClassification.CONFIRMED).intValue()); // Assuming 1 CONFIRMED case
				assertEquals(1, classifications.get(CaseClassification.PROBABLE).intValue()); // Assuming 1 PROBABLE case
			} else {
				assertEquals(0, classifications.size()); // No cases expected for other dates
			}
		}
	}



	@Test
	public void testGetEpiCurveSeriesElementsPerPresentCondition() {
		// Create necessary data for testing
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		Date currentDate = new Date();

		// Create cases with different present conditions
		createCasesForPersonWithCondition(PresentCondition.ALIVE, user.toReference(), rdcf, 2);
		createCasesForPersonWithCondition(PresentCondition.DEAD, user.toReference(), rdcf, 3);

		// Define dashboard criteria
		DashboardCriteria dashboardCriteria = new DashboardCriteria()
				.region(rdcf.region)
				.district(rdcf.district)
				.disease(Disease.CORONAVIRUS)
				.newCaseDateType(NewCaseDateType.MOST_RELEVANT)
				.setEpiCurveGrouping(EpiCurveGrouping.DAY)
				.dateBetween(DateHelper.subtractDays(currentDate, 1), DateHelper.addDays(currentDate, 1));

		// Call the method under test
		Map<Date, Map<PresentCondition, Integer>> result = getDashboardFacade().getEpiCurveSeriesElementsPerPresentCondition(dashboardCriteria);

		// Ensure the result map contains the expected currentDate without time comparison
		boolean currentDateFound = false;
		for (Date date : result.keySet()) {
			if (DateUtils.isSameDay(date, currentDate)) {
				currentDateFound = true;
				Map<PresentCondition, Integer> conditionMap = result.get(date);

				assertNotNull(conditionMap, "Condition map for currentDate is null in the result");

				// Perform assertions on PresentCondition.ALIVE
				Integer aliveCount = conditionMap.get(PresentCondition.ALIVE);
				assertNotNull(aliveCount, "Alive count is null in the condition map");
				assertEquals(2, aliveCount.intValue(), "Expected count for ALIVE");

				// Perform assertions on PresentCondition.DEAD
				Integer deadCount = conditionMap.get(PresentCondition.DEAD);
				assertNotNull(deadCount, "Dead count is null in the condition map");
				assertEquals(3, deadCount.intValue(), "Expected count for DEAD");

				break;
			}
		}

		assertTrue(currentDateFound, "CurrentDate not found in the result map");
	}

	@Test
	public void testGetCaseMeasurePerDistrict() {
		// Create necessary data for testing
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		Date currentDate = new Date();

		// Define dashboard criteria
		DashboardCriteria dashboardCriteria = new DashboardCriteria()
				.region(rdcf.region)
				.district(rdcf.district)
				.disease(Disease.EVD)
				.newCaseDateType(NewCaseDateType.MOST_RELEVANT)
				.dateBetween(DateHelper.subtractDays(currentDate, 1), DateHelper.addDays(currentDate, 1));

		// Call the method under test
		DashboardCaseMeasureDto result = getDashboardFacade().getCaseMeasurePerDistrict(dashboardCriteria);

		// Assertions
		assertNotNull(result); // Ensure the result is not null
	}

	@Test
	public void testCountCasesConvertedFromContacts() {
		// Create necessary data for testing
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		Date currentDate = new Date();

		// Define dashboard criteria
		DashboardCriteria dashboardCriteria = new DashboardCriteria()
				.region(rdcf.region)
				.district(rdcf.district)
				.disease(Disease.EVD)
				.newCaseDateType(NewCaseDateType.MOST_RELEVANT)
				.dateBetween(DateHelper.subtractDays(currentDate, 1), DateHelper.addDays(currentDate, 1));

		// Call the method under test
		long result = getDashboardFacade().countCasesConvertedFromContacts(dashboardCriteria);

		// Assertions
		assertEquals(0, result); // Assuming no cases converted from contacts
	}

	@Test
	public void testGetEpiCurveSeriesElementsPerContactClassification() {
		// Create necessary data for testing
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		Date currentDate = new Date();

		// Create cases
		CaseDataDto case1 = creator.createCase(
				user.toReference(),
				creator.createPerson("Case", "Person1").toReference(),
				Disease.EVD,
				CaseClassification.CONFIRMED,
				InvestigationStatus.PENDING,
				currentDate,
				rdcf);

		CaseDataDto case2 = creator.createCase(
				user.toReference(),
				creator.createPerson("Case", "Person2").toReference(),
				Disease.EVD,
				CaseClassification.PROBABLE,
				InvestigationStatus.PENDING,
				currentDate,
				rdcf);

		// Create contacts with classifications
		creator.createContact(
				user.toReference(),
				creator.createPerson("Contact", "Person1").toReference(),
				case1,
				rdcf,
				ContactClassification.CONFIRMED,new Date());

		creator.createContact(
				user.toReference(),
				creator.createPerson("Contact", "Person2").toReference(),
				case2,
				rdcf,
				ContactClassification.UNCONFIRMED,new Date());

		// Define dashboard criteria
		DashboardCriteria dashboardCriteria = new DashboardCriteria()
				.region(rdcf.region)
				.district(rdcf.district)
				.disease(Disease.EVD)
				.newCaseDateType(NewCaseDateType.MOST_RELEVANT)
				.setEpiCurveGrouping(EpiCurveGrouping.DAY)
				.dateBetween(DateHelper.subtractDays(currentDate, 1), DateHelper.addDays(currentDate, 1));

		// Call the method under test
		Map<Date, Map<ContactClassification, Long>> result = getDashboardFacade().getEpiCurveSeriesElementsPerContactClassification(dashboardCriteria);

		// Assertions
		assertEquals(3, result.size()); // Ensure only one date entry for current date
		assertTrue(result.containsKey(DateHelper.resetTime(currentDate))); // Ensure currentDate is in the map

		// Get the map for currentDate
		Map<ContactClassification, Long> classificationMap = result.get(DateHelper.resetTime(currentDate));
		assertNotNull(classificationMap); // Ensure the classificationMap for currentDate is not null

		// Perform assertions on ContactClassification.HIGH_RISK
		Long confirmedContactCount = classificationMap.get(ContactClassification.CONFIRMED);
		assertNotNull(confirmedContactCount); // Ensure the count for HIGH_RISK is not null
		assertEquals(1L, confirmedContactCount.longValue()); // Check the count for HIGH_RISK

		// Perform assertions on ContactClassification.LOW_RISK
		Long unconfirmedCount = classificationMap.get(ContactClassification.UNCONFIRMED);
		assertNotNull(unconfirmedCount); // Ensure the count for LOW_RISK is not null
		assertEquals(1L, unconfirmedCount.longValue()); // Check the count for LOW_RISK
	}

	@Test
	public void testGetEpiCurveSeriesElementsPerContactFollowUpUntil() {
		// Create necessary data for testing
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		Date currentDate = new Date();

		// Create cases
		CaseDataDto case1 = creator.createCase(
				user.toReference(),
				creator.createPerson("Case", "Person1").toReference(),
				Disease.EVD,
				CaseClassification.CONFIRMED,
				InvestigationStatus.DONE,
				currentDate,
				rdcf);

		// Create contacts with follow-up dates
		creator.createContact(
				user.toReference(),
				creator.createPerson("Contact", "Person1").toReference(),
				case1,
				rdcf,
				ContactClassification.CONFIRMED,
				DateHelper.subtractDays(currentDate, 5)); // Example follow-up date 5 days before currentDate

		// Define dashboard criteria
		DashboardCriteria dashboardCriteria = new DashboardCriteria()
				.region(rdcf.region)
				.district(rdcf.district)
				.disease(Disease.EVD)
				.newCaseDateType(NewCaseDateType.MOST_RELEVANT)
				.setEpiCurveGrouping(EpiCurveGrouping.DAY)
				.dateBetween(DateHelper.subtractDays(currentDate, 1), DateHelper.addDays(currentDate, 1));

		// Call the method under test
		Map<Date, Integer> result = getDashboardFacade().getEpiCurveSeriesElementsPerContactFollowUpUntil(dashboardCriteria);

		// Assertions
		assertEquals(3, result.size()); // Ensure only one date entry for current date
		assertTrue(result.containsKey(DateHelper.resetTime(currentDate))); // Ensure currentDate is in the map
	}


	@Test
	public void testGetEventCountByStatus() {
		// Create necessary data for testing
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		Date currentDate = new Date();

		// Create mock events with different statuses
		creator.createEvent(user.toReference(), Disease.EVD, rdcf);
		creator.createEvent(user.toReference(), Disease.MALARIA, rdcf);
		creator.createEvent(user.toReference(), Disease.NEW_INFLUENZA, rdcf);

		// Define dashboard criteria
		DashboardCriteria dashboardCriteria = new DashboardCriteria()
				.region(rdcf.region)
				.district(rdcf.district)
				.disease(Disease.EVD) // Adjust disease as needed for your test case
				.newCaseDateType(NewCaseDateType.MOST_RELEVANT)
				.dateBetween(DateHelper.subtractDays(currentDate, 1), DateHelper.addDays(currentDate, 1));

		// Call the method under test
		Map<EventStatus, Long> result = getDashboardFacade().getEventCountByStatus(dashboardCriteria);

		// Assertions
		assertNotNull(result); // Ensure result is not null

		// Verify specific status counts (adjust these based on your mock events and expected logic)
		assertTrue(result.containsKey(EventStatus.SIGNAL));
		assertEquals(1L, result.getOrDefault(EventStatus.SIGNAL, 0L).longValue()); // Example assertion
	}

	@Test
	public void testGetIntervalEndDate() {
		DashboardFacadeEjb dashboardFacadeEjb = new DashboardFacadeEjb();

		// Test case 1: DAY grouping
		Date startDate1 = new Date(); // Replace with actual date values
		Date expectedEndDate1 = DateHelper.getEndOfDay(startDate1);
		assertEquals(expectedEndDate1, dashboardFacadeEjb.getIntervalEndDate(startDate1, EpiCurveGrouping.DAY));

		// Test case 2: WEEK grouping
		Date startDate2 = new Date(); // Replace with actual date values
		Date expectedEndDate2 = DateHelper.getEndOfWeek(startDate2);
		assertEquals(expectedEndDate2, dashboardFacadeEjb.getIntervalEndDate(startDate2, EpiCurveGrouping.WEEK));

		// Test case 3: MONTH grouping
		Date startDate3 = new Date(); // Replace with actual date values
		Date expectedEndDate3 = DateHelper.getEndOfMonth(startDate3);
		assertEquals(expectedEndDate3, dashboardFacadeEjb.getIntervalEndDate(startDate3, EpiCurveGrouping.MONTH));
	}

	// Method to test

	@Test
	public void testSetNewCaseDatesInCaseCriteria() {
		DashboardFacadeEjb dashboardFacadeEjb = new DashboardFacadeEjb();

		// Test case 1: DAY grouping
		Date date1 = new Date(); // Replace with actual date values
		DashboardCriteria dashboardCriteria1 = new DashboardCriteria()
				.setEpiCurveGrouping(EpiCurveGrouping.DAY);
		DashboardCriteria result1 = dashboardFacadeEjb.setNewCaseDatesInCaseCriteria(date1, dashboardCriteria1);
		assertEquals(DateHelper.getStartOfDay(date1), result1.getDateFrom());
		assertEquals(DateHelper.getEndOfDay(date1), result1.getDateTo());

		// Test case 2: WEEK grouping
		Date date2 = new Date(); // Replace with actual date values
		DashboardCriteria dashboardCriteria2 = new DashboardCriteria()
				.setEpiCurveGrouping(EpiCurveGrouping.WEEK);
		DashboardCriteria result2 = dashboardFacadeEjb.setNewCaseDatesInCaseCriteria(date2, dashboardCriteria2);
		assertEquals(DateHelper.getStartOfWeek(date2), result2.getDateFrom());
		assertEquals(DateHelper.getEndOfWeek(date2), result2.getDateTo());

		// Test case 3: MONTH grouping
		Date date3 = new Date(); // Replace with actual date values
		DashboardCriteria dashboardCriteria3 = new DashboardCriteria()
				.setEpiCurveGrouping(EpiCurveGrouping.MONTH);
		DashboardCriteria result3 = dashboardFacadeEjb.setNewCaseDatesInCaseCriteria(date3, dashboardCriteria3);
		assertEquals(DateHelper.getStartOfMonth(date3), result3.getDateFrom());
		assertEquals(DateHelper.getEndOfMonth(date3), result3.getDateTo());
	}

}
