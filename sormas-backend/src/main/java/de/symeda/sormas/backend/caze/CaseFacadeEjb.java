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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.CaseMeasure;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.ExportType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.IntegerRange;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.CaseSimilarityCriteria;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.api.caze.porthealthinfo.PortHealthInfoDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseReferenceDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitCriteria;
import de.symeda.sormas.api.epidata.EpiDataTravelHelper;
import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.CauseOfDeath;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
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
import de.symeda.sormas.api.therapy.PrescriptionCriteria;
import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.therapy.TherapyReferenceDto;
import de.symeda.sormas.api.therapy.TreatmentCriteria;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.caze.classification.CaseClassificationFacadeEjb.CaseClassificationFacadeEjbLocal;
import de.symeda.sormas.backend.caze.maternalhistory.MaternalHistoryFacadeEjb;
import de.symeda.sormas.backend.caze.maternalhistory.MaternalHistoryFacadeEjb.MaternalHistoryFacadeEjbLocal;
import de.symeda.sormas.backend.caze.porthealthinfo.PortHealthInfoFacadeEjb;
import de.symeda.sormas.backend.caze.porthealthinfo.PortHealthInfoFacadeEjb.PortHealthInfoFacadeEjbLocal;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourse;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourseFacadeEjb;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourseFacadeEjb.ClinicalCourseFacadeEjbLocal;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisit;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisitService;
import de.symeda.sormas.backend.clinicalcourse.HealthConditions;
import de.symeda.sormas.backend.clinicalcourse.HealthConditionsService;
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
import de.symeda.sormas.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.backend.hospitalization.PreviousHospitalizationService;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.PointOfEntryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.PointOfEntryService;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.location.LocationService;
import de.symeda.sormas.backend.outbreak.OutbreakFacadeEjb.OutbreakFacadeEjbLocal;
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
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestService;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb.SymptomsFacadeEjbLocal;
import de.symeda.sormas.backend.symptoms.SymptomsService;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.therapy.Prescription;
import de.symeda.sormas.backend.therapy.PrescriptionService;
import de.symeda.sormas.backend.therapy.TherapyFacadeEjb;
import de.symeda.sormas.backend.therapy.TherapyFacadeEjb.TherapyFacadeEjbLocal;
import de.symeda.sormas.backend.therapy.Treatment;
import de.symeda.sormas.backend.therapy.TreatmentService;
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
	private PathogenTestService pathogenTestService;
	@EJB
	private PathogenTestFacadeEjbLocal sampleTestFacade;
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
	@EJB
	private TherapyFacadeEjbLocal therapyFacade;
	@EJB
	private ClinicalCourseFacadeEjbLocal clinicalCourseFacade;
	@EJB
	private PrescriptionService prescriptionService;
	@EJB
	private TreatmentService treatmentService;
	@EJB
	private ClinicalVisitService clinicalVisitService;
	@EJB
	private OutbreakFacadeEjbLocal outbreakFacade;
	@EJB
	private MaternalHistoryFacadeEjbLocal maternalHistoryFacade;
	@EJB
	private PointOfEntryService pointOfEntryService;
	@EJB
	private PortHealthInfoFacadeEjbLocal portHealthInfoFacade;
	@EJB
	private HealthConditionsService healthConditionsService;

	private static final Logger logger = LoggerFactory.getLogger(CaseFacadeEjb.class);

	@Override
	public List<CaseDataDto> getAllActiveCasesAfter(Date date, String userUuid) {
		User user = userService.getByUuid(userUuid);

		if (user == null) {
			return Collections.emptyList();
		}

		return caseService.getAllActiveCasesAfter(date, user).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<CaseDataDto> getByUuids(List<String> uuids) {
		return caseService.getByUuids(uuids).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public long count(String userUuid, CaseCriteria caseCriteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Case> root = cq.from(Case.class);
		User user = userService.getByUuid(userUuid);
		Predicate filter = caseService.createUserFilter(cb, cq, root, user);
		if (caseCriteria != null) {
			Predicate criteriaFilter = caseService.buildCriteriaFilter(caseCriteria, cb, root);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		}
		if (filter != null) {
			cq.where(filter);
		}
		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<CaseIndexDto> getIndexList(String userUuid, CaseCriteria caseCriteria, int first, int max,
			List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CaseIndexDto> cq = cb.createQuery(CaseIndexDto.class);
		Root<Case> caze = cq.from(Case.class);

		selectIndexDtoFields(cq, caze);
		setIndexDtoSortingOrder(cb, cq, caze, sortProperties);

		User user = userService.getByUuid(userUuid);
		Predicate filter = caseService.createUserFilter(cb, cq, caze, user);

		if (caseCriteria != null) {
			Predicate criteriaFilter = caseService.buildCriteriaFilter(caseCriteria, cb, caze);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		return em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
	}

	@Override
	public List<CaseExportDto> getExportList(String userUuid, CaseCriteria caseCriteria, ExportType exportType, int first, int max) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CaseExportDto> cq = cb.createQuery(CaseExportDto.class);
		Root<Case> caze = cq.from(Case.class);
		Join<Case, Person> person = caze.join(Case.PERSON, JoinType.LEFT);
		Join<Case, Hospitalization> hospitalization = caze.join(Case.HOSPITALIZATION, JoinType.LEFT);
		Join<Case, EpiData> epiData = caze.join(Case.EPI_DATA, JoinType.LEFT);
		Join<Case, Symptoms> symptoms = caze.join(Case.SYMPTOMS, JoinType.LEFT);
		Join<Case, ClinicalCourse> clinicalCourse = caze.join(Case.CLINICAL_COURSE, JoinType.LEFT);
		Join<ClinicalCourse, HealthConditions> healthConditions = clinicalCourse.join(ClinicalCourse.HEALTH_CONDITIONS, JoinType.LEFT);
		Join<Case, Region> region = caze.join(Case.REGION, JoinType.LEFT);
		Join<Case, District> district = caze.join(Case.DISTRICT, JoinType.LEFT);
		Join<Case, Community> community = caze.join(Case.COMMUNITY, JoinType.LEFT);
		Join<Case, Facility> facility = caze.join(Case.HEALTH_FACILITY, JoinType.LEFT);
		Join<Person, Facility> occupationFacility = person.join(Person.OCCUPATION_FACILITY, JoinType.LEFT);

		cq.multiselect(caze.get(Case.ID), person.get(Person.ID), epiData.get(EpiData.ID), symptoms.get(Symptoms.ID),
				hospitalization.get(Hospitalization.ID), district.get(District.ID), healthConditions.get(HealthConditions.ID), 
				caze.get(Case.UUID), caze.get(Case.EPID_NUMBER), caze.get(Case.DISEASE), caze.get(Case.DISEASE_DETAILS),
				person.get(Person.FIRST_NAME), person.get(Person.LAST_NAME), person.get(Person.SEX),
				person.get(Person.APPROXIMATE_AGE), person.get(Person.APPROXIMATE_AGE_TYPE),
				person.get(Person.BIRTHDATE_DD), person.get(Person.BIRTHDATE_MM), person.get(Person.BIRTHDATE_YYYY),
				caze.get(Case.REPORT_DATE), region.get(Region.NAME), district.get(District.NAME),
				community.get(Community.NAME), facility.get(Facility.NAME), facility.get(Facility.UUID),
				caze.get(Case.HEALTH_FACILITY_DETAILS), caze.get(Case.CASE_CLASSIFICATION),
				caze.get(Case.INVESTIGATION_STATUS), caze.get(Case.OUTCOME),
				hospitalization.get(Hospitalization.ADMITTED_TO_HEALTH_FACILITY),
				hospitalization.get(Hospitalization.ADMISSION_DATE),
				hospitalization.get(Hospitalization.DISCHARGE_DATE),
				hospitalization.get(Hospitalization.LEFT_AGAINST_ADVICE), person.get(Person.PRESENT_CONDITION),
				person.get(Person.DEATH_DATE), person.get(Person.BURIAL_DATE), person.get(Person.BURIAL_CONDUCTOR),
				person.get(Person.BURIAL_PLACE_DESCRIPTION), person.get(Person.PHONE), person.get(Person.PHONE_OWNER),
				person.get(Person.EDUCATION_TYPE), person.get(Person.EDUCATION_DETAILS),
				person.get(Person.OCCUPATION_TYPE), person.get(Person.OCCUPATION_DETAILS),
				occupationFacility.get(Facility.NAME), occupationFacility.get(Facility.UUID),
				person.get(Person.OCCUPATION_FACILITY_DETAILS), epiData.get(EpiData.TRAVELED),
				epiData.get(EpiData.BURIAL_ATTENDED), epiData.get(EpiData.DIRECT_CONTACT_CONFIRMED_CASE),
				epiData.get(EpiData.RODENTS),
				// symptoms.get(Symptoms.ONSET_DATE),
				caze.get(Case.VACCINATION), caze.get(Case.VACCINATION_DOSES), caze.get(Case.VACCINATION_DATE),
				caze.get(Case.VACCINATION_INFO_SOURCE));

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
			// TODO: Speed up this code, e.g. by persisting symptoms, lab results, etc. as a
			// String in the database
			if (exportType == ExportType.CASE_SURVEILLANCE) {
				List<Date> sampleDates = sampleService.getSampleDatesForCase(exportDto.getId());
				exportDto.setSampleTaken(
						(sampleDates == null || sampleDates.isEmpty()) ? YesNoUnknown.NO : YesNoUnknown.YES);
				exportDto.setSampleDates(sampleDates);
				exportDto.setLabResults(pathogenTestService.getPathogenTestResultsForCase(exportDto.getId()));

				// Build travel history - done here to avoid transforming EpiDataTravel to
				// EpiDataTravelDto
				List<EpiDataTravel> travels = epiDataTravelService.getAllByEpiDataId(exportDto.getEpiDataId());
				StringBuilder travelHistoryBuilder = new StringBuilder();
				for (int i = 0; i < travels.size(); i++) {
					EpiDataTravel travel = travels.get(i);
					if (i > 0) {
						travelHistoryBuilder.append(", ");
					}
					travelHistoryBuilder.append(EpiDataTravelHelper.buildTravelString(travel.getTravelType(),
							travel.getTravelDestination(), travel.getTravelDateFrom(), travel.getTravelDateTo()));
				}
				if (travelHistoryBuilder.length() == 0 && exportDto.getTraveled() != null) {
					travelHistoryBuilder.append(exportDto.getTraveled());
				}
				exportDto.setTravelHistory(travelHistoryBuilder.toString());
			} else if (exportType == ExportType.CASE_MANAGEMENT) {
				exportDto.setNumberOfPrescriptions(prescriptionService.getPrescriptionCountByCase(exportDto.getId()));
				exportDto.setNumberOfTreatments(treatmentService.getTreatmentCountByCase(exportDto.getId()));
				exportDto.setNumberOfClinicalVisits(clinicalVisitService.getClinicalVisitCountByCase(exportDto.getId()));
				exportDto.setHealthConditions(ClinicalCourseFacadeEjb.toHealthConditionsDto(healthConditionsService.getById(exportDto.getHealthConditionsId())));
			}

			// exportDto.setSymptoms(symptomsService.getById(exportDto.getSymptomsId()).toHumanString(false));
			exportDto.setSymptoms(SymptomsFacadeEjb.toDto(symptomsService.getById(exportDto.getSymptomsId())));
			exportDto.setAddress(personService.getAddressByPersonId(exportDto.getPersonId()).toString());
			List<CaseClassification> sourceCaseClassifications = contactService
					.getSourceCaseClassifications(exportDto.getId());
			exportDto.setMaxSourceCaseClassifcation(sourceCaseClassifications.stream()
					.filter(c -> c != CaseClassification.NO_CASE).max(Comparator.naturalOrder()).orElse(null));

			// Place of initial detection
			PreviousHospitalization firstPrevHosp = previousHospitalizationService
					.getInitialHospitalization(exportDto.getHospitalizationId());
			if (firstPrevHosp != null) {
				exportDto.setInitialDetectionPlace(firstPrevHosp.getHealthFacility().toString());
			} else {
				exportDto.setInitialDetectionPlace(exportDto.getHealthFacility());
			}

			// Associated with outbreak?
			DistrictReferenceDto districtRef = districtFacade.getDistrictReferenceById(exportDto.getDistrictId());
			exportDto.setAssociatedWithOutbreak(
					outbreakFacade.hasOutbreakAtDate(districtRef, exportDto.getDisease(), exportDto.getReportDate()));

			// Country
			exportDto.setCountry(configFacade.getEpidPrefix());
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
	public List<DashboardCaseDto> getCasesForDashboard(CaseCriteria caseCriteria, String userUuid) {
		User user = userService.getByUuid(userUuid);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardCaseDto> cq = cb.createQuery(DashboardCaseDto.class);
		Root<Case> caze = cq.from(Case.class);
		Join<Case, Symptoms> symptoms = caze.join(Case.SYMPTOMS, JoinType.LEFT);
		Join<Case, Person> person = caze.join(Case.PERSON, JoinType.LEFT);

		Predicate filter = caseService.createUserFilter(cb, cq, caze, user);
		Predicate criteriaFilter = caseService.buildCriteriaFilter(caseCriteria, cb, caze);
		if (filter != null) {
			filter = cb.and(filter, criteriaFilter);
		} else {
			filter = criteriaFilter;
		}

		if (filter != null) {
			cq.where(filter);
		}

		List<DashboardCaseDto> result;
		if (filter != null) {
			cq.where(filter);
			cq.multiselect(caze.get(Case.REPORT_DATE), symptoms.get(Symptoms.ONSET_DATE),
					caze.get(Case.CASE_CLASSIFICATION), caze.get(Case.DISEASE), caze.get(Case.INVESTIGATION_STATUS),
					person.get(Person.PRESENT_CONDITION), person.get(Person.CAUSE_OF_DEATH_DISEASE));

			result = em.createQuery(cq).getResultList();
		} else {
			result = Collections.emptyList();
		}

		return result;
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
	public CaseDataDto getMatchingCaseForImport(CaseDataDto importCaze, PersonReferenceDto existingPerson,
			String userUuid) {
		User user = userService.getByUuid(userUuid);
		Date newCaseDate = CaseLogic.getStartDate(importCaze.getSymptoms().getOnsetDate(), importCaze.getReportDate());

		CaseCriteria criteria = new CaseCriteria().person(existingPerson).disease(importCaze.getDisease())
				.archived(false).newCaseDateBetween(DateHelper.subtractMonths(newCaseDate, 2),
						DateHelper.addMonths(newCaseDate, 2), null);

		List<Case> matchingCases = caseService.findBy(criteria, user).stream().sorted(new Comparator<Case>() {
			@Override
			public int compare(Case c1, Case c2) {
				return CaseLogic.getStartDate(c2.getSymptoms().getOnsetDate(), c2.getReportDate())
						.compareTo(CaseLogic.getStartDate(c1.getSymptoms().getOnsetDate(), c1.getReportDate()));
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

		return caseService.findBy(new CaseCriteria().person(new PersonReferenceDto(personUuid)), user).stream()
				.map(c -> toDto(c)).collect(Collectors.toList());
	}

	public Map<CaseClassification, Long> getCaseCountPerClassification(CaseCriteria caseCriteria, String userUuid) {
		User user = userService.getByUuid(userUuid);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> caze = cq.from(Case.class);

		Predicate filter = caseService.createUserFilter(cb, cq, caze, user);
		Predicate criteriaFilter = caseService.buildCriteriaFilter(caseCriteria, cb, caze);
		if (filter != null) {
			filter = cb.and(filter, criteriaFilter);
		} else {
			filter = criteriaFilter;
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(caze.get(Case.CASE_CLASSIFICATION));
		cq.multiselect(caze.get(Case.CASE_CLASSIFICATION), cb.count(caze));
		List<Object[]> results = em.createQuery(cq).getResultList();

		Map<CaseClassification, Long> resultMap = results.stream()
				.collect(Collectors.toMap(e -> (CaseClassification) e[0], e -> (Long) e[1]));
		return resultMap;
	}

	public Map<PresentCondition, Long> getCaseCountPerPersonCondition(CaseCriteria caseCriteria, String userUuid) {
		User user = userService.getByUuid(userUuid);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> caze = cq.from(Case.class);
		Join<Case, Person> person = caze.join(Case.PERSON, JoinType.LEFT);

		Predicate filter = caseService.createUserFilter(cb, cq, caze, user);
		Predicate criteriaFilter = caseService.buildCriteriaFilter(caseCriteria, cb, caze);
		if (filter != null) {
			filter = cb.and(filter, criteriaFilter);
		} else {
			filter = criteriaFilter;
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(person.get(Person.PRESENT_CONDITION));
		cq.multiselect(person.get(Person.PRESENT_CONDITION), cb.count(caze));
		List<Object[]> results = em.createQuery(cq).getResultList();

		Map<PresentCondition, Long> resultMap = results.stream()
				.collect(Collectors.toMap(e -> (PresentCondition) e[0], e -> (Long) e[1]));
		return resultMap;
	}

	@Override
	public Map<Disease, Long> getCaseCountByDisease(CaseCriteria caseCriteria, String userUuid) {
		User user = userService.getByUuid(userUuid);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> caze = cq.from(Case.class);

		Predicate filter = caseService.createUserFilter(cb, cq, caze, user);

		filter = AbstractAdoService.and(cb, filter, caseService.buildCriteriaFilter(caseCriteria, cb, caze));

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(caze.get(Case.DISEASE));
		cq.multiselect(caze.get(Case.DISEASE), cb.count(caze));
		List<Object[]> results = em.createQuery(cq).getResultList();

		Map<Disease, Long> resultMap = results.stream()
				.collect(Collectors.toMap(e -> (Disease) e[0], e -> (Long) e[1]));

		return resultMap;
	}

	public Map<Disease, District> getLastReportedDistrictByDisease(CaseCriteria caseCriteria, String userUuid) {
		User user = userService.getByUuid(userUuid);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> caze = cq.from(Case.class);
		Join<Case, District> districtJoin = caze.join(Case.DISTRICT, JoinType.LEFT);

		Predicate filter = caseService.createUserFilter(cb, cq, caze, user);

		filter = AbstractAdoService.and(cb, filter, caseService.buildCriteriaFilter(caseCriteria, cb, caze));

		if (filter != null) {
			cq.where(filter);
		}

		Expression<Number> maxReportDate = cb.max(caze.get(Case.REPORT_DATE));
		cq.multiselect(caze.get(Case.DISEASE), districtJoin, maxReportDate);
		cq.groupBy(caze.get(Case.DISEASE), districtJoin);
		cq.orderBy(cb.desc(maxReportDate));

		List<Object[]> results = em.createQuery(cq).getResultList();

		Map<Disease, District> resultMap = new HashMap<Disease, District>();
		for (Object[] e : results) {
			Disease disease = (Disease) e[0];
			if (!resultMap.containsKey(disease)) {
				District district = (District) e[1];
				resultMap.put(disease, district);
			}
		}

		return resultMap;
	}

	@Override
	public List<CaseIndexDto> getSimilarCases(CaseSimilarityCriteria criteria, String userUuid) {
		User user = userService.getByUuid(userUuid);
		CaseCriteria caseCriteria = criteria.getCaseCriteria();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CaseIndexDto> cq = cb.createQuery(CaseIndexDto.class);
		Root<Case> root = cq.from(Case.class);
		Join<Case, Person> person = root.join(Case.PERSON, JoinType.LEFT);
		Join<Case, Region> region = root.join(Case.REGION, JoinType.LEFT);

		selectIndexDtoFields(cq, root);

		Predicate userFilter = caseService.createUserFilter(cb, cq, root, user);
		Expression<String> nameSimilarityExpr = cb.concat(person.get(Person.FIRST_NAME), " ");
		nameSimilarityExpr = cb.concat(nameSimilarityExpr, person.get(Person.LAST_NAME));
		Predicate nameSimilarityFilter = cb.gt(cb.function("similarity", double.class, cb.parameter(String.class, "name"), nameSimilarityExpr), FacadeProvider.getConfigFacade().getNameSimilarityThreshold());
		Predicate diseaseFilter = caseCriteria.getDisease() != null ? cb.equal(root.get(Case.DISEASE), caseCriteria.getDisease()) : null;
		Predicate regionFilter = caseCriteria.getRegion() != null ? cb.equal(region.get(Region.UUID), caseCriteria.getRegion().getUuid()) : null;
		Predicate reportDateFilter = criteria.getReportDate() != null ? cb.between(root.get(Case.REPORT_DATE), DateHelper.subtractDays(criteria.getReportDate(), 30), DateHelper.addDays(criteria.getReportDate(), 30)) : null;

		Predicate filter = userFilter;

		if (filter != null) {
			filter = cb.and(userFilter, nameSimilarityFilter);
		} else {
			filter = nameSimilarityFilter;
		}
		if (diseaseFilter != null) {
			filter = cb.and(filter, diseaseFilter);
		}
		if (regionFilter != null) {
			filter = cb.and(filter, regionFilter);
		}
		if (reportDateFilter != null) {
			filter = cb.and(filter, reportDateFilter);
		}

		cq.where(filter);

		return em.createQuery(cq).setParameter("name", criteria.getFirstName() + " " + criteria.getLastName()).getResultList();
	}

	@Override
	public List<CaseIndexDto[]> getCasesForDuplicateMerging(CaseCriteria criteria, String userUuid) {
		User user = userService.getByUuid(userUuid);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> root = cq.from(Case.class);
		Root<Case> root2 = cq.from(Case.class);
		Join<Case, Person> person = root.join(Case.PERSON, JoinType.LEFT);
		Join<Case, Person> person2 = root2.join(Case.PERSON, JoinType.LEFT);
		Join<Case, Region> region = root.join(Case.REGION, JoinType.LEFT);
		Join<Case, Region> region2 = root2.join(Case.REGION, JoinType.LEFT);

		Predicate userFilter = caseService.createUserFilter(cb, cq, root, user);
		Predicate criteriaFilter = criteria != null ? caseService.buildCriteriaFilter(criteria, cb, root) : null;
		Expression<String> nameSimilarityExpr = cb.concat(person.get(Person.FIRST_NAME), " ");
		nameSimilarityExpr = cb.concat(nameSimilarityExpr, person.get(Person.LAST_NAME));
		Expression<String> nameSimilarityExpr2 = cb.concat(person2.get(Person.FIRST_NAME), " ");
		nameSimilarityExpr2 = cb.concat(nameSimilarityExpr2, person2.get(Person.LAST_NAME));
		Predicate nameSimilarityFilter = cb.gt(cb.function("similarity", double.class, nameSimilarityExpr, nameSimilarityExpr2), FacadeProvider.getConfigFacade().getNameSimilarityThreshold());
		Predicate diseaseFilter = cb.equal(root.get(Case.DISEASE), root2.get(Case.DISEASE));
		Predicate regionFilter = cb.equal(region.get(Region.ID), region2.get(Region.ID));
		Predicate reportDateFilter = cb.lessThanOrEqualTo(
				cb.abs(
						cb.diff(
								cb.function("date_part", Long.class, cb.parameter(String.class, "date_type"), root.get(Case.REPORT_DATE)),
								cb.function("date_part", Long.class, cb.parameter(String.class, "date_type"), root2.get(Case.REPORT_DATE)))),
				new Long(30 * 24 * 60 * 60) // 30 days
				);
		Predicate creationDateFilter = cb.lessThan(root.get(Case.CREATION_DATE), root2.get(Case.CREATION_DATE));

		Predicate filter = userFilter;

		if (filter != null) {
			filter = cb.and(filter, criteriaFilter);
		} else {
			filter = criteriaFilter;
		}
		if (filter != null) {
			filter = cb.and(filter, nameSimilarityFilter);
		} else {
			filter = nameSimilarityFilter;
		}
		filter = cb.and(filter, diseaseFilter);
		filter = cb.and(filter, regionFilter);
		filter = cb.and(filter, reportDateFilter);
		filter = cb.and(filter, creationDateFilter);

		cq.where(filter);
		cq.multiselect(
				root.get(Case.ID),
				root2.get(Case.ID));
		cq.orderBy(cb.desc(root.get(Case.CREATION_DATE)));

		List<Object[]> foundIds = (List<Object[]>) em.createQuery(cq).setParameter("date_type", "epoch").getResultList();
		List<CaseIndexDto[]> resultList = new ArrayList<>();

		if (!foundIds.isEmpty()) {
			//			List<Object> parentIds = foundIds.stream().map(ids -> ids[0]).collect(Collectors.toList());
			//			List<Object> childrenIds = foundIds.stream().map(ids -> ids[1]).collect(Collectors.toList());
			//			List<CaseIndexDto> parentList = new ArrayList<>();
			//			List<CaseIndexDto> childrenList = new ArrayList<>();
			//
			//			CriteriaQuery<CaseIndexDto> indexCq = cb.createQuery(CaseIndexDto.class);
			//			Root<Case> indexRoot = indexCq.from(Case.class);
			//			selectIndexDtoFields(indexCq, indexRoot);
			//			indexCq.where(indexRoot.get(Case.ID).in(parentIds));
			//			parentList = em.createQuery(indexCq).getResultList();
			//			indexCq.where(indexRoot.get(Case.ID).in(childrenIds));
			//			childrenList = em.createQuery(indexCq).getResultList();
			//
			//			for (Object[] idPair : foundIds) {
			//				CaseIndexDto parent = parentList.stream().filter(c -> c.getId() == (long) idPair[0]).findFirst().get();
			//				CaseIndexDto child = childrenList.stream().filter(c -> c.getId() == (long) idPair[1]).findFirst().get();
			//				
			//				if (parent.getCompleteness() == null && child.getCompleteness() == null
			//						|| parent.getCompleteness() != null && (child.getCompleteness() == null 
			//						|| (parent.getCompleteness() >= child.getCompleteness()))) {
			//					resultList.add(new CaseIndexDto[] {parent, child});
			//				} else {
			//					resultList.add(new CaseIndexDto[] {child, parent});
			//				}
			//			}
			for (Object[] idPair : foundIds) {
				CriteriaQuery<CaseIndexDto> indexCq = cb.createQuery(CaseIndexDto.class);
				Root<Case> indexRoot = indexCq.from(Case.class);
				selectIndexDtoFields(indexCq, indexRoot);
				indexCq.where(cb.equal(indexRoot.get(Case.ID), idPair[0]));
				CaseIndexDto parent = em.createQuery(indexCq).setMaxResults(1).getSingleResult();
				indexCq.where(cb.equal(indexRoot.get(Case.ID), idPair[1]));
				CaseIndexDto child = em.createQuery(indexCq).setMaxResults(1).getSingleResult();

				if (parent.getCompleteness() == null && child.getCompleteness() == null
						|| parent.getCompleteness() != null && (child.getCompleteness() == null 
						|| (parent.getCompleteness() >= child.getCompleteness()))) {
					resultList.add(new CaseIndexDto[] {parent, child});
				} else {
					resultList.add(new CaseIndexDto[] {child, parent});
				}
			}
		}

		return resultList;
	}

	public void updateCompleteness(String caseUuid) {
		Case caze = caseService.getByUuid(caseUuid);
		caze.setCompleteness(calculateCompleteness(caze));
		caseService.ensurePersisted(caze);
	}

	private void selectIndexDtoFields(CriteriaQuery<CaseIndexDto> cq, Root<Case> root) {
		Join<Case, Person> person = root.join(Case.PERSON, JoinType.LEFT);
		Join<Case, Region> region = root.join(Case.REGION, JoinType.LEFT);
		Join<Case, District> district = root.join(Case.DISTRICT, JoinType.LEFT);
		Join<Case, Facility> facility = root.join(Case.HEALTH_FACILITY, JoinType.LEFT);
		Join<Case, PointOfEntry> pointOfEntry = root.join(Case.POINT_OF_ENTRY, JoinType.LEFT);
		Join<Case, User> surveillanceOfficer = root.join(Case.SURVEILLANCE_OFFICER, JoinType.LEFT);

		cq.multiselect(root.get(AbstractDomainObject.ID), root.get(Case.UUID), root.get(Case.EPID_NUMBER), person.get(Person.FIRST_NAME),
				person.get(Person.LAST_NAME), root.get(Case.DISEASE), root.get(Case.DISEASE_DETAILS),
				root.get(Case.CASE_CLASSIFICATION), root.get(Case.INVESTIGATION_STATUS),
				person.get(Person.PRESENT_CONDITION), root.get(Case.REPORT_DATE),
				root.get(AbstractDomainObject.CREATION_DATE), region.get(Region.UUID), district.get(District.UUID),
				district.get(District.NAME), facility.get(Facility.UUID), facility.get(Facility.NAME),
				root.get(Case.HEALTH_FACILITY_DETAILS), pointOfEntry.get(PointOfEntry.UUID),
				pointOfEntry.get(PointOfEntry.NAME), root.get(Case.POINT_OF_ENTRY_DETAILS),
				surveillanceOfficer.get(User.UUID), root.get(Case.OUTCOME),
				person.get(Person.APPROXIMATE_AGE), person.get(Person.APPROXIMATE_AGE_TYPE),
				person.get(Person.BIRTHDATE_DD), person.get(Person.BIRTHDATE_MM), person.get(Person.BIRTHDATE_YYYY),
				person.get(Person.SEX), root.get(Case.COMPLETENESS));
	}

	private void setIndexDtoSortingOrder(CriteriaBuilder cb, CriteriaQuery<CaseIndexDto> cq, Root<Case> root, List<SortProperty> sortProperties) {
		Join<Case, Person> person = root.join(Case.PERSON, JoinType.LEFT);
		Join<Case, Region> region = root.join(Case.REGION, JoinType.LEFT);
		Join<Case, District> district = root.join(Case.DISTRICT, JoinType.LEFT);
		Join<Case, Facility> facility = root.join(Case.HEALTH_FACILITY, JoinType.LEFT);
		Join<Case, PointOfEntry> pointOfEntry = root.join(Case.POINT_OF_ENTRY, JoinType.LEFT);
		Join<Case, User> surveillanceOfficer = root.join(Case.SURVEILLANCE_OFFICER, JoinType.LEFT);

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<Order>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case CaseIndexDto.ID:
				case CaseIndexDto.UUID:
				case CaseIndexDto.EPID_NUMBER:
				case CaseIndexDto.DISEASE:
				case CaseIndexDto.DISEASE_DETAILS:
				case CaseIndexDto.CASE_CLASSIFICATION:
				case CaseIndexDto.INVESTIGATION_STATUS:
				case CaseIndexDto.REPORT_DATE:
				case CaseIndexDto.CREATION_DATE:
				case CaseIndexDto.OUTCOME:
				case CaseIndexDto.COMPLETENESS:
					expression = root.get(sortProperty.propertyName);
					break;
				case CaseIndexDto.PERSON_FIRST_NAME:
					expression = person.get(Person.FIRST_NAME);
					break;
				case CaseIndexDto.PERSON_LAST_NAME:
					expression = person.get(Person.LAST_NAME);
					break;
				case CaseIndexDto.PRESENT_CONDITION:
					expression = person.get(sortProperty.propertyName);
					break;
				case CaseIndexDto.REGION_UUID:
					expression = region.get(Region.UUID);
					break;
				case CaseIndexDto.DISTRICT_UUID:
					expression = district.get(District.UUID);
					break;
				case CaseIndexDto.DISTRICT_NAME:
					expression = district.get(District.NAME);
					break;
				case CaseIndexDto.HEALTH_FACILITY_UUID:
					expression = facility.get(Facility.UUID);
					break;
				case CaseIndexDto.HEALTH_FACILITY_NAME:
					expression = facility.get(Facility.NAME);
					break;
				case CaseIndexDto.POINT_OF_ENTRY_NAME:
					expression = pointOfEntry.get(PointOfEntry.NAME);
					break;
				case CaseIndexDto.SURVEILLANCE_OFFICER_UUID:
					expression = surveillanceOfficer.get(User.UUID);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(root.get(Case.CHANGE_DATE)));
		}
	}

	public String getLastReportedDistrictName(CaseCriteria caseCriteria, String userUuid) {
		User user = userService.getByUuid(userUuid);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> caze = cq.from(Case.class);
		Join<Case, District> district = caze.join(Case.DISTRICT, JoinType.LEFT);

		Predicate filter = caseService.createUserFilter(cb, cq, caze, user);

		filter = AbstractAdoService.and(cb, filter, caseService.buildCriteriaFilter(caseCriteria, cb, caze));

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(district.get(District.NAME));
		cq.orderBy(cb.desc(caze.get(Case.REPORT_DATE)));

		TypedQuery<String> query = em.createQuery(cq).setMaxResults(1);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return "";
		}
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

		validate(dto);

		caze = fillOrBuildEntity(dto, caze);

		// Set version number on a new case
		if (existingCaseDto == null && StringUtils.isEmpty(dto.getCreationVersion())) {
			caze.setCreationVersion(InfoProvider.get().getVersion());
		}

		caseService.ensurePersisted(caze);
		onCaseChanged(existingCaseDto, caze);

		return toDto(caze);
	}

	@Override
	public void validate(CaseDataDto caze) throws ValidationRuntimeException {
		// Check whether any required field that does not have a not null constraint in
		// the database is empty
		if (caze.getRegion() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validRegion));
		}
		if (caze.getDistrict() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validDistrict));
		}
		if ((caze.getCaseOrigin() == null || caze.getCaseOrigin() == CaseOrigin.IN_COUNTRY) && caze.getHealthFacility() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validFacility));
		}
		if (CaseOrigin.POINT_OF_ENTRY.equals(caze.getCaseOrigin()) && caze.getPointOfEntry() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validPointOfEntry));
		}
		if (caze.getDisease() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validDisease));
		}
		// Check whether there are any infrastructure errors
		if (!districtFacade.getDistrictByUuid(caze.getDistrict().getUuid()).getRegion().equals(caze.getRegion())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noDistrictInRegion));
		}
		if (caze.getCommunity() != null
				&& !communityFacade.getByUuid(caze.getCommunity().getUuid()).getDistrict().equals(caze.getDistrict())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noCommunityInDistrict));
		}
		if (caze.getHealthFacility() != null) {
			if (caze.getCommunity() == null
					&& facilityFacade.getByUuid(caze.getHealthFacility().getUuid()).getDistrict() != null
					&& !facilityFacade.getByUuid(caze.getHealthFacility().getUuid()).getDistrict()
					.equals(caze.getDistrict())) {
				throw new ValidationRuntimeException(
						I18nProperties.getValidationError(Validations.noFacilityInDistrict));
			}
			if (caze.getCommunity() != null
					&& facilityFacade.getByUuid(caze.getHealthFacility().getUuid()).getCommunity() != null
					&& !caze.getCommunity()
					.equals(facilityFacade.getByUuid(caze.getHealthFacility().getUuid()).getCommunity())) {
				throw new ValidationRuntimeException(
						I18nProperties.getValidationError(Validations.noFacilityInCommunity));
			}
			if (facilityFacade.getByUuid(caze.getHealthFacility().getUuid()).getRegion() != null && !caze.getRegion()
					.equals(facilityFacade.getByUuid(caze.getHealthFacility().getUuid()).getRegion())) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noFacilityInRegion));
			}
		}
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

		// Generate epid number if missing or incomplete
		if (!CaseLogic.isCompleteEpidNumber(newCase.getEpidNumber())) {
			newCase.setEpidNumber(generateEpidNumber(newCase));
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

		// Re-assign the tasks associated with this case to the new officer (if
		// selected) or the surveillance supervisor if the facility has changed
		if (existingCase != null && newCase.getHealthFacility() != null && existingCase.getHealthFacility() != null
				&& !newCase.getHealthFacility().getUuid().equals(existingCase.getHealthFacility().getUuid())) {
			for (Task task : newCase.getTasks()) {
				if (task.getTaskStatus() != TaskStatus.PENDING) {
					continue;
				}

				if (newCase.getSurveillanceOfficer() != null) {
					task.setAssigneeUser(newCase.getSurveillanceOfficer());
				} else {
					List<User> supervisors = userService.getAllByRegionAndUserRoles(newCase.getRegion(),
							UserRole.SURVEILLANCE_SUPERVISOR);
					if (supervisors.size() >= 1) {
						task.setAssigneeUser(supervisors.get(0));
					} else {
						task.setAssigneeUser(null);
					}
				}

				taskService.ensurePersisted(task);
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
				List<PathogenTestDto> sampleTests = pathogenTestService.getAllByCase(newCase).stream()
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

		// Set Yes/No/Unknown fields associated with embedded lists to Yes if the lists are not empty
		if (!newCase.getHospitalization().getPreviousHospitalizations().isEmpty() && YesNoUnknown.YES != newCase.getHospitalization().getHospitalizedPreviously()) {
			newCase.getHospitalization().setHospitalizedPreviously(YesNoUnknown.YES);
		}
		if (!newCase.getEpiData().getBurials().isEmpty() && YesNoUnknown.YES != newCase.getEpiData().getBurialAttended()) {
			newCase.getEpiData().setBurialAttended(YesNoUnknown.YES);
		}
		if (!newCase.getEpiData().getTravels().isEmpty() && YesNoUnknown.YES != newCase.getEpiData().getTraveled()) {
			newCase.getEpiData().setTraveled(YesNoUnknown.YES);
		}
		if (!newCase.getEpiData().getGatherings().isEmpty() && YesNoUnknown.YES != newCase.getEpiData().getGatheringAttended()) {
			newCase.getEpiData().setGatheringAttended(YesNoUnknown.YES);
		}

		// Update completeness value
		newCase.setCompleteness(calculateCompleteness(newCase));

		// Send an email to all responsible supervisors when the case classification has
		// changed
		if (existingCase != null && existingCase.getCaseClassification() != newCase.getCaseClassification()) {
			List<User> messageRecipients = userService.getAllByRegionAndUserRoles(newCase.getRegion(),
					UserRole.SURVEILLANCE_SUPERVISOR, UserRole.CASE_SUPERVISOR, UserRole.CONTACT_SUPERVISOR);
			for (User recipient : messageRecipients) {
				try {
					messagingService.sendMessage(recipient,
							I18nProperties.getString(MessagingService.SUBJECT_CASE_CLASSIFICATION_CHANGED),
							String.format(
									I18nProperties.getString(MessagingService.CONTENT_CASE_CLASSIFICATION_CHANGED),
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

		// Send an email to all responsible supervisors when the disease of an
		// Unspecified VHF case has changed
		if (existingCase != null && existingCase.getDisease() == Disease.UNSPECIFIED_VHF
				&& existingCase.getDisease() != newCase.getDisease()) {
			List<User> messageRecipients = userService.getAllByRegionAndUserRoles(newCase.getRegion(),
					UserRole.SURVEILLANCE_SUPERVISOR, UserRole.CASE_SUPERVISOR, UserRole.CONTACT_SUPERVISOR);
			for (User recipient : messageRecipients) {
				try {
					messagingService.sendMessage(recipient,
							I18nProperties.getString(MessagingService.SUBJECT_DISEASE_CHANGED),
							String.format(I18nProperties.getString(MessagingService.CONTENT_DISEASE_CHANGED),
									DataHelper.getShortUuid(newCase.getUuid()), existingCase.getDisease().toString(),
									newCase.getDisease().toString()),
							MessageType.EMAIL, MessageType.SMS);
				} catch (NotificationDeliveryFailedException e) {
					logger.error(String.format(
							"NotificationDeliveryFailedException when trying to notify supervisors about the change of a case disease. "
									+ "Failed to send " + e.getMessageType() + " to user with UUID %s.",
									recipient.getUuid()));
				}
			}
		}
	}

	private float calculateCompleteness(Case caze) {
		float completeness = 0f;

		if (InvestigationStatus.DONE.equals(caze.getInvestigationStatus())) {
			completeness += 0.2f;
		}
		if (!CaseClassification.NOT_CLASSIFIED.equals(caze.getCaseClassification())) {
			completeness += 0.2f;
		}
		if (sampleService.getSampleCountByCase(caze) > 0) {
			completeness += 0.15f;
		}
		if (Boolean.TRUE.equals(caze.getSymptoms().getSymptomatic())) {
			completeness += 0.15f;
		}
		if (contactService.getContactCountByCase(caze) > 0) {
			completeness += 0.10f;
		}
		if (!CaseOutcome.NO_OUTCOME.equals(caze.getOutcome())) {
			completeness += 0.05f;
		}
		if (caze.getPerson().getBirthdateYYYY() != null || caze.getPerson().getApproximateAge() != null) {
			completeness += 0.05f;
		}
		if (caze.getPerson().getSex() != null) {
			completeness += 0.05f;
		}
		if (caze.getSymptoms().getOnsetDate() != null) {
			completeness += 0.05f;
		}

		return completeness;
	}

	@Override
	public String generateEpidNumber(CaseReferenceDto caze) {
		return generateEpidNumber(caseService.getByReferenceDto(caze));
	}

	public String generateEpidNumber(Case caze) {
		String newEpidNumber = caze.getEpidNumber();

		if (!CaseLogic.isEpidNumberPrefix(caze.getEpidNumber())) {
			// Generate a completely new epid number if the prefix is not complete or
			// doesn't match the pattern
			Calendar calendar = Calendar.getInstance();
			String year = String.valueOf(calendar.get(Calendar.YEAR)).substring(2);
			newEpidNumber = (caze.getRegion().getEpidCode() != null ? caze.getRegion().getEpidCode() : "") + "-"
					+ (caze.getDistrict().getEpidCode() != null ? caze.getDistrict().getEpidCode() : "") + "-" + year
					+ "-";
		}

		// Generate a suffix number
		String highestEpidNumber = caseService.getHighestEpidNumber(newEpidNumber);
		if (highestEpidNumber == null || highestEpidNumber.endsWith("-")) {
			// If there is not yet a case with a suffix for this epid number in the
			// database, use 01
			newEpidNumber = newEpidNumber + "01";
		} else {
			// Otherwise, extract the suffix from the highest existing epid number and
			// increase it by 1
			String suffixString = highestEpidNumber.substring(highestEpidNumber.lastIndexOf('-'));
			// Remove all non-digits from the suffix to ignore earlier input errors
			suffixString = suffixString.replaceAll("[^\\d]", "");
			if (suffixString.isEmpty()) {
				// If the suffix is empty now, that means there is not yet an epid number with a
				// suffix containing numbers
				newEpidNumber = newEpidNumber + "01";
			} else {
				int suffix = Integer.valueOf(suffixString);
				if (suffix < 9) {
					newEpidNumber = newEpidNumber + "0" + (++suffix);
				} else {
					newEpidNumber = newEpidNumber + (++suffix);
				}
			}
		}

		return newEpidNumber;
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
		if (newCase.getPerson().getApproximateAge() != null) {
			if (existingCase == null || CaseLogic.getStartDate(existingCase.getSymptoms().getOnsetDate(),
					existingCase.getReportDate()) != CaseLogic.getStartDate(newCase.getSymptoms().getOnsetDate(),
							newCase.getReportDate())) {
				if (newCase.getPerson().getApproximateAgeType() == ApproximateAgeType.MONTHS) {
					newCase.setCaseAge(0);
				} else {
					Date personChangeDate = newCase.getPerson().getChangeDate();
					Date referenceDate = CaseLogic.getStartDate(newCase.getSymptoms().getOnsetDate(),
							newCase.getReportDate());
					newCase.setCaseAge(newCase.getPerson().getApproximateAge()
							- DateHelper.getYearsBetween(referenceDate, personChangeDate));
					if (newCase.getCaseAge() < 0) {
						newCase.setCaseAge(0);
					}
				}

			}
		}
	}

	/**
	 * Updates the Hospitalization of the given Case when its Health Facility has
	 * changed and adds a PreviousHospitalization with the information of the
	 * current Hospitalization. DOES NOT update or save the existing Case in the
	 * database, only manipulates the Case delivered as a parameter.
	 */
	@Override
	public CaseDataDto saveAndTransferCase(CaseDataDto caze) {
		Case existingCase = caseService.getByUuid(caze.getUuid());

		// Only update Hospitalization when Health Facility has been changed
		if (!existingCase.getHealthFacility().getUuid().equals(caze.getHealthFacility().getUuid())) {
			caze.getHospitalization().getPreviousHospitalizations().add(HospitalizationFacadeEjbLocal.toDto(
					previousHospitalizationService.buildPreviousHospitalizationFromHospitalization(existingCase)));
			caze.getHospitalization().setHospitalizedPreviously(YesNoUnknown.YES);
			caze.getHospitalization().setAdmissionDate(new Date());
			caze.getHospitalization().setDischargeDate(null);
			caze.getHospitalization().setIsolated(null);
		}

		return saveCase(caze);
	}

	@Override
	public void deleteCase(CaseReferenceDto caseRef, String userUuid) {
		User user = userService.getByUuid(userUuid);
		if (!user.getUserRoles().contains(UserRole.ADMIN)) {
			throw new UnsupportedOperationException("Only admins are allowed to delete entities.");
		}

		Case caze = caseService.getByReferenceDto(caseRef);

		// Delete all contacts associated with this case
		List<Contact> contacts = contactService.getAllByCase(caze);
		for (Contact contact : contacts) {
			contactService.delete(contact);
		}
		contacts = contactService.getAllByResultingCase(caze);
		for (Contact contact : contacts) {
			contact.setResultingCase(null);
		}

		// Delete all samples associated with this case
		List<Sample> samples = sampleService.getAllByCase(caze);
		for (Sample sample : samples) {
			sampleService.delete(sample);
		}

		// Delete all tasks associated with this case
		List<Task> tasks = taskService.findBy(new TaskCriteria().caze(caseRef));
		for (Task task : tasks) {
			taskService.delete(task);
		}

		// Delete all prescriptions/treatments/clinical visits
		if (caze.getTherapy() != null) {
			TherapyReferenceDto therapy = new TherapyReferenceDto(caze.getTherapy().getUuid());
			treatmentService.findBy(new TreatmentCriteria().therapy(therapy)).stream()
			.forEach(t -> treatmentService.delete(t));
			prescriptionService.findBy(new PrescriptionCriteria().therapy(therapy)).stream()
			.forEach(p -> prescriptionService.delete(p));
		}
		if (caze.getClinicalCourse() != null) {
			ClinicalCourseReferenceDto clinicalCourse = new ClinicalCourseReferenceDto(
					caze.getClinicalCourse().getUuid());
			clinicalVisitService.findBy(new ClinicalVisitCriteria().clinicalCourse(clinicalCourse)).stream()
			.forEach(c -> clinicalVisitService.delete(c));
		}

		// Delete the case
		caseService.delete(caze);
	}

	@Override
	public void deleteCaseAsDuplicate(String caseUuid, String duplicateOfCaseUuid, String userUuid) {
		Case caze = caseService.getByUuid(caseUuid);
		Case duplicateOfCase = caseService.getByUuid(duplicateOfCaseUuid);
		caze.setDuplicateOf(duplicateOfCase);
		caseService.ensurePersisted(caze);

		deleteCase(new CaseReferenceDto(caseUuid), userUuid);
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
		if (source.getReportDate() != null) {
			target.setReportDate(source.getReportDate());
		} else { // make sure we do have a report date
			target.setReportDate(new Date());
		}
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setInvestigatedDate(source.getInvestigatedDate());
		target.setRegionLevelDate(source.getRegionLevelDate());
		target.setNationalLevelDate(source.getNationalLevelDate());
		target.setDistrictLevelDate(source.getDistrictLevelDate());
		target.setPerson(personService.getByReferenceDto(source.getPerson()));
		target.setCaseClassification(source.getCaseClassification());
		target.setClassificationUser(userService.getByReferenceDto(source.getClassificationUser()));
		target.setClassificationDate(source.getClassificationDate());
		target.setClassificationComment(source.getClassificationComment());
		target.setInvestigationStatus(source.getInvestigationStatus());
		target.setHospitalization(hospitalizationFacade.fromDto(source.getHospitalization()));
		target.setEpiData(epiDataFacade.fromDto(source.getEpiData()));
		if (source.getTherapy() == null) {
			source.setTherapy(TherapyDto.build());
		}
		target.setTherapy(therapyFacade.fromDto(source.getTherapy()));
		if (source.getClinicalCourse() == null) {
			source.setClinicalCourse(ClinicalCourseDto.build());
		}
		target.setClinicalCourse(clinicalCourseFacade.fromDto(source.getClinicalCourse()));
		if (source.getMaternalHistory() == null) {
			source.setMaternalHistory(MaternalHistoryDto.build());
		}
		target.setMaternalHistory(maternalHistoryFacade.fromDto(source.getMaternalHistory()));
		if (source.getPortHealthInfo() == null) {
			source.setPortHealthInfo(PortHealthInfoDto.build());
		}
		target.setPortHealthInfo(portHealthInfoFacade.fromDto(source.getPortHealthInfo()));

		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setCommunity(communityService.getByReferenceDto(source.getCommunity()));
		target.setHealthFacility(facilityService.getByReferenceDto(source.getHealthFacility()));
		target.setHealthFacilityDetails(source.getHealthFacilityDetails());

		target.setSurveillanceOfficer(userService.getByReferenceDto(source.getSurveillanceOfficer()));
		target.setClinicianName(source.getClinicianName());
		target.setClinicianPhone(source.getClinicianPhone());
		target.setClinicianEmail(source.getClinicianEmail());
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
		target.setSequelae(source.getSequelae());
		target.setSequelaeDetails(source.getSequelaeDetails());
		target.setNotifyingClinic(source.getNotifyingClinic());
		target.setNotifyingClinicDetails(source.getNotifyingClinicDetails());

		target.setCreationVersion(source.getCreationVersion());
		target.setCaseOrigin(source.getCaseOrigin());
		target.setPointOfEntry(pointOfEntryService.getByReferenceDto(source.getPointOfEntry()));
		target.setPointOfEntryDetails(source.getPointOfEntryDetails());

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
		if (source.getTherapy() != null) {
			target.setTherapy(TherapyFacadeEjb.toDto(source.getTherapy()));
		}
		if (source.getClinicalCourse() != null) {
			target.setClinicalCourse(ClinicalCourseFacadeEjb.toDto(source.getClinicalCourse()));
		}
		if (source.getMaternalHistory() != null) {
			target.setMaternalHistory(MaternalHistoryFacadeEjb.toDto(source.getMaternalHistory()));
		}
		if (source.getPortHealthInfo() != null) {
			target.setPortHealthInfo(PortHealthInfoFacadeEjb.toDto(source.getPortHealthInfo()));
		}

		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setCommunity(CommunityFacadeEjb.toReferenceDto(source.getCommunity()));
		target.setHealthFacility(FacilityFacadeEjb.toReferenceDto(source.getHealthFacility()));
		target.setHealthFacilityDetails(source.getHealthFacilityDetails());

		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setReportDate(source.getReportDate());
		target.setInvestigatedDate(source.getInvestigatedDate());
		target.setRegionLevelDate(source.getRegionLevelDate());
		target.setNationalLevelDate(source.getNationalLevelDate());
		target.setDistrictLevelDate(source.getDistrictLevelDate());

		target.setSurveillanceOfficer(UserFacadeEjb.toReferenceDto(source.getSurveillanceOfficer()));
		target.setClinicianName(source.getClinicianName());
		target.setClinicianPhone(source.getClinicianPhone());
		target.setClinicianEmail(source.getClinicianEmail());
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
		target.setSequelae(source.getSequelae());
		target.setSequelaeDetails(source.getSequelaeDetails());
		target.setNotifyingClinic(source.getNotifyingClinic());
		target.setNotifyingClinicDetails(source.getNotifyingClinicDetails());

		target.setCreationVersion(source.getCreationVersion());
		target.setCaseOrigin(source.getCaseOrigin());
		target.setPointOfEntry(PointOfEntryFacadeEjb.toReferenceDto(source.getPointOfEntry()));
		target.setPointOfEntryDetails(source.getPointOfEntryDetails());

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
			List<Task> pendingTasks = taskService.findBy(new TaskCriteria().taskType(TaskType.CASE_INVESTIGATION)
					.caze(caseRef).taskStatus(TaskStatus.PENDING));
			for (Task task : pendingTasks) {
				task.setTaskStatus(TaskStatus.REMOVED);
				task.setStatusChangeDate(new Date());
			}

			if (caze.getInvestigationStatus() == InvestigationStatus.DONE && existingCase != null
					&& existingCase.getInvestigationStatus() != InvestigationStatus.DONE) {
				sendInvestigationDoneNotifications(caze);
			}
		} else {
			// Remove the investigation date
			caze.setInvestigatedDate(null);

			// Create a new investigation task if none is present
			long pendingCount = taskService.getCount(new TaskCriteria().taskType(TaskType.CASE_INVESTIGATION)
					.caze(caseRef).taskStatus(TaskStatus.PENDING));

			if (pendingCount == 0) {
				createInvestigationTask(caze);
			}
		}
	}

	public void updateInvestigationByTask(Case caze) {
		CaseReferenceDto caseRef = caze.toReference();

		// any pending case investigation task?
		long pendingCount = taskService.getCount(
				new TaskCriteria().taskType(TaskType.CASE_INVESTIGATION).caze(caseRef).taskStatus(TaskStatus.PENDING));

		if (pendingCount > 0) {
			// set status to investigation pending
			caze.setInvestigationStatus(InvestigationStatus.PENDING);
			// .. and clear date
			caze.setInvestigatedDate(null);
		} else {
			// get "case investigation" task created last
			List<Task> cazeTasks = taskService
					.findBy(new TaskCriteria().taskType(TaskType.CASE_INVESTIGATION).caze(caseRef));

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

		Join<Case, Region> regionJoin = from.join(Case.REGION, JoinType.LEFT);
		cq.groupBy(regionJoin);
		cq.multiselect(regionJoin, cb.count(from));
		List<Object[]> results = em.createQuery(cq).getResultList();

		Map<RegionDto, Long> resultMap = results.stream()
				.collect(Collectors.toMap(e -> RegionFacadeEjb.toDto((Region) e[0]), e -> (Long) e[1]));
		return resultMap;
	}

	@Override
	public boolean doesEpidNumberExist(String epidNumber, String caseUuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Case> from = cq.from(Case.class);

		Predicate filter = cb.equal(from.get(Case.EPID_NUMBER), epidNumber);
		if (caseUuid != null) {
			filter = cb.and(filter, cb.notEqual(from.get(Case.UUID), caseUuid));
		}
		cq.where(filter);
		cq.select(cb.count(from));
		return em.createQuery(cq).getSingleResult() > 0;
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

		Join<Case, District> districtJoin = from.join(Case.DISTRICT, JoinType.LEFT);
		cq.groupBy(districtJoin);
		cq.multiselect(districtJoin, cb.count(from));
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
						I18nProperties.getString(MessagingService.SUBJECT_CASE_INVESTIGATION_DONE),
						String.format(I18nProperties.getString(MessagingService.CONTENT_CASE_INVESTIGATION_DONE),
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
		List<Object> filterBuilderParameters = new ArrayList<Object>();

		if (CollectionUtils.isNotEmpty(caseCriteria.getOnsetYears())) {
			extendFilterBuilderWithDateElement(filterBuilder, filterBuilderParameters, "YEAR", Symptoms.TABLE_NAME,
					Symptoms.ONSET_DATE, caseCriteria.getOnsetYears(), dateValue -> (dateValue.getValue()));
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getOnsetQuarters())) {
			extendFilterBuilderWithDateElement(filterBuilder, filterBuilderParameters, "QUARTER", Symptoms.TABLE_NAME,
					Symptoms.ONSET_DATE, caseCriteria.getOnsetQuarters(), dateValue -> (dateValue.getValue()));
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getOnsetMonths())) {
			extendFilterBuilderWithDateElement(filterBuilder, filterBuilderParameters, "MONTH", Symptoms.TABLE_NAME,
					Symptoms.ONSET_DATE, caseCriteria.getOnsetMonths(), dateValue -> (dateValue.ordinal() + 1));
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getOnsetEpiWeeks())) {
			extendFilterBuilderWithEpiWeek(filterBuilder, filterBuilderParameters, Symptoms.TABLE_NAME,
					Symptoms.ONSET_DATE, caseCriteria.getOnsetEpiWeeks(), value -> value.getWeek());
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getOnsetQuartersOfYear())) {
			extendFilterBuilderWithQuarterOfYear(filterBuilder, filterBuilderParameters, Symptoms.TABLE_NAME,
					Symptoms.ONSET_DATE, caseCriteria.getOnsetQuartersOfYear(),
					value -> value.getYear().getValue() * 10 + value.getQuarter().getValue());
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getOnsetMonthsOfYear())) {
			extendFilterBuilderWithMonthOfYear(filterBuilder, filterBuilderParameters, Symptoms.TABLE_NAME,
					Symptoms.ONSET_DATE, caseCriteria.getOnsetMonthsOfYear(),
					value -> value.getYear().getValue() * 100 + (value.getMonth().ordinal() + 1));
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getOnsetEpiWeeksOfYear())) {
			extendFilterBuilderWithEpiWeekOfYear(filterBuilder, filterBuilderParameters, Symptoms.TABLE_NAME,
					Symptoms.ONSET_DATE, caseCriteria.getOnsetEpiWeeksOfYear(),
					value -> value.getYear() * 100 + value.getWeek());
		}

		if (caseCriteria.getOnsetDateFrom() != null || caseCriteria.getOnsetDateTo() != null) {
			extendFilterBuilderWithDate(filterBuilder, filterBuilderParameters, caseCriteria.getOnsetDateFrom(),
					caseCriteria.getOnsetDateTo(), Symptoms.TABLE_NAME, Symptoms.ONSET_DATE);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getReportYears())) {
			extendFilterBuilderWithDateElement(filterBuilder, filterBuilderParameters, "YEAR", Case.TABLE_NAME,
					Case.REPORT_DATE, caseCriteria.getReportYears(), dateValue -> (dateValue.getValue()));
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getReportQuarters())) {
			extendFilterBuilderWithDateElement(filterBuilder, filterBuilderParameters, "QUARTER", Case.TABLE_NAME,
					Case.REPORT_DATE, caseCriteria.getReportQuarters(), dateValue -> (dateValue.getValue()));
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getReportMonths())) {
			extendFilterBuilderWithDateElement(filterBuilder, filterBuilderParameters, "MONTH", Case.TABLE_NAME,
					Case.REPORT_DATE, caseCriteria.getReportMonths(), dateValue -> (dateValue.ordinal() + 1));
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getReportEpiWeeks())) {
			extendFilterBuilderWithEpiWeek(filterBuilder, filterBuilderParameters, Case.TABLE_NAME, Case.REPORT_DATE,
					caseCriteria.getReportEpiWeeks(), value -> value.getWeek());
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getReportQuartersOfYear())) {
			extendFilterBuilderWithQuarterOfYear(filterBuilder, filterBuilderParameters, Case.TABLE_NAME,
					Case.REPORT_DATE, caseCriteria.getReportQuartersOfYear(),
					value -> value.getYear().getValue() * 10 + value.getQuarter().getValue());
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getReportMonthsOfYear())) {
			extendFilterBuilderWithMonthOfYear(filterBuilder, filterBuilderParameters, Case.TABLE_NAME,
					Case.REPORT_DATE, caseCriteria.getReportMonthsOfYear(),
					value -> value.getYear().getValue() * 100 + (value.getMonth().ordinal() + 1));
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getReportEpiWeeksOfYear())) {
			extendFilterBuilderWithEpiWeekOfYear(filterBuilder, filterBuilderParameters, Case.TABLE_NAME,
					Case.REPORT_DATE, caseCriteria.getReportEpiWeeksOfYear(),
					value -> value.getYear() * 100 + value.getWeek());
		}

		if (caseCriteria.getReportDateFrom() != null || caseCriteria.getReportDateTo() != null) {
			extendFilterBuilderWithDate(filterBuilder, filterBuilderParameters, caseCriteria.getReportDateFrom(),
					caseCriteria.getReportDateTo(), Case.TABLE_NAME, Case.REPORT_DATE);
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getSexes()) || caseCriteria.isSexUnknown() != null) {
			if (filterBuilder.length() > 0) {
				filterBuilder.append(" AND ");
			}

			filterBuilder.append("(");
			StringBuilder subFilterBuilder = new StringBuilder();

			if (CollectionUtils.isNotEmpty(caseCriteria.getSexes())) {
				extendFilterBuilderWithSimpleValue(subFilterBuilder, filterBuilderParameters, Person.TABLE_NAME,
						Person.SEX, caseCriteria.getSexes(), entry -> entry.name());
			}

			if (caseCriteria.isSexUnknown() != null) {
				if (subFilterBuilder.length() > 0) {
					subFilterBuilder.append(" OR ");
				}
				subFilterBuilder.append(Person.TABLE_NAME).append(".").append(Person.SEX).append(" IS ")
				.append(caseCriteria.isSexUnknown() == true ? "NULL" : "NOT NULL");
			}

			filterBuilder.append(subFilterBuilder);
			filterBuilder.append(")");
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getAgeIntervals())) {
			if (filterBuilder.length() > 0) {
				filterBuilder.append(" AND ");
			}

			filterBuilder.append("(");
			StringBuilder subFilterBuilder = new StringBuilder();

			Integer upperRangeBoundary = null;
			boolean appendUnknown = false;
			List<Integer> agesList = new ArrayList<Integer>();
			for (IntegerRange range : caseCriteria.getAgeIntervals()) {
				if (range.getTo() == null) {
					if (range.getFrom() == null) {
						appendUnknown = true;
					} else {
						upperRangeBoundary = range.getFrom();
					}
				} else {
					agesList.addAll(
							IntStream.rangeClosed(range.getFrom(), range.getTo()).boxed().collect(Collectors.toList()));
				}
			}

			if (agesList.size() > 0) {
				extendFilterBuilderWithSimpleValue(subFilterBuilder, filterBuilderParameters, Case.TABLE_NAME,
						Case.CASE_AGE, agesList, value -> value);
			}

			if (upperRangeBoundary != null) {
				if (subFilterBuilder.length() > 0) {
					subFilterBuilder.append(" OR ");
				}
				subFilterBuilder.append(Case.TABLE_NAME).append(".").append(Case.CASE_AGE).append(" >= ?")
				.append(filterBuilderParameters.size() + 1);
				filterBuilderParameters.add(upperRangeBoundary);
			}

			if (appendUnknown) {
				if (subFilterBuilder.length() > 0) {
					subFilterBuilder.append(" OR ");
				}
				subFilterBuilder.append(Case.TABLE_NAME).append(".").append(Case.CASE_AGE).append(" IS NULL");
			}

			filterBuilder.append(subFilterBuilder);
			filterBuilder.append(")");
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getDiseases())) {
			extendFilterBuilderWithSimpleValue(filterBuilder, filterBuilderParameters, Case.TABLE_NAME, Case.DISEASE,
					caseCriteria.getDiseases(), entry -> entry.name());
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getClassifications())) {
			extendFilterBuilderWithSimpleValue(filterBuilder, filterBuilderParameters, Case.TABLE_NAME,
					Case.CASE_CLASSIFICATION, caseCriteria.getClassifications(), entry -> entry.name());
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getOutcomes())) {
			extendFilterBuilderWithSimpleValue(filterBuilder, filterBuilderParameters, Case.TABLE_NAME, Case.OUTCOME,
					caseCriteria.getOutcomes(), entry -> entry.name());
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getRegions())) {
			extendFilterBuilderWithSimpleValue(filterBuilder, filterBuilderParameters, Region.TABLE_NAME, Region.UUID,
					caseCriteria.getRegions(), entry -> entry.getUuid());
		}

		if (CollectionUtils.isNotEmpty(caseCriteria.getDistricts())) {
			extendFilterBuilderWithSimpleValue(filterBuilder, filterBuilderParameters, District.TABLE_NAME,
					District.UUID, caseCriteria.getDistricts(), entry -> entry.getUuid());
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

		Query query = em.createNativeQuery(sqlBuilder.toString());
		for (int i = 0; i < filterBuilderParameters.size(); i++) {
			query.setParameter(i + 1, filterBuilderParameters.get(i));
		}
		if (groupingA == null && groupingB == null) {
			long result = ((Number) query.getSingleResult()).longValue();
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
			List<Object[]> results = (List<Object[]>) query.getResultList();
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

	private <T> StringBuilder appendInFilterValues(StringBuilder filterBuilder, List<Object> filterBuilderParameters,
			List<T> values, Function<T, ?> valueMapper) {

		filterBuilder.append("(");
		boolean first = true;
		for (T value : values) {
			if (first) {
				filterBuilder.append("?");
				first = false;
			} else {
				filterBuilder.append(",?");
			}
			filterBuilder.append(filterBuilderParameters.size() + 1);
			filterBuilderParameters.add(valueMapper.apply(value));
		}
		filterBuilder.append(")");
		return filterBuilder;
	}

	private <T> StringBuilder extendFilterBuilderWithSimpleValue(StringBuilder filterBuilder,
			List<Object> filterBuilderParameters, String tableName, String fieldName, List<T> values,
			Function<T, ?> valueMapper) {
		if (filterBuilder.length() > 0) {
			filterBuilder.append(" AND ");
		}

		filterBuilder.append(tableName).append(".").append(fieldName).append(" IN ");
		return appendInFilterValues(filterBuilder, filterBuilderParameters, values, valueMapper);
	}

	private StringBuilder extendFilterBuilderWithDate(StringBuilder filterBuilder, List<Object> filterBuilderParameters,
			Date from, Date to, String tableName, String fieldName) {

		if (from != null || to != null) {
			if (filterBuilder.length() > 0) {
				filterBuilder.append(" AND ");
			}

			if (from != null && to != null) {
				filterBuilder.append(tableName).append(".").append(fieldName).append(" BETWEEN ?")
				.append(filterBuilderParameters.size() + 1);
				filterBuilderParameters.add(from);
				filterBuilder.append(" AND ?").append(filterBuilderParameters.size() + 1).append("");
				filterBuilderParameters.add(to);
			} else if (from != null) {
				filterBuilder.append(tableName).append(".").append(fieldName).append(" >= ?")
				.append(filterBuilderParameters.size() + 1);
				filterBuilderParameters.add(from);
			} else {
				filterBuilder.append(tableName).append(".").append(fieldName).append(" <= ?")
				.append(filterBuilderParameters.size() + 1);
				filterBuilderParameters.add(to);
			}
		}

		return filterBuilder;
	}

	private <T> StringBuilder extendFilterBuilderWithDateElement(StringBuilder filterBuilder,
			List<Object> filterBuilderParameters, String dateElementToExtract, String tableName, String fieldName,
			List<T> values, Function<T, Integer> valueMapper) {
		if (filterBuilder.length() > 0) {
			filterBuilder.append(" AND ");
		}

		filterBuilder.append("(CAST(EXTRACT(" + dateElementToExtract + " FROM ").append(tableName).append(".")
		.append(fieldName).append(")  AS integer))").append(" IN ");
		return appendInFilterValues(filterBuilder, filterBuilderParameters, values, valueMapper);
	}

	private <T> StringBuilder extendFilterBuilderWithEpiWeek(StringBuilder filterBuilder,
			List<Object> filterBuilderParameters, String tableName, String fieldName, List<T> values,
			Function<T, Integer> valueMapper) {
		if (filterBuilder.length() > 0) {
			filterBuilder.append(" AND ");
		}

		filterBuilder.append("epi_week(").append(tableName).append(".").append(fieldName).append(")").append(" IN ");
		return appendInFilterValues(filterBuilder, filterBuilderParameters, values, valueMapper);
	}

	private <T> StringBuilder extendFilterBuilderWithEpiWeekOfYear(StringBuilder filterBuilder,
			List<Object> filterBuilderParameters, String tableName, String fieldName, List<T> values,
			Function<T, Integer> valueMapper) {
		if (filterBuilder.length() > 0) {
			filterBuilder.append(" AND ");
		}

		filterBuilder.append("(epi_year(").append(tableName).append(".").append(fieldName).append(")").append(" * 100")
		.append(" + epi_week(").append(tableName).append(".").append(fieldName).append("))").append(" IN ");
		return appendInFilterValues(filterBuilder, filterBuilderParameters, values, valueMapper);
	}

	private <T> StringBuilder extendFilterBuilderWithQuarterOfYear(StringBuilder filterBuilder,
			List<Object> filterBuilderParameters, String tableName, String fieldName, List<T> values,
			Function<T, Integer> valueMapper) {
		if (filterBuilder.length() > 0) {
			filterBuilder.append(" AND ");
		}

		filterBuilder.append("((CAST(EXTRACT(YEAR FROM ").append(tableName).append(".").append(fieldName).append(")")
		.append(" * 10) AS integer)) + (CAST(EXTRACT(QUARTER FROM ").append(tableName).append(".")
		.append(fieldName).append(") AS integer))").append(" IN ");
		return appendInFilterValues(filterBuilder, filterBuilderParameters, values, valueMapper);
	}

	private <T> StringBuilder extendFilterBuilderWithMonthOfYear(StringBuilder filterBuilder,
			List<Object> filterBuilderParameters, String tableName, String fieldName, List<T> values,
			Function<T, Integer> valueMapper) {
		if (filterBuilder.length() > 0) {
			filterBuilder.append(" AND ");
		}

		filterBuilder.append("((CAST(EXTRACT(YEAR FROM ").append(tableName).append(".").append(fieldName).append(")")
		.append(" * 100) AS integer)) + (CAST(EXTRACT(MONTH FROM ").append(tableName).append(".")
		.append(fieldName).append(") AS integer))").append(" IN ");
		return appendInFilterValues(filterBuilder, filterBuilderParameters, values, valueMapper);
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

		// Workaround for probable bug in Eclipse Link/Postgre that throws a
		// NoResultException when trying to
		// query for a true Boolean result
		cq.where(cb.and(cb.equal(from.get(Case.ARCHIVED), true),
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
		groupingBuilder.append("(CAST(EXTRACT(" + dateToExtract + " FROM ").append(tableName).append(".")
		.append(fieldName).append(") AS integer)) AS ").append(groupAlias);
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
		groupingBuilder.append("((CAST(EXTRACT(YEAR FROM ").append(tableName).append(".").append(fieldName)
		.append(") * 10) AS integer))").append(" + (CAST(EXTRACT(QUARTER FROM ").append(tableName).append(".")
		.append(fieldName).append(") AS integer)) AS ").append(groupAlias);
	}

	private void extendGroupingBuilderWithMonthOfYear(StringBuilder groupingBuilder, String tableName, String fieldName,
			String groupAlias) {
		groupingBuilder.append("((CAST(EXTRACT(YEAR FROM ").append(tableName).append(".").append(fieldName)
		.append(") * 100) AS integer))").append(" + (CAST(EXTRACT(MONTH FROM ").append(tableName).append(".")
		.append(fieldName).append(") AS integer)) AS ").append(groupAlias);
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

	@Override
	public void mergeCase(String leadUuid, String otherUuid) {

		// 1 Merge Dtos
		// 1.1 Case
		CaseDataDto leadCaseData = getCaseDataByUuid(leadUuid);
		CaseDataDto otherCaseData = getCaseDataByUuid(otherUuid);

		CaseDataDto mergedCase = DtoHelper.mergeDto(leadCaseData, otherCaseData);

		saveCase(mergedCase);

		// 1.2 Person
		PersonDto leadPerson = personFacade.getPersonByUuid(leadCaseData.getPerson().getUuid());
		PersonDto otherPerson = personFacade.getPersonByUuid(otherCaseData.getPerson().getUuid());

		PersonDto mergedPerson = DtoHelper.mergeDto(leadPerson, otherPerson);

		personFacade.savePerson(mergedPerson);

		// 2 Change CaseReference
		Case leadCase = caseService.getByUuid(leadUuid);
		Case otherCase = caseService.getByUuid(otherUuid);

		// 2.1 Contacts

		List<Contact> contacts = contactService.getAllByCase(otherCase);

		for (Contact contact : contacts) {

			contact.setCaze(leadCase);
			contactService.ensurePersisted(contact);
		}

		// 2.2 Samples
		List<Sample> samples = sampleService.getAllByCase(otherCase);

		for (Sample sample : samples) {

			sample.setAssociatedCase(leadCase);
			sampleService.ensurePersisted(sample);
		}

		// 2.3 Tasks
		List<Task> tasks = taskService.findBy(new TaskCriteria().caze(new CaseReferenceDto(otherCase.getUuid())));
		for (Task task : tasks) {

			task.setCaze(leadCase);
			taskService.ensurePersisted(task);
		}

		// 3 Change Therapy Reference
		// 3.1 Treatments
		List<Treatment> treatments = treatmentService.findBy(new TreatmentCriteria().therapy(new TherapyReferenceDto(otherCase.getTherapy().getUuid())));

		for (Treatment treatment : treatments) {

			treatment.setTherapy(leadCase.getTherapy());
			treatmentService.ensurePersisted(treatment);
		}

		// 3.2 Prescriptions
		List<Prescription> prescriptions = prescriptionService.findBy(new PrescriptionCriteria().therapy(new TherapyReferenceDto(otherCase.getTherapy().getUuid())));

		for (Prescription prescription : prescriptions) {

			prescription.setTherapy(leadCase.getTherapy());
			prescriptionService.ensurePersisted(prescription);
		}

		// 4 Change Clinical Course Reference
		// 4.1 Clinical Visits
		List<ClinicalVisit> clinicalVisits = clinicalVisitService.findBy(new ClinicalVisitCriteria().clinicalCourse(new ClinicalCourseReferenceDto(otherCase.getClinicalCourse().getUuid())));

		for (ClinicalVisit clinicalVisit : clinicalVisits) {

			clinicalVisit.setClinicalCourse(leadCase.getClinicalCourse());
			clinicalVisitService.ensurePersisted(clinicalVisit);
		}
	}

	@LocalBean
	@Stateless
	public static class CaseFacadeEjbLocal extends CaseFacadeEjb {
	}

}
