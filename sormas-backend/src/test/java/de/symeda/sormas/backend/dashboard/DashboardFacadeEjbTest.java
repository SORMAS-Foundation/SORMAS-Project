package de.symeda.sormas.backend.dashboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.dashboard.DashboardCaseDto;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.dashboard.DashboardEventDto;
import de.symeda.sormas.api.dashboard.DashboardFacade;
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
}
