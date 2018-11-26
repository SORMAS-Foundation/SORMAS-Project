/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 *******************************************************************************/
package de.symeda.sormas.backend.caze;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.CaseMeasure;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.IntegerRange;
import de.symeda.sormas.api.Month;
import de.symeda.sormas.api.MonthOfYear;
import de.symeda.sormas.api.Quarter;
import de.symeda.sormas.api.QuarterOfYear;
import de.symeda.sormas.api.Year;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.epidata.EpiDataTravelHelper;
import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.CauseOfDeath;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseCriteria;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;
import de.symeda.sormas.api.statistics.StatisticsHelper;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskHelper;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.caze.classification.CaseClassificationFacadeEjb.CaseClassificationFacadeEjbLocal;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.common.MessageType;
import de.symeda.sormas.backend.common.MessagingService;
import de.symeda.sormas.backend.common.NotificationDeliveryFailedException;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.epidata.EpiDataFacadeEjb;
import de.symeda.sormas.backend.epidata.EpiDataFacadeEjb.EpiDataFacadeEjbLocal;
import de.symeda.sormas.backend.epidata.EpiDataService;
import de.symeda.sormas.backend.epidata.EpiDataTravel;
import de.symeda.sormas.backend.epidata.EpiDataTravelService;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.hospitalization.Hospitalization;
import de.symeda.sormas.backend.hospitalization.HospitalizationFacadeEjb;
import de.symeda.sormas.backend.hospitalization.HospitalizationFacadeEjb.HospitalizationFacadeEjbLocal;
import de.symeda.sormas.backend.hospitalization.HospitalizationService;
import de.symeda.sormas.backend.hospitalization.PreviousHospitalizationService;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.location.LocationService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.CommunityFacadeEjb;
import de.symeda.sormas.backend.region.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.sample.SampleTestFacadeEjb.SampleTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleTestService;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb.SymptomsFacadeEjbLocal;
import de.symeda.sormas.backend.symptoms.SymptomsService;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "CaseFacade")
public class CaseFacadeEjb implements CaseFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

	@EJB
	private CaseClassificationFacadeEjbLocal caseClassificationFacade;
	@EJB
	private CaseService caseService;
	@EJB
	private PersonService personService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private UserService userService;
	@EJB
	private UserFacadeEjbLocal userFacade;
	@EJB
	private SymptomsFacadeEjbLocal symptomsFacade;
	@EJB
	private LocationFacadeEjbLocal locationFacade;
	@EJB
	private RegionFacadeEjbLocal regionFacade;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityFacadeEjbLocal communityFacade;
	@EJB
	private CommunityService communityService;
	@EJB
	private FacilityFacadeEjbLocal facilityFacade;
	@EJB
	private TaskService taskService;
	@EJB
	private LocationService locationService;
	@EJB
	private HospitalizationService hospitalizationService;
	@EJB
	private EpiDataService epiDataService;
	@EJB
	private EpiDataTravelService epiDataTravelService;
	@EJB
	private SymptomsService symptomsService;
	@EJB
	private ContactService contactService;
	@EJB
	private SampleService sampleService;
	@EJB
	private SampleTestService sampleTestService;
	@EJB
	private SampleTestFacadeEjbLocal sampleTestFacade;
	@EJB
	private HospitalizationFacadeEjbLocal hospitalizationFacade;
	@EJB
	private PreviousHospitalizationService previousHospitalizationService;
	@EJB
	private EpiDataFacadeEjbLocal epiDataFacade;
	@EJB
	private ContactFacadeEjbLocal contactFacade;
	@EJB
	private MessagingService messagingService;
	@EJB
	private EventParticipantService eventParticipantService;
	@EJB
	private PersonFacadeEjbLocal personFacade;
	@EJB
	private ConfigFacadeEjbLocal configFacade;

	private static final Logger logger = LoggerFactory.getLogger(CaseFacadeEjb.class);

	@Override
	public List<CaseDataDto> getAllActiveCasesAfter(Date date, String userUuid) {
		User user = userService.getByUuid(userUuid);

		if (user == null) {
			return Collections.emptyList();
		}

		return caseService.getAllActiveCasesAfter(date, user).stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}

	@Override
	public List<CaseDataDto> getByUuids(List<String> uuids) {
		return caseService.getByUuids(uuids).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<CaseIndexDto> getIndexList(String userUuid, CaseCriteria caseCriteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CaseIndexDto> cq = cb.createQuery(CaseIndexDto.class);
		Root<Case> caze = cq.from(Case.class);
		Join<Case, Person> person = caze.join(Case.PERSON, JoinType.LEFT);
		Join<Case, Region> region = caze.join(Case.REGION, JoinType.LEFT);
		Join<Case, District> district = caze.join(Case.DISTRICT, JoinType.LEFT);
		Join<Case, Facility> facility = caze.join(Case.HEALTH_FACILITY, JoinType.LEFT);
		Join<Case, User> surveillanceOfficer = caze.join(Case.SURVEILLANCE_OFFICER, JoinType.LEFT);

		cq.multiselect(caze.get(Case.UUID), caze.get(Case.EPID_NUMBER), person.get(Person.FIRST_NAME),
				person.get(Person.LAST_NAME), caze.get(Case.DISEASE), caze.get(Case.DISEASE_DETAILS),
				caze.get(Case.CASE_CLASSIFICATION), caze.get(Case.INVESTIGATION_STATUS),
				person.get(Person.PRESENT_CONDITION), caze.get(Case.REPORT_DATE), region.get(Region.UUID),
				district.get(District.UUID), district.get(District.NAME), facility.get(Facility.UUID),
				facility.get(Facility.NAME), caze.get(Case.HEALTH_FACILITY_DETAILS), surveillanceOfficer.get(User.UUID),
				caze.get(Case.OUTCOME));

		User user = userService.getByUuid(userUuid);
		Predicate filter = caseService.createUserFilter(cb, cq, caze, user);

		if (caseCriteria != null) {
			Predicate criteriaFilter = caseService.buildCriteriaFilter(caseCriteria, cb, caze);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		List<CaseIndexDto> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	@Override
	public List<CaseExportDto> getExportList(String userUuid, CaseCriteria caseCriteria, int first, int max) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CaseExportDto> cq = cb.createQuery(CaseExportDto.class);
		Root<Case> caze = cq.from(Case.class);
		Join<Case, Person> person = caze.join(Case.PERSON, JoinType.LEFT);
		Join<Case, Hospitalization> hospitalization = caze.join(Case.HOSPITALIZATION, JoinType.LEFT);
		Join<Case, EpiData> epiData = caze.join(Case.EPI_DATA, JoinType.LEFT);
		Join<Case, Symptoms> symptoms = caze.join(Case.SYMPTOMS, JoinType.LEFT);
		Join<Case, Region> region = caze.join(Case.REGION, JoinType.LEFT);
		Join<Case, District> district = caze.join(Case.DISTRICT, JoinType.LEFT);
		Join<Case, Community> community = caze.join(Case.COMMUNITY, JoinType.LEFT);
		Join<Case, Facility> facility = caze.join(Case.HEALTH_FACILITY, JoinType.LEFT);
		Join<Person, Facility> occupationFacility = person.join(Person.OCCUPATION_FACILITY, JoinType.LEFT);

		cq.multiselect(
				caze.get(Case.ID),
				person.get(Person.ID),
				epiData.get(EpiData.ID),
				symptoms.get(Symptoms.ID),
				caze.get(Case.UUID),
				caze.get(Case.EPID_NUMBER),
				caze.get(Case.DISEASE),
				caze.get(Case.DISEASE_DETAILS),
				person.get(Person.FIRST_NAME),
				person.get(Person.LAST_NAME),
				person.get(Person.SEX),
				person.get(Person.APPROXIMATE_AGE),
				person.get(Person.APPROXIMATE_AGE_TYPE),
				caze.get(Case.REPORT_DATE),
				region.get(Region.NAME),
				district.get(District.NAME),
				community.get(Community.NAME),
				hospitalization.get(Hospitalization.ADMISSION_DATE),
				facility.get(Facility.NAME),
				facility.get(Facility.UUID),
				caze.get(Case.HEALTH_FACILITY_DETAILS),
				caze.get(Case.CASE_CLASSIFICATION),
				caze.get(Case.INVESTIGATION_STATUS),
				person.get(Person.PRESENT_CONDITION),
				caze.get(Case.OUTCOME),
				person.get(Person.DEATH_DATE),
				person.get(Person.PHONE),
				person.get(Person.PHONE_OWNER),
				person.get(Person.OCCUPATION_TYPE),
				person.get(Person.OCCUPATION_DETAILS),
				occupationFacility.get(Facility.NAME),
				occupationFacility.get(Facility.UUID),
				person.get(Person.OCCUPATION_FACILITY_DETAILS),
				epiData.get(EpiData.RODENTS),
				epiData.get(EpiData.DIRECT_CONTACT_CONFIRMED_CASE),
				symptoms.get(Symptoms.ONSET_DATE));

		User user = userService.getByUuid(userUuid);
		Predicate filter = caseService.createUserFilter(cb, cq, caze, user);

		if (caseCriteria != null) {
			Predicate criteriaFilter = caseService.buildCriteriaFilter(caseCriteria, cb, caze);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.orderBy(cb.desc(caze.get(Case.REPORT_DATE)));

		List<CaseExportDto> resultList = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();

		for (CaseExportDto exportDto : resultList) {
			// TODO: Speed up this code, e.g. by persisting symtoms, lab results, etc. as a String in the database
			List<Date> sampleDates = sampleService.getSampleDatesForCase(exportDto.getId());
			exportDto.setSampleTaken((sampleDates == null || sampleDates.isEmpty()) ? YesNoUnknown.NO : YesNoUnknown.YES);
			exportDto.setSampleDates(sampleDates);
			exportDto.setLabResults(sampleTestService.getSampleTestResultsForCase(exportDto.getId()));
			exportDto.setSymptoms(symptomsService.getById(exportDto.getSymptomsId()).toHumanString(false));
			exportDto.setAddress(personService.getAddressByPersonId(exportDto.getPersonId()).toString());

			// Build travel history - done here to avoid transforming EpiDataTravel to EpiDataTravelDto
			List<EpiDataTravel> travels = epiDataTravelService.getAllByEpiDataId(exportDto.getEpiDataId());
			StringBuilder travelHistoryBuilder = new StringBuilder();
			for (int i = 0; i < travels.size(); i++) {
				EpiDataTravel travel = travels.get(i);
				if (i > 0) {
					travelHistoryBuilder.append(", ");
				}
				travelHistoryBuilder.append(EpiDataTravelHelper.buildTravelString(
						travel.getTravelType(), travel.getTravelDestination(),
						travel.getTravelDateFrom(), travel.getTravelDateTo()));
			}
			exportDto.setTravelHistory(travelHistoryBuilder.toString());
		}

		return resultList;
	}

	@Override
	public List<String> getAllActiveUuids(String userUuid) {
		User user = userService.getByUuid(userUuid);

		if (user == null) {
			return Collections.emptyList();
		}

		return caseService.getAllActiveUuids(user);
	}

	@Override
	public List<CaseReferenceDto> getSelectableCases(UserReferenceDto userRef) {

		User user = userService.getByReferenceDto(userRef);

		return caseService.getAllAfter(null, user).stream().map(c -> toReferenceDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<DashboardCaseDto> getNewCasesForDashboard(RegionReferenceDto regionRef,
			DistrictReferenceDto districtRef, Disease disease, Date from, Date to, String userUuid) {
		Region region = regionService.getByReferenceDto(regionRef);
		District district = districtService.getByReferenceDto(districtRef);
		User user = userService.getByUuid(userUuid);

		return caseService.getNewCasesForDashboard(region, district, disease, from, to, user);
	}

	@Override
	public List<MapCaseDto> getCasesForMap(RegionReferenceDto regionRef, DistrictReferenceDto districtRef,
			Disease disease, Date from, Date to, String userUuid) {
		Region region = regionService.getByReferenceDto(regionRef);
		District district = districtService.getByReferenceDto(districtRef);
		User user = userService.getByUuid(userUuid);

		return caseService.getCasesForMap(region, district, disease, from, to, user);
	}

	@Override
	public CaseDataDto getLatestCaseByPerson(String personUuid, String userUuid) {
		User user = userService.getByUuid(userUuid);
		Person person = personService.getByUuid(personUuid);

		return toDto(caseService.getLatestCaseByPerson(person, user));
	}

	@Override
	public CaseDataDto getMatchingCaseForImport(CaseDataDto importCaze, PersonReferenceDto existingPerson, String userUuid) {
		User user = userService.getByUuid(userUuid);
		Date newCaseDate = CaseLogic.getStartDate(importCaze.getSymptoms().getOnsetDate(), importCaze.getReceptionDate(), importCaze.getReportDate());

		CaseCriteria criteria = new CaseCriteria()
				.personEquals(existingPerson)
				.diseaseEquals(importCaze.getDisease())
				.archived(false)
				.newCaseDateBetween(DateHelper.subtractMonths(newCaseDate, 2), DateHelper.addMonths(newCaseDate, 2), null);

		List<Case> matchingCases = caseService.findBy(criteria, user).stream().sorted(new Comparator<Case>() {
			@Override
			public int compare(Case c1, Case c2) {
				return CaseLogic.getStartDate(c1.getSymptoms().getOnsetDate(), c1.getReceptionDate(), c1.getReportDate()).compareTo(
						CaseLogic.getStartDate(c2.getSymptoms().getOnsetDate(), c2.getReceptionDate(), c2.getReportDate()));
			}
		}).collect(Collectors.toList());

		if (!matchingCases.isEmpty()) {
			return toDto(matchingCases.get(0));
		} else {
			return null;
		}
	}

	@Override
	public List<CaseDataDto> getAllCasesOfPerson(String personUuid, String userUuid) {
		User user = userService.getByUuid(userUuid);

		return caseService.findBy(new CaseCriteria().personEquals(new PersonReferenceDto(personUuid)), user)
				.stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}

	@Override
	public Map<CaseClassification, Long> getNewCaseCountPerClassification(CaseCriteria caseCriteria, String userUuid) {
		User user = userService.getByUuid(userUuid);

		return caseService.getNewCaseCountPerClassification(caseCriteria, user);
	}

	@Override
	public Map<PresentCondition, Long> getNewCaseCountPerPersonCondition(CaseCriteria caseCriteria, String userUuid) {
		User user = userService.getByUuid(userUuid);

		return caseService.getNewCaseCountPerPersonCondition(caseCriteria, user);
	}

	@Override
	public CaseDataDto getCaseDataByUuid(String uuid) {
		return toDto(caseService.getByUuid(uuid));
	}

	@Override
	public CaseReferenceDto getReferenceByUuid(String uuid) {
		return toReferenceDto(caseService.getByUuid(uuid));
	}

	@Override
	public CaseDataDto saveCase(CaseDataDto dto) throws ValidationRuntimeException {
		Case caze = caseService.getByUuid(dto.getUuid());
		CaseDataDto existingCaseDto = toDto(caseService.getByUuid(dto.getUuid()));

		SymptomsHelper.updateIsSymptomatic(dto.getSymptoms());

		// Check whether any required field that does not have a not null constraint in
		// the database is empty
		if (dto.getRegion() == null) {
			throw new ValidationRuntimeException("You have to specify a valid region");
		}
		if (dto.getDistrict() == null) {
			throw new ValidationRuntimeException("You have to specify a valid district");
		}
		if (dto.getHealthFacility() == null) {
			throw new ValidationRuntimeException("You have to specify a valid health facility");
		}
		if (dto.getDisease() == null) {
			throw new ValidationRuntimeException("You have to specify a valid disease");
		}
		// Check whether there are any infrastructure errors
		if (!districtFacade.getDistrictByUuid(dto.getDistrict().getUuid()).getRegion().equals(dto.getRegion())) {
			throw new ValidationRuntimeException(
					"Could not find a database entry for the specified district in the specified region");
		}
		if (dto.getCommunity() != null && !communityFacade.getByUuid(dto.getCommunity().getUuid()).getDistrict().equals(dto.getDistrict())) {
			throw new ValidationRuntimeException(
					"Could not find a database entry for the specified community in the specified district");
		}
		if (dto.getCommunity() == null && facilityFacade.getByUuid(dto.getHealthFacility().getUuid()).getDistrict() != null
				&& !facilityFacade.getByUuid(dto.getHealthFacility().getUuid()).getDistrict().equals(dto.getDistrict())) {
			throw new ValidationRuntimeException(
					"Could not find a database entry for the specified health facility in the specified district");
		}
		if (dto.getCommunity() != null && facilityFacade.getByUuid(dto.getHealthFacility().getUuid()).getCommunity() != null
				&& !dto.getCommunity().equals(facilityFacade.getByUuid(dto.getHealthFacility().getUuid()).getCommunity())) {
			throw new ValidationRuntimeException(
					"Could not find a database entry for the specified health facility in the specified community");
		}
		if (facilityFacade.getByUuid(dto.getHealthFacility().getUuid()).getRegion() != null
				&& !dto.getRegion().equals(facilityFacade.getByUuid(dto.getHealthFacility().getUuid()).getRegion())) {
			throw new ValidationRuntimeException(
					"Could not find a database entry for the specified health facility in the specified region");
		}
		if (facilityFacade.getByUuid(dto.getHealthFacility().getUuid()).getRegion() != null
				&& !dto.getRegion().equals(facilityFacade.getByUuid(dto.getHealthFacility().getUuid()).getRegion())) {
			throw new ValidationRuntimeException(
					"Could not find a database entry for the specified health facility in the specified region");
		}

		caze = fillOrBuildEntity(dto, caze);
		caseService.ensurePersisted(caze);
		onCaseChanged(existingCaseDto, caze);

		return toDto(caze);
	}

	@Override
	public void archiveOrDearchiveCase(String caseUuid, boolean archive) {
		Case caze = caseService.getByUuid(caseUuid);
		caze.setArchived(archive);
		caseService.ensurePersisted(caze);
	}

	/**
	 * Handles potential changes, processes and backend logic that needs to be done
	 * after a case has been created/saved
	 */
	public void onCaseChanged(CaseDataDto existingCase, Case newCase) {
		// If the case is new and the geo coordinates of the case's health facility are
		// null, set its coordinates to the case's report coordinates, if available
		Facility facility = newCase.getHealthFacility();
		if (existingCase == null && facility != null && !FacilityHelper.isOtherOrNoneHealthFacility(facility.getUuid())
				&& (facility.getLatitude() == null || facility.getLongitude() == null)) {
			if (newCase.getReportLat() != null && newCase.getReportLon() != null) {
				facility.setLatitude(newCase.getReportLat());
				facility.setLongitude(newCase.getReportLon());
				facilityService.ensurePersisted(facility);
			}
		}

		// update the plague type based on symptoms
		if (newCase.getDisease() == Disease.PLAGUE) {
			PlagueType plagueType = DiseaseHelper
					.getPlagueTypeForSymptoms(SymptomsFacadeEjb.toDto(newCase.getSymptoms()));
			if (plagueType != newCase.getPlagueType() && plagueType != null) {
				newCase.setPlagueType(plagueType);
			}
		}

		updateInvestigationByStatus(existingCase, newCase);

		updatePersonAndCaseByOutcome(existingCase, newCase);

		updateCaseAge(existingCase, newCase);

		if (existingCase == null || newCase.getDisease() != existingCase.getDisease()
				|| newCase.getReportDate() != existingCase.getReportDate()
				|| newCase.getReceptionDate() != existingCase.getReceptionDate()
				|| newCase.getSymptoms().getOnsetDate() != existingCase.getSymptoms().getOnsetDate()) {

			// Update follow-up until and status of all contacts
			for (Contact contact : contactService.getAllByCase(newCase)) {
				contactService.updateFollowUpUntilAndStatus(contact);
				contactService.udpateContactStatus(contact);
			}
			for (Contact contact : contactService.getAllByResultingCase(newCase)) {
				contactService.updateFollowUpUntilAndStatus(contact);
				contactService.udpateContactStatus(contact);
			}
		}

		// Create a task to search for other cases for new Plague cases
		if (existingCase == null && newCase.getDisease() == Disease.PLAGUE) {
			createActiveSearchForOtherCasesTask(newCase);
		}

		// Update case classification if the feature is enabled
		if (configFacade.isFeatureAutomaticCaseClassification()) {
			if (newCase.getCaseClassification() != CaseClassification.NO_CASE) {
				// calculate classification
				CaseDataDto newCaseDto = toDto(newCase);
				List<SampleTestDto> sampleTests = sampleTestService.getAllByCase(newCase).stream()
						.map(s -> sampleTestFacade.toDto(s)).collect(Collectors.toList());
				CaseClassification classification = caseClassificationFacade.getClassification(newCaseDto, sampleTests);

				// only update when classification by system changes - user may overwrite this
				if (classification != newCase.getSystemCaseClassification()) {
					newCase.setSystemCaseClassification(classification);

					// really a change? (user may have already set it)
					if (classification != newCase.getCaseClassification()) {
						newCase.setCaseClassification(classification);
						newCase.setClassificationUser(null);
						newCase.setClassificationDate(new Date());
					}
				}
			}
		}

		// Send an email to all responsible supervisors when the case classification has
		// changed
		if (existingCase != null && existingCase.getCaseClassification() != newCase.getCaseClassification()) {
			List<User> messageRecipients = userService.getAllByRegionAndUserRoles(newCase.getRegion(),
					UserRole.SURVEILLANCE_SUPERVISOR, UserRole.CASE_SUPERVISOR, UserRole.CONTACT_SUPERVISOR);
			for (User recipient : messageRecipients) {
				try {
					messagingService.sendMessage(recipient,
							I18nProperties.getMessage(MessagingService.SUBJECT_CASE_CLASSIFICATION_CHANGED),
							String.format(
									I18nProperties.getMessage(MessagingService.CONTENT_CASE_CLASSIFICATION_CHANGED),
									DataHelper.getShortUuid(newCase.getUuid()),
									newCase.getCaseClassification().toString()),
							MessageType.EMAIL, MessageType.SMS);
				} catch (NotificationDeliveryFailedException e) {
					logger.error(String.format(
							"NotificationDeliveryFailedException when trying to notify supervisors about the change of a case classification. "
									+ "Failed to send " + e.getMessageType() + " to user with UUID %s.",
									recipient.getUuid()));
				}
			}
		}

	}

	private void updatePersonAndCaseByOutcome(CaseDataDto existingCase, Case newCase) {

		if (existingCase != null && newCase.getOutcome() != existingCase.getOutcome()) {

			if (newCase.getOutcome() == null || newCase.getOutcome() == CaseOutcome.NO_OUTCOME) {
				newCase.setOutcomeDate(null);
			} else if (newCase.getOutcomeDate() == null) {
				newCase.setOutcomeDate(new Date());
			}

			if (newCase.getOutcome() == CaseOutcome.DECEASED) {
				if (newCase.getPerson().getPresentCondition() != PresentCondition.DEAD
						&& newCase.getPerson().getPresentCondition() != PresentCondition.BURIED) {
					PersonDto existingPerson = PersonFacadeEjb.toDto(newCase.getPerson());
					newCase.getPerson().setPresentCondition(PresentCondition.DEAD);
					newCase.getPerson().setDeathDate(newCase.getOutcomeDate());
					newCase.getPerson().setCauseOfDeath(CauseOfDeath.EPIDEMIC_DISEASE);
					newCase.getPerson().setCauseOfDeathDisease(newCase.getDisease());
					// attention: this may lead to infinite recursion when not properly implemented
					personFacade.onPersonChanged(existingPerson, newCase.getPerson());
				}
			} else if (newCase.getOutcome() == CaseOutcome.NO_OUTCOME) {
				if (newCase.getPerson().getPresentCondition() != PresentCondition.ALIVE) {
					PersonDto existingPerson = PersonFacadeEjb.toDto(newCase.getPerson());
					newCase.getPerson().setPresentCondition(PresentCondition.ALIVE);
					// attention: this may lead to infinite recursion when not properly implemented
					personFacade.onPersonChanged(existingPerson, newCase.getPerson());
				}
			}
		}
	}

	private void updateCaseAge(CaseDataDto existingCase, Case newCase) {
		if (existingCase != null && newCase.getPerson().getApproximateAge() != null
				&& CaseLogic.getStartDate(existingCase.getSymptoms().getOnsetDate(), existingCase.getReceptionDate(),
						existingCase.getReportDate()) != CaseLogic.getStartDate(newCase.getSymptoms().getOnsetDate(),
								newCase.getReceptionDate(), newCase.getReportDate())) {
			if (newCase.getPerson().getApproximateAgeType() == ApproximateAgeType.MONTHS) {
				newCase.setCaseAge(0);
			} else {
				Date personChangeDate = newCase.getPerson().getChangeDate();
				Date referenceDate = CaseLogic.getStartDate(newCase.getSymptoms().getOnsetDate(),
						newCase.getReceptionDate(), newCase.getReportDate());
				newCase.setCaseAge(newCase.getPerson().getApproximateAge()
						- DateHelper.getYearsBetween(referenceDate, personChangeDate));
				if (newCase.getCaseAge() < 0) {
					newCase.setCaseAge(0);
				}
			}

		}
	}

	@Override
	public CaseDataDto transferCase(CaseReferenceDto cazeRef, RegionReferenceDto regionDto, DistrictReferenceDto districtDto,
			CommunityReferenceDto communityDto,	FacilityReferenceDto facilityDto, String facilityDetails, UserReferenceDto officerDto) {
		Case caze = fillOrBuildEntity(getCaseDataByUuid(cazeRef.getUuid()), caseService.getByUuid(cazeRef.getUuid()));

		Community community = communityDto != null ? communityService.getByUuid(communityDto.getUuid()) : null;
		Facility facility = facilityService.getByUuid(facilityDto.getUuid());
		District district = districtService.getByUuid(districtDto.getUuid());
		Region region = regionService.getByUuid(regionDto.getUuid());
		User officer = null;
		if (officerDto != null) {
			officer = userService.getByUuid(officerDto.getUuid());
		}

		// Create a new previous hospitalization object if a new facility is set and
		// reset the
		// current hospitalization
		if (!caze.getHealthFacility().getUuid().equals(facility.getUuid())) {
			caze.getHospitalization().getPreviousHospitalizations()
			.add(previousHospitalizationService.buildPreviousHospitalizationFromHospitalization(caze));
			caze.getHospitalization().setHospitalizedPreviously(YesNoUnknown.YES);
			caze.getHospitalization().setAdmissionDate(new Date());
			caze.getHospitalization().setDischargeDate(null);
			caze.getHospitalization().setIsolated(null);
		}

		caze.setRegion(region);
		caze.setDistrict(district);
		caze.setCommunity(community);
		caze.setHealthFacility(facility);
		caze.setHealthFacilityDetails(facilityDetails);
		caze.setSurveillanceOfficer(officer);

		caseService.ensurePersisted(caze);

		// Assign all tasks associated with this case to the new officer or, if none has
		// been selected,
		// to the region supervisor
		for (Task task : caze.getTasks()) {
			if (task.getTaskStatus() != TaskStatus.PENDING) {
				continue;
			}

			if (officer != null) {
				task.setAssigneeUser(officer);
			} else {
				List<User> supervisors = userService.getAllByRegionAndUserRoles(region,
						UserRole.SURVEILLANCE_SUPERVISOR);
				if (supervisors.size() >= 1) {
					task.setAssigneeUser(supervisors.get(0));
				} else {
					task.setAssigneeUser(null);
				}
			}

			taskService.ensurePersisted(task);
		}

		return toDto(caze);
	}

	@Override
	public void deleteCase(CaseReferenceDto caseRef, String userUuid) {
		User user = userService.getByUuid(userUuid);
		if (!user.getUserRoles().contains(UserRole.ADMIN)) {
			throw new UnsupportedOperationException("Only admins are allowed to delete entities.");
		}

		Case caze = caseService.getByReferenceDto(caseRef);
		List<Contact> contacts = contactService.getAllByCase(caze);
		for (Contact contact : contacts) {
			contactService.delete(contact);
		}
		contacts = contactService.getAllByResultingCase(caze);
		for (Contact contact : contacts) {
			contact.setResultingCase(null);
		}
		List<Sample> samples = sampleService.getAllByCase(caze);
		for (Sample sample : samples) {
			sampleService.delete(sample);
		}
		List<Task> tasks = taskService.findBy(new TaskCriteria().cazeEquals(caseRef));
		for (Task task : tasks) {
			taskService.delete(task);
		}
		caseService.delete(caze);
	}

	@Override
	public List<String> getArchivedUuidsSince(String userUuid, Date since) {
		User user = userService.getByUuid(userUuid);

		if (user == null) {
			return Collections.emptyList();
		}

		return caseService.getArchivedUuidsSince(user, since);
	}

	public Case fillOrBuildEntity(@NotNull CaseDataDto source, Case target) {

		if (target == null) {
			target = new Case();
			target.setUuid(source.getUuid());
			target.setSystemCaseClassification(CaseClassification.NOT_CLASSIFIED);
		}

		DtoHelper.validateDto(source, target);

		target.setDisease(source.getDisease());
		target.setDiseaseDetails(source.getDiseaseDetails());
		target.setPlagueType(source.getPlagueType());
		target.setDengueFeverType(source.getDengueFeverType());
		target.setReportDate(source.getReportDate());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setInvestigatedDate(source.getInvestigatedDate());
		target.setReceptionDate(source.getReceptionDate());
		target.setPerson(personService.getByReferenceDto(source.getPerson()));
		target.setCaseClassification(source.getCaseClassification());
		target.setClassificationUser(userService.getByReferenceDto(source.getClassificationUser()));
		target.setClassificationDate(source.getClassificationDate());
		target.setClassificationComment(source.getClassificationComment());
		target.setInvestigationStatus(source.getInvestigationStatus());
		target.setHospitalization(hospitalizationFacade.fromDto(source.getHospitalization()));
		target.setEpiData(epiDataFacade.fromDto(source.getEpiData()));

		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setCommunity(communityService.getByReferenceDto(source.getCommunity()));
		target.setHealthFacility(facilityService.getByReferenceDto(source.getHealthFacility()));
		target.setHealthFacilityDetails(source.getHealthFacilityDetails());

		target.setSurveillanceOfficer(userService.getByReferenceDto(source.getSurveillanceOfficer()));
		target.setCaseOfficer(userService.getByReferenceDto(source.getCaseOfficer()));
		target.setSymptoms(symptomsFacade.fromDto(source.getSymptoms()));

		target.setPregnant(source.getPregnant());
		target.setVaccination(source.getVaccination());
		target.setVaccinationDoses(source.getVaccinationDoses());
		target.setVaccinationInfoSource(source.getVaccinationInfoSource());
		target.setSmallpoxVaccinationScar(source.getSmallpoxVaccinationScar());
		target.setSmallpoxVaccinationReceived(source.getSmallpoxVaccinationReceived());
		target.setVaccinationDate(source.getVaccinationDate());

		target.setEpidNumber(source.getEpidNumber());

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		target.setOutcome(source.getOutcome());
		target.setOutcomeDate(source.getOutcomeDate());

		return target;
	}

	public static CaseReferenceDto toReferenceDto(Case entity) {
		if (entity == null) {
			return null;
		}
		CaseReferenceDto dto = new CaseReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}

	public static CaseDataDto toDto(Case source) {
		if (source == null) {
			return null;
		}
		CaseDataDto target = new CaseDataDto();
		DtoHelper.fillDto(target, source);

		target.setDisease(source.getDisease());
		target.setDiseaseDetails(source.getDiseaseDetails());
		target.setPlagueType(source.getPlagueType());
		target.setDengueFeverType(source.getDengueFeverType());
		target.setCaseClassification(source.getCaseClassification());
		target.setClassificationUser(UserFacadeEjb.toReferenceDto(source.getClassificationUser()));
		target.setClassificationDate(source.getClassificationDate());
		target.setClassificationComment(source.getClassificationComment());
		target.setInvestigationStatus(source.getInvestigationStatus());
		target.setPerson(PersonFacadeEjb.toReferenceDto(source.getPerson()));
		target.setHospitalization(HospitalizationFacadeEjb.toDto(source.getHospitalization()));
		target.setEpiData(EpiDataFacadeEjb.toDto(source.getEpiData()));

		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setCommunity(CommunityFacadeEjb.toReferenceDto(source.getCommunity()));
		target.setHealthFacility(FacilityFacadeEjb.toReferenceDto(source.getHealthFacility()));
		target.setHealthFacilityDetails(source.getHealthFacilityDetails());

		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setReportDate(source.getReportDate());
		target.setInvestigatedDate(source.getInvestigatedDate());
		target.setReceptionDate(source.getReceptionDate());

		target.setSurveillanceOfficer(UserFacadeEjb.toReferenceDto(source.getSurveillanceOfficer()));
		target.setCaseOfficer(UserFacadeEjb.toReferenceDto(source.getCaseOfficer()));
		target.setSymptoms(SymptomsFacadeEjb.toDto(source.getSymptoms()));

		target.setPregnant(source.getPregnant());
		target.setVaccination(source.getVaccination());
		target.setVaccinationDoses(source.getVaccinationDoses());
		target.setVaccinationInfoSource(source.getVaccinationInfoSource());
		target.setSmallpoxVaccinationScar(source.getSmallpoxVaccinationScar());
		target.setSmallpoxVaccinationReceived(source.getSmallpoxVaccinationReceived());
		target.setVaccinationDate(source.getVaccinationDate());

		target.setEpidNumber(source.getEpidNumber());

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		target.setOutcome(source.getOutcome());
		target.setOutcomeDate(source.getOutcomeDate());

		return target;
	}

	public void updateInvestigationByStatus(CaseDataDto existingCase, Case caze) {
		CaseReferenceDto caseRef = caze.toReference();
		InvestigationStatus investigationStatus = caze.getInvestigationStatus();

		if (investigationStatus != InvestigationStatus.PENDING) {
			// Set the investigation date
			if (caze.getInvestigatedDate() == null) {
				caze.setInvestigatedDate(new Date());
			}

			// Set the task status of all investigation tasks to "Removed" because
			// the case status has been updated manually
			List<Task> pendingTasks = taskService.findBy(new TaskCriteria().taskTypeEquals(TaskType.CASE_INVESTIGATION)
					.cazeEquals(caseRef).taskStatusEquals(TaskStatus.PENDING));
			for (Task task : pendingTasks) {
				task.setTaskStatus(TaskStatus.REMOVED);
				task.setStatusChangeDate(new Date());
			}

			if (caze.getInvestigationStatus() == InvestigationStatus.DONE
					&& existingCase.getInvestigationStatus() != InvestigationStatus.DONE) {
				sendInvestigationDoneNotifications(caze);
			}
		} else {
			// Remove the investigation date
			caze.setInvestigatedDate(null);

			// Create a new investigation task if none is present
			long pendingCount = taskService.getCount(new TaskCriteria().taskTypeEquals(TaskType.CASE_INVESTIGATION)
					.cazeEquals(caseRef).taskStatusEquals(TaskStatus.PENDING));

			if (pendingCount == 0) {
				createInvestigationTask(caze);
			}
		}
	}

	public void updateInvestigationByTask(Case caze) {
		CaseReferenceDto caseRef = caze.toReference();

		// any pending case investigation task?
		long pendingCount = taskService.getCount(new TaskCriteria().taskTypeEquals(TaskType.CASE_INVESTIGATION)
				.cazeEquals(caseRef).taskStatusEquals(TaskStatus.PENDING));

		if (pendingCount > 0) {
			// set status to investigation pending
			caze.setInvestigationStatus(InvestigationStatus.PENDING);
			// .. and clear date
			caze.setInvestigatedDate(null);
		} else {
			// get "case investigation" task created last
			List<Task> cazeTasks = taskService
					.findBy(new TaskCriteria().taskTypeEquals(TaskType.CASE_INVESTIGATION).cazeEquals(caseRef));

			Task youngestTask = cazeTasks.stream().max(new Comparator<Task>() {
				@Override
				public int compare(Task o1, Task o2) {
					return o1.getCreationDate().compareTo(o2.getCreationDate());
				}
			}).get();

			switch (youngestTask.getTaskStatus()) {
			case PENDING:
				throw new UnsupportedOperationException("there should not be any pending tasks");
			case DONE:
				caze.setInvestigationStatus(InvestigationStatus.DONE);
				caze.setInvestigatedDate(youngestTask.getStatusChangeDate());
				sendInvestigationDoneNotifications(caze);
				break;
			case REMOVED:
				caze.setInvestigationStatus(InvestigationStatus.DISCARDED);
				caze.setInvestigatedDate(youngestTask.getStatusChangeDate());
				break;
			case NOT_EXECUTABLE:
				caze.setInvestigationStatus(InvestigationStatus.PENDING);
				caze.setInvestigatedDate(null);
				break;
			default:
				break;
			}
		}
	}

	private void createInvestigationTask(Case caze) {
		Task task = new Task();
		task.setTaskStatus(TaskStatus.PENDING);
		task.setTaskContext(TaskContext.CASE);
		task.setCaze(caze);
		task.setTaskType(TaskType.CASE_INVESTIGATION);
		task.setSuggestedStart(TaskHelper.getDefaultSuggestedStart());
		task.setDueDate(TaskHelper.getDefaultDueDate());
		task.setPriority(TaskPriority.NORMAL);

		assignOfficerOrSupervisorToTask(caze, task);

		taskService.ensurePersisted(task);
	}

	private void createActiveSearchForOtherCasesTask(Case caze) {
		Task task = new Task();
		task.setTaskStatus(TaskStatus.PENDING);
		task.setTaskContext(TaskContext.CASE);
		task.setCaze(caze);
		task.setTaskType(TaskType.ACTIVE_SEARCH_FOR_OTHER_CASES);
		task.setSuggestedStart(TaskHelper.getDefaultSuggestedStart());
		task.setDueDate(TaskHelper.getDefaultDueDate());
		task.setPriority(TaskPriority.NORMAL);

		assignOfficerOrSupervisorToTask(caze, task);

		taskService.ensurePersisted(task);
	}

	private void assignOfficerOrSupervisorToTask(Case caze, Task task) {
		if (caze.getSurveillanceOfficer() != null) {
			task.setAssigneeUser(caze.getSurveillanceOfficer());
		} else {
			// assign the first supervisor
			List<User> supervisors = userService.getAllByRegionAndUserRoles(caze.getRegion(),
					UserRole.SURVEILLANCE_SUPERVISOR);
			if (!supervisors.isEmpty()) {
				task.setAssigneeUser(supervisors.get(0));
			} else {
				User currentUser = userService.getCurrentUser();
				if (currentUser != null) {
					task.setAssigneeUser(currentUser);
				} else {
					logger.warn("No valid assignee user found for task " + task.getUuid());
				}
			}
		}
	}

	@Override
	public Map<RegionDto, Long> getCaseCountPerRegion(Date fromDate, Date toDate, Disease disease) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> from = cq.from(Case.class);

		Predicate filter = null;
		if (fromDate != null || toDate != null) {
			filter = caseService.createActiveCaseFilter(cb, from, fromDate, toDate);
		}

		if (disease != null) {
			Predicate diseaseFilter = cb.equal(from.get(Case.DISEASE), disease);
			filter = filter != null ? cb.and(filter, diseaseFilter) : diseaseFilter;
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(from.get(Case.REGION));
		cq.multiselect(from.get(Case.REGION), cb.count(from));
		List<Object[]> results = em.createQuery(cq).getResultList();

		Map<RegionDto, Long> resultMap = results.stream()
				.collect(Collectors.toMap(e -> RegionFacadeEjb.toDto((Region) e[0]), e -> (Long) e[1]));
		return resultMap;
	}

	@Override
	public List<Pair<DistrictDto, BigDecimal>> getCaseMeasurePerDistrict(Date fromDate, Date toDate, Disease disease,
			CaseMeasure caseMeasure) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> from = cq.from(Case.class);

		Predicate filter = null;
		if (fromDate != null || toDate != null) {
			filter = caseService.createActiveCaseFilter(cb, from, fromDate, toDate);
		}

		if (disease != null) {
			Predicate diseaseFilter = cb.equal(from.get(Case.DISEASE), disease);
			filter = filter != null ? cb.and(filter, diseaseFilter) : diseaseFilter;
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(from.get(Case.DISTRICT));
		cq.multiselect(from.get(Case.DISTRICT), cb.count(from));
		if (caseMeasure == CaseMeasure.CASE_COUNT) {
			cq.orderBy(cb.asc(cb.count(from)));
		}
		List<Object[]> results = em.createQuery(cq).getResultList();

		if (caseMeasure == CaseMeasure.CASE_COUNT) {
			List<Pair<DistrictDto, BigDecimal>> resultList = results.stream()
					.map(e -> new Pair<DistrictDto, BigDecimal>(DistrictFacadeEjb.toDto((District) e[0]),
							new BigDecimal((Long) e[1])))
					.collect(Collectors.toList());
			return resultList;
		} else {
			List<Pair<DistrictDto, BigDecimal>> resultList = results.stream().map(e -> {
				District district = (District) e[0];
				Integer population = district.getPopulation();
				Long caseCount = (Long) e[1];

				if (population == null || population <= 0) {
					// No or negative population - these entries will be cut off in the UI
					return new Pair<DistrictDto, BigDecimal>(DistrictFacadeEjb.toDto(district), new BigDecimal(0));
				} else {
					return new Pair<DistrictDto, BigDecimal>(DistrictFacadeEjb.toDto(district),
							new BigDecimal(caseCount).divide(
									new BigDecimal((double) population / DistrictDto.CASE_INCIDENCE_DIVISOR), 1,
									RoundingMode.HALF_UP));
				}
			}).sorted(new Comparator<Pair<DistrictDto, BigDecimal>>() {
				@Override
				public int compare(Pair<DistrictDto, BigDecimal> o1, Pair<DistrictDto, BigDecimal> o2) {
					return o1.getElement1().compareTo(o2.getElement1());
				}
			}).collect(Collectors.toList());
			return resultList;
		}
	}

	private void sendInvestigationDoneNotifications(Case caze) {
		List<User> messageRecipients = userService.getAllByRegionAndUserRoles(caze.getRegion(),
				UserRole.SURVEILLANCE_SUPERVISOR, UserRole.CASE_SUPERVISOR, UserRole.CONTACT_SUPERVISOR);
		for (User recipient : messageRecipients) {
			try {
				messagingService.sendMessage(recipient,
						I18nProperties.getMessage(MessagingService.SUBJECT_CASE_INVESTIGATION_DONE),
						String.format(I18nProperties.getMessage(MessagingService.CONTENT_CASE_INVESTIGATION_DONE),
								DataHelper.getShortUuid(caze.getUuid())),
						MessageType.EMAIL, MessageType.SMS);
			} catch (NotificationDeliveryFailedException e) {
				logger.error(String.format(
						"NotificationDeliveryFailedException when trying to notify supervisors about the completion of a case investigation. "
								+ "Failed to send " + e.getMessageType() + " to user with UUID %s.",
								recipient.getUuid()));
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> queryCaseCount(StatisticsCaseCriteria caseCriteria, StatisticsCaseAttribute groupingA,
			StatisticsCaseSubAttribute subGroupingA, StatisticsCaseAttribute groupingB,
			StatisticsCaseSubAttribute subGroupingB) {

		// Steps to build the query:
		// 1. Join the required tables
		// 2. Build the filter query
		// 3. Add selected groupings
		// 4. Retrieve and prepare the results

		// 1. Join tables that cases are grouped by or that are used in the caseCriteria

		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append(" FROM ").append(Case.TABLE_NAME).append(" LEFT JOIN ").append(Symptoms.TABLE_NAME)
		.append(" ON ").append(Case.TABLE_NAME).append(".").append(Case.SYMPTOMS).append("_id").append(" = ")
		.append(Symptoms.TABLE_NAME).append(".").append(Symptoms.ID);

		if (subGroupingA == StatisticsCaseSubAttribute.REGION || subGroupingB == StatisticsCaseSubAttribute.REGION
				|| caseCriteria.getRegions() != null) {
			sqlBuilder.append(" LEFT JOIN ").append(Region.TABLE_NAME).append(" ON ").append(Case.TABLE_NAME)
			.append(".").append(Case.REGION).append("_id").append(" = ").append(Region.TABLE_NAME).append(".")
			.append(Region.ID);
		}

		if (subGroupingA == StatisticsCaseSubAttribute.DISTRICT || subGroupingB == StatisticsCaseSubAttribute.DISTRICT
				|| caseCriteria.getDistricts() != null) {
			sqlBuilder.append(" LEFT JOIN ").append(District.TABLE_NAME).append(" ON ").append(Case.TABLE_NAME)
			.append(".").append(Case.DISTRICT).append("_id").append(" = ").append(District.TABLE_NAME)
			.append(".").append(District.ID);
		}

		if (groupingA == StatisticsCaseAttribute.SEX || groupingB == StatisticsCaseAttribute.SEX
				|| groupingA == StatisticsCaseAttribute.AGE_INTERVAL_1_YEAR
				|| groupingB == StatisticsCaseAttribute.AGE_INTERVAL_1_YEAR
				|| groupingA == StatisticsCaseAttribute.AGE_INTERVAL_5_YEARS
				|| groupingB == StatisticsCaseAttribute.AGE_INTERVAL_5_YEARS
				|| groupingA == StatisticsCaseAttribute.AGE_INTERVAL_CHILDREN_COARSE
				|| groupingB == StatisticsCaseAttribute.AGE_INTERVAL_CHILDREN_COARSE
				|| groupingA == StatisticsCaseAttribute.AGE_INTERVAL_CHILDREN_FINE
				|| groupingB == StatisticsCaseAttribute.AGE_INTERVAL_CHILDREN_FINE
				|| groupingA == StatisticsCaseAttribute.AGE_INTERVAL_CHILDREN_MEDIUM
				|| groupingB == StatisticsCaseAttribute.AGE_INTERVAL_CHILDREN_MEDIUM
				|| groupingA == StatisticsCaseAttribute.AGE_INTERVAL_BASIC
				|| groupingB == StatisticsCaseAttribute.AGE_INTERVAL_BASIC || caseCriteria.getSexes() != null
				|| caseCriteria.getAgeIntervals() != null) {
			sqlBuilder.append(" LEFT JOIN ").append(Person.TABLE_NAME).append(" ON ").append(Case.TABLE_NAME)
			.append(".").append(Case.PERSON).append("_id").append(" = ").append(Person.TABLE_NAME).append(".")
			.append(Person.ID);
		}

		// 2. Build filter based on caseCriteria

		StringBuilder filterBuilder = new StringBuilder();

		if (CollectionUtils.isNotEmpty(caseCriteria.getOnsetYears())) {
			extendFilterBuilderWithDateElement(filterBuilder, "YEAR", Symptoms.TABLE_NAME, Symptoms.ONSET_DATE);
			for (Year onsetYear : caseCriteria.getOnsetYears()) {
				filterBuilder.append(onsetYear.getValue()).append(",");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getOnsetQuarters())) {
			extendFilterBuilderWithDateElement(filterBuilder, "QUARTER", Symptoms.TABLE_NAME, Symptoms.ONSET_DATE);
			for (Quarter onsetQuarter : caseCriteria.getOnsetQuarters()) {
				filterBuilder.append(onsetQuarter.getValue()).append(",");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getOnsetMonths())) {
			extendFilterBuilderWithDateElement(filterBuilder, "MONTH", Symptoms.TABLE_NAME, Symptoms.ONSET_DATE);
			for (Month onsetMonth : caseCriteria.getOnsetMonths()) {
				filterBuilder.append(onsetMonth.ordinal() + 1).append(",");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getOnsetEpiWeeks())) {
			extendFilterBuilderWithEpiWeek(filterBuilder, Symptoms.TABLE_NAME, Symptoms.ONSET_DATE);
			for (EpiWeek epiWeek : caseCriteria.getOnsetEpiWeeks()) {
				filterBuilder.append(epiWeek.getWeek()).append(",");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getOnsetQuartersOfYear())) {
			extendFilterBuilderWithQuarterOfYear(filterBuilder, Symptoms.TABLE_NAME, Symptoms.ONSET_DATE);
			for (QuarterOfYear quarterOfYear : caseCriteria.getOnsetQuartersOfYear()) {
				filterBuilder.append(quarterOfYear.getYear().getValue() * 10 + quarterOfYear.getQuarter().getValue())
				.append(",");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getOnsetMonthsOfYear())) {
			extendFilterBuilderWithMonthOfYear(filterBuilder, Symptoms.TABLE_NAME, Symptoms.ONSET_DATE);
			for (MonthOfYear monthOfYear : caseCriteria.getOnsetMonthsOfYear()) {
				filterBuilder.append(monthOfYear.getYear().getValue() * 100 + (monthOfYear.getMonth().ordinal() + 1))
				.append(",");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getOnsetEpiWeeksOfYear())) {
			extendFilterBuilderWithEpiWeekOfYear(filterBuilder, Symptoms.TABLE_NAME, Symptoms.ONSET_DATE);
			for (EpiWeek epiWeek : caseCriteria.getOnsetEpiWeeksOfYear()) {
				filterBuilder.append(epiWeek.getYear() * 100 + epiWeek.getWeek()).append(",");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (caseCriteria.getOnsetDateFrom() != null || caseCriteria.getOnsetDateTo() != null) {
			extendFilterBuilderWithDate(filterBuilder, caseCriteria.getOnsetDateFrom(), caseCriteria.getOnsetDateTo(),
					Symptoms.TABLE_NAME, Symptoms.ONSET_DATE);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getReceptionYears())) {
			extendFilterBuilderWithDateElement(filterBuilder, "YEAR", Case.TABLE_NAME, Case.RECEPTION_DATE);
			for (Year receptionYear : caseCriteria.getReceptionYears()) {
				filterBuilder.append(receptionYear.getValue()).append(",");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getReceptionQuarters())) {
			extendFilterBuilderWithDateElement(filterBuilder, "QUARTER", Case.TABLE_NAME, Case.RECEPTION_DATE);
			for (Quarter receptionQuarter : caseCriteria.getReceptionQuarters()) {
				filterBuilder.append(receptionQuarter.getValue()).append(",");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getReceptionMonths())) {
			extendFilterBuilderWithDateElement(filterBuilder, "MONTH", Case.TABLE_NAME, Case.RECEPTION_DATE);
			for (Month receptionMonth : caseCriteria.getReceptionMonths()) {
				filterBuilder.append(receptionMonth.ordinal() + 1).append(",");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getReceptionEpiWeeks())) {
			extendFilterBuilderWithEpiWeek(filterBuilder, Case.TABLE_NAME, Case.RECEPTION_DATE);
			for (EpiWeek epiWeek : caseCriteria.getReceptionEpiWeeks()) {
				filterBuilder.append(epiWeek.getWeek()).append(",");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getReceptionQuartersOfYear())) {
			extendFilterBuilderWithQuarterOfYear(filterBuilder, Case.TABLE_NAME, Case.RECEPTION_DATE);
			for (QuarterOfYear quarterOfYear : caseCriteria.getReceptionQuartersOfYear()) {
				filterBuilder.append(quarterOfYear.getYear().getValue() * 10 + quarterOfYear.getQuarter().getValue())
				.append(",");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getReceptionMonthsOfYear())) {
			extendFilterBuilderWithMonthOfYear(filterBuilder, Case.TABLE_NAME, Case.RECEPTION_DATE);
			for (MonthOfYear monthOfYear : caseCriteria.getReceptionMonthsOfYear()) {
				filterBuilder.append(monthOfYear.getYear().getValue() * 100 + (monthOfYear.getMonth().ordinal() + 1))
				.append(",");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getReceptionEpiWeeksOfYear())) {
			extendFilterBuilderWithEpiWeekOfYear(filterBuilder, Case.TABLE_NAME, Case.RECEPTION_DATE);
			for (EpiWeek epiWeek : caseCriteria.getReceptionEpiWeeksOfYear()) {
				filterBuilder.append(epiWeek.getYear() * 100 + epiWeek.getWeek()).append(",");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (caseCriteria.getReceptionDateFrom() != null || caseCriteria.getReceptionDateTo() != null) {
			extendFilterBuilderWithDate(filterBuilder, caseCriteria.getReceptionDateFrom(),
					caseCriteria.getReceptionDateTo(), Case.TABLE_NAME, Case.RECEPTION_DATE);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getReportYears())) {
			extendFilterBuilderWithDateElement(filterBuilder, "YEAR", Case.TABLE_NAME, Case.REPORT_DATE);
			for (Year reportYear : caseCriteria.getReportYears()) {
				filterBuilder.append(reportYear.getValue()).append(",");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getReportQuarters())) {
			extendFilterBuilderWithDateElement(filterBuilder, "QUARTER", Case.TABLE_NAME, Case.REPORT_DATE);
			for (Quarter reportQuarter : caseCriteria.getReportQuarters()) {
				filterBuilder.append(reportQuarter.getValue()).append(",");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getReportMonths())) {
			extendFilterBuilderWithDateElement(filterBuilder, "MONTH", Case.TABLE_NAME, Case.REPORT_DATE);
			for (Month reportMonth : caseCriteria.getReportMonths()) {
				filterBuilder.append(reportMonth.ordinal() + 1).append(",");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getReportEpiWeeks())) {
			extendFilterBuilderWithEpiWeek(filterBuilder, Case.TABLE_NAME, Case.REPORT_DATE);
			for (EpiWeek epiWeek : caseCriteria.getReportEpiWeeks()) {
				filterBuilder.append(epiWeek.getWeek()).append(",");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getReportQuartersOfYear())) {
			extendFilterBuilderWithQuarterOfYear(filterBuilder, Case.TABLE_NAME, Case.REPORT_DATE);
			for (QuarterOfYear quarterOfYear : caseCriteria.getReportQuartersOfYear()) {
				filterBuilder.append(quarterOfYear.getYear().getValue() * 10 + quarterOfYear.getQuarter().getValue())
				.append(",");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getReportMonthsOfYear())) {
			extendFilterBuilderWithMonthOfYear(filterBuilder, Case.TABLE_NAME, Case.REPORT_DATE);
			for (MonthOfYear monthOfYear : caseCriteria.getReportMonthsOfYear()) {
				filterBuilder.append(monthOfYear.getYear().getValue() * 100 + (monthOfYear.getMonth().ordinal() + 1))
				.append(",");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getReportEpiWeeksOfYear())) {
			extendFilterBuilderWithEpiWeekOfYear(filterBuilder, Case.TABLE_NAME, Case.REPORT_DATE);
			for (EpiWeek epiWeek : caseCriteria.getReportEpiWeeksOfYear()) {
				filterBuilder.append(epiWeek.getYear() * 100 + epiWeek.getWeek()).append(",");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (caseCriteria.getReportDateFrom() != null || caseCriteria.getReportDateTo() != null) {
			extendFilterBuilderWithDate(filterBuilder, caseCriteria.getReportDateFrom(), caseCriteria.getReportDateTo(),
					Case.TABLE_NAME, Case.REPORT_DATE);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getSexes()) || caseCriteria.isSexUnknown() != null) {
			if (filterBuilder.length() > 0) {
				filterBuilder.append(" AND ");
			}

			filterBuilder.append("(").append(Person.TABLE_NAME).append(".").append(Person.SEX);

			if (CollectionUtils.isNotEmpty(caseCriteria.getSexes())) {
				filterBuilder.append(" IN (");
				for (Sex sex : caseCriteria.getSexes()) {
					filterBuilder.append("'" + sex.name() + "',");
				}
				finalizeFilterBuilderSegment(filterBuilder);
			}

			if (caseCriteria.isSexUnknown() != null) {
				if (CollectionUtils.isNotEmpty(caseCriteria.getSexes())) {
					filterBuilder.append(" OR ").append(Person.TABLE_NAME).append(".").append(Person.SEX);
				}
				filterBuilder.append(" IS ").append(caseCriteria.isSexUnknown() == true ? " NULL" : " NOT NULL");
			}

			filterBuilder.append(")");
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getAgeIntervals())) {
			StringBuilder ageIntervalStringBuilder = new StringBuilder();

			if (filterBuilder.length() > 0) {
				filterBuilder.append(" AND (");
			} else {
				filterBuilder.append(" (");
			}

			Integer upperRangeBoundary = null;
			boolean appendUnknown = false;
			for (IntegerRange range : caseCriteria.getAgeIntervals()) {
				if (range.getTo() == null) {
					if (range.getFrom() == null) {
						appendUnknown = true;
					} else {
						upperRangeBoundary = range.getFrom();
					}
				} else {
					for (int age : IntStream.rangeClosed(range.getFrom(), range.getTo()).toArray()) {
						if (ageIntervalStringBuilder.length() == 0) {
							ageIntervalStringBuilder.append(Case.TABLE_NAME).append(".").append(Case.CASE_AGE)
							.append(" IN (");
						}
						ageIntervalStringBuilder.append(age + ",");
					}
				}
			}

			if (ageIntervalStringBuilder.length() > 0) {
				finalizeFilterBuilderSegment(ageIntervalStringBuilder);
			}

			if (upperRangeBoundary != null) {
				if (ageIntervalStringBuilder.length() > 0) {
					ageIntervalStringBuilder.append(" OR ");
				}
				ageIntervalStringBuilder.append(Case.TABLE_NAME).append(".").append(Case.CASE_AGE)
				.append(" >= " + upperRangeBoundary);
			}

			if (appendUnknown) {
				if (ageIntervalStringBuilder.length() > 0) {
					ageIntervalStringBuilder.append(" OR ");
				}
				ageIntervalStringBuilder.append(Case.TABLE_NAME).append(".").append(Case.CASE_AGE).append(" IS NULL");
			}

			ageIntervalStringBuilder.append(")");

			filterBuilder.append(ageIntervalStringBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getDiseases())) {
			extendFilterBuilderWithSimpleValue(filterBuilder, Case.TABLE_NAME, Case.DISEASE);
			for (Disease disease : caseCriteria.getDiseases()) {
				filterBuilder.append("'" + disease.name() + "',");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getClassifications())) {
			extendFilterBuilderWithSimpleValue(filterBuilder, Case.TABLE_NAME, Case.CASE_CLASSIFICATION);
			for (CaseClassification classification : caseCriteria.getClassifications()) {
				filterBuilder.append("'" + classification.name() + "',");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getOutcomes())) {
			extendFilterBuilderWithSimpleValue(filterBuilder, Case.TABLE_NAME, Case.OUTCOME);
			for (CaseOutcome outcome : caseCriteria.getOutcomes()) {
				filterBuilder.append("'" + outcome.name() + "',");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getRegions())) {
			extendFilterBuilderWithSimpleValue(filterBuilder, Region.TABLE_NAME, Region.UUID);
			for (RegionReferenceDto region : caseCriteria.getRegions()) {
				filterBuilder.append("'" + region.getUuid() + "',");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getDistricts())) {
			extendFilterBuilderWithSimpleValue(filterBuilder, District.TABLE_NAME, District.UUID);
			for (DistrictReferenceDto district : caseCriteria.getDistricts()) {
				filterBuilder.append("'" + district.getUuid() + "',");
			}
			finalizeFilterBuilderSegment(filterBuilder);
		}

		if (filterBuilder.length() > 0) {
			sqlBuilder.append(" WHERE ").append(filterBuilder);
		}

		// 3. Add selected groupings

		if (groupingA != null || groupingB != null) {
			sqlBuilder.append(" GROUP BY ");
			String groupAAlias = "groupA";
			String groupBAlias = "groupB";
			String groupingSelectQueryA = null, groupingSelectQueryB = null;

			if (groupingA != null) {
				groupingSelectQueryA = buildGroupingSelectQuery(groupingA, subGroupingA, groupAAlias);
				sqlBuilder.append(groupAAlias);
			}
			if (groupingB != null) {
				groupingSelectQueryB = buildGroupingSelectQuery(groupingB, subGroupingB, groupBAlias);
				if (groupingA != null) {
					sqlBuilder.append(",");
				}
				sqlBuilder.append(groupBAlias);
			}

			sqlBuilder.append(" ORDER BY ");
			if (groupingA != null) {
				sqlBuilder.append(groupAAlias).append(" NULLS LAST");
			}
			if (groupingB != null) {
				if (groupingA != null) {
					sqlBuilder.append(",");
				}
				sqlBuilder.append(groupBAlias).append(" NULLS LAST");
			}

			// Select
			if (groupingSelectQueryB != null) {
				sqlBuilder.insert(0, "," + groupingSelectQueryB);
			}
			if (groupingSelectQueryA != null) {
				sqlBuilder.insert(0, "," + groupingSelectQueryA);
			}
		}
		sqlBuilder.insert(0, "SELECT COUNT(*)");

		// 4. Retrieve the results of the query and prepare the results for usage in the
		// UI

		if (groupingA == null && groupingB == null) {
			long result = (long) em.createNativeQuery(sqlBuilder.toString()).getSingleResult();
			if (result == 0) {
				// Return an empty list if no cases have been found
				return new ArrayList<>();
			} else {
				Object[] resultArray = new Object[] { result };
				List<Object[]> results = new ArrayList<>();
				results.add(resultArray);
				return results;
			}
		} else {
			List<Object[]> results = (List<Object[]>) em.createNativeQuery(sqlBuilder.toString()).getResultList();
			replaceIdsWithGroupingKeys(results, groupingA, subGroupingA, groupingB, subGroupingB);
			return results;
		}
	}

	/**
	 * Replaces the ids in each row with the appropriate StatisticsGroupingKey based
	 * on the grouping.
	 */
	private void replaceIdsWithGroupingKeys(List<Object[]> results, StatisticsCaseAttribute groupingA,
			StatisticsCaseSubAttribute subGroupingA, StatisticsCaseAttribute groupingB,
			StatisticsCaseSubAttribute subGroupingB) {

		for (Object[] resultRow : results) {
			for (int i = 1; i < resultRow.length; i++) {
				Object resultsEntry = resultRow[i];
				if (resultsEntry != null && !StatisticsHelper.UNKNOWN.equals(resultsEntry)) {
					StatisticsGroupingKey reformattedEntry = null;
					if (i == 1) {
						if (groupingA != null) {
							reformattedEntry = StatisticsHelper.buildGroupingKey(resultsEntry, groupingA, subGroupingA);
						} else {
							reformattedEntry = StatisticsHelper.buildGroupingKey(resultsEntry, groupingB, subGroupingB);
						}
					} else {
						reformattedEntry = StatisticsHelper.buildGroupingKey(resultsEntry, groupingB, subGroupingB);
					}
					resultRow[i] = reformattedEntry;
				}
			}
		}
	}

	private StringBuilder extendFilterBuilderWithSimpleValue(StringBuilder filterBuilder, String tableName,
			String fieldName) {
		if (filterBuilder.length() > 0) {
			filterBuilder.append(" AND ");
		}

		filterBuilder.append(tableName).append(".").append(fieldName).append(" IN (");

		return filterBuilder;
	}

	private StringBuilder extendFilterBuilderWithDate(StringBuilder filterBuilder, Date from, Date to, String tableName,
			String fieldName) {

		if (from != null || to != null) {
			if (filterBuilder.length() > 0) {
				filterBuilder.append(" AND ");
			}

			if (from != null && to != null) {
				filterBuilder.append(tableName).append(".").append(fieldName).append(" BETWEEN '").append(from)
				.append("' AND '").append(to).append("'");
			} else if (from != null) {
				filterBuilder.append(tableName).append(".").append(fieldName).append(" >= '").append(from).append("'");
			} else {
				filterBuilder.append(tableName).append(".").append(fieldName).append(" <= '").append(to).append("'");
			}
		}

		return filterBuilder;
	}

	private StringBuilder extendFilterBuilderWithDateElement(StringBuilder filterBuilder, String dateElementToExtract,
			String tableName, String fieldName) {
		if (filterBuilder.length() > 0) {
			filterBuilder.append(" AND ");
		}

		filterBuilder.append("(EXTRACT(" + dateElementToExtract + " FROM ").append(tableName).append(".")
		.append(fieldName).append(")::integer)").append(" IN (");

		return filterBuilder;
	}

	private StringBuilder extendFilterBuilderWithEpiWeek(StringBuilder filterBuilder, String tableName,
			String fieldName) {
		if (filterBuilder.length() > 0) {
			filterBuilder.append(" AND ");
		}

		filterBuilder.append("epi_week(").append(tableName).append(".").append(fieldName).append(")").append(" IN (");

		return filterBuilder;
	}

	private StringBuilder extendFilterBuilderWithEpiWeekOfYear(StringBuilder filterBuilder, String tableName,
			String fieldName) {
		if (filterBuilder.length() > 0) {
			filterBuilder.append(" AND ");
		}

		filterBuilder.append("(epi_year(").append(tableName).append(".").append(fieldName).append(")").append(" * 100")
		.append(" + epi_week(").append(tableName).append(".").append(fieldName).append("))").append(" IN (");

		return filterBuilder;
	}

	// TODO THIS DOESN'T WORK
	private StringBuilder extendFilterBuilderWithQuarterOfYear(StringBuilder filterBuilder, String tableName,
			String fieldName) {
		if (filterBuilder.length() > 0) {
			filterBuilder.append(" AND ");
		}

		filterBuilder.append("((EXTRACT(YEAR FROM ").append(tableName).append(".").append(fieldName).append(")")
		.append(" * 10)::integer) + (EXTRACT(QUARTER FROM ").append(tableName).append(".").append(fieldName)
		.append(")::integer)").append(" IN (");

		return filterBuilder;
	}

	private StringBuilder extendFilterBuilderWithMonthOfYear(StringBuilder filterBuilder, String tableName,
			String fieldName) {
		if (filterBuilder.length() > 0) {
			filterBuilder.append(" AND ");
		}

		filterBuilder.append("((EXTRACT(YEAR FROM ").append(tableName).append(".").append(fieldName).append(")")
		.append(" * 100)::integer) + (EXTRACT(MONTH FROM ").append(tableName).append(".").append(fieldName)
		.append(")::integer)").append(" IN (");

		return filterBuilder;
	}

	private StringBuilder finalizeFilterBuilderSegment(StringBuilder filterBuilder) {
		filterBuilder.deleteCharAt(filterBuilder.length() - 1);
		filterBuilder.append(")");

		return filterBuilder;
	}

	@Override
	public Date getOldestCaseOnsetDate() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Timestamp> cq = cb.createQuery(Timestamp.class);
		Root<Case> from = cq.from(Case.class);
		Join<Case, Symptoms> symptoms = from.join(Case.SYMPTOMS, JoinType.LEFT);

		cq.select(cb.least((Path<Timestamp>) symptoms.<Timestamp>get(Symptoms.ONSET_DATE)));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public Date getOldestCaseReceptionDate() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Timestamp> cq = cb.createQuery(Timestamp.class);
		Root<Case> from = cq.from(Case.class);

		cq.select(cb.least(from.<Timestamp>get(Case.RECEPTION_DATE)));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public Date getOldestCaseReportDate() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Timestamp> cq = cb.createQuery(Timestamp.class);
		Root<Case> from = cq.from(Case.class);

		cq.select(cb.least(from.<Timestamp>get(Case.REPORT_DATE)));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public boolean isArchived(String caseUuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Case> from = cq.from(Case.class);

		// Workaround for probable bug in Eclipse Link/Postgre that throws a NoResultException when trying to
		// query for a true Boolean result
		cq.where(
				cb.and(
						cb.equal(from.get(Case.ARCHIVED), true), 
						cb.equal(from.get(AbstractDomainObject.UUID), caseUuid)));
		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	private String buildGroupingSelectQuery(StatisticsCaseAttribute grouping, StatisticsCaseSubAttribute subGrouping,
			String groupAlias) {
		StringBuilder groupingSelectPartBuilder = new StringBuilder();
		switch (grouping) {
		case SEX:
			groupingSelectPartBuilder.append(Person.TABLE_NAME).append(".").append(Person.SEX).append(" AS ")
			.append(groupAlias);
			break;
		case DISEASE:
			groupingSelectPartBuilder.append(Case.TABLE_NAME).append(".").append(Case.DISEASE).append(" AS ")
			.append(groupAlias);
			break;
		case CLASSIFICATION:
			groupingSelectPartBuilder.append(Case.TABLE_NAME).append(".").append(Case.CASE_CLASSIFICATION)
			.append(" AS ").append(groupAlias);
			break;
		case OUTCOME:
			groupingSelectPartBuilder.append(Case.TABLE_NAME).append(".").append(Case.OUTCOME).append(" AS ")
			.append(groupAlias);
			break;
		case REGION_DISTRICT: {
			switch (subGrouping) {
			case REGION:
				groupingSelectPartBuilder.append(Region.TABLE_NAME).append(".").append(Region.UUID).append(" AS ")
				.append(groupAlias);
				break;
			case DISTRICT:
				groupingSelectPartBuilder.append(District.TABLE_NAME).append(".").append(District.UUID).append(" AS ")
				.append(groupAlias);
				break;
			default:
				throw new IllegalArgumentException(subGrouping.toString());
			}
			break;
		}
		case AGE_INTERVAL_1_YEAR:
		case AGE_INTERVAL_5_YEARS:
		case AGE_INTERVAL_CHILDREN_COARSE:
		case AGE_INTERVAL_CHILDREN_FINE:
		case AGE_INTERVAL_CHILDREN_MEDIUM:
		case AGE_INTERVAL_BASIC:
			extendGroupingBuilderWithAgeInterval(groupingSelectPartBuilder, grouping, groupAlias);
			break;
		case ONSET_TIME:
			switch (subGrouping) {
			case YEAR:
				extendGroupingBuilderWithDate(groupingSelectPartBuilder, "YEAR", Symptoms.TABLE_NAME,
						Symptoms.ONSET_DATE, groupAlias);
				break;
			case QUARTER:
				extendGroupingBuilderWithDate(groupingSelectPartBuilder, "QUARTER", Symptoms.TABLE_NAME,
						Symptoms.ONSET_DATE, groupAlias);
				break;
			case MONTH:
				extendGroupingBuilderWithDate(groupingSelectPartBuilder, "MONTH", Symptoms.TABLE_NAME,
						Symptoms.ONSET_DATE, groupAlias);
				break;
			case EPI_WEEK:
				extendGroupingBuilderWithEpiWeek(groupingSelectPartBuilder, Symptoms.TABLE_NAME, Symptoms.ONSET_DATE,
						groupAlias);
				break;
			case QUARTER_OF_YEAR:
				extendGroupingBuilderWithQuarterOfYear(groupingSelectPartBuilder, Symptoms.TABLE_NAME,
						Symptoms.ONSET_DATE, groupAlias);
				break;
			case MONTH_OF_YEAR:
				extendGroupingBuilderWithMonthOfYear(groupingSelectPartBuilder, Symptoms.TABLE_NAME,
						Symptoms.ONSET_DATE, groupAlias);
				break;
			case EPI_WEEK_OF_YEAR:
				extendGroupingBuilderWithEpiWeekOfYear(groupingSelectPartBuilder, Symptoms.TABLE_NAME,
						Symptoms.ONSET_DATE, groupAlias);
				break;
			default:
				throw new IllegalArgumentException(subGrouping.toString());
			}
			break;
		case RECEPTION_TIME:
			switch (subGrouping) {
			case YEAR:
				extendGroupingBuilderWithDate(groupingSelectPartBuilder, "YEAR", Case.TABLE_NAME, Case.RECEPTION_DATE,
						groupAlias);
				break;
			case QUARTER:
				extendGroupingBuilderWithDate(groupingSelectPartBuilder, "QUARTER", Case.TABLE_NAME,
						Case.RECEPTION_DATE, groupAlias);
				break;
			case MONTH:
				extendGroupingBuilderWithDate(groupingSelectPartBuilder, "MONTH", Case.TABLE_NAME, Case.RECEPTION_DATE,
						groupAlias);
				break;
			case EPI_WEEK:
				extendGroupingBuilderWithEpiWeek(groupingSelectPartBuilder, Case.TABLE_NAME, Case.RECEPTION_DATE,
						groupAlias);
				break;
			case QUARTER_OF_YEAR:
				extendGroupingBuilderWithQuarterOfYear(groupingSelectPartBuilder, Case.TABLE_NAME, Case.RECEPTION_DATE,
						groupAlias);
				break;
			case MONTH_OF_YEAR:
				extendGroupingBuilderWithMonthOfYear(groupingSelectPartBuilder, Case.TABLE_NAME, Case.RECEPTION_DATE,
						groupAlias);
				break;
			case EPI_WEEK_OF_YEAR:
				extendGroupingBuilderWithEpiWeekOfYear(groupingSelectPartBuilder, Case.TABLE_NAME, Case.RECEPTION_DATE,
						groupAlias);
				break;
			default:
				throw new IllegalArgumentException(subGrouping.toString());
			}
			break;
		case REPORT_TIME:
			switch (subGrouping) {
			case YEAR:
				extendGroupingBuilderWithDate(groupingSelectPartBuilder, "YEAR", Case.TABLE_NAME, Case.REPORT_DATE,
						groupAlias);
				break;
			case QUARTER:
				extendGroupingBuilderWithDate(groupingSelectPartBuilder, "QUARTER", Case.TABLE_NAME, Case.REPORT_DATE,
						groupAlias);
				break;
			case MONTH:
				extendGroupingBuilderWithDate(groupingSelectPartBuilder, "MONTH", Case.TABLE_NAME, Case.REPORT_DATE,
						groupAlias);
				break;
			case EPI_WEEK:
				extendGroupingBuilderWithEpiWeek(groupingSelectPartBuilder, Case.TABLE_NAME, Case.REPORT_DATE,
						groupAlias);
				break;
			case QUARTER_OF_YEAR:
				extendGroupingBuilderWithQuarterOfYear(groupingSelectPartBuilder, Case.TABLE_NAME, Case.REPORT_DATE,
						groupAlias);
				break;
			case MONTH_OF_YEAR:
				extendGroupingBuilderWithMonthOfYear(groupingSelectPartBuilder, Case.TABLE_NAME, Case.REPORT_DATE,
						groupAlias);
				break;
			case EPI_WEEK_OF_YEAR:
				extendGroupingBuilderWithEpiWeekOfYear(groupingSelectPartBuilder, Case.TABLE_NAME, Case.REPORT_DATE,
						groupAlias);
				break;
			default:
				throw new IllegalArgumentException(subGrouping.toString());
			}
			break;
		default:
			throw new IllegalArgumentException(subGrouping.toString());
		}
		return groupingSelectPartBuilder.toString();
	}

	private void extendGroupingBuilderWithDate(StringBuilder groupingBuilder, String dateToExtract, String tableName,
			String fieldName, String groupAlias) {
		groupingBuilder.append("(EXTRACT(" + dateToExtract + " FROM ").append(tableName).append(".").append(fieldName)
		.append(")::integer) AS ").append(groupAlias);
	}

	private void extendGroupingBuilderWithEpiWeek(StringBuilder groupingBuilder, String tableName, String fieldName,
			String groupAlias) {
		groupingBuilder.append("epi_week(").append(tableName).append(".").append(fieldName).append(") AS ")
		.append(groupAlias);
	}

	private void extendGroupingBuilderWithEpiWeekOfYear(StringBuilder groupingBuilder, String tableName,
			String fieldName, String groupAlias) {
		groupingBuilder.append("(epi_year(").append(tableName).append(".").append(fieldName).append(") * 100")
		.append(" + epi_week(").append(tableName).append(".").append(fieldName).append(")) AS ")
		.append(groupAlias);
	}

	private void extendGroupingBuilderWithQuarterOfYear(StringBuilder groupingBuilder, String tableName,
			String fieldName, String groupAlias) {
		groupingBuilder.append("((EXTRACT(YEAR FROM ").append(tableName).append(".").append(fieldName)
		.append(") * 10)::integer)").append(" + (EXTRACT(QUARTER FROM ").append(tableName).append(".")
		.append(fieldName).append(")::integer) AS ").append(groupAlias);
	}

	private void extendGroupingBuilderWithMonthOfYear(StringBuilder groupingBuilder, String tableName, String fieldName,
			String groupAlias) {
		groupingBuilder.append("((EXTRACT(YEAR FROM ").append(tableName).append(".").append(fieldName)
		.append(") * 100)::integer)").append(" + (EXTRACT(MONTH FROM ").append(tableName).append(".")
		.append(fieldName).append(")::integer) AS ").append(groupAlias);
	}

	private void extendGroupingBuilderWithAgeInterval(StringBuilder groupingBuilder, StatisticsCaseAttribute grouping,
			String groupAlias) {
		groupingBuilder.append("CASE ");
		switch (grouping) {
		case AGE_INTERVAL_1_YEAR:
			for (int i = 0; i < 80; i++) {
				groupingBuilder.append("WHEN ").append(Case.TABLE_NAME).append(".").append(Case.CASE_AGE).append(" = ")
				.append(i < 10 ? "0" + i : i).append(" THEN ").append("'").append(i < 10 ? "0" + i : i)
				.append("' ");
			}
			break;
		case AGE_INTERVAL_5_YEARS:
			for (int i = 0; i < 80; i += 5) {
				addAgeIntervalToStringBuilder(groupingBuilder, i, 4);
			}
			break;
		case AGE_INTERVAL_CHILDREN_COARSE:
			addAgeIntervalToStringBuilder(groupingBuilder, 0, 14);
			for (int i = 15; i < 30; i += 5) {
				addAgeIntervalToStringBuilder(groupingBuilder, i, 4);
			}
			for (int i = 30; i < 80; i += 10) {
				addAgeIntervalToStringBuilder(groupingBuilder, i, 9);
			}
			break;
		case AGE_INTERVAL_CHILDREN_FINE:
			for (int i = 0; i < 5; i++) {
				groupingBuilder.append("WHEN ").append(Case.TABLE_NAME).append(".").append(Case.CASE_AGE).append(" = ")
				.append(i).append(" THEN ").append("'").append("0" + i).append("-").append("0" + i)
				.append("' ");
			}
			for (int i = 5; i < 30; i += 5) {
				addAgeIntervalToStringBuilder(groupingBuilder, i, 4);
			}
			for (int i = 30; i < 80; i += 10) {
				addAgeIntervalToStringBuilder(groupingBuilder, i, 9);
			}
			break;
		case AGE_INTERVAL_CHILDREN_MEDIUM:
			for (int i = 0; i < 30; i += 5) {
				addAgeIntervalToStringBuilder(groupingBuilder, i, 4);
			}
			for (int i = 30; i < 80; i += 10) {
				addAgeIntervalToStringBuilder(groupingBuilder, i, 9);
			}
			break;
		case AGE_INTERVAL_BASIC:
			addAgeIntervalToStringBuilder(groupingBuilder, 0, 0);
			addAgeIntervalToStringBuilder(groupingBuilder, 1, 3);
			addAgeIntervalToStringBuilder(groupingBuilder, 5, 9);
			groupingBuilder.append("WHEN ").append(Case.TABLE_NAME).append(".").append(Case.CASE_AGE)
			.append(" >= 15 THEN '15+' ");
			break;
		default:
			throw new IllegalArgumentException(grouping.toString());
		}

		if (grouping != StatisticsCaseAttribute.AGE_INTERVAL_BASIC) {
			groupingBuilder.append("WHEN ").append(Case.TABLE_NAME).append(".").append(Case.CASE_AGE)
			.append(" >= 80 THEN '80+' ");
		}
		groupingBuilder.append("ELSE 'Unknown' END AS " + groupAlias);
	}

	private void addAgeIntervalToStringBuilder(StringBuilder groupingBuilder, int number, int increase) {
		String lowerNumberString = number < 10 ? "0" + number : String.valueOf(number);
		String higherNumberString = number + increase < 10 ? "0" + (number + increase)
				: String.valueOf(number + increase);
		groupingBuilder.append("WHEN ").append(Case.TABLE_NAME).append(".").append(Case.CASE_AGE).append(" BETWEEN ")
		.append(number).append(" AND ").append(number + increase).append(" THEN '").append(lowerNumberString)
		.append("-").append(higherNumberString).append("' ");
	}

	@LocalBean
	@Stateless
	public static class CaseFacadeEjbLocal extends CaseFacadeEjb {
	}

}
