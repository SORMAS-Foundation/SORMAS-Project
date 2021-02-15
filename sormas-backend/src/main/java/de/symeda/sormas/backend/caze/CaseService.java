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
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
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
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.followup.FollowUpLogic;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.therapy.PrescriptionCriteria;
import de.symeda.sormas.api.therapy.TherapyReferenceDto;
import de.symeda.sormas.api.therapy.TreatmentCriteria;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourse;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisit;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisitService;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb;
import de.symeda.sormas.backend.disease.DiseaseVariant;
import de.symeda.sormas.backend.epidata.EpiDataService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.hospitalization.Hospitalization;
import de.symeda.sormas.backend.hospitalization.HospitalizationService;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleJoins;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasShareInfoService;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.therapy.Prescription;
import de.symeda.sormas.backend.therapy.PrescriptionService;
import de.symeda.sormas.backend.therapy.Treatment;
import de.symeda.sormas.backend.therapy.TreatmentService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DateHelper8;
import de.symeda.sormas.backend.visit.Visit;
import de.symeda.sormas.utils.CaseJoins;

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
	@EJB
	private DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;

	@EJB
	private CaseJurisdictionChecker caseJurisdictionChecker;
	@EJB
	private SormasToSormasShareInfoService sormasToSormasShareInfoService;

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
		CaseJoins<Case> joins = new CaseJoins<>(from);

		Predicate filter = createCriteriaFilter(caseCriteria, cb, cq, from, joins);
		if (!ignoreUserFilter) {
			filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(cb, cq, from));
		}

		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.asc(from.get(Case.CREATION_DATE)));

		return em.createQuery(cq).getResultList();
	}

	public List<Case> getAllActiveCasesAfter(Date date, boolean includeExtendedChangeDateFilters) {

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
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		cq.where(filter);
		cq.select(from.get(Case.UUID));

		return em.createQuery(cq).getResultList();
	}

	public Long countCasesForMap(Region region, District district, Disease disease, Date from, Date to) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Case> caze = cq.from(getElementClass());

		Predicate filter = createMapCasesFilter(cb, cq, caze, region, district, disease, from, to);

		if (filter != null) {
			cq.where(filter);
			cq.select(cb.count(caze.get(Case.ID)));

			return em.createQuery(cq).getSingleResult();
		}

		return 0L;
	}

	public List<MapCaseDto> getCasesForMap(Region region, District district, Disease disease, Date from, Date to) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MapCaseDto> cq = cb.createQuery(MapCaseDto.class);
		Root<Case> caze = cq.from(getElementClass());

		CaseJoins<Case> joins = new CaseJoins<>(caze);

		Predicate filter = createMapCasesFilter(cb, cq, caze, region, district, disease, from, to);

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

	private Predicate createMapCasesFilter(
		CriteriaBuilder cb,
		CriteriaQuery<?> cq,
		Root<Case> root,
		Region region,
		District district,
		Disease disease,
		Date from,
		Date to) {
		Predicate filter = createActiveCasesFilter(cb, root);
		filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(cb, cq, root, new CaseUserFilterCriteria().excludeCasesFromContacts(true)));
		filter = CriteriaBuilderHelper.and(cb, filter, createCaseRelevanceFilter(cb, root, from, to));

		if (region != null) {
			Predicate regionFilter = cb.equal(root.get(Case.REGION), region);
			if (filter != null) {
				filter = cb.and(filter, regionFilter);
			} else {
				filter = regionFilter;
			}
		}

		if (district != null) {
			Predicate districtFilter = cb.equal(root.get(Case.DISTRICT), district);
			if (filter != null) {
				filter = cb.and(filter, districtFilter);
			} else {
				filter = districtFilter;
			}
		}

		if (disease != null) {
			Predicate diseaseFilter = cb.equal(root.get(Case.DISEASE), disease);
			if (filter != null) {
				filter = cb.and(filter, diseaseFilter);
			} else {
				filter = diseaseFilter;
			}
		}

		return filter;
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
			cb.equal(cb.lower(root.get(Case.EXTERNAL_TOKEN)), searchTerm.toLowerCase()),
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

	public <T extends AbstractDomainObject> Predicate createCriteriaFilter(
		CaseCriteria caseCriteria,
		CriteriaBuilder cb,
		CriteriaQuery<?> cq,
		From<T, Case> from,
		CaseJoins<T> joins) {

		Join<Case, Person> person = joins.getPerson();
		Join<Case, User> reportingUser = joins.getReportingUser();
		Join<Case, Region> region = joins.getRegion();
		Join<Case, District> district = joins.getDistrict();
		Join<Case, Community> community = joins.getCommunity();
		Join<Case, Facility> facility = joins.getFacility();
		Join<Person, Location> location = person.join(Person.ADDRESS, JoinType.LEFT);
		Join<Case, DiseaseVariant> diseaseVariant = joins.getDiseaseVariant();

		Predicate filter = null;
		if (caseCriteria.getReportingUserRole() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.isMember(caseCriteria.getReportingUserRole(), from.join(Case.REPORTING_USER, JoinType.LEFT).get(User.USER_ROLES)));
		}
		if (caseCriteria.getDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.DISEASE), caseCriteria.getDisease()));
		}
		if (caseCriteria.getDiseaseVariant() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(diseaseVariant.get(DiseaseVariant.UUID), caseCriteria.getDiseaseVariant().getUuid()));
		}
		if (caseCriteria.getOutcome() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.OUTCOME), caseCriteria.getOutcome()));
		}
		if (caseCriteria.getRegion() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(region.get(Region.UUID), caseCriteria.getRegion().getUuid()));
		}
		if (caseCriteria.getDistrict() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(district.get(District.UUID), caseCriteria.getDistrict().getUuid()));
		}
		if (caseCriteria.getCommunity() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(community.get(Community.UUID), caseCriteria.getCommunity().getUuid()));
		}
		if (caseCriteria.getFollowUpStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.FOLLOW_UP_STATUS), caseCriteria.getFollowUpStatus()));
		}
		if (caseCriteria.getFollowUpUntilFrom() != null && caseCriteria.getFollowUpUntilTo() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.between(from.get(Case.FOLLOW_UP_UNTIL), caseCriteria.getFollowUpUntilFrom(), caseCriteria.getFollowUpUntilTo()));
		} else if (caseCriteria.getFollowUpUntilFrom() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.greaterThanOrEqualTo(from.get(Case.FOLLOW_UP_UNTIL), caseCriteria.getFollowUpUntilFrom()));
		} else if (caseCriteria.getFollowUpUntilTo() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.lessThanOrEqualTo(from.get(Case.FOLLOW_UP_UNTIL), caseCriteria.getFollowUpUntilTo()));
		}
		if (caseCriteria.getReportDateTo() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.lessThanOrEqualTo(from.get(Case.REPORT_DATE), caseCriteria.getReportDateTo()));
		}
		if (caseCriteria.getCaseOrigin() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.CASE_ORIGIN), caseCriteria.getCaseOrigin()));
		}
		if (caseCriteria.getHealthFacility() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(from.join(Case.HEALTH_FACILITY, JoinType.LEFT).get(Facility.UUID), caseCriteria.getHealthFacility().getUuid()));
		}
		if (caseCriteria.getPointOfEntry() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(from.join(Case.POINT_OF_ENTRY, JoinType.LEFT).get(PointOfEntry.UUID), caseCriteria.getPointOfEntry().getUuid()));
		}
		if (caseCriteria.getSurveillanceOfficer() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(from.join(Case.SURVEILLANCE_OFFICER, JoinType.LEFT).get(User.UUID), caseCriteria.getSurveillanceOfficer().getUuid()));
		}
		if (caseCriteria.getCaseClassification() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.CASE_CLASSIFICATION), caseCriteria.getCaseClassification()));
		}
		if (caseCriteria.getInvestigationStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.INVESTIGATION_STATUS), caseCriteria.getInvestigationStatus()));
		}
		if (caseCriteria.getPresentCondition() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(person.get(Person.PRESENT_CONDITION), caseCriteria.getPresentCondition()));
		}
		if (caseCriteria.getNewCaseDateFrom() != null && caseCriteria.getNewCaseDateTo() != null) {
			filter = CriteriaBuilderHelper.and(
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
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.greaterThan(from.get(Case.CREATION_DATE), DateHelper.getStartOfDay(caseCriteria.getCreationDateFrom())));
		}
		if (caseCriteria.getCreationDateTo() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.lessThan(from.get(Case.CREATION_DATE), DateHelper.getEndOfDay(caseCriteria.getCreationDateTo())));
		}
		if (caseCriteria.getQuarantineTo() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.between(
					from.get(Case.QUARANTINE_TO),
					DateHelper.getStartOfDay(caseCriteria.getQuarantineTo()),
					DateHelper.getEndOfDay(caseCriteria.getQuarantineTo())));
		}
		if (caseCriteria.getPerson() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(person.get(Person.UUID), caseCriteria.getPerson().getUuid()));
		}
		if (caseCriteria.getMustHaveNoGeoCoordinates() != null && caseCriteria.getMustHaveNoGeoCoordinates() == true) {
			Join<Person, Location> personAddress = person.join(Person.ADDRESS, JoinType.LEFT);
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.and(
					cb.or(cb.isNull(from.get(Case.REPORT_LAT)), cb.isNull(from.get(Case.REPORT_LON))),
					cb.or(cb.isNull(personAddress.get(Location.LATITUDE)), cb.isNull(personAddress.get(Location.LONGITUDE)))));
		}
		if (caseCriteria.getMustBePortHealthCaseWithoutFacility() != null && caseCriteria.getMustBePortHealthCaseWithoutFacility() == true) {
			filter = CriteriaBuilderHelper.and(
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
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.or(cb.exists(prescriptionSubquery), cb.exists(treatmentSubquery), cb.exists(clinicalVisitSubquery)));
		}
		if (Boolean.TRUE.equals(caseCriteria.getWithoutResponsibleOfficer())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNull(from.get(Case.SURVEILLANCE_OFFICER)));
		}
		if (Boolean.TRUE.equals(caseCriteria.getWithExtendedQuarantine())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isTrue(from.get(Case.QUARANTINE_EXTENDED)));
		}
		if (Boolean.TRUE.equals(caseCriteria.getWithReducedQuarantine())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isTrue(from.get(Case.QUARANTINE_REDUCED)));
		}
		if (caseCriteria.getRelevanceStatus() != null) {
			if (caseCriteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.or(cb.equal(from.get(Case.ARCHIVED), false), cb.isNull(from.get(Case.ARCHIVED))));
			} else if (caseCriteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.ARCHIVED), true));
			}
		}
		if (caseCriteria.getDeleted() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.DELETED), caseCriteria.getDeleted()));
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
						cb.like(cb.lower(from.get(Case.EXTERNAL_ID)), textFilter),
						cb.like(cb.lower(from.get(Case.EXTERNAL_TOKEN)), textFilter),
						cb.like(cb.lower(from.get(Case.HEALTH_FACILITY_DETAILS)), textFilter),
						phoneNumberPredicate(cb, person.get(Person.PHONE), textFilter),
						cb.like(cb.lower(location.get(Location.CITY)), textFilter),
						cb.like(cb.lower(location.get(Location.POSTAL_CODE)), textFilter));
					filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
				}
			}
		}
		boolean hasEventLikeCriteria = caseCriteria.getEventLike() != null && !caseCriteria.getEventLike().trim().isEmpty();
		boolean hasOnlyCasesWithEventsCriteria = Boolean.TRUE.equals(caseCriteria.getOnlyCasesWithEvents());
		if (hasEventLikeCriteria || hasOnlyCasesWithEventsCriteria) {
			Join<Case, EventParticipant> eventParticipant = joins.getEventParticipants();
			Join<EventParticipant, Event> event = eventParticipant.join(EventParticipant.EVENT, JoinType.LEFT);

			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.isFalse(event.get(Event.DELETED)),
				cb.isFalse(event.get(Event.ARCHIVED)),
				cb.isFalse(eventParticipant.get(EventParticipant.DELETED)));

			if (hasEventLikeCriteria) {
				String[] textFilters = caseCriteria.getEventLike().trim().split("\\s+");
				for (int i = 0; i < textFilters.length; i++) {
					String textFilter = formatForLike(textFilters[i]);
					if (!DataHelper.isNullOrEmpty(textFilter)) {
						Predicate likeFilters = cb.or(
							cb.like(cb.lower(event.get(Event.EVENT_DESC)), textFilter),
							cb.like(cb.lower(event.get(Event.EVENT_TITLE)), textFilter),
							cb.like(cb.lower(event.get(Event.UUID)), textFilter));
						filter = CriteriaBuilderHelper.and(cb, filter, likeFilters, cb.isFalse(eventParticipant.get(EventParticipant.DELETED)));
					}
				}
			}
			if (hasOnlyCasesWithEventsCriteria) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.isNotNull(event.get(Event.ID)));
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
					filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
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
					filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
				}
			}
		}
		if (caseCriteria.getBirthdateYYYY() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(person.get(Person.BIRTHDATE_YYYY), caseCriteria.getBirthdateYYYY()));
		}
		if (caseCriteria.getBirthdateMM() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(person.get(Person.BIRTHDATE_MM), caseCriteria.getBirthdateMM()));
		}
		if (caseCriteria.getBirthdateDD() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(person.get(Person.BIRTHDATE_DD), caseCriteria.getBirthdateDD()));
		}
		if (Boolean.TRUE.equals(caseCriteria.getOnlyContactsFromOtherInstances())) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.or(
					cb.isNotNull(joins.getSormasToSormasShareInfo().get(SormasToSormasShareInfo.CAZE)),
					cb.isNotNull(from.get(Contact.SORMAS_TO_SORMAS_ORIGIN_INFO))));
		}

		return filter;
	}

	/**
	 * Creates a filter that excludes all cases that are either {@link Case#archived} or {@link CoreAdo#deleted}.
	 */
	public Predicate createActiveCasesFilter(CriteriaBuilder cb, Root<Case> root) {
		return cb.and(cb.isFalse(root.get(Case.ARCHIVED)), cb.isFalse(root.get(Case.DELETED)));
	}

	public Predicate createActiveCasesFilter(CriteriaBuilder cb, Join<?, Case> join) {
		return cb.and(cb.isFalse(join.get(Case.ARCHIVED)), cb.isFalse(join.get(Case.DELETED)));
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
		List<Task> tasks = taskService.findBy(new TaskCriteria().caze(new CaseReferenceDto(caze.getUuid())), true);
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

		//Remove all events linked to case by removing the case_id from event participant
		caze.getEventParticipants().stream().forEach(eventParticipant -> eventParticipant.setResultingCase(null));

		// Mark the case as deleted
		super.delete(caze);
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, Case> casePath, Timestamp date) {
		return createChangeDateFilter(cb, casePath, date, false);
	}

	/**
	 * 
	 * @param cb
	 * @param casePath
	 * @param date
	 * @param includeExtendedChangeDateFilters
	 *            additional change dates filters for: sample, pathogenTests, patient and location
	 * @return
	 */
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, Case> casePath, Timestamp date, boolean includeExtendedChangeDateFilters) {

		Builder<Predicate> filters = Stream.builder();

		filters.add(changeDateFilter(cb, date, casePath));
		filters.add(changeDateFilter(cb, date, casePath, Case.SYMPTOMS));

		Join<Case, Hospitalization> hospitalization = casePath.join(Case.HOSPITALIZATION, JoinType.LEFT);
		filters.add(changeDateFilter(cb, date, hospitalization));
		filters.add(changeDateFilter(cb, date, hospitalization, Hospitalization.PREVIOUS_HOSPITALIZATIONS));

		filters.add(epiDataService.createChangeDateFilter(cb, casePath.join(Contact.EPI_DATA, JoinType.LEFT), date));

		filters.add(changeDateFilter(cb, date, casePath, Case.THERAPY));

		Join<Case, ClinicalCourse> clinicalCourse = casePath.join(Case.CLINICAL_COURSE, JoinType.LEFT);
		filters.add(changeDateFilter(cb, date, clinicalCourse));
		filters.add(changeDateFilter(cb, date, clinicalCourse, ClinicalCourse.HEALTH_CONDITIONS));

		filters.add(changeDateFilter(cb, date, casePath, Case.MATERNAL_HISTORY));
		filters.add(changeDateFilter(cb, date, casePath, Case.PORT_HEALTH_INFO));

		filters.add(changeDateFilter(cb, date, casePath, Case.SORMAS_TO_SORMAS_SHARES));

		if (includeExtendedChangeDateFilters) {
			Join<Case, Sample> caseSampleJoin = casePath.join(Case.SAMPLES, JoinType.LEFT);
			filters.add(changeDateFilter(cb, date, caseSampleJoin));
			filters.add(changeDateFilter(cb, date, caseSampleJoin, Sample.PATHOGENTESTS));

			Join<Case, Person> casePersonJoin = casePath.join(Case.PERSON, JoinType.LEFT);
			filters.add(changeDateFilter(cb, date, casePersonJoin));
			filters.add(changeDateFilter(cb, date, casePersonJoin, Person.ADDRESS));
		}

		return cb.or(filters.build().toArray(Predicate[]::new));
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
		if (jurisdictionLevel != JurisdictionLevel.NATION && !currentUser.hasAnyUserRole(UserRole.REST_USER, UserRole.REST_EXTERNAL_VISITS_USER)) {
			// whoever created the case or is assigned to it is allowed to access it
			if (userFilterCriteria == null || (userFilterCriteria.getIncludeCasesFromOtherJurisdictions())) {
				filterResponsible = cb.equal(casePath.get(Case.REPORTING_USER).get(User.ID), currentUser.getId());
				filterResponsible = cb.or(filterResponsible, cb.equal(casePath.get(Case.SURVEILLANCE_OFFICER).get(User.ID), currentUser.getId()));
				filterResponsible = cb.or(filterResponsible, cb.equal(casePath.get(Case.CASE_OFFICER).get(User.ID), currentUser.getId()));
			}

			switch (jurisdictionLevel) {
			case REGION:
				final Region region = currentUser.getRegion();
				if (region != null) {
					filter = CriteriaBuilderHelper.or(cb, filter, cb.equal(casePath.get(Case.REGION).get(Region.ID), region.getId()));
				}
				break;
			case DISTRICT:
				final District district = currentUser.getDistrict();
				if (district != null) {
					filter = CriteriaBuilderHelper.or(cb, filter, cb.equal(casePath.get(Case.DISTRICT).get(District.ID), district.getId()));
				}
				break;
			case HEALTH_FACILITY:
				final Facility healthFacility = currentUser.getHealthFacility();
				if (healthFacility != null) {
					filter =
						CriteriaBuilderHelper.or(cb, filter, cb.equal(casePath.get(Case.HEALTH_FACILITY).get(Facility.ID), healthFacility.getId()));
				}
				break;
			case COMMUNITY:
				final Community community = currentUser.getCommunity();
				if (community != null) {
					filter = CriteriaBuilderHelper.or(cb, filter, cb.equal(casePath.get(Case.COMMUNITY).get(Community.ID), community.getId()));
				}
				break;
			case POINT_OF_ENTRY:
				final PointOfEntry pointOfEntry = currentUser.getPointOfEntry();
				if (pointOfEntry != null) {
					filter =
						CriteriaBuilderHelper.or(cb, filter, cb.equal(casePath.get(Case.POINT_OF_ENTRY).get(PointOfEntry.ID), pointOfEntry.getId()));
				}
				break;
			case LABORATORY:
				Subquery<Long> sampleCaseSubquery = cq.subquery(Long.class);
				Root<Sample> sampleRoot = sampleCaseSubquery.from(Sample.class);
				sampleCaseSubquery.where(sampleService.createUserFilterWithoutCase(cb, new SampleJoins(sampleRoot)));
				sampleCaseSubquery.select(sampleRoot.get(Sample.ASSOCIATED_CASE).get(Case.ID));
				filter = CriteriaBuilderHelper.or(cb, filter, cb.in(casePath.get(Case.ID)).value(sampleCaseSubquery));
				break;
			default:
			}

			// get all cases based on the user's contact association
			if (userFilterCriteria == null
				|| (!userFilterCriteria.isExcludeCasesFromContacts()
					&& Boolean.TRUE.equals(userFilterCriteria.getIncludeCasesFromOtherJurisdictions()))) {
				Subquery<Long> contactCaseSubquery = cq.subquery(Long.class);
				Root<Contact> contactRoot = contactCaseSubquery.from(Contact.class);
				contactCaseSubquery.where(contactService.createUserFilterWithoutCase(cb, cq, contactRoot));
				contactCaseSubquery.select(contactRoot.get(Contact.CAZE).get(Case.ID));
				filter = CriteriaBuilderHelper.or(cb, filter, cb.in(casePath.get(Case.ID)).value(contactCaseSubquery));
			}

			// users can only be assigned to a task when they have also access to the case
			//Join<Case, Task> tasksJoin = from.join(Case.TASKS, JoinType.LEFT);
			//filter = cb.or(filter, cb.equal(tasksJoin.get(Task.ASSIGNEE_USER), user));

			// all users (without specific restrictions) get access to cases that have been made available to the whole country
			if ((userFilterCriteria == null || userFilterCriteria.getIncludeCasesFromOtherJurisdictions())
				&& !featureConfigurationFacade.isFeatureDisabled(FeatureType.NATIONAL_CASE_SHARING)) {
				filter = CriteriaBuilderHelper.or(cb, filter, cb.isTrue(casePath.get(Case.SHARED_TO_COUNTRY)));
			}
		}

		// only show cases of a specific disease if a limited disease is set
		if (currentUser.getLimitedDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(casePath.get(Case.DISEASE), currentUser.getLimitedDisease()));
		}

		// port health users can only see port health cases
		if (UserRole.isPortHealthUser(currentUser.getUserRoles())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(casePath.get(Case.CASE_ORIGIN), CaseOrigin.POINT_OF_ENTRY));
		}

		filter = CriteriaBuilderHelper.or(cb, filter, filterResponsible);

		return filter;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Case> casePath) {
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

	public Predicate isInJurisdictionOrOwned(CriteriaBuilder cb, CaseJoins<Case> joins) {

		final User currentUser = userService.getCurrentUser();

		final Predicate reportedByCurrentUser =
			cb.and(cb.isNotNull(joins.getReportingUser()), cb.equal(joins.getReportingUser().get(User.UUID), currentUser.getUuid()));

		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		final Predicate jurisdictionPredicate;
		switch (jurisdictionLevel) {
		case NATION:
			jurisdictionPredicate = cb.conjunction();
			break;
		case REGION:
			jurisdictionPredicate = cb.equal(joins.getRegion().get(Region.ID), currentUser.getRegion().getId());
			break;
		case DISTRICT:
			jurisdictionPredicate = cb.equal(joins.getDistrict().get(District.ID), currentUser.getDistrict().getId());
			break;
		case COMMUNITY:
			jurisdictionPredicate = cb.equal(joins.getCommunity().get(Community.ID), currentUser.getCommunity().getId());
			break;
		case HEALTH_FACILITY:
			jurisdictionPredicate = cb.equal(joins.getFacility().get(Facility.ID), currentUser.getHealthFacility().getId());
			break;
		case POINT_OF_ENTRY:
			jurisdictionPredicate = cb.equal(joins.getPointOfEntry().get(PointOfEntry.ID), currentUser.getPointOfEntry().getId());
			break;
		case LABORATORY:
		case EXTERNAL_LABORATORY:
		case NONE:
		default:
			jurisdictionPredicate = cb.disjunction();
		}
		return cb.or(reportedByCurrentUser, jurisdictionPredicate);
	}

	public Case getRelevantCaseForFollowUp(Person person, Disease disease, Date referenceDate) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> cq = cb.createQuery(Case.class);
		Root<Case> caseRoot = cq.from(Case.class);

		Predicate filter = CriteriaBuilderHelper
			.and(cb, createDefaultFilter(cb, caseRoot), buildRelevantCasesFilterForFollowUp(person, disease, referenceDate, cb, caseRoot));
		cq.where(filter);

		return em.createQuery(cq).getResultStream().findFirst().orElse(null);
	}

	/**
	 * Returns a filter that can be used to retrieve all cases with the specified
	 * person and disease whose report date is before the reference date and,
	 * if available, whose follow-up until date is after the reference date,
	 * including an offset to allow some tolerance.
	 */
	private Predicate buildRelevantCasesFilterForFollowUp(Person person, Disease disease, Date referenceDate, CriteriaBuilder cb, Root<Case> from) {

		Date referenceDateStart = DateHelper.getStartOfDay(referenceDate);
		Date referenceDateEnd = DateHelper.getEndOfDay(referenceDate);

		Predicate filter = CriteriaBuilderHelper.and(cb, cb.equal(from.get(Case.PERSON), person), cb.equal(from.get(Case.DISEASE), disease));

		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			cb.lessThanOrEqualTo(from.get(Case.REPORT_DATE), DateHelper.addDays(referenceDateEnd, FollowUpLogic.ALLOWED_DATE_OFFSET)));

		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			CriteriaBuilderHelper.or(
				cb,
				// If the case does not have a follow-up until date, use the case report date as a fallback
				CriteriaBuilderHelper.and(
					cb,
					cb.isNull(from.get(Case.FOLLOW_UP_UNTIL)),
					cb.greaterThanOrEqualTo(
						from.get(Case.REPORT_DATE),
						DateHelper.subtractDays(referenceDateStart, FollowUpLogic.ALLOWED_DATE_OFFSET))),
				cb.greaterThanOrEqualTo(
					from.get(Case.FOLLOW_UP_UNTIL),
					DateHelper.subtractDays(referenceDateStart, FollowUpLogic.ALLOWED_DATE_OFFSET))));

		return filter;
	}

	/**
	 * Calculates and sets the follow-up until date and status of the case. If
	 * the date has been overwritten by a user, only the status changes and
	 * extensions of the follow-up until date based on missed visits are executed.
	 * <ul>
	 * <li>Disease with no follow-up: Leave empty and set follow-up status to "No
	 * follow-up"</li>
	 * <li>Others: Use follow-up duration of the disease. Reference for calculation
	 * is the reporting date If the last visit was not cooperative and happened
	 * at the last date of case tracing, we need to do an additional visit.</li>
	 * </ul>
	 */
	public void updateFollowUpUntilAndStatus(Case caze) {

		Disease disease = caze.getDisease();
		boolean changeStatus = caze.getFollowUpStatus() != FollowUpStatus.CANCELED && caze.getFollowUpStatus() != FollowUpStatus.LOST;

		if (!diseaseConfigurationFacade.hasFollowUp(disease)) {
			caze.setFollowUpUntil(null);
			if (changeStatus) {
				caze.setFollowUpStatus(FollowUpStatus.NO_FOLLOW_UP);
			}
		} else {
			int followUpDuration = diseaseConfigurationFacade.getCaseFollowUpDuration(disease);
			LocalDate beginDate = DateHelper8.toLocalDate(caze.getReportDate());
			LocalDate untilDate =
				caze.isOverwriteFollowUpUntil() ? DateHelper8.toLocalDate(caze.getFollowUpUntil()) : beginDate.plusDays(followUpDuration);

			Visit lastVisit;
			boolean additionalVisitNeeded;
			do {
				additionalVisitNeeded = false;
				lastVisit = caze.getVisits().stream().max(Comparator.comparing(Visit::getVisitDateTime)).orElse(null);
				if (lastVisit != null) {
					// if the last visit was not cooperative and happened at the last date of
					// contact tracing ..
					if (lastVisit.getVisitStatus() != VisitStatus.COOPERATIVE
						&& DateHelper8.toLocalDate(lastVisit.getVisitDateTime()).isEqual(untilDate)) {
						// .. we need to do an additional visit
						additionalVisitNeeded = true;
						untilDate = untilDate.plusDays(1);
					}
					// if the last visit was cooperative and happened at the last date of contact tracing,
					// revert the follow-up until date back to the original
					if (!caze.isOverwriteFollowUpUntil()
						&& lastVisit.getVisitStatus() == VisitStatus.COOPERATIVE
						&& DateHelper8.toLocalDate(lastVisit.getVisitDateTime()).isEqual(beginDate.plusDays(followUpDuration))) {
						additionalVisitNeeded = false;
						untilDate = beginDate.plusDays(followUpDuration);
					}
				}
			}
			while (additionalVisitNeeded);

			caze.setFollowUpUntil(DateHelper8.toDate(untilDate));
			if (changeStatus) {
				if (lastVisit != null && DateHelper.isSameDay(lastVisit.getVisitDateTime(), DateHelper8.toDate(untilDate))) {
					caze.setFollowUpStatus(FollowUpStatus.COMPLETED);
				} else {
					caze.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
				}
			}
		}

		ensurePersisted(caze);
	}

	/**
	 * @param caseUuids
	 *            {@link Case}s identified by {@code uuid} to be archived or not.
	 * @param archived
	 *            {@code true} archives the Case, {@code false} unarchives it.
	 * @see {@link Case#setArchived(boolean)}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateArchived(List<String> caseUuids, boolean archived) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Case> cu = cb.createCriteriaUpdate(Case.class);
		Root<Case> root = cu.from(Case.class);

		cu.set(Case.CHANGE_DATE, Timestamp.from(Instant.now()));
		cu.set(root.get(Case.ARCHIVED), archived);

		cu.where(root.get(Case.UUID).in(caseUuids));

		em.createQuery(cu).executeUpdate();
	}

	public boolean isCaseEditAllowed(Case caze) {
		if (caze.getSormasToSormasOriginInfo() != null) {
			return caze.getSormasToSormasOriginInfo().isOwnershipHandedOver();
		}

		return caseJurisdictionChecker.isInJurisdictionOrOwned(caze) && !sormasToSormasShareInfoService.isCaseOwnershipHandedOver(caze);
	}
}
