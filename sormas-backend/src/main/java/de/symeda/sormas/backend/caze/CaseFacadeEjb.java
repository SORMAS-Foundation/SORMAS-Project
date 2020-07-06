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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.caze;

import static de.symeda.sormas.backend.util.DtoHelper.fillDto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.CaseMeasure;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.caze.CaseExportType;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseIndexDetailedDto;
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
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.epidata.EpiDataTravelHelper;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.CauseOfDeath;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskHelper;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.therapy.PrescriptionCriteria;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.therapy.TherapyReferenceDto;
import de.symeda.sormas.api.therapy.TreatmentCriteria;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.user.UserRight;
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
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourseFacadeEjb;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourseFacadeEjb.ClinicalCourseFacadeEjbLocal;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisit;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisitFacadeEjb;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisitFacadeEjb.ClinicalVisitFacadeEjbLocal;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisitService;
import de.symeda.sormas.backend.clinicalcourse.HealthConditions;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.common.MessageType;
import de.symeda.sormas.backend.common.MessagingService;
import de.symeda.sormas.backend.common.NotificationDeliveryFailedException;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.epidata.EpiDataFacadeEjb;
import de.symeda.sormas.backend.epidata.EpiDataFacadeEjb.EpiDataFacadeEjbLocal;
import de.symeda.sormas.backend.epidata.EpiDataTravel;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.hospitalization.Hospitalization;
import de.symeda.sormas.backend.hospitalization.HospitalizationFacadeEjb;
import de.symeda.sormas.backend.hospitalization.HospitalizationFacadeEjb.HospitalizationFacadeEjbLocal;
import de.symeda.sormas.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.PointOfEntryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.PointOfEntryService;
import de.symeda.sormas.backend.infrastructure.PopulationDataFacadeEjb.PopulationDataFacadeEjbLocal;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.outbreak.OutbreakService;
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
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.sample.PathogenTest;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestService;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb.SymptomsFacadeEjbLocal;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.therapy.Prescription;
import de.symeda.sormas.backend.therapy.PrescriptionFacadeEjb;
import de.symeda.sormas.backend.therapy.PrescriptionFacadeEjb.PrescriptionFacadeEjbLocal;
import de.symeda.sormas.backend.therapy.PrescriptionService;
import de.symeda.sormas.backend.therapy.TherapyFacadeEjb;
import de.symeda.sormas.backend.therapy.TherapyFacadeEjb.TherapyFacadeEjbLocal;
import de.symeda.sormas.backend.therapy.Treatment;
import de.symeda.sormas.backend.therapy.TreatmentFacadeEjb;
import de.symeda.sormas.backend.therapy.TreatmentFacadeEjb.TreatmentFacadeEjbLocal;
import de.symeda.sormas.backend.therapy.TreatmentService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserRoleConfigFacadeEjb.UserRoleConfigFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.PseudonymizationService;

@Stateless(name = "CaseFacade")
public class CaseFacadeEjb implements CaseFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private CaseClassificationFacadeEjbLocal caseClassificationFacade;
	@EJB
	private CaseService caseService;
	@EJB
	private CaseListCriteriaBuilder listQueryBuilder;
	@EJB
	private PersonService personService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private UserService userService;
	@EJB
	private SymptomsFacadeEjbLocal symptomsFacade;
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
	private ContactService contactService;
	@EJB
	private EventParticipantService eventParticipantService;
	@EJB
	private SampleService sampleService;
	@EJB
	private PathogenTestService pathogenTestService;
	@EJB
	private PathogenTestFacadeEjbLocal sampleTestFacade;
	@EJB
	private HospitalizationFacadeEjbLocal hospitalizationFacade;
	@EJB
	private EpiDataFacadeEjbLocal epiDataFacade;
	@EJB
	private ContactFacadeEjbLocal contactFacade;
	@EJB
	private SampleFacadeEjbLocal sampleFacade;
	@EJB
	private TreatmentFacadeEjbLocal treatmentFacade;
	@EJB
	private PrescriptionFacadeEjbLocal prescriptionFacade;
	@EJB
	private ClinicalVisitFacadeEjbLocal clinicalVisitFacade;
	@EJB
	private MessagingService messagingService;
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
	private OutbreakService outbreakService;
	@EJB
	private MaternalHistoryFacadeEjbLocal maternalHistoryFacade;
	@EJB
	private PointOfEntryService pointOfEntryService;
	@EJB
	private PortHealthInfoFacadeEjbLocal portHealthInfoFacade;
	@EJB
	private UserRoleConfigFacadeEjbLocal userRoleConfigFacade;
	@EJB
	private PopulationDataFacadeEjbLocal populationDataFacade;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	private CaseJurisdictionChecker caseJurisdictionChecker;
	@EJB
	private PseudonymizationService pseudonymizationService;

	@Override
	public List<CaseDataDto> getAllActiveCasesAfter(Date date) {
		return getAllActiveCasesAfter(date, false);
	}

	@Override
	public List<CaseDataDto> getAllActiveCasesAfter(Date date, Boolean includeExtendedChangeDateFilters) {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}
		return caseService.getAllActiveCasesAfter(date, includeExtendedChangeDateFilters)
			.stream()
			.map(c -> convertToDto(c))
			.collect(Collectors.toList());
	}

	@Override
	public List<CaseDataDto> getByUuids(List<String> uuids) {
		return caseService.getByUuids(uuids).stream().map(c -> convertToDto(c)).collect(Collectors.toList());
	}

	@Override
	public String getUuidByUuidEpidNumberOrExternalId(String searchTerm) {
		return caseService.getUuidByUuidEpidNumberOrExternalId(searchTerm);
	}

	@Override
	public long count(CaseCriteria caseCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Case> root = cq.from(Case.class);
		Predicate filter = caseService.createUserFilter(cb, cq, root);

		if (caseCriteria != null) {
			Predicate criteriaFilter = caseService.createCriteriaFilter(caseCriteria, cb, cq, root);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		}
		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<CaseIndexDto> getIndexList(CaseCriteria caseCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaQuery<CaseIndexDto> cq = listQueryBuilder.buildIndexCriteria(caseCriteria, sortProperties);

		List<CaseIndexDto> cases;
		if (first != null && max != null) {
			cases = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			cases = em.createQuery(cq).getResultList();
		}

//		pseudonymizationService.pseudonymizeDtoCollection(
//			CaseIndexDto.class,
//			cases,
//			c -> caseJurisdictionChecker.isInJurisdiction(c.getJurisdiction()),
//			(c, isInJurisdiction) -> {
//				pseudonymizationService.pseudonymizeDto(AgeAndBirthDateDto.class, c.getAgeAndBirthDate(), isInJurisdiction, null);
//			});

		return cases;
	}

	@Override
	public List<CaseIndexDetailedDto> getIndexDetailedList(CaseCriteria caseCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaQuery<CaseIndexDetailedDto> cq = listQueryBuilder.buildIndexDetailedCriteria(caseCriteria, sortProperties);

		List<CaseIndexDetailedDto> cases;
		if (first != null && max != null) {
			cases = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			cases = em.createQuery(cq).getResultList();
		}

//		pseudonymizationService.pseudonymizeDtoCollection(
//			CaseIndexDetailedDto.class,
//			cases,
//			c -> caseJurisdictionChecker.isInJurisdiction(c.getJurisdiction()),
//			(c, isInJurisdiction) -> {
//				pseudonymizationService.pseudonymizeDto(AgeAndBirthDateDto.class, c.getAgeAndBirthDate(), isInJurisdiction, null);
//			});

		return cases;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<CaseExportDto> getExportList(
		CaseCriteria caseCriteria,
		CaseExportType exportType,
		int first,
		int max,
		ExportConfigurationDto exportConfiguration,
		Language userLanguage) {

		Boolean previousCaseManagementDataCriteria = caseCriteria.getMustHaveCaseManagementData();
		if (CaseExportType.CASE_MANAGEMENT == exportType) {
			caseCriteria.setMustHaveCaseManagementData(Boolean.TRUE);
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CaseExportDto> cq = cb.createQuery(CaseExportDto.class);
		Root<Case> caseRoot = cq.from(Case.class);

		CaseJoins<Case> joins = new CaseJoins<>(caseRoot);

		//@formatter:off
		cq.multiselect(caseRoot.get(Case.ID), joins.getPerson().get(Person.ID), joins.getPersonAddress().get(Location.ID),
				joins.getEpiData().get(EpiData.ID), joins.getSymptoms().get(Symptoms.ID), joins.getHospitalization().get(Hospitalization.ID),
				joins.getDistrict().get(District.ID), joins.getHealthConditions().get(HealthConditions.ID), caseRoot.get(Case.UUID),
				caseRoot.get(Case.EPID_NUMBER), caseRoot.get(Case.DISEASE), caseRoot.get(Case.DISEASE_DETAILS),
				joins.getPerson().get(Person.FIRST_NAME), joins.getPerson().get(Person.LAST_NAME), joins.getPerson().get(Person.SEX),
				caseRoot.get(Case.PREGNANT), joins.getPerson().get(Person.APPROXIMATE_AGE),
				joins.getPerson().get(Person.APPROXIMATE_AGE_TYPE), joins.getPerson().get(Person.BIRTHDATE_DD),
				joins.getPerson().get(Person.BIRTHDATE_MM), joins.getPerson().get(Person.BIRTHDATE_YYYY),
				caseRoot.get(Case.REPORT_DATE), joins.getReportingUser().get(User.UUID),
				joins.getRegion().get(Region.UUID), joins.getRegion().get(Region.NAME),
				joins.getDistrict().get(District.UUID), joins.getDistrict().get(District.NAME),
				joins.getCommunity().get(Community.UUID), joins.getCommunity().get(Community.NAME),
				joins.getFacility().get(Facility.NAME), joins.getFacility().get(Facility.UUID), caseRoot.get(Case.HEALTH_FACILITY_DETAILS),
				joins.getPointOfEntry().get(PointOfEntry.NAME), joins.getPointOfEntry().get(PointOfEntry.UUID), caseRoot.get(Case.POINT_OF_ENTRY_DETAILS),
				caseRoot.get(Case.CASE_CLASSIFICATION), caseRoot.get(Case.INVESTIGATION_STATUS), caseRoot.get(Case.OUTCOME),
				// quarantine
				caseRoot.get(Case.QUARANTINE), caseRoot.get(Case.QUARANTINE_TYPE_DETAILS), caseRoot.get(Case.QUARANTINE_FROM), caseRoot.get(Case.QUARANTINE_TO),
				caseRoot.get(Contact.QUARANTINE_ORDERED_VERBALLY),
				caseRoot.get(Contact.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT),
				caseRoot.get(Contact.QUARANTINE_ORDERED_VERBALLY_DATE),
				caseRoot.get(Contact.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT_DATE),

				joins.getHospitalization().get(Hospitalization.ADMITTED_TO_HEALTH_FACILITY), joins.getHospitalization().get(Hospitalization.ADMISSION_DATE),
				joins.getHospitalization().get(Hospitalization.DISCHARGE_DATE), joins.getHospitalization().get(Hospitalization.LEFT_AGAINST_ADVICE),
				joins.getPerson().get(Person.PRESENT_CONDITION), joins.getPerson().get(Person.DEATH_DATE), joins.getPerson().get(Person.BURIAL_DATE),
				joins.getPerson().get(Person.BURIAL_CONDUCTOR), joins.getPerson().get(Person.BURIAL_PLACE_DESCRIPTION),
				// address
				joins.getPersonAddressRegion().get(Region.NAME), joins.getPersonAddressDistrict().get(District.NAME),
				joins.getPersonAddress().get(Location.CITY), joins.getPersonAddress().get(Location.ADDRESS), joins.getPersonAddress().get(Location.POSTAL_CODE),
				// phone
				joins.getPerson().get(Person.PHONE), joins.getPerson().get(Person.PHONE_OWNER), joins.getPerson().get(Person.EDUCATION_TYPE),
				joins.getPerson().get(Person.EDUCATION_DETAILS), joins.getPerson().get(Person.OCCUPATION_TYPE),
				joins.getPerson().get(Person.OCCUPATION_DETAILS), joins.getOccupationFacility().get(Facility.NAME), joins.getOccupationFacility().get(Facility.UUID),
				joins.getPerson().get(Person.OCCUPATION_FACILITY_DETAILS), joins.getEpiData().get(EpiData.TRAVELED), joins.getEpiData().get(EpiData.BURIAL_ATTENDED),
				joins.getEpiData().get(EpiData.DIRECT_CONTACT_CONFIRMED_CASE), joins.getEpiData().get(EpiData.DIRECT_CONTACT_PROBABLE_CASE),
				joins.getEpiData().get(EpiData.RODENTS), caseRoot.get(Case.VACCINATION), caseRoot.get(Case.VACCINATION_DOSES),
				caseRoot.get(Case.VACCINATION_DATE), caseRoot.get(Case.VACCINATION_INFO_SOURCE),
				caseRoot.get(Case.POSTPARTUM), caseRoot.get(Case.TRIMESTER)
				);
		//@formatter:on

		cq.distinct(true);

		Predicate filter = caseService.createUserFilter(cb, cq, caseRoot);

		if (caseCriteria != null) {
			Predicate criteriaFilter = caseService.createCriteriaFilter(caseCriteria, cb, cq, caseRoot);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		/*
		 * Sort by report date DESC, but also by id for stable Sorting in case of equal report dates.
		 * Since this method supports paging, values might jump between pages when sorting is unstable.
		 */
		cq.orderBy(cb.desc(caseRoot.get(Case.REPORT_DATE)), cb.desc(caseRoot.get(Case.ID)));

		List<CaseExportDto> resultList =
			em.createQuery(cq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).setFirstResult(first).setMaxResults(max).getResultList();
		List<Long> resultCaseIds = resultList.stream().map(CaseExportDto::getId).collect(Collectors.toList());

		if (!resultList.isEmpty()) {

			Map<Long, Symptoms> symptoms = null;
			if (exportConfiguration == null || exportConfiguration.getProperties().contains(CaseDataDto.SYMPTOMS)) {
				List<Symptoms> symptomsList = null;
				CriteriaQuery<Symptoms> symptomsCq = cb.createQuery(Symptoms.class);
				Root<Symptoms> symptomsRoot = symptomsCq.from(Symptoms.class);
				Expression<String> symptomsIdsExpr = symptomsRoot.get(Symptoms.ID);
				symptomsCq.where(symptomsIdsExpr.in(resultList.stream().map(CaseExportDto::getSymptomsId).collect(Collectors.toList())));
				symptomsList = em.createQuery(symptomsCq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).getResultList();
				symptoms = symptomsList.stream().collect(Collectors.toMap(Symptoms::getId, Function.identity()));
			}

			Map<Long, Location> personAddresses = null;
			if (exportConfiguration == null
				|| exportConfiguration.getProperties().contains(PersonDto.ADDRESS)
				|| exportConfiguration.getProperties().contains(CaseExportDto.ADDRESS_GPS_COORDINATES)) {
				List<Location> personAddressesList = null;
				CriteriaQuery<Location> personAddressesCq = cb.createQuery(Location.class);
				Root<Location> personAddressesRoot = personAddressesCq.from(Location.class);
				Expression<String> personAddressesIdsExpr = personAddressesRoot.get(Location.ID);
				personAddressesCq
					.where(personAddressesIdsExpr.in(resultList.stream().map(CaseExportDto::getPersonAddressId).collect(Collectors.toList())));
				personAddressesList = em.createQuery(personAddressesCq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).getResultList();
				personAddresses = personAddressesList.stream().collect(Collectors.toMap(Location::getId, Function.identity()));
			}

			Map<Long, Integer> prescriptionCounts = null;
			Map<Long, Integer> treatmentCounts = null;
			Map<Long, Integer> clinicalVisitCounts = null;
			Map<Long, HealthConditions> healthConditions = null;
			if (exportType == null || exportType == CaseExportType.CASE_MANAGEMENT) {
				if (exportConfiguration == null || exportConfiguration.getProperties().contains(CaseExportDto.NUMBER_OF_PRESCRIPTIONS)) {
					prescriptionCounts = prescriptionService.getPrescriptionCountByCases(resultCaseIds)
						.stream()
						.collect(Collectors.toMap(e -> (Long) e[0], e -> ((Long) e[1]).intValue()));
				}
				if (exportConfiguration == null || exportConfiguration.getProperties().contains(CaseExportDto.NUMBER_OF_TREATMENTS)) {
					treatmentCounts = treatmentService.getTreatmentCountByCases(resultCaseIds)
						.stream()
						.collect(Collectors.toMap(e -> (Long) e[0], e -> ((Long) e[1]).intValue()));
				}
				if (exportConfiguration == null || exportConfiguration.getProperties().contains(CaseExportDto.NUMBER_OF_CLINICAL_VISITS)) {
					clinicalVisitCounts = clinicalVisitService.getClinicalVisitCountByCases(resultCaseIds)
						.stream()
						.collect(Collectors.toMap(e -> (Long) e[0], e -> ((Long) e[1]).intValue()));
				}
				if (exportConfiguration == null || exportConfiguration.getProperties().contains(ClinicalCourseDto.HEALTH_CONDITIONS)) {
					List<HealthConditions> healthConditionsList = null;
					CriteriaQuery<HealthConditions> healthConditionsCq = cb.createQuery(HealthConditions.class);
					Root<HealthConditions> healthConditionsRoot = healthConditionsCq.from(HealthConditions.class);
					Expression<String> healthConditionsIdsExpr = healthConditionsRoot.get(HealthConditions.ID);
					healthConditionsCq.where(
						healthConditionsIdsExpr.in(resultList.stream().map(CaseExportDto::getHealthConditionsId).collect(Collectors.toList())));
					healthConditionsList = em.createQuery(healthConditionsCq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).getResultList();
					healthConditions = healthConditionsList.stream().collect(Collectors.toMap(HealthConditions::getId, Function.identity()));
				}
			}

			Map<Long, PreviousHospitalization> firstPreviousHospitalizations = null;
			if (exportConfiguration == null || exportConfiguration.getProperties().contains(CaseExportDto.INITIAL_DETECTION_PLACE)) {
				List<PreviousHospitalization> prevHospsList = null;
				CriteriaQuery<PreviousHospitalization> prevHospsCq = cb.createQuery(PreviousHospitalization.class);
				Root<PreviousHospitalization> prevHospsRoot = prevHospsCq.from(PreviousHospitalization.class);
				Join<PreviousHospitalization, Hospitalization> prevHospsHospitalizationJoin =
					prevHospsRoot.join(PreviousHospitalization.HOSPITALIZATION, JoinType.LEFT);
				Expression<String> hospitalizationIdsExpr = prevHospsHospitalizationJoin.get(Hospitalization.ID);
				prevHospsCq
					.where(hospitalizationIdsExpr.in(resultList.stream().map(CaseExportDto::getHospitalizationId).collect(Collectors.toList())));
				prevHospsCq.orderBy(cb.asc(prevHospsRoot.get(PreviousHospitalization.ADMISSION_DATE)));
				prevHospsList = em.createQuery(prevHospsCq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).getResultList();
				firstPreviousHospitalizations = prevHospsList.stream()
					.collect(Collectors.toMap(p -> p.getHospitalization().getId(), Function.identity(), (id1, id2) -> {
						return id1;
					}));
			}

			Map<Long, CaseClassification> sourceCaseClassifications = null;
			if (exportConfiguration == null || exportConfiguration.getProperties().contains(CaseExportDto.MAX_SOURCE_CASE_CLASSIFICATION)) {
				sourceCaseClassifications = contactService.getSourceCaseClassifications(resultCaseIds)
					.stream()
					.collect(Collectors.toMap(e -> (Long) e[0], e -> (CaseClassification) e[1]));
			}

			List<Long> caseIdsWithOutbreak = null;
			if (exportConfiguration == null || exportConfiguration.getProperties().contains(CaseExportDto.ASSOCIATED_WITH_OUTBREAK)) {
				caseIdsWithOutbreak = outbreakService.getCaseIdsWithOutbreak(resultCaseIds);
			}

			Map<Long, List<EpiDataTravel>> travels = null;
			if ((exportType == null || exportType == CaseExportType.CASE_SURVEILLANCE)
				&& (exportConfiguration == null || exportConfiguration.getProperties().contains(CaseExportDto.TRAVEL_HISTORY))) {
				List<EpiDataTravel> travelsList = null;
				CriteriaQuery<EpiDataTravel> travelsCq = cb.createQuery(EpiDataTravel.class);
				Root<EpiDataTravel> travelsRoot = travelsCq.from(EpiDataTravel.class);
				Join<EpiDataTravel, EpiData> travelsEpiDataJoin = travelsRoot.join(EpiDataTravel.EPI_DATA, JoinType.LEFT);
				Expression<String> epiDataIdsExpr = travelsEpiDataJoin.get(EpiData.ID);
				travelsCq.where(epiDataIdsExpr.in(resultList.stream().map(CaseExportDto::getEpiDataId).collect(Collectors.toList())));
				travelsCq.orderBy(cb.asc(travelsEpiDataJoin.get(EpiData.ID)));
				travelsList = em.createQuery(travelsCq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).getResultList();
				travels = travelsList.stream().collect(Collectors.groupingBy(t -> t.getEpiData().getId()));
			}

			Map<Long, List<Sample>> samples = null;
			if ((exportType == null || exportType == CaseExportType.CASE_SURVEILLANCE)
				&& (exportConfiguration == null || exportConfiguration.getProperties().contains(CaseExportDto.SAMPLE_INFORMATION))) {
				List<Sample> samplesList = null;
				CriteriaQuery<Sample> samplesCq = cb.createQuery(Sample.class);
				Root<Sample> samplesRoot = samplesCq.from(Sample.class);
				Join<Sample, Case> samplesCaseJoin = samplesRoot.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);
				Expression<String> caseIdsExpr = samplesCaseJoin.get(Case.ID);
				samplesCq.where(caseIdsExpr.in(resultList.stream().map(CaseExportDto::getId).collect(Collectors.toList())));
				samplesList = em.createQuery(samplesCq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).getResultList();
				samples = samplesList.stream().collect(Collectors.groupingBy(s -> s.getAssociatedCase().getId()));
			}

			for (CaseExportDto exportDto : resultList) {
				if (exportConfiguration == null || exportConfiguration.getProperties().contains(CaseExportDto.COUNTRY)) {
					exportDto.setCountry(configFacade.getEpidPrefix());
				}
				if (symptoms != null) {
					Optional.ofNullable(symptoms.get(exportDto.getSymptomsId()))
						.ifPresent(symptom -> exportDto.setSymptoms(SymptomsFacadeEjb.toDto(symptom)));
				}
				if (personAddresses != null || exportConfiguration.getProperties().contains(CaseExportDto.ADDRESS_GPS_COORDINATES)) {
					Optional.ofNullable(personAddresses.get(exportDto.getPersonAddressId()))
						.ifPresent(personAddress -> exportDto.setAddressGpsCoordinates(personAddress.buildGpsCoordinatesCaption()));
				}
				if (prescriptionCounts != null) {
					Optional.ofNullable(prescriptionCounts.get(exportDto.getId()))
						.ifPresent(prescriptionCount -> exportDto.setNumberOfPrescriptions(prescriptionCount));
				}
				if (treatmentCounts != null) {
					Optional.ofNullable(treatmentCounts.get(exportDto.getId()))
						.ifPresent(treatmentCount -> exportDto.setNumberOfTreatments(treatmentCount));
				}
				if (clinicalVisitCounts != null) {
					Optional.ofNullable(clinicalVisitCounts.get(exportDto.getId()))
						.ifPresent(clinicalVisitCount -> exportDto.setNumberOfClinicalVisits(clinicalVisitCount));
				}
				if (healthConditions != null) {
					Optional.ofNullable(healthConditions.get(exportDto.getHealthConditionsId()))
						.ifPresent(healthCondition -> exportDto.setHealthConditions(ClinicalCourseFacadeEjb.toHealthConditionsDto(healthCondition)));
				}
				if (firstPreviousHospitalizations != null) {
					Optional.ofNullable(firstPreviousHospitalizations.get(exportDto.getHospitalizationId()))
						.ifPresent(
							firstPreviousHospitalization -> exportDto.setInitialDetectionPlace(
								FacilityHelper.buildFacilityString(
									firstPreviousHospitalization.getHealthFacility().getUuid(),
									firstPreviousHospitalization.getHealthFacility().getName(),
									firstPreviousHospitalization.getHealthFacilityDetails())));
					if (StringUtils.isEmpty(exportDto.getInitialDetectionPlace())) {
						if (!StringUtils.isEmpty(exportDto.getHealthFacility())) {
							exportDto.setInitialDetectionPlace(exportDto.getHealthFacility());
						} else {
							exportDto.setInitialDetectionPlace(exportDto.getPointOfEntry());
						}
					}
				}
				if (sourceCaseClassifications != null) {
					Optional.ofNullable(sourceCaseClassifications.get(exportDto.getId()))
						.ifPresent(sourceCaseClassification -> exportDto.setMaxSourceCaseClassification(sourceCaseClassification));
				}
				if (caseIdsWithOutbreak != null) {
					exportDto.setAssociatedWithOutbreak(caseIdsWithOutbreak.contains(exportDto.getId()));
				}
				if (travels != null) {
					Optional.ofNullable(travels.get(exportDto.getEpiDataId())).ifPresent(caseTravels -> {
						StringBuilder travelHistoryBuilder = new StringBuilder();
						caseTravels.forEach(travel -> {
							travelHistoryBuilder.append(
								EpiDataTravelHelper.buildTravelString(
									travel.getTravelType(),
									travel.getTravelDestination(),
									travel.getTravelDateFrom(),
									travel.getTravelDateTo(),
									userLanguage))
								.append(", ");
						});
						if (travelHistoryBuilder.length() > 0) {
							travelHistoryBuilder.delete(travelHistoryBuilder.lastIndexOf(", "), travelHistoryBuilder.length() - 1);
						}
						if (travelHistoryBuilder.length() == 0 && exportDto.getTraveled() != null) {
							travelHistoryBuilder.append(exportDto.getTraveled());
						}
						exportDto.setTravelHistory(travelHistoryBuilder.toString());
					});
				}
				if (samples != null) {
					Optional.ofNullable(samples.get(exportDto.getId())).ifPresent(caseSamples -> {
						int count = 0;
						for (Sample sample : caseSamples) {
							switch (++count) {
							case 1:
								exportDto.setSampleDateTime1(sample.getSampleDateTime());
								if (sample.getLab() != null) {
									exportDto.setSampleLab1(
										FacilityHelper
											.buildFacilityString(sample.getLab().getUuid(), sample.getLab().getName(), sample.getLabDetails()));
								}
								exportDto.setSampleResult1(sample.getPathogenTestResult());
								break;
							case 2:
								exportDto.setSampleDateTime2(sample.getSampleDateTime());
								if (sample.getLab() != null) {
									exportDto.setSampleLab2(
										FacilityHelper
											.buildFacilityString(sample.getLab().getUuid(), sample.getLab().getName(), sample.getLabDetails()));
								}
								exportDto.setSampleResult2(sample.getPathogenTestResult());
								break;
							case 3:
								exportDto.setSampleDateTime3(sample.getSampleDateTime());
								if (sample.getLab() != null) {
									exportDto.setSampleLab3(
										FacilityHelper
											.buildFacilityString(sample.getLab().getUuid(), sample.getLab().getName(), sample.getLabDetails()));
								}
								exportDto.setSampleResult3(sample.getPathogenTestResult());
								break;
							default:
								StringBuilder sb = new StringBuilder();
								if (!exportDto.getOtherSamples().isEmpty()) {
									sb.append(", ");
								}
								sb.append(DateHelper.formatDateForExport(sample.getSampleDateTime())).append(" (");
								if (sample.getLab() != null) {
									sb.append(
										FacilityHelper
											.buildFacilityString(sample.getLab().getUuid(), sample.getLab().getName(), sample.getLabDetails()))
										.append(", ");
								}
								sb.append(sample.getPathogenTestResult()).append(")");
								exportDto.setOtherSamples(exportDto.getOtherSamples() + sb.toString());
								break;
							}
						}
					});
				}
//				boolean inJurisdiction = caseJurisdictionChecker.isInJurisdiction(exportDto.getJurisdiction());
//				pseudonymizationService.pseudonymizeDto(CaseExportDto.class, exportDto, inJurisdiction, (c) -> {
//					pseudonymizationService.pseudonymizeDto(BirthDateDto.class, c.getBirthdate(), inJurisdiction, null);
//				});
			}
		}

		caseCriteria.setMustHaveCaseManagementData(previousCaseManagementDataCriteria);
		return resultList;
	}

	@Override
	public List<String> getAllActiveUuids() {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return caseService.getAllActiveUuids();
	}

	@Override
	public List<DashboardCaseDto> getCasesForDashboard(CaseCriteria caseCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardCaseDto> cq = cb.createQuery(DashboardCaseDto.class);
		Root<Case> caze = cq.from(Case.class);
		Join<Case, Symptoms> symptoms = caze.join(Case.SYMPTOMS, JoinType.LEFT);
		Join<Case, Person> person = caze.join(Case.PERSON, JoinType.LEFT);

		Predicate filter =
			caseService.createUserFilter(cb, cq, caze, new CaseUserFilterCriteria().excludeSharedCases(true).excludeCasesFromContacts(true));
		Predicate criteriaFilter = caseService.createCriteriaFilter(caseCriteria, cb, cq, caze);
		filter = AbstractAdoService.and(cb, filter, criteriaFilter);

		if (filter != null) {
			cq.where(filter);
		}

		List<DashboardCaseDto> result;
		if (filter != null) {
			cq.where(filter);
			cq.multiselect(
				caze.get(Case.ID),
				caze.get(Case.UUID),
				caze.get(Case.REPORT_DATE),
				symptoms.get(Symptoms.ONSET_DATE),
				caze.get(Case.CASE_CLASSIFICATION),
				caze.get(Case.DISEASE),
				caze.get(Case.INVESTIGATION_STATUS),
				person.get(Person.PRESENT_CONDITION),
				person.get(Person.CAUSE_OF_DEATH_DISEASE));

			result = em.createQuery(cq).getResultList();
		} else {
			result = Collections.emptyList();
		}

		return result;
	}

	@Override
	public List<MapCaseDto> getCasesForMap(RegionReferenceDto regionRef, DistrictReferenceDto districtRef, Disease disease, Date from, Date to) {

		Region region = regionService.getByReferenceDto(regionRef);
		District district = districtService.getByReferenceDto(districtRef);

		List<MapCaseDto> cases = caseService.getCasesForMap(region, district, disease, from, to);

//		pseudonymizationService.pseudonymizeDtoCollection(
//			MapCaseDto.class,
//			cases,
//			c -> caseJurisdictionChecker.isInJurisdiction(c.getJurisdiction()),
//			(c, isInJurisdiction) -> {
//				pseudonymizationService.pseudonymizeDto(PersonReferenceDto.class, c.getPerson(), isInJurisdiction, null);
//			});

		return cases;
	}

	@Override
	public List<CaseDataDto> getAllCasesOfPerson(String personUuid) {

		return caseService.findBy(new CaseCriteria().person(new PersonReferenceDto(personUuid)), false)
			.stream()
			.map(c -> convertToDto(c))
			.collect(Collectors.toList());
	}

	@Override
	public Map<CaseClassification, Long> getCaseCountPerClassification(
		CaseCriteria caseCriteria,
		boolean excludeSharedCases,
		boolean excludeCasesFromContacts) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> caze = cq.from(Case.class);

		Predicate filter = caseService.createUserFilter(
			cb,
			cq,
			caze,
			new CaseUserFilterCriteria().excludeSharedCases(excludeSharedCases).excludeCasesFromContacts(excludeCasesFromContacts));
		Predicate criteriaFilter = caseService.createCriteriaFilter(caseCriteria, cb, cq, caze);
		filter = AbstractAdoService.and(cb, filter, criteriaFilter);

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(caze.get(Case.CASE_CLASSIFICATION));
		cq.multiselect(caze.get(Case.CASE_CLASSIFICATION), cb.count(caze));
		List<Object[]> results = em.createQuery(cq).getResultList();

		Map<CaseClassification, Long> resultMap = results.stream().collect(Collectors.toMap(e -> (CaseClassification) e[0], e -> (Long) e[1]));
		return resultMap;
	}

	@Override
	public Map<PresentCondition, Long> getCaseCountPerPersonCondition(
		CaseCriteria caseCriteria,
		boolean excludeSharedCases,
		boolean excludeCasesFromContacts) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> caze = cq.from(Case.class);
		Join<Case, Person> person = caze.join(Case.PERSON, JoinType.LEFT);

		Predicate filter = caseService.createUserFilter(
			cb,
			cq,
			caze,
			new CaseUserFilterCriteria().excludeSharedCases(excludeSharedCases).excludeCasesFromContacts(excludeCasesFromContacts));
		Predicate criteriaFilter = caseService.createCriteriaFilter(caseCriteria, cb, cq, caze);
		filter = AbstractAdoService.and(cb, filter, criteriaFilter);

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(person.get(Person.PRESENT_CONDITION));
		cq.multiselect(person.get(Person.PRESENT_CONDITION), cb.count(caze));
		List<Object[]> results = em.createQuery(cq).getResultList();

		Map<PresentCondition, Long> resultMap = results.stream().collect(Collectors.toMap(e -> (PresentCondition) e[0], e -> (Long) e[1]));
		return resultMap;
	}

	@Override
	public Map<Disease, Long> getCaseCountByDisease(CaseCriteria caseCriteria, boolean excludeSharedCases, boolean excludeCasesFromContacts) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> caze = cq.from(Case.class);

		Predicate filter = caseService.createUserFilter(
			cb,
			cq,
			caze,
			new CaseUserFilterCriteria().excludeSharedCases(excludeSharedCases).excludeCasesFromContacts(excludeCasesFromContacts));

		filter = AbstractAdoService.and(cb, filter, caseService.createCriteriaFilter(caseCriteria, cb, cq, caze));

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(caze.get(Case.DISEASE));
		cq.multiselect(caze.get(Case.DISEASE), cb.count(caze));
		List<Object[]> results = em.createQuery(cq).getResultList();

		Map<Disease, Long> resultMap = results.stream().collect(Collectors.toMap(e -> (Disease) e[0], e -> (Long) e[1]));

		return resultMap;
	}

	@Override
	public List<CaseReferenceDto> getRandomCaseReferences(CaseCriteria criteria, int count) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> caze = cq.from(Case.class);

		Predicate filter =
			caseService.createUserFilter(cb, cq, caze, new CaseUserFilterCriteria().excludeSharedCases(true).excludeCasesFromContacts(true));
		filter = AbstractAdoService.and(cb, filter, caseService.createCriteriaFilter(criteria, cb, cq, caze));
		if (filter != null) {
			cq.where(filter);
		}

		cq.select(caze.get(Case.UUID));

		List<String> uuids = em.createQuery(cq).getResultList();

		return new Random().ints(count, 0, uuids.size()).mapToObj(i -> new CaseReferenceDto(uuids.get(i))).collect(Collectors.toList());
	}

	public Map<Disease, District> getLastReportedDistrictByDisease(
		CaseCriteria caseCriteria,
		boolean excludeSharedCases,
		boolean excludeCasesFromContacts) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> caze = cq.from(Case.class);
		Join<Case, District> districtJoin = caze.join(Case.DISTRICT, JoinType.LEFT);

		Predicate filter = caseService.createUserFilter(
			cb,
			cq,
			caze,
			new CaseUserFilterCriteria().excludeSharedCases(excludeSharedCases).excludeCasesFromContacts(excludeCasesFromContacts));

		filter = AbstractAdoService.and(cb, filter, caseService.createCriteriaFilter(caseCriteria, cb, cq, caze));

		if (filter != null) {
			cq.where(filter);
		}

		Expression<Number> maxReportDate = cb.max(caze.get(Case.REPORT_DATE));
		cq.multiselect(caze.get(Case.DISEASE), districtJoin, maxReportDate);
		cq.groupBy(caze.get(Case.DISEASE), districtJoin);
		cq.orderBy(cb.desc(maxReportDate));

		List<Object[]> results = em.createQuery(cq).getResultList();

		Map<Disease, District> resultMap = new HashMap<>();
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
	public List<CaseIndexDto> getSimilarCases(CaseSimilarityCriteria criteria) {

		CaseCriteria caseCriteria = criteria.getCaseCriteria();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CaseIndexDto> cq = cb.createQuery(CaseIndexDto.class);
		Root<Case> root = cq.from(Case.class);
		Join<Case, Person> person = root.join(Case.PERSON, JoinType.LEFT);
		Join<Case, Region> region = root.join(Case.REGION, JoinType.LEFT);

		selectIndexDtoFields(cq, root);

		Predicate userFilter = caseService.createUserFilter(cb, cq, root);
		Predicate personSimilarityFilter = criteria.getPersonUuid() != null ? cb.equal(person.get(Person.UUID), criteria.getPersonUuid()) : null;
		Predicate diseaseFilter = caseCriteria.getDisease() != null ? cb.equal(root.get(Case.DISEASE), caseCriteria.getDisease()) : null;
		Predicate regionFilter = caseCriteria.getRegion() != null ? cb.equal(region.get(Region.UUID), caseCriteria.getRegion().getUuid()) : null;
		Predicate reportDateFilter = criteria.getReportDate() != null
			? cb.between(
				root.get(Case.REPORT_DATE),
				DateHelper.subtractDays(criteria.getReportDate(), 30),
				DateHelper.addDays(criteria.getReportDate(), 30))
			: null;

		Predicate filter = caseService.createDefaultFilter(cb, root);
		filter = AbstractAdoService.and(cb, filter, userFilter);
		filter = AbstractAdoService.and(cb, filter, personSimilarityFilter);
		filter = AbstractAdoService.and(cb, filter, diseaseFilter);
		filter = AbstractAdoService.and(cb, filter, regionFilter);
		filter = AbstractAdoService.and(cb, filter, reportDateFilter);

		cq.where(filter);

		return em.createQuery(cq).getResultList();
	}

	@Override
	public List<CaseIndexDto[]> getCasesForDuplicateMerging(CaseCriteria criteria, boolean ignoreRegion) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> root = cq.from(Case.class);
		Root<Case> root2 = cq.from(Case.class);
		Join<Case, Person> person = root.join(Case.PERSON, JoinType.LEFT);
		Join<Case, Person> person2 = root2.join(Case.PERSON, JoinType.LEFT);
		Join<Case, Region> region = root.join(Case.REGION, JoinType.LEFT);
		Join<Case, Region> region2 = root2.join(Case.REGION, JoinType.LEFT);
		Join<Case, Symptoms> symptoms = root.join(Case.SYMPTOMS, JoinType.LEFT);
		Join<Case, Symptoms> symptoms2 = root2.join(Case.SYMPTOMS, JoinType.LEFT);

		Predicate userFilter = caseService.createUserFilter(cb, cq, root);
		Predicate criteriaFilter = criteria != null ? caseService.createCriteriaFilter(criteria, cb, cq, root) : null;
		Expression<String> nameSimilarityExpr = cb.concat(person.get(Person.FIRST_NAME), " ");
		nameSimilarityExpr = cb.concat(nameSimilarityExpr, person.get(Person.LAST_NAME));
		Expression<String> nameSimilarityExpr2 = cb.concat(person2.get(Person.FIRST_NAME), " ");
		nameSimilarityExpr2 = cb.concat(nameSimilarityExpr2, person2.get(Person.LAST_NAME));
		Predicate nameSimilarityFilter = cb.gt(
			cb.function("similarity", double.class, nameSimilarityExpr, nameSimilarityExpr2),
			FacadeProvider.getConfigFacade().getNameSimilarityThreshold());
		Predicate diseaseFilter = cb.equal(root.get(Case.DISEASE), root2.get(Case.DISEASE));
		Predicate regionFilter = cb.equal(region.get(Region.ID), region2.get(Region.ID));
		Predicate reportDateFilter = cb.lessThanOrEqualTo(
			cb.abs(
				cb.diff(
					cb.function("date_part", Long.class, cb.parameter(String.class, "date_type"), root.get(Case.REPORT_DATE)),
					cb.function("date_part", Long.class, cb.parameter(String.class, "date_type"), root2.get(Case.REPORT_DATE)))),
			new Long(30 * 24 * 60 * 60) // 30 days
		);
		// Sex filter: only when sex is filled in for both cases
		Predicate sexFilter = cb.or(
			cb.or(cb.isNull(person.get(Person.SEX)), cb.isNull(person2.get(Person.SEX))),
			cb.equal(person.get(Person.SEX), person2.get(Person.SEX)));
		// Birth date filter: only when birth date is filled in for both cases
		Predicate birthDateFilter = cb.or(
			cb.or(
				cb.isNull(person.get(Person.BIRTHDATE_DD)),
				cb.isNull(person.get(Person.BIRTHDATE_MM)),
				cb.isNull(person.get(Person.BIRTHDATE_YYYY)),
				cb.isNull(person2.get(Person.BIRTHDATE_DD)),
				cb.isNull(person2.get(Person.BIRTHDATE_MM)),
				cb.isNull(person2.get(Person.BIRTHDATE_YYYY))),
			cb.and(
				cb.equal(person.get(Person.BIRTHDATE_DD), person2.get(Person.BIRTHDATE_DD)),
				cb.equal(person.get(Person.BIRTHDATE_MM), person2.get(Person.BIRTHDATE_MM)),
				cb.equal(person.get(Person.BIRTHDATE_YYYY), person2.get(Person.BIRTHDATE_YYYY))));
		// Onset date filter: only when onset date is filled in for both cases
		Predicate onsetDateFilter = cb.or(
			cb.or(cb.isNull(symptoms.get(Symptoms.ONSET_DATE)), cb.isNull(symptoms2.get(Symptoms.ONSET_DATE))),
			cb.lessThanOrEqualTo(
				cb.abs(
					cb.diff(
						cb.function("date_part", Long.class, cb.parameter(String.class, "date_type"), symptoms.get(Symptoms.ONSET_DATE)),
						cb.function("date_part", Long.class, cb.parameter(String.class, "date_type"), symptoms2.get(Symptoms.ONSET_DATE)))),
				new Long(30 * 24 * 60 * 60) // 30 days
			));
		Predicate creationDateFilter = cb.lessThan(root.get(Case.CREATION_DATE), root2.get(Case.CREATION_DATE));

		Predicate filter = cb.and(caseService.createDefaultFilter(cb, root), caseService.createDefaultFilter(cb, root2));
		if (userFilter != null) {
			filter = cb.and(filter, userFilter);
		}
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

		if (!ignoreRegion) {
			filter = cb.and(filter, regionFilter);
		}

		filter = cb.and(filter, reportDateFilter);
		filter = cb.and(filter, cb.or(sexFilter, birthDateFilter));
		filter = cb.and(filter, onsetDateFilter);
		filter = cb.and(filter, creationDateFilter);

		cq.where(filter);
		cq.multiselect(root.get(Case.ID), root2.get(Case.ID));
		cq.orderBy(cb.desc(root.get(Case.CREATION_DATE)));

		List<Object[]> foundIds = em.createQuery(cq).setParameter("date_type", "epoch").getResultList();
		List<CaseIndexDto[]> resultList = new ArrayList<>();

		if (!foundIds.isEmpty()) {
			CriteriaQuery<CaseIndexDto> indexCasesCq = cb.createQuery(CaseIndexDto.class);
			Root<Case> indexRoot = indexCasesCq.from(Case.class);
			selectIndexDtoFields(indexCasesCq, indexRoot);
			indexCasesCq.where(indexRoot.get(Case.ID).in(foundIds.stream().flatMap(Arrays::stream).collect(Collectors.toSet())));
			Map<Long, CaseIndexDto> indexCases =
				em.createQuery(indexCasesCq).getResultStream().collect(Collectors.toMap(c -> c.getId(), Function.identity()));

			for (Object[] idPair : foundIds) {
				try {
					// Cloning is necessary here to allow us to add the same CaseIndexDto to the grid multiple times
					CaseIndexDto parent = (CaseIndexDto) indexCases.get(idPair[0]).clone();
					CaseIndexDto child = (CaseIndexDto) indexCases.get(idPair[1]).clone();

					if (parent.getCompleteness() == null && child.getCompleteness() == null
						|| parent.getCompleteness() != null
							&& (child.getCompleteness() == null || (parent.getCompleteness() >= child.getCompleteness()))) {
						resultList.add(
							new CaseIndexDto[] {
								parent,
								child });
					} else {
						resultList.add(
							new CaseIndexDto[] {
								child,
								parent });
					}
				} catch (CloneNotSupportedException e) {
					throw new RuntimeException(e);
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
		cq.multiselect(listQueryBuilder.getCaseIndexSelections(root, new CaseJoins<>(root)));
	}

	@Override
	public String getLastReportedDistrictName(CaseCriteria caseCriteria, boolean excludeSharedCases, boolean excludeCasesFromContacts) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> caze = cq.from(Case.class);
		Join<Case, District> district = caze.join(Case.DISTRICT, JoinType.LEFT);

		Predicate filter = caseService.createUserFilter(
			cb,
			cq,
			caze,
			new CaseUserFilterCriteria().excludeSharedCases(excludeSharedCases).excludeCasesFromContacts(excludeCasesFromContacts));

		filter = AbstractAdoService.and(cb, filter, caseService.createCriteriaFilter(caseCriteria, cb, cq, caze));

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
		return convertToDto(caseService.getByUuid(uuid));
	}

	@Override
	public CaseReferenceDto getReferenceByUuid(String uuid) {
		return convertToReferenceDto(caseService.getByUuid(uuid));
	}

	@Override
	public CaseDataDto saveCase(CaseDataDto dto) throws ValidationRuntimeException {
		return saveCase(dto, true);
	}

	public CaseDataDto saveCase(CaseDataDto dto, boolean handleChanges) throws ValidationRuntimeException {

		Case caze = caseService.getByUuid(dto.getUuid());
		CaseDataDto existingCaseDto = handleChanges ? toDto(caze) : null;

		SymptomsHelper.updateIsSymptomatic(dto.getSymptoms());

//		if (existingCaseDto != null) {
//			boolean inJurisdiction = caseJurisdictionChecker.isInJurisdiction(JurisdictionHelper.createCaseJurisdictionDto(caze));
//			pseudonymizationService.restorePseudonymizedValues(CaseDataDto.class, dto, existingCaseDto, inJurisdiction);
//		}

		validate(dto);

		caze = fillOrBuildEntity(dto, caze);

		// Set version number on a new case
		if (caze.getCreationDate() == null && StringUtils.isEmpty(dto.getCreationVersion())) {
			caze.setCreationVersion(InfoProvider.get().getVersion());
		}

		caseService.ensurePersisted(caze);
		if (handleChanges) {
			onCaseChanged(existingCaseDto, caze);
		}

		return convertToDto(caze);
	}

	@Override
	public void setSampleAssociations(ContactReferenceDto sourceContact, CaseReferenceDto cazeRef) {

		if (sourceContact != null) {
			final Contact contact = contactService.getByUuid(sourceContact.getUuid());
			final Case caze = caseService.getByUuid(cazeRef.getUuid());
			contact.getSamples().forEach(sample -> sample.setAssociatedCase(caze));
		}
	}

	@Override
	public void setSampleAssociations(EventParticipantReferenceDto sourceEventParticipant, CaseReferenceDto cazeRef) {
		if (sourceEventParticipant != null) {
			final EventParticipant eventParticipant = eventParticipantService.getByUuid(sourceEventParticipant.getUuid());
			final Case caze = caseService.getByUuid(cazeRef.getUuid());
			eventParticipant.getSamples().forEach(sample -> sample.setAssociatedCase(caze));
		}
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
		if (caze.getCommunity() != null && !communityFacade.getByUuid(caze.getCommunity().getUuid()).getDistrict().equals(caze.getDistrict())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noCommunityInDistrict));
		}
		if (caze.getHealthFacility() != null) {
			FacilityDto healthFacility = facilityFacade.getByUuid(caze.getHealthFacility().getUuid());

			if (caze.getCommunity() == null && healthFacility.getDistrict() != null && !healthFacility.getDistrict().equals(caze.getDistrict())) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noFacilityInDistrict));
			}
			if (caze.getCommunity() != null && healthFacility.getCommunity() != null && !caze.getCommunity().equals(healthFacility.getCommunity())) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noFacilityInCommunity));
			}
			if (healthFacility.getRegion() != null && !caze.getRegion().equals(healthFacility.getRegion())) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noFacilityInRegion));
			}
			if (FacilityHelper.isOtherOrNoneHealthFacility(caze.getHealthFacility().getUuid())
				&& StringUtils.isEmpty(caze.getHealthFacilityDetails())) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noFacilityDetails));
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

		// If its a new case and the case is new and the geo coordinates of the case's
		// health facility are null, set its coordinates to the case's report
		// coordinates, if available. Else if case report coordinates are null set them
		// to the facility's coordinates
		Facility facility = newCase.getHealthFacility();
		if (existingCase == null && facility != null && !FacilityHelper.isOtherOrNoneHealthFacility(facility.getUuid())) {
			if ((facility.getLatitude() == null || facility.getLongitude() == null)
				&& newCase.getReportLat() != null
				&& newCase.getReportLon() != null) {
				facility.setLatitude(newCase.getReportLat());
				facility.setLongitude(newCase.getReportLon());
				facilityService.ensurePersisted(facility);
			} else if (newCase.getReportLat() == null && newCase.getReportLon() == null && newCase.getReportLatLonAccuracy() == null) {
				newCase.setReportLat(facility.getLatitude());
				newCase.setReportLon(facility.getLongitude());
			}
		}

		// Generate epid number if missing or incomplete
		if (!CaseLogic.isCompleteEpidNumber(newCase.getEpidNumber())) {
			newCase.setEpidNumber(generateEpidNumber(newCase));
		}

		// update the plague type based on symptoms
		if (newCase.getDisease() == Disease.PLAGUE) {
			PlagueType plagueType = DiseaseHelper.getPlagueTypeForSymptoms(SymptomsFacadeEjb.toDto(newCase.getSymptoms()));
			if (plagueType != newCase.getPlagueType() && plagueType != null) {
				newCase.setPlagueType(plagueType);
			}
		}

		if (newCase.getSurveillanceOfficer() == null || !newCase.getSurveillanceOfficer().getDistrict().equals(newCase.getDistrict())) {
			setResponsibleSurveillanceOfficer(newCase);
		}

		updateInvestigationByStatus(existingCase, newCase);

		updatePersonAndCaseByOutcome(existingCase, newCase);

		updateCaseAge(existingCase, newCase);

		// Change the disease of all contacts if the case disease or disease details have changed
		if (existingCase != null
			&& (newCase.getDisease() != existingCase.getDisease()
				|| !StringUtils.equals(newCase.getDiseaseDetails(), existingCase.getDiseaseDetails()))) {
			for (Contact contact : contactService.findBy(new ContactCriteria().caze(newCase.toReference()), null)) {
				if (contact.getDisease() != newCase.getDisease() || !StringUtils.equals(contact.getDiseaseDetails(), newCase.getDiseaseDetails())) {
					// Only do the change if it hasn't been done in the mobile app before
					contact.setDisease(newCase.getDisease());
					contact.setDiseaseDetails(newCase.getDiseaseDetails());
					contactService.ensurePersisted(contact);
				}
			}
		}

		if (existingCase == null
			|| newCase.getDisease() != existingCase.getDisease()
			|| newCase.getReportDate() != existingCase.getReportDate()
			|| newCase.getSymptoms().getOnsetDate() != existingCase.getSymptoms().getOnsetDate()) {

			// Update follow-up until and status of all contacts
			for (Contact contact : contactService.findBy(new ContactCriteria().caze(newCase.toReference()), null)) {
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
		if (existingCase != null
			&& newCase.getHealthFacility() != null
			&& existingCase.getHealthFacility() != null
			&& !newCase.getHealthFacility().getUuid().equals(existingCase.getHealthFacility().getUuid())) {
			reassignTasks(newCase);
		}

		// Create a task to search for other cases for new Plague cases
		if (existingCase == null
			&& newCase.getDisease() == Disease.PLAGUE
			&& featureConfigurationFacade.isTaskGenerationFeatureEnabled(TaskType.ACTIVE_SEARCH_FOR_OTHER_CASES)) {
			createActiveSearchForOtherCasesTask(newCase);
		}

		// Update case classification if the feature is enabled
		if (configFacade.isFeatureAutomaticCaseClassification()) {
			if (newCase.getCaseClassification() != CaseClassification.NO_CASE) {
				// calculate classification
				CaseDataDto newCaseDto = toDto(newCase);
				List<PathogenTestDto> sampleTests =
					pathogenTestService.getAllByCase(newCase).stream().map(s -> PathogenTestFacadeEjbLocal.toDto(s)).collect(Collectors.toList());
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

		// Set Yes/No/Unknown fields associated with embedded lists to Yes if the lists
		// are not empty
		if (!newCase.getHospitalization().getPreviousHospitalizations().isEmpty()
			&& YesNoUnknown.YES != newCase.getHospitalization().getHospitalizedPreviously()) {
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
			List<User> messageRecipients = userService.getAllByRegionAndUserRoles(
				newCase.getRegion(),
				UserRole.SURVEILLANCE_SUPERVISOR,
				UserRole.CASE_SUPERVISOR,
				UserRole.CONTACT_SUPERVISOR);
			for (User recipient : messageRecipients) {
				try {
					messagingService.sendMessage(
						recipient,
						I18nProperties.getString(MessagingService.SUBJECT_CASE_CLASSIFICATION_CHANGED),
						String.format(
							I18nProperties.getString(MessagingService.CONTENT_CASE_CLASSIFICATION_CHANGED),
							DataHelper.getShortUuid(newCase.getUuid()),
							newCase.getCaseClassification().toString()),
						MessageType.EMAIL,
						MessageType.SMS);
				} catch (NotificationDeliveryFailedException e) {
					logger.error(
						String.format(
							"NotificationDeliveryFailedException when trying to notify supervisors about the change of a case classification. "
								+ "Failed to send " + e.getMessageType() + " to user with UUID %s.",
							recipient.getUuid()));
				}
			}
		}

		// Send an email to all responsible supervisors when the disease of an
		// Unspecified VHF case has changed
		if (existingCase != null && existingCase.getDisease() == Disease.UNSPECIFIED_VHF && existingCase.getDisease() != newCase.getDisease()) {
			List<User> messageRecipients = userService.getAllByRegionAndUserRoles(
				newCase.getRegion(),
				UserRole.SURVEILLANCE_SUPERVISOR,
				UserRole.CASE_SUPERVISOR,
				UserRole.CONTACT_SUPERVISOR);
			for (User recipient : messageRecipients) {
				try {
					messagingService.sendMessage(
						recipient,
						I18nProperties.getString(MessagingService.SUBJECT_DISEASE_CHANGED),
						String.format(
							I18nProperties.getString(MessagingService.CONTENT_DISEASE_CHANGED),
							DataHelper.getShortUuid(newCase.getUuid()),
							existingCase.getDisease().toString(),
							newCase.getDisease().toString()),
						MessageType.EMAIL,
						MessageType.SMS);
				} catch (NotificationDeliveryFailedException e) {
					logger.error(
						String.format(
							"NotificationDeliveryFailedException when trying to notify supervisors about the change of a case disease. "
								+ "Failed to send " + e.getMessageType() + " to user with UUID %s.",
							recipient.getUuid()));
				}
			}
		}
	}

	public void setResponsibleSurveillanceOfficer(Case caze) {
		if (caze.getReportingUser().getUserRoles().contains(UserRole.SURVEILLANCE_OFFICER)
			&& caze.getReportingUser().getDistrict().equals(caze.getDistrict())) {
			caze.setSurveillanceOfficer(caze.getReportingUser());
		} else {
			List<User> informants =
				caze.getHealthFacility() != null ? userService.getInformantsOfFacility(caze.getHealthFacility()) : new ArrayList<>();
			Random rand = new Random();
			if (!informants.isEmpty()) {
				caze.setSurveillanceOfficer(informants.get(rand.nextInt(informants.size())).getAssociatedOfficer());
			} else {
				List<User> survOffs = userService.getAllByDistrict(caze.getDistrict(), false, UserRole.SURVEILLANCE_OFFICER);
				if (!survOffs.isEmpty()) {
					caze.setSurveillanceOfficer(survOffs.get(rand.nextInt(survOffs.size())));
				} else {
					caze.setSurveillanceOfficer(null);
				}
			}
		}
	}

	public void reassignTasks(Case caze) {
		for (Task task : caze.getTasks()) {
			if (task.getTaskStatus() != TaskStatus.PENDING) {
				continue;
			}

			assignOfficerOrSupervisorToTask(caze, task);

			taskService.ensurePersisted(task);
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
			// Generate a completely new epid number if the prefix is not complete or doesn't match the pattern
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(caze.getReportDate());
			String year = String.valueOf(calendar.get(Calendar.YEAR)).substring(2);
			newEpidNumber = districtFacade.getFullEpidCodeForDistrict(caze.getDistrict().getUuid()) + "-" + year + "-";
		}

		// Generate a suffix number
		String highestEpidNumber = caseService.getHighestEpidNumber(newEpidNumber, caze.getUuid(), caze.getDisease());
		if (highestEpidNumber == null || highestEpidNumber.endsWith("-")) {
			// If there is not yet a case with a suffix for this epid number in the database, use 001
			newEpidNumber = newEpidNumber + "001";
		} else {
			// Otherwise, extract the suffix from the highest existing epid number and increase it by 1
			String suffixString = highestEpidNumber.substring(highestEpidNumber.lastIndexOf('-'));
			// Remove all non-digits from the suffix to ignore earlier input errors
			suffixString = suffixString.replaceAll("[^\\d]", "");
			if (suffixString.isEmpty()) {
				// If the suffix is empty now, that means there is not yet an epid number with a
				// suffix containing numbers
				newEpidNumber = newEpidNumber + "001";
			} else {
				int suffix = Integer.valueOf(suffixString) + 1;
				newEpidNumber += String.format("%03d", suffix);
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
			if (existingCase == null
				|| CaseLogic.getStartDate(existingCase.getSymptoms().getOnsetDate(), existingCase.getReportDate())
					!= CaseLogic.getStartDate(newCase.getSymptoms().getOnsetDate(), newCase.getReportDate())) {
				if (newCase.getPerson().getApproximateAgeType() == ApproximateAgeType.MONTHS) {
					newCase.setCaseAge(0);
				} else {
					Date personChangeDate = newCase.getPerson().getChangeDate();
					Date referenceDate = CaseLogic.getStartDate(newCase.getSymptoms().getOnsetDate(), newCase.getReportDate());
					newCase.setCaseAge(newCase.getPerson().getApproximateAge() - DateHelper.getYearsBetween(referenceDate, personChangeDate));
					if (newCase.getCaseAge() < 0) {
						newCase.setCaseAge(0);
					}
				}

			}
		}
	}

	@Override
	public void deleteCase(String caseUuid) {

		if (!userService.hasRight(UserRight.CASE_DELETE)) {
			throw new UnsupportedOperationException("User " + userService.getCurrentUser().getUuid() + " is not allowed to delete cases.");
		}

		caseService.delete(caseService.getByUuid(caseUuid));
	}

	@Override
	public void deleteCaseAsDuplicate(String caseUuid, String duplicateOfCaseUuid) {

		Case caze = caseService.getByUuid(caseUuid);
		Case duplicateOfCase = caseService.getByUuid(duplicateOfCaseUuid);
		caze.setDuplicateOf(duplicateOfCase);
		caseService.ensurePersisted(caze);

		deleteCase(caseUuid);
	}

	@Override
	public List<String> getArchivedUuidsSince(Date since) {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return caseService.getArchivedUuidsSince(since);
	}

	@Override
	public List<String> getDeletedUuidsSince(Date since) {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return caseService.getDeletedUuidsSince(since);
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
		target.setRabiesType(source.getRabiesType());
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
		target.setClinicalConfirmation(source.getClinicalConfirmation());
		target.setEpidemiologicalConfirmation(source.getEpidemiologicalConfirmation());
		target.setLaboratoryDiagnosticConfirmation(source.getLaboratoryDiagnosticConfirmation());
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
		target.setVaccine(source.getVaccine());
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
		target.setAdditionalDetails(source.getAdditionalDetails());
		target.setExternalID(source.getExternalID());
		target.setSharedToCountry(source.isSharedToCountry());
		target.setQuarantine(source.getQuarantine());
		target.setQuarantineTypeDetails(source.getQuarantineTypeDetails());
		target.setQuarantineTo(source.getQuarantineTo());
		target.setQuarantineFrom(source.getQuarantineFrom());
		target.setQuarantineHelpNeeded(source.getQuarantineHelpNeeded());
		target.setQuarantineOrderedVerbally(source.isQuarantineOrderedVerbally());
		target.setQuarantineOrderedOfficialDocument(source.isQuarantineOrderedOfficialDocument());
		target.setQuarantineOrderedVerballyDate(source.getQuarantineOrderedVerballyDate());
		target.setQuarantineOrderedOfficialDocumentDate(source.getQuarantineOrderedOfficialDocumentDate());
		target.setQuarantineHomePossible(source.getQuarantineHomePossible());
		target.setQuarantineHomePossibleComment(source.getQuarantineHomePossibleComment());
		target.setQuarantineHomeSupplyEnsured(source.getQuarantineHomeSupplyEnsured());
		target.setQuarantineHomeSupplyEnsuredComment(source.getQuarantineHomeSupplyEnsuredComment());
		target.setReportingType(source.getReportingType());
		target.setPostpartum(source.getPostpartum());
		target.setTrimester(source.getTrimester());

		return target;
	}

	public CaseDataDto convertToDto(Case source) {

		CaseDataDto dto = toDto(source);

//		if (dto != null) {
//			boolean inJurisdiction = caseJurisdictionChecker.isInJurisdiction(JurisdictionHelper.createCaseJurisdictionDto(source));
//			pseudonymizationService.pseudonymizeDto(CaseDataDto.class, dto, inJurisdiction, c -> {
//				pseudonymizationService.pseudonymizeDto(PersonReferenceDto.class, dto.getPerson(), inJurisdiction, null);
//			});
//		}

		return dto;
	}

	public CaseReferenceDto convertToReferenceDto(Case source) {

		CaseReferenceDto dto = toReferenceDto(source);

//		if (dto != null) {
//			boolean inJurisdiction = caseJurisdictionChecker.isInJurisdiction(JurisdictionHelper.createCaseJurisdictionDto(source));
//			pseudonymizationService.pseudonymizeDto(CaseReferenceDto.class, dto, inJurisdiction, null);
//		}

		return dto;
	}

	public static CaseReferenceDto toReferenceDto(Case entity) {

		if (entity == null) {
			return null;
		}

		return entity.toReference();
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
		target.setRabiesType(source.getRabiesType());
		target.setCaseClassification(source.getCaseClassification());
		target.setClassificationUser(UserFacadeEjb.toReferenceDto(source.getClassificationUser()));
		target.setClassificationDate(source.getClassificationDate());
		target.setClassificationComment(source.getClassificationComment());
		target.setClinicalConfirmation(source.getClinicalConfirmation());
		target.setEpidemiologicalConfirmation(source.isEpidemiologicalConfirmation());
		target.setLaboratoryDiagnosticConfirmation(source.getLaboratoryDiagnosticConfirmation());
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
		target.setVaccine(source.getVaccine());
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
		target.setAdditionalDetails(source.getAdditionalDetails());
		target.setExternalID(source.getExternalID());
		target.setSharedToCountry(source.isSharedToCountry());
		target.setQuarantine(source.getQuarantine());
		target.setQuarantineTypeDetails(source.getQuarantineTypeDetails());
		target.setQuarantineTo(source.getQuarantineTo());
		target.setQuarantineFrom(source.getQuarantineFrom());
		target.setQuarantineHelpNeeded(source.getQuarantineHelpNeeded());
		target.setQuarantineOrderedVerbally(source.isQuarantineOrderedVerbally());
		target.setQuarantineOrderedOfficialDocument(source.isQuarantineOrderedOfficialDocument());
		target.setQuarantineOrderedVerballyDate(source.getQuarantineOrderedVerballyDate());
		target.setQuarantineOrderedOfficialDocumentDate(source.getQuarantineOrderedOfficialDocumentDate());
		target.setQuarantineHomePossible(source.getQuarantineHomePossible());
		target.setQuarantineHomePossibleComment(source.getQuarantineHomePossibleComment());
		target.setQuarantineHomeSupplyEnsured(source.getQuarantineHomeSupplyEnsured());
		target.setQuarantineHomeSupplyEnsuredComment(source.getQuarantineHomeSupplyEnsuredComment());
		target.setReportingType(source.getReportingType());
		target.setPostpartum(source.getPostpartum());
		target.setTrimester(source.getTrimester());

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
			List<Task> pendingTasks =
				taskService.findBy(new TaskCriteria().taskType(TaskType.CASE_INVESTIGATION).caze(caseRef).taskStatus(TaskStatus.PENDING));
			for (Task task : pendingTasks) {
				task.setTaskStatus(TaskStatus.REMOVED);
				task.setStatusChangeDate(new Date());
			}

			if (caze.getInvestigationStatus() == InvestigationStatus.DONE
				&& existingCase != null
				&& existingCase.getInvestigationStatus() != InvestigationStatus.DONE) {
				sendInvestigationDoneNotifications(caze);
			}
		} else {
			// Remove the investigation date
			caze.setInvestigatedDate(null);

			// Create a new investigation task if none is present
			long pendingCount =
				taskService.getCount(new TaskCriteria().taskType(TaskType.CASE_INVESTIGATION).caze(caseRef).taskStatus(TaskStatus.PENDING));

			if (pendingCount == 0 && featureConfigurationFacade.isTaskGenerationFeatureEnabled(TaskType.CASE_INVESTIGATION)) {
				createInvestigationTask(caze);
			}
		}
	}

	public void updateInvestigationByTask(Case caze) {

		CaseReferenceDto caseRef = caze.toReference();

		// any pending case investigation task?
		long pendingCount =
			taskService.getCount(new TaskCriteria().taskType(TaskType.CASE_INVESTIGATION).caze(caseRef).taskStatus(TaskStatus.PENDING));

		if (pendingCount > 0) {
			// set status to investigation pending
			caze.setInvestigationStatus(InvestigationStatus.PENDING);
			// .. and clear date
			caze.setInvestigatedDate(null);
		} else {
			// get "case investigation" task created last
			List<Task> cazeTasks = taskService.findBy(new TaskCriteria().taskType(TaskType.CASE_INVESTIGATION).caze(caseRef));

			if (!cazeTasks.isEmpty()) {
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

		User assignee = null;

		if (caze.getSurveillanceOfficer() != null) {
			// 1) The surveillance officer that is responsible for the case
			assignee = caze.getSurveillanceOfficer();
		} else if (caze.getDistrict() != null) {
			// 2) A random surveillance officer from the case district
			List<User> officers = userService.getAllByDistrict(caze.getDistrict(), false, UserRole.SURVEILLANCE_OFFICER);
			if (!officers.isEmpty()) {
				Random rand = new Random();
				assignee = officers.get(rand.nextInt(officers.size()));
			}
		}

		if (assignee == null) {
			if (caze.getReportingUser() != null && caze.getReportingUser().getUserRoles().contains(UserRole.SURVEILLANCE_SUPERVISOR)) {
				// 3) If the case was created by a surveillance supervisor, assign them
				assignee = caze.getReportingUser();
			} else if (caze.getRegion() != null) {
				// 4) Assign a random surveillance supervisor from the case region
				List<User> supervisors = userService.getAllByRegionAndUserRoles(caze.getRegion(), UserRole.SURVEILLANCE_SUPERVISOR);
				if (!supervisors.isEmpty()) {
					Random rand = new Random();
					assignee = supervisors.get(rand.nextInt(supervisors.size()));
				}
			}
		}

		task.setAssigneeUser(assignee);
		if (assignee == null) {
			logger.warn("No valid assignee user found for task " + task.getUuid());
		}
	}

	@Override
	public boolean doesEpidNumberExist(String epidNumber, String caseUuid, Disease caseDisease) {

		int suffixSeperatorIndex = epidNumber.lastIndexOf('-');
		if (suffixSeperatorIndex == -1) {
			// no suffix - use the whole string as prefix
			suffixSeperatorIndex = epidNumber.length() - 1;
		}
		String prefixString = epidNumber.substring(0, suffixSeperatorIndex + 1);
		String suffixString = epidNumber.substring(suffixSeperatorIndex + 1);
		suffixString = suffixString.replaceAll("[^\\d]", "");

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> caze = cq.from(Case.class);

		Predicate filter = cb.and(cb.equal(caze.get(Case.DELETED), false), cb.equal(caze.get(Case.DISEASE), caseDisease));
		if (!DataHelper.isNullOrEmpty(caseUuid)) {
			filter = cb.and(filter, cb.notEqual(caze.get(Case.UUID), caseUuid));
		}

		ParameterExpression<String> regexPattern = null, regexReplacement = null, regexFlags = null;
		if (suffixString.length() > 0) {
			// has to start with prefix
			filter = cb.and(filter, cb.like(caze.get(Case.EPID_NUMBER), prefixString + "%"));

			// for the suffix only consider the actual number. Any other characters and leading zeros are ignored
			int suffixNumber;
			try {
				suffixNumber = Integer.parseInt(suffixString);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(
					String.format("Invalid suffix for epid number. epidNumber: '%s', suffixString: '%s'", epidNumber, suffixString),
					e);
			}
			regexPattern = cb.parameter(String.class);
			regexReplacement = cb.parameter(String.class);
			regexFlags = cb.parameter(String.class);
			Expression<String> epidNumberSuffixClean = cb.function(
				"regexp_replace",
				String.class,
				cb.substring(caze.get(Case.EPID_NUMBER), suffixSeperatorIndex + 2),
				regexPattern,
				regexReplacement,
				regexFlags);
			filter = cb.and(filter, cb.equal(cb.concat("0", epidNumberSuffixClean).as(Integer.class), suffixNumber));
		} else {
			filter = cb.and(filter, cb.equal(caze.get(Case.EPID_NUMBER), prefixString));
		}
		cq.where(filter);

		cq.select(caze.get(Case.EPID_NUMBER));
		TypedQuery<String> query = em.createQuery(cq);
		if (regexPattern != null) {
			query.setParameter(regexPattern, "\\D"); // Non-digits
			query.setParameter(regexReplacement, ""); // Replace all non-digits with empty string
			query.setParameter(regexFlags, "g"); // Global search
		}
		query.setMaxResults(1);
		return !query.getResultList().isEmpty();
	}

	@Override
	public List<Pair<DistrictDto, BigDecimal>> getCaseMeasurePerDistrict(Date fromDate, Date toDate, Disease disease, CaseMeasure caseMeasure) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> from = cq.from(Case.class);

		Predicate filter = caseService.createDefaultFilter(cb, from);
		if (fromDate != null || toDate != null) {
			filter = caseService.createCaseRelevanceFilter(cb, from, fromDate, toDate);
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
				.map(e -> new Pair<DistrictDto, BigDecimal>(districtFacade.toDto((District) e[0]), new BigDecimal((Long) e[1])))
				.collect(Collectors.toList());
			return resultList;
		} else {
			List<Pair<DistrictDto, BigDecimal>> resultList = results.stream().map(e -> {
				District district = (District) e[0];
				Integer population = populationDataFacade.getProjectedDistrictPopulation(district.getUuid());
				Long caseCount = (Long) e[1];

				if (population == null || population <= 0) {
					// No or negative population - these entries will be cut off in the UI
					return new Pair<DistrictDto, BigDecimal>(districtFacade.toDto(district), new BigDecimal(0));
				} else {
					return new Pair<DistrictDto, BigDecimal>(
						districtFacade.toDto(district),
						InfrastructureHelper.getCaseIncidence(caseCount.intValue(), population, InfrastructureHelper.CASE_INCIDENCE_DIVISOR));
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

		List<User> messageRecipients = userService
			.getAllByRegionAndUserRoles(caze.getRegion(), UserRole.SURVEILLANCE_SUPERVISOR, UserRole.CASE_SUPERVISOR, UserRole.CONTACT_SUPERVISOR);
		for (User recipient : messageRecipients) {
			try {
				messagingService.sendMessage(
					recipient,
					I18nProperties.getString(MessagingService.SUBJECT_CASE_INVESTIGATION_DONE),
					String
						.format(I18nProperties.getString(MessagingService.CONTENT_CASE_INVESTIGATION_DONE), DataHelper.getShortUuid(caze.getUuid())),
					MessageType.EMAIL,
					MessageType.SMS);
			} catch (NotificationDeliveryFailedException e) {
				logger.error(
					String.format(
						"NotificationDeliveryFailedException when trying to notify supervisors about the completion of a case investigation. "
							+ "Failed to send " + e.getMessageType() + " to user with UUID %s.",
						recipient.getUuid()));
			}
		}
	}

	@Override
	public Date getOldestCaseOnsetDate() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Timestamp> cq = cb.createQuery(Timestamp.class);
		Root<Case> from = cq.from(Case.class);
		Join<Case, Symptoms> symptoms = from.join(Case.SYMPTOMS, JoinType.LEFT);

		Path<Timestamp> expression = symptoms.get(Symptoms.ONSET_DATE);
		cq.select(cb.least(expression));
		cq.where(cb.greaterThan(symptoms.get(Symptoms.ONSET_DATE), DateHelper.getDateZero(2000, 1, 1)));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public Date getOldestCaseReportDate() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Timestamp> cq = cb.createQuery(Timestamp.class);
		Root<Case> from = cq.from(Case.class);

		final Path<Timestamp> reportDate = from.get(Case.REPORT_DATE);
		cq.select(cb.least(reportDate));
		cq.where(cb.greaterThan(from.get(Case.REPORT_DATE), DateHelper.getDateZero(2000, 1, 1)));
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
		cq.where(cb.and(cb.equal(from.get(Case.ARCHIVED), true), cb.equal(from.get(AbstractDomainObject.UUID), caseUuid)));
		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	@Override
	public boolean isDeleted(String caseUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Case> from = cq.from(Case.class);

		cq.where(cb.and(cb.isTrue(from.get(Case.DELETED)), cb.equal(from.get(AbstractDomainObject.UUID), caseUuid)));
		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	@Override
	public void mergeCase(String leadUuid, String otherUuid) {

		mergeCase(getCaseDataByUuid(leadUuid), getCaseDataByUuid(otherUuid), false);
	}

	private void mergeCase(CaseDataDto leadCaseData, CaseDataDto otherCaseData, boolean cloning) {

		// 1 Merge Dtos
		// 1.1 Case
		fillDto(leadCaseData, otherCaseData, cloning);
		saveCase(leadCaseData, !cloning);

		// 1.2 Person
		if (!cloning) {
			PersonDto leadPerson = personFacade.getPersonByUuid(leadCaseData.getPerson().getUuid());
			PersonDto otherPerson = personFacade.getPersonByUuid(otherCaseData.getPerson().getUuid());
			fillDto(leadPerson, otherPerson, cloning);
			personFacade.savePerson(leadPerson);
		} else {
			assert (DataHelper.equal(leadCaseData.getPerson().getUuid(), otherCaseData.getPerson().getUuid()));
		}

		// 2 Change CaseReference
		Case leadCase = caseService.getByUuid(leadCaseData.getUuid());
		Case otherCase = caseService.getByUuid(otherCaseData.getUuid());

		// 2.1 Contacts
		List<Contact> contacts = contactService.findBy(new ContactCriteria().caze(otherCase.toReference()), null);
		for (Contact contact : contacts) {
			if (cloning) {
				ContactDto newContact = ContactDto.build(leadCase.toReference(), leadCase.getDisease(), leadCase.getDiseaseDetails());
				newContact.setPerson(new PersonReferenceDto(contact.getPerson().getUuid()));
				fillDto(newContact, ContactFacadeEjb.toDto(contact), cloning);
				contactFacade.saveContact(newContact, false);
			} else {
				// simply move existing entities to the merge target
				contact.setCaze(leadCase);
				contactService.ensurePersisted(contact);
			}
		}

		// 2.2 Samples
		List<Sample> samples = sampleService.findBy(new SampleCriteria().caze(otherCase.toReference()), null);
		for (Sample sample : samples) {
			if (cloning) {
				SampleDto newSample = SampleDto.build(sample.getReportingUser().toReference(), leadCase.toReference());
				fillDto(newSample, SampleFacadeEjb.toDto(sample), cloning);
				sampleFacade.saveSample(newSample, false);

				// 2.2.1 Pathogen Tests
				for (PathogenTest pathogenTest : sample.getPathogenTests()) {
					PathogenTestDto newPathogenTest = PathogenTestDto.build(newSample.toReference(), pathogenTest.getLabUser().toReference());
					fillDto(newPathogenTest, PathogenTestFacadeEjbLocal.toDto(pathogenTest), cloning);
					sampleTestFacade.savePathogenTest(newPathogenTest);

				}
			} else {
				// simply move existing entities to the merge target
				sample.setAssociatedCase(leadCase);
				sampleService.ensurePersisted(sample);
			}
		}

		// 2.3 Tasks
		if (!cloning) {
			// simply move existing entities to the merge target

			List<Task> tasks = taskService.findBy(new TaskCriteria().caze(new CaseReferenceDto(otherCase.getUuid())));
			for (Task task : tasks) {
				task.setCaze(leadCase);
				taskService.ensurePersisted(task);
			}
		}

		// 3 Change Therapy Reference
		// 3.1 Treatments
		List<Treatment> treatments =
			treatmentService.findBy(new TreatmentCriteria().therapy(new TherapyReferenceDto(otherCase.getTherapy().getUuid())));
		TherapyReferenceDto leadCaseTherapyReference = new TherapyReferenceDto(leadCase.getTherapy().getUuid());
		for (Treatment treatment : treatments) {
			if (cloning) {
				TreatmentDto newTreatment = TreatmentDto.build(leadCaseTherapyReference);
				fillDto(newTreatment, TreatmentFacadeEjb.toDto(treatment), cloning);
				treatmentFacade.saveTreatment(newTreatment);
			} else {
				// simply move existing entities to the merge target
				treatment.setTherapy(leadCase.getTherapy());
				treatmentService.ensurePersisted(treatment);
			}
		}

		// 3.2 Prescriptions
		List<Prescription> prescriptions =
			prescriptionService.findBy(new PrescriptionCriteria().therapy(new TherapyReferenceDto(otherCase.getTherapy().getUuid())));
		for (Prescription prescription : prescriptions) {
			if (cloning) {
				PrescriptionDto newPrescription = PrescriptionDto.buildPrescription(leadCaseTherapyReference);
				fillDto(newPrescription, PrescriptionFacadeEjb.toDto(prescription), cloning);
				prescriptionFacade.savePrescription(newPrescription);
			} else {
				// simply move existing entities to the merge target
				prescription.setTherapy(leadCase.getTherapy());
				prescriptionService.ensurePersisted(prescription);
			}
		}

		// 4 Change Clinical Course Reference
		// 4.1 Clinical Visits
		List<ClinicalVisit> clinicalVisits = clinicalVisitService
			.findBy(new ClinicalVisitCriteria().clinicalCourse(new ClinicalCourseReferenceDto(otherCase.getClinicalCourse().getUuid())));
		for (ClinicalVisit clinicalVisit : clinicalVisits) {
			if (cloning) {
				ClinicalVisitDto newClinicalVisit = ClinicalVisitDto.build(leadCaseData.getClinicalCourse().toReference(), leadCase.getDisease());
				fillDto(newClinicalVisit, ClinicalVisitFacadeEjb.toDto(clinicalVisit), cloning);
				clinicalVisitFacade.saveClinicalVisit(newClinicalVisit, leadCase.getUuid(), false);
			} else {
				// simply move existing entities to the merge target
				clinicalVisit.setClinicalCourse(leadCase.getClinicalCourse());
				clinicalVisitService.ensurePersisted(clinicalVisit);
			}
		}
	}

	@Override
	public CaseDataDto cloneCase(CaseDataDto existingCaseDto) {

		CaseDataDto newCase = CaseDataDto.build(existingCaseDto.getPerson(), existingCaseDto.getDisease());
		mergeCase(newCase, existingCaseDto, true);
		return getCaseDataByUuid(newCase.getUuid());
	}

	/**
	 * Archives all cases that have not been changed for a defined amount of days
	 *
	 * @param daysAfterCaseGetsArchived
	 *            defines the amount of days
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void archiveAllArchivableCases(int daysAfterCaseGetsArchived) {

		archiveAllArchivableCases(daysAfterCaseGetsArchived, LocalDate.now());
	}

	void archiveAllArchivableCases(int daysAfterCaseGetsArchived, LocalDate referenceDate) {

		LocalDate notChangedSince = referenceDate.minusDays(daysAfterCaseGetsArchived);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> from = cq.from(Case.class);

		Timestamp notChangedTimestamp = Timestamp.valueOf(notChangedSince.atStartOfDay());
		cq.where(cb.equal(from.get(Case.ARCHIVED), false), cb.not(caseService.createChangeDateFilter(cb, from, notChangedTimestamp, true)));
		cq.select(from.get(Case.UUID));
		List<String> uuids = em.createQuery(cq).getResultList();

		if (!uuids.isEmpty()) {

			CriteriaUpdate<Case> cu = cb.createCriteriaUpdate(Case.class);
			Root<Case> root = cu.from(Case.class);

			cu.set(root.get(Case.ARCHIVED), true);

			cu.where(root.get(Case.UUID).in(uuids));

			em.createQuery(cu).executeUpdate();
		}
	}

	@Override
	public boolean exists(String uuid) {
		return caseService.exists(uuid);
	}

	@LocalBean
	@Stateless
	public static class CaseFacadeEjbLocal extends CaseFacadeEjb {

	}

	public Boolean isCaseEditAllowed(String caseUuid) {
		Case caze = caseService.getByUuid(caseUuid);
		return caseJurisdictionChecker.isInJurisdiction(caze);
	}
}
