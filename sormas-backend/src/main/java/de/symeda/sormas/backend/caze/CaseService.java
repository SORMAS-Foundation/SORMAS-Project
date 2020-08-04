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

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseReferenceDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitCriteria;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.therapy.PrescriptionCriteria;
import de.symeda.sormas.api.therapy.TherapyReferenceDto;
import de.symeda.sormas.api.therapy.TreatmentCriteria;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.caze.maternalhistory.MaternalHistory;
import de.symeda.sormas.backend.caze.porthealthinfo.PortHealthInfo;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourse;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisit;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisitService;
import de.symeda.sormas.backend.clinicalcourse.HealthConditions;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.epidata.EpiDataService;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.hospitalization.Hospitalization;
import de.symeda.sormas.backend.hospitalization.HospitalizationService;
import de.symeda.sormas.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.sample.PathogenTest;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleJoins;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.therapy.Prescription;
import de.symeda.sormas.backend.therapy.PrescriptionService;
import de.symeda.sormas.backend.therapy.Therapy;
import de.symeda.sormas.backend.therapy.Treatment;
import de.symeda.sormas.backend.therapy.TreatmentService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class CaseService extends AbstractCoreAdoService<Case> {

	@EJB
	private ContactService contactService;
	@EJB
	private SampleService sampleService;
	@EJB
	private PersonFacadeEjbLocal personFacade;
	@EJB
	private PersonService personService;
	@EJB
	private EventParticipantService eventParticipantService;
	@EJB
	private HospitalizationService hospitalizationService;
	@EJB
	private EpiDataService epiDataService;
	@EJB
	private UserService userService;
	@EJB
	private TaskService taskService;
	@EJB
	private ClinicalVisitService clinicalVisitService;
	@EJB
	private TreatmentService treatmentService;
	@EJB
	private PrescriptionService prescriptionService;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	public CaseService() {
		super(Case.class);
	}

	/**
	 * Returns all cases that match the specified {@code caseCriteria} and that the current user has access to.
	 * This should be the preferred method of retrieving cases from the database if there is no special logic required
	 * that can not be part of the {@link CaseCriteria}.
	 */
	public List<Case> findBy(CaseCriteria caseCriteria, boolean ignoreUserFilter) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> cq = cb.createQuery(getElementClass());
		Root<Case> from = cq.from(getElementClass());

		Predicate filter = createCriteriaFilter(caseCriteria, cb, cq, from);
		if (!ignoreUserFilter) {
			filter = and(cb, filter, createUserFilter(cb, cq, from));
		}

		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.asc(from.get(Case.CREATION_DATE)));

		List<Case> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public List<Case> getAllActiveCasesAfter(Date date, Boolean includeExtendedChangeDateFilters) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> cq = cb.createQuery(getElementClass());
		Root<Case> from = cq.from(getElementClass());
		from.fetch(Case.SYMPTOMS);
		from.fetch(Case.THERAPY);
		Fetch<Case, ClinicalCourse> clinicalCourseFetch = from.fetch(Case.CLINICAL_COURSE);
		clinicalCourseFetch.fetch(ClinicalCourse.HEALTH_CONDITIONS);
		from.fetch(Case.HOSPITALIZATION);
		from.fetch(Case.EPI_DATA);
		from.fetch(Case.PORT_HEALTH_INFO);
		from.fetch(Case.MATERNAL_HISTORY);

		Predicate filter = createActiveCasesFilter(cb, from);

		if (getCurrentUser() != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			if (userFilter != null) {
				filter = cb.and(filter, userFilter);
			}
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, DateHelper.toTimestampUpper(date), includeExtendedChangeDateFilters);
			if (dateFilter != null) {
				filter = cb.and(filter, dateFilter);
			}
		}

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(Case.CHANGE_DATE)));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}

	public List<String> getAllActiveUuids() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> from = cq.from(getElementClass());

		Predicate filter = createActiveCasesFilter(cb, from);

		if (getCurrentUser() != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = AbstractAdoService.and(cb, filter, userFilter);
		}

		cq.where(filter);
		cq.select(from.get(Case.UUID));

		return em.createQuery(cq).getResultList();
	}

	public List<MapCaseDto> getCasesForMap(Region region, District district, Disease disease, Date from, Date to) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MapCaseDto> cq = cb.createQuery(MapCaseDto.class);
		Root<Case> caze = cq.from(getElementClass());

		CaseJoins<Case> joins = new CaseJoins<>(caze);

		Predicate filter = createActiveCasesFilter(cb, caze);
		filter = AbstractAdoService
			.and(cb, filter, createUserFilter(cb, cq, caze, new CaseUserFilterCriteria().excludeSharedCases(true).excludeCasesFromContacts(true)));
		filter = AbstractAdoService.and(cb, filter, createCaseRelevanceFilter(cb, caze, from, to));

		if (region != null) {
			Predicate regionFilter = cb.equal(caze.get(Case.REGION), region);
			if (filter != null) {
				filter = cb.and(filter, regionFilter);
			} else {
				filter = regionFilter;
			}
		}

		if (district != null) {
			Predicate districtFilter = cb.equal(caze.get(Case.DISTRICT), district);
			if (filter != null) {
				filter = cb.and(filter, districtFilter);
			} else {
				filter = districtFilter;
			}
		}

		if (disease != null) {
			Predicate diseaseFilter = cb.equal(caze.get(Case.DISEASE), disease);
			if (filter != null) {
				filter = cb.and(filter, diseaseFilter);
			} else {
				filter = diseaseFilter;
			}
		}

		List<MapCaseDto> result;
		if (filter != null) {
			cq.where(filter);
			cq.multiselect(
				caze.get(Case.UUID),
				caze.get(Case.REPORT_DATE),
				caze.get(Case.CASE_CLASSIFICATION),
				caze.get(Case.DISEASE),
				joins.getPerson().get(Person.UUID),
				joins.getPerson().get(Person.FIRST_NAME),
				joins.getPerson().get(Person.LAST_NAME),
				joins.getFacility().get(Facility.UUID),
				joins.getFacility().get(Facility.LATITUDE),
				joins.getFacility().get(Facility.LONGITUDE),
				caze.get(Case.REPORT_LAT),
				caze.get(Case.REPORT_LON),
				joins.getPersonAddress().get(Location.LATITUDE),
				joins.getPersonAddress().get(Location.LONGITUDE),
				joins.getReportingUser().get(User.UUID),
				joins.getRegion().get(Region.UUID),
				joins.getDistrict().get(District.UUID),
				joins.getCommunity().get(Community.UUID),
				joins.getPointOfEntry().get(PointOfEntry.UUID));

			result = em.createQuery(cq).getResultList();
		} else {
			result = Collections.emptyList();
		}

		return result;
	}

	public String getHighestEpidNumber(String epidNumberPrefix, String caseUuid, Disease caseDisease) {

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Case> caze = cq.from(Case.class);

			Predicate filter = cb.and(cb.equal(caze.get(Case.DELETED), false), cb.equal(caze.get(Case.DISEASE), caseDisease));
			if (!DataHelper.isNullOrEmpty(caseUuid)) {
				filter = cb.and(filter, cb.notEqual(caze.get(Case.UUID), caseUuid));
			}
			filter = cb.and(filter, cb.like(caze.get(Case.EPID_NUMBER), epidNumberPrefix + "%"));
			cq.where(filter);

			ParameterExpression<String> regexPattern = cb.parameter(String.class);
			ParameterExpression<String> regexReplacement = cb.parameter(String.class);
			ParameterExpression<String> regexFlags = cb.parameter(String.class);
			Expression<String> epidNumberSuffixClean = cb.function(
				"regexp_replace",
				String.class,
				cb.substring(caze.get(Case.EPID_NUMBER), epidNumberPrefix.length() + 1),
				regexPattern,
				regexReplacement,
				regexFlags);
			cq.orderBy(cb.desc(cb.concat("0", epidNumberSuffixClean).as(Integer.class)));
			cq.select(caze.get(Case.EPID_NUMBER));
			TypedQuery<String> query = em.createQuery(cq);
			query.setParameter(regexPattern, "\\D"); // Non-digits
			query.setParameter(regexReplacement, ""); // Replace all non-digits with empty string
			query.setParameter(regexFlags, "g"); // Global search
			query.setMaxResults(1);
			return query.getSingleResult();

		} catch (NoResultException e) {
			return null;
		}
	}

	public String getUuidByUuidEpidNumberOrExternalId(String searchTerm) {

		if (StringUtils.isEmpty(searchTerm)) {
			return null;
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> root = cq.from(Case.class);

		Predicate filter = cb.or(
			cb.equal(cb.lower(root.get(Case.UUID)), searchTerm.toLowerCase()),
			cb.equal(cb.lower(root.get(Case.EPID_NUMBER)), searchTerm.toLowerCase()),
			cb.equal(cb.lower(root.get(Case.EXTERNAL_ID)), searchTerm.toLowerCase()));

		cq.where(filter);
		cq.orderBy(cb.desc(root.get(Case.REPORT_DATE)));
		cq.select(root.get(Case.UUID));

		try {
			return em.createQuery(cq).setMaxResults(1).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<String> getArchivedUuidsSince(Date since) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> caze = cq.from(Case.class);

		Predicate filter = createUserFilter(cb, cq, caze);
		if (since != null) {
			Predicate dateFilter = cb.greaterThanOrEqualTo(caze.get(Case.CHANGE_DATE), since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		Predicate archivedFilter = cb.equal(caze.get(Case.ARCHIVED), true);
		if (filter != null) {
			filter = cb.and(filter, archivedFilter);
		} else {
			filter = archivedFilter;
		}

		cq.where(filter);
		cq.select(caze.get(Case.UUID));

		return em.createQuery(cq).getResultList();
	}

	public List<String> getDeletedUuidsSince(Date since) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> caze = cq.from(Case.class);

		Predicate filter = createUserFilter(cb, cq, caze);
		if (since != null) {
			Predicate dateFilter = cb.greaterThanOrEqualTo(caze.get(Case.CHANGE_DATE), since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		Predicate deletedFilter = cb.equal(caze.get(Case.DELETED), true);
		if (filter != null) {
			filter = cb.and(filter, deletedFilter);
		} else {
			filter = deletedFilter;
		}

		cq.where(filter);
		cq.select(caze.get(Case.UUID));

		return em.createQuery(cq).getResultList();
	}

	/**
	 * Creates a filter that checks whether the case is considered "relevant" in the time frame specified by {@code fromDate} and
	 * {@code toDate}, i.e. either the {@link Symptoms#onsetDate} or {@link Case#reportDate} OR the {@link Case#outcomeDate} are
	 * within the time frame.
	 */
	public Predicate createCaseRelevanceFilter(CriteriaBuilder cb, Root<Case> from, Date fromDate, Date toDate) {
		Predicate dateFromFilter = null;
		Predicate dateToFilter = null;
		if (fromDate != null) {
			dateFromFilter = cb.or(cb.isNull(from.get(Case.OUTCOME_DATE)), cb.greaterThanOrEqualTo(from.get(Case.OUTCOME_DATE), fromDate));
		}
		if (toDate != null) {
			Join<Case, Symptoms> symptoms = from.join(Case.SYMPTOMS, JoinType.LEFT);
			dateToFilter = cb.or(
				cb.lessThanOrEqualTo(symptoms.get(Symptoms.ONSET_DATE), toDate),
				cb.and(cb.isNull(symptoms.get(Symptoms.ONSET_DATE)), cb.lessThanOrEqualTo(from.get(Case.REPORT_DATE), toDate)));
		}

		if (dateFromFilter != null && dateToFilter != null) {
			return cb.and(dateFromFilter, dateToFilter);
		} else if (dateFromFilter != null) {
			return dateFromFilter;
		} else {
			return dateToFilter;
		}
	}

	public Predicate createCriteriaFilter(CaseCriteria caseCriteria, CriteriaBuilder cb, CriteriaQuery<?> cq, From<?, Case> from) {

		Join<Case, Person> person = from.join(Case.PERSON, JoinType.LEFT);
		Join<Case, User> reportingUser = from.join(Case.REPORTING_USER, JoinType.LEFT);
		Join<Case, Region> region = from.join(Case.REGION, JoinType.LEFT);
		Join<Case, District> district = from.join(Case.DISTRICT, JoinType.LEFT);
		Join<Case, Community> community = from.join(Case.COMMUNITY, JoinType.LEFT);
		Join<Case, Facility> facility = from.join(Case.HEALTH_FACILITY, JoinType.LEFT);
		Join<Person, Location> location = person.join(Person.ADDRESS, JoinType.LEFT);
		Predicate filter = null;
		if (caseCriteria.getReportingUserRole() != null) {
			filter =
				and(cb, filter, cb.isMember(caseCriteria.getReportingUserRole(), from.join(Case.REPORTING_USER, JoinType.LEFT).get(User.USER_ROLES)));
		}
		if (caseCriteria.getDisease() != null) {
			filter = and(cb, filter, cb.equal(from.get(Case.DISEASE), caseCriteria.getDisease()));
		}
		if (caseCriteria.getOutcome() != null) {
			filter = and(cb, filter, cb.equal(from.get(Case.OUTCOME), caseCriteria.getOutcome()));
		}
		if (caseCriteria.getRegion() != null) {
			filter = and(cb, filter, cb.equal(region.get(Region.UUID), caseCriteria.getRegion().getUuid()));
		}
		if (caseCriteria.getDistrict() != null) {
			filter = and(cb, filter, cb.equal(district.get(District.UUID), caseCriteria.getDistrict().getUuid()));
		}
		if (caseCriteria.getCommunity() != null) {
			filter = and(cb, filter, cb.equal(community.get(Community.UUID), caseCriteria.getCommunity().getUuid()));
		}
		if (Boolean.TRUE.equals(caseCriteria.getExcludeSharedCases())) {
			User currentUser = getCurrentUser();
			if (currentUser != null) {
				if (currentUser.getDistrict() != null) {
					filter = and(
						cb,
						filter,
						cb.not(
							cb.and(
								cb.equal(from.get(Case.SHARED_TO_COUNTRY), true),
								cb.notEqual(region.get(District.UUID), currentUser.getDistrict().getUuid()))));
				} else if (currentUser.getRegion() != null) {
					filter = and(
						cb,
						filter,
						cb.not(
							cb.and(
								cb.equal(from.get(Case.SHARED_TO_COUNTRY), true),
								cb.notEqual(region.get(Region.UUID), currentUser.getRegion().getUuid()))));
				}
			}
		}
		if (caseCriteria.getCaseOrigin() != null) {
			filter = and(cb, filter, cb.equal(from.get(Case.CASE_ORIGIN), caseCriteria.getCaseOrigin()));
		}
		if (caseCriteria.getHealthFacility() != null) {
			filter = and(
				cb,
				filter,
				cb.equal(from.join(Case.HEALTH_FACILITY, JoinType.LEFT).get(Facility.UUID), caseCriteria.getHealthFacility().getUuid()));
		}
		if (caseCriteria.getPointOfEntry() != null) {
			filter = and(
				cb,
				filter,
				cb.equal(from.join(Case.POINT_OF_ENTRY, JoinType.LEFT).get(PointOfEntry.UUID), caseCriteria.getPointOfEntry().getUuid()));
		}
		if (caseCriteria.getSurveillanceOfficer() != null) {
			filter = and(
				cb,
				filter,
				cb.equal(from.join(Case.SURVEILLANCE_OFFICER, JoinType.LEFT).get(User.UUID), caseCriteria.getSurveillanceOfficer().getUuid()));
		}
		if (caseCriteria.getCaseClassification() != null) {
			filter = and(cb, filter, cb.equal(from.get(Case.CASE_CLASSIFICATION), caseCriteria.getCaseClassification()));
		}
		if (caseCriteria.getInvestigationStatus() != null) {
			filter = and(cb, filter, cb.equal(from.get(Case.INVESTIGATION_STATUS), caseCriteria.getInvestigationStatus()));
		}
		if (caseCriteria.getPresentCondition() != null) {
			filter = and(cb, filter, cb.equal(person.get(Person.PRESENT_CONDITION), caseCriteria.getPresentCondition()));
		}
		if (caseCriteria.getNewCaseDateFrom() != null && caseCriteria.getNewCaseDateTo() != null) {
			filter = and(
				cb,
				filter,
				createNewCaseFilter(
					cb,
					from,
					DateHelper.getStartOfDay(caseCriteria.getNewCaseDateFrom()),
					DateHelper.getEndOfDay(caseCriteria.getNewCaseDateTo()),
					caseCriteria.getNewCaseDateType()));
		}
		if (caseCriteria.getCreationDateFrom() != null) {
			filter = and(cb, filter, cb.greaterThan(from.get(Case.CREATION_DATE), DateHelper.getStartOfDay(caseCriteria.getCreationDateFrom())));
		}
		if (caseCriteria.getCreationDateTo() != null) {
			filter = and(cb, filter, cb.lessThan(from.get(Case.CREATION_DATE), DateHelper.getEndOfDay(caseCriteria.getCreationDateTo())));
		}
		if (caseCriteria.getQuarantineTo() != null) {
			filter = and(
				cb,
				filter,
				cb.between(
					from.get(Case.QUARANTINE_TO),
					DateHelper.getStartOfDay(caseCriteria.getQuarantineTo()),
					DateHelper.getEndOfDay(caseCriteria.getQuarantineTo())));
		}
		if (caseCriteria.getPerson() != null) {
			filter = and(cb, filter, cb.equal(from.join(Case.PERSON, JoinType.LEFT).get(Person.UUID), caseCriteria.getPerson().getUuid()));
		}
		if (caseCriteria.getMustHaveNoGeoCoordinates() != null && caseCriteria.getMustHaveNoGeoCoordinates() == true) {
			Join<Person, Location> personAddress = person.join(Person.ADDRESS, JoinType.LEFT);
			filter = and(
				cb,
				filter,
				cb.and(
					cb.or(cb.isNull(from.get(Case.REPORT_LAT)), cb.isNull(from.get(Case.REPORT_LON))),
					cb.or(cb.isNull(personAddress.get(Location.LATITUDE)), cb.isNull(personAddress.get(Location.LONGITUDE)))));
		}
		if (caseCriteria.getMustBePortHealthCaseWithoutFacility() != null && caseCriteria.getMustBePortHealthCaseWithoutFacility() == true) {
			filter = and(
				cb,
				filter,
				cb.and(cb.equal(from.get(Case.CASE_ORIGIN), CaseOrigin.POINT_OF_ENTRY), cb.isNull(from.join(Case.HEALTH_FACILITY, JoinType.LEFT))));
		}
		if (caseCriteria.getMustHaveCaseManagementData() != null && caseCriteria.getMustHaveCaseManagementData() == true) {
			Subquery<Prescription> prescriptionSubquery = cq.subquery(Prescription.class);
			Root<Prescription> prescriptionRoot = prescriptionSubquery.from(Prescription.class);
			prescriptionSubquery.select(prescriptionRoot).where(cb.equal(prescriptionRoot.get(Prescription.THERAPY), from.get(Case.THERAPY)));
			Subquery<Treatment> treatmentSubquery = cq.subquery(Treatment.class);
			Root<Treatment> treatmentRoot = treatmentSubquery.from(Treatment.class);
			treatmentSubquery.select(treatmentRoot).where(cb.equal(treatmentRoot.get(Treatment.THERAPY), from.get(Case.THERAPY)));
			Subquery<ClinicalVisit> clinicalVisitSubquery = cq.subquery(ClinicalVisit.class);
			Root<ClinicalVisit> clinicalVisitRoot = clinicalVisitSubquery.from(ClinicalVisit.class);
			clinicalVisitSubquery.select(clinicalVisitRoot)
				.where(cb.equal(clinicalVisitRoot.get(ClinicalVisit.CLINICAL_COURSE), from.get(Case.CLINICAL_COURSE)));
			filter = and(cb, filter, cb.or(cb.exists(prescriptionSubquery), cb.exists(treatmentSubquery), cb.exists(clinicalVisitSubquery)));
		}
		if (Boolean.TRUE.equals(caseCriteria.getWithoutResponsibleOfficer())) {
			filter = and(cb, filter, cb.isNull(from.get(Case.SURVEILLANCE_OFFICER)));
		}
		if (caseCriteria.getRelevanceStatus() != null) {
			if (caseCriteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = and(cb, filter, cb.or(cb.equal(from.get(Case.ARCHIVED), false), cb.isNull(from.get(Case.ARCHIVED))));
			} else if (caseCriteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = and(cb, filter, cb.equal(from.get(Case.ARCHIVED), true));
			}
		}
		if (caseCriteria.getDeleted() != null) {
			filter = and(cb, filter, cb.equal(from.get(Case.DELETED), caseCriteria.getDeleted()));
		}
		if (caseCriteria.getNameUuidEpidNumberLike() != null) {
			String[] textFilters = caseCriteria.getNameUuidEpidNumberLike().split("\\s+");
			for (int i = 0; i < textFilters.length; i++) {
				String textFilter = formatForLike(textFilters[i]);
				if (!DataHelper.isNullOrEmpty(textFilter)) {
					Predicate likeFilters = cb.or(
						cb.like(cb.lower(person.get(Person.FIRST_NAME)), textFilter),
						cb.like(cb.lower(person.get(Person.LAST_NAME)), textFilter),
						cb.like(cb.lower(from.get(Case.UUID)), textFilter),
						cb.like(cb.lower(from.get(Case.EPID_NUMBER)), textFilter),
						cb.like(cb.lower(facility.get(Facility.NAME)), textFilter),
						cb.like(cb.lower(from.get(Case.HEALTH_FACILITY_DETAILS)), textFilter),
						phoneNumberPredicate(cb, person.get(Person.PHONE), textFilter),
						cb.like(cb.lower(location.get(Location.CITY)), textFilter),
						cb.like(cb.lower(location.get(Location.POSTAL_CODE)), textFilter));
					filter = and(cb, filter, likeFilters);
				}
			}
		}
		if (caseCriteria.getReportingUserLike() != null) {
			String[] textFilters = caseCriteria.getReportingUserLike().split("\\s+");
			for (int i = 0; i < textFilters.length; i++) {
				String textFilter = formatForLike(textFilters[i]);
				if (!DataHelper.isNullOrEmpty(textFilter)) {
					Predicate likeFilters = cb.or(
						cb.like(cb.lower(reportingUser.get(User.FIRST_NAME)), textFilter),
						cb.like(cb.lower(reportingUser.get(User.LAST_NAME)), textFilter),
						cb.like(cb.lower(reportingUser.get(User.USER_NAME)), textFilter));
					filter = and(cb, filter, likeFilters);
				}
			}
		}
		if (caseCriteria.getSourceCaseInfoLike() != null) {
			String[] textFilters = caseCriteria.getSourceCaseInfoLike().split("\\s+");
			for (int i = 0; i < textFilters.length; i++) {
				String textFilter = formatForLike(textFilters[i]);
				if (!DataHelper.isNullOrEmpty(textFilter)) {
					Predicate likeFilters = cb.or(
						cb.like(cb.lower(person.get(Person.FIRST_NAME)), textFilter),
						cb.like(cb.lower(person.get(Person.LAST_NAME)), textFilter),
						cb.like(cb.lower(from.get(Case.UUID)), textFilter),
						cb.like(cb.lower(from.get(Case.EPID_NUMBER)), textFilter),
						cb.like(cb.lower(from.get(Case.EXTERNAL_ID)), textFilter));
					filter = and(cb, filter, likeFilters);
				}
			}
		}
		if (caseCriteria.getBirthdateYYYY() != null) {
			filter = and(cb, filter, cb.equal(person.get(Person.BIRTHDATE_YYYY), caseCriteria.getBirthdateYYYY()));
		}
		if (caseCriteria.getBirthdateMM() != null) {
			filter = and(cb, filter, cb.equal(person.get(Person.BIRTHDATE_MM), caseCriteria.getBirthdateMM()));
		}
		if (caseCriteria.getBirthdateDD() != null) {
			filter = and(cb, filter, cb.equal(person.get(Person.BIRTHDATE_DD), caseCriteria.getBirthdateDD()));
		}

		return filter;
	}

	/**
	 * Creates a filter that excludes all cases that are either {@link Case#archived} or {@link CoreAdo#deleted}.
	 */
	public Predicate createActiveCasesFilter(CriteriaBuilder cb, Root<Case> root) {
		return cb.and(cb.isFalse(root.get(Case.ARCHIVED)), cb.isFalse(root.get(Case.DELETED)));
	}

	/**
	 * Creates a default filter that should be used as the basis of queries that do not use {@link CaseCriteria}.
	 * This essentially removes {@link CoreAdo#deleted} cases from the queries.
	 */
	public Predicate createDefaultFilter(CriteriaBuilder cb, From<?, Case> root) {
		return cb.isFalse(root.get(Case.DELETED));
	}

	@Override
	public void delete(Case caze) {

		// Mark all contacts associated with this case as deleted and remove this case
		// from any contacts where it is set as the resulting case
		List<Contact> contacts = contactService.findBy(new ContactCriteria().caze(caze.toReference()), null);
		for (Contact contact : contacts) {
			contactService.delete(contact);
		}
		contacts = contactService.getAllByResultingCase(caze);
		for (Contact contact : contacts) {
			contact.setResultingCase(null);
			contactService.ensurePersisted(contact);
		}

		caze.getSamples()
			.stream()
			.filter(sample -> sample.getAssociatedContact() == null && sample.getAssociatedEventParticipant() == null)
			.forEach(sample -> sampleService.delete(sample));

		// Delete all tasks associated with this case
		List<Task> tasks = taskService.findBy(new TaskCriteria().caze(new CaseReferenceDto(caze.getUuid())));
		for (Task task : tasks) {
			taskService.delete(task);
		}

		// Delete all prescriptions/treatments/clinical visits
		if (caze.getTherapy() != null) {
			TherapyReferenceDto therapy = new TherapyReferenceDto(caze.getTherapy().getUuid());
			treatmentService.findBy(new TreatmentCriteria().therapy(therapy)).stream().forEach(t -> treatmentService.delete(t));
			prescriptionService.findBy(new PrescriptionCriteria().therapy(therapy)).stream().forEach(p -> prescriptionService.delete(p));
		}
		if (caze.getClinicalCourse() != null) {
			ClinicalCourseReferenceDto clinicalCourse = new ClinicalCourseReferenceDto(caze.getClinicalCourse().getUuid());
			clinicalVisitService.findBy(new ClinicalVisitCriteria().clinicalCourse(clinicalCourse))
				.stream()
				.forEach(c -> clinicalVisitService.delete(c));
		}

		// Mark the case as deleted
		super.delete(caze);
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, Case> casePath, Timestamp date) {
		return createChangeDateFilter(cb, casePath, date, false);
	}

	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, Case> casePath, Timestamp date, Boolean includeExtendedChangeDateFilters) {

		Predicate dateFilter = greaterThanAndNotNull(cb, casePath.get(Case.CHANGE_DATE), date);

		Join<Case, Symptoms> symptoms = casePath.join(Case.SYMPTOMS, JoinType.LEFT);
		dateFilter = cb.or(dateFilter, greaterThanAndNotNull(cb, symptoms.get(AbstractDomainObject.CHANGE_DATE), date));

		Join<Case, Hospitalization> hospitalization = casePath.join(Case.HOSPITALIZATION, JoinType.LEFT);
		dateFilter = cb.or(dateFilter, greaterThanAndNotNull(cb, hospitalization.get(AbstractDomainObject.CHANGE_DATE), date));

		Join<Hospitalization, PreviousHospitalization> previousHospitalization =
			hospitalization.join(Hospitalization.PREVIOUS_HOSPITALIZATIONS, JoinType.LEFT);
		dateFilter = cb.or(dateFilter, greaterThanAndNotNull(cb, previousHospitalization.get(AbstractDomainObject.CHANGE_DATE), date));

		dateFilter = cb.or(dateFilter, epiDataService.createChangeDateFilter(cb, casePath.join(Contact.EPI_DATA, JoinType.LEFT), date));

		Join<Case, EpiData> epiData = casePath.join(Case.EPI_DATA, JoinType.LEFT);
		dateFilter = cb.or(dateFilter, greaterThanAndNotNull(cb, epiData.get(AbstractDomainObject.CHANGE_DATE), date));

		Join<Case, Therapy> therapy = casePath.join(Case.THERAPY, JoinType.LEFT);
		dateFilter = cb.or(dateFilter, greaterThanAndNotNull(cb, therapy.get(AbstractDomainObject.CHANGE_DATE), date));

		Join<Case, ClinicalCourse> clinicalCourse = casePath.join(Case.CLINICAL_COURSE, JoinType.LEFT);
		dateFilter = cb.or(dateFilter, greaterThanAndNotNull(cb, clinicalCourse.get(AbstractDomainObject.CHANGE_DATE), date));

		Join<ClinicalCourse, HealthConditions> healthConditions = clinicalCourse.join(ClinicalCourse.HEALTH_CONDITIONS, JoinType.LEFT);
		dateFilter = cb.or(dateFilter, greaterThanAndNotNull(cb, healthConditions.get(AbstractDomainObject.CHANGE_DATE), date));

		Join<Case, MaternalHistory> maternalHistory = casePath.join(Case.MATERNAL_HISTORY, JoinType.LEFT);
		dateFilter = cb.or(dateFilter, greaterThanAndNotNull(cb, maternalHistory.get(AbstractDomainObject.CHANGE_DATE), date));

		Join<Case, PortHealthInfo> portHealthInfo = casePath.join(Case.PORT_HEALTH_INFO, JoinType.LEFT);
		dateFilter = cb.or(dateFilter, greaterThanAndNotNull(cb, portHealthInfo.get(AbstractDomainObject.CHANGE_DATE), date));

		if (includeExtendedChangeDateFilters) {
			Join<Case, Sample> caseSampleJoin = casePath.join(Case.SAMPLES, JoinType.LEFT);
			dateFilter = cb.or(dateFilter, greaterThanAndNotNull(cb, caseSampleJoin.get(AbstractDomainObject.CHANGE_DATE), date));

			Join<Sample, PathogenTest> samplePathogenTestJoin = caseSampleJoin.join(Sample.PATHOGENTESTS, JoinType.LEFT);
			dateFilter = cb.or(dateFilter, greaterThanAndNotNull(cb, samplePathogenTestJoin.get(AbstractDomainObject.CHANGE_DATE), date));

			Join<Case, Person> casePersonJoin = casePath.join(Case.PERSON, JoinType.LEFT);
			dateFilter = cb.or(dateFilter, greaterThanAndNotNull(cb, casePersonJoin.get(AbstractDomainObject.CHANGE_DATE), date));

			Join<Person, Location> personLocationJoin = casePersonJoin.join(Person.ADDRESS, JoinType.LEFT);
			dateFilter = cb.or(dateFilter, greaterThanAndNotNull(cb, personLocationJoin.get(AbstractDomainObject.CHANGE_DATE), date));
		}

		return dateFilter;
	}

	@SuppressWarnings("rawtypes")
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Case> casePath, CaseUserFilterCriteria userFilterCriteria) {

		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return null;
		}

		Predicate filterResponsible = null;
		Predicate filter = null;

		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		if (jurisdictionLevel != JurisdictionLevel.NATION && !currentUser.hasAnyUserRole(UserRole.REST_USER)) {
			// whoever created the case or is assigned to it is allowed to access it
			filterResponsible = cb.equal(casePath.join(Case.REPORTING_USER, JoinType.LEFT), currentUser);
			filterResponsible = cb.or(filterResponsible, cb.equal(casePath.join(Case.SURVEILLANCE_OFFICER, JoinType.LEFT), currentUser));
			filterResponsible = cb.or(filterResponsible, cb.equal(casePath.join(Case.CASE_OFFICER, JoinType.LEFT), currentUser));

			switch (jurisdictionLevel) {
			case REGION:
				final Region region = currentUser.getRegion();
				if (region != null) {
					filter = or(cb, filter, cb.equal(casePath.get(Case.REGION), region));
				}
				break;
			case DISTRICT:
				final District district = currentUser.getDistrict();
				if (district != null) {
					filter = or(cb, filter, cb.equal(casePath.get(Case.DISTRICT), district));
				}
				break;
			case HEALTH_FACILITY:
				final Facility healthFacility = currentUser.getHealthFacility();
				if (healthFacility != null) {
					filter = or(cb, filter, cb.equal(casePath.get(Case.HEALTH_FACILITY), healthFacility));
				}
				break;
			case COMMUNITY:
				final Community community = currentUser.getCommunity();
				if (community != null) {
					filter = or(cb, filter, cb.equal(casePath.get(Case.COMMUNITY), community));
				}
				break;
			case POINT_OF_ENTRY:
				final PointOfEntry pointOfEntry = currentUser.getPointOfEntry();
				if (pointOfEntry != null) {
					filter = or(cb, filter, cb.equal(casePath.get(Case.POINT_OF_ENTRY), pointOfEntry));
				}
				break;
			case LABORATORY:
				Subquery<Long> sampleCaseSubquery = cq.subquery(Long.class);
				Root<Sample> sampleRoot = sampleCaseSubquery.from(Sample.class);
				sampleCaseSubquery.where(sampleService.createUserFilterWithoutCase(cb, new SampleJoins(sampleRoot)));
				sampleCaseSubquery.select(sampleRoot.get(Sample.ASSOCIATED_CASE).get(Case.ID));
				filter = or(cb, filter, cb.in(casePath.get(Case.ID)).value(sampleCaseSubquery));
				break;
			default:
			}

			// get all cases based on the user's contact association
			if (userFilterCriteria == null || !userFilterCriteria.isExcludeCasesFromContacts()) {
				Subquery<Long> contactCaseSubquery = cq.subquery(Long.class);
				Root<Contact> contactRoot = contactCaseSubquery.from(Contact.class);
				contactCaseSubquery.where(contactService.createUserFilterWithoutCase(cb, cq, contactRoot));
				contactCaseSubquery.select(contactRoot.get(Contact.CAZE).get(Case.ID));
				filter = or(cb, filter, cb.in(casePath.get(Case.ID)).value(contactCaseSubquery));
			}

			// users can only be assigned to a task when they have also access to the case
			//Join<Case, Task> tasksJoin = from.join(Case.TASKS, JoinType.LEFT);
			//filter = cb.or(filter, cb.equal(tasksJoin.get(Task.ASSIGNEE_USER), user));

			// all users (without specific restrictions) get access to cases that have been made available to the whole country
			if ((userFilterCriteria == null || !userFilterCriteria.isExcludeSharedCases())
				&& !featureConfigurationFacade.isFeatureDisabled(FeatureType.NATIONAL_CASE_SHARING)) {
				filter = or(cb, filter, cb.isTrue(casePath.get(Case.SHARED_TO_COUNTRY)));
			}
		}

		// only show cases of a specific disease if a limited disease is set
		if (currentUser.getLimitedDisease() != null) {
			filter = and(cb, filter, cb.equal(casePath.get(Case.DISEASE), currentUser.getLimitedDisease()));
		}

		// port health users can only see port health cases
		if (UserRole.isPortHealthUser(currentUser.getUserRoles())) {
			filter = and(cb, filter, cb.equal(casePath.get(Case.CASE_ORIGIN), CaseOrigin.POINT_OF_ENTRY));
		}

		filter = or(cb, filter, filterResponsible);

		return filter;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Case, Case> casePath) {
		return createUserFilter(cb, cq, casePath, null);
	}

	/**
	 * Creates a filter that checks whether the case has "started" within the time frame specified by {@code fromDate} and {@code toDate}.
	 * By default (if {@code newCaseDateType} is null), this logic looks at the {@link Symptoms#onsetDate} first or, if this is null,
	 * the {@link Case#reportDate}.
	 */
	private Predicate createNewCaseFilter(CriteriaBuilder cb, From<?, Case> caze, Date fromDate, Date toDate, NewCaseDateType newCaseDateType) {

		Join<Case, Symptoms> symptoms = caze.join(Case.SYMPTOMS, JoinType.LEFT);

		toDate = DateHelper.getEndOfDay(toDate);

		Predicate onsetDateFilter = cb.between(symptoms.get(Symptoms.ONSET_DATE), fromDate, toDate);
		Predicate reportDateFilter = cb.between(caze.get(Case.REPORT_DATE), fromDate, toDate);

		Predicate newCaseFilter = null;
		if (newCaseDateType == null || newCaseDateType == NewCaseDateType.MOST_RELEVANT) {
			newCaseFilter = cb.or(onsetDateFilter, cb.and(cb.isNull(symptoms.get(Symptoms.ONSET_DATE)), reportDateFilter));
		} else if (newCaseDateType == NewCaseDateType.ONSET) {
			newCaseFilter = onsetDateFilter;
		} else {
			newCaseFilter = reportDateFilter;
		}

		return newCaseFilter;
	}
}
