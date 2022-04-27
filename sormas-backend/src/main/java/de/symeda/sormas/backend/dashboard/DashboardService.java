package de.symeda.sormas.backend.dashboard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.dashboard.DashboardCaseDto;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.dashboard.DashboardEventDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseJoins;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.caze.CaseUserFilterCriteria;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventJoins;
import de.symeda.sormas.backend.event.EventQueryContext;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless
@LocalBean
public class DashboardService {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private CaseService caseService;

	@EJB
	private EventService eventService;

	public List<DashboardCaseDto> getCases(DashboardCriteria dashboardCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardCaseDto> cq = cb.createQuery(DashboardCaseDto.class);
		Root<Case> caze = cq.from(Case.class);

		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);
		final CaseJoins joins = caseQueryContext.getJoins();
		Join<Case, Person> person = joins.getPerson();

		Predicate filter = caseService.createUserFilter(caseQueryContext, new CaseUserFilterCriteria().excludeCasesFromContacts(true));
		Predicate criteriaFilter = createCaseCriteriaFilter(dashboardCriteria, caseQueryContext);
		filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);

		List<DashboardCaseDto> result;
		if (filter != null) {
			cq.where(filter);
			cq.multiselect(
				caze.get(Case.ID),
				caze.get(Case.UUID),
				caze.get(Case.REPORT_DATE),
				caze.get(Case.CASE_CLASSIFICATION),
				caze.get(Case.DISEASE),
				person.get(Person.PRESENT_CONDITION),
				person.get(Person.CAUSE_OF_DEATH_DISEASE),
				caze.get(Case.QUARANTINE_FROM),
				caze.get(Case.QUARANTINE_TO),
				caze.get(Case.CASE_REFERENCE_DEFINITION));

			result = em.createQuery(cq).getResultList();
		} else {
			result = Collections.emptyList();
		}

		return result;
	}
	
	public Map<PathogenTestResultType, Long> getNewTestResultCountByResultType(DashboardCriteria dashboardCriteria) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<CaseTestsDto> cq = cb.createQuery(CaseTestsDto.class);
		final Root<Case> caze = cq.from(Case.class);
		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);
		final CaseJoins joins = caseQueryContext.getJoins();
		final Join<Case, Sample> samples = joins.getSamples();

		cq.multiselect(caze.get(Case.ID), samples.get(Sample.PATHOGEN_TEST_RESULT), samples.get(Sample.SAMPLE_DATE_TIME));
		cq.distinct(true);

		Predicate filter = caseService.createUserFilter(caseQueryContext, new CaseUserFilterCriteria().excludeCasesFromContacts(true));
		final Predicate criteriaFilter = createCaseCriteriaFilter(dashboardCriteria, caseQueryContext);
		final Predicate sampleFilter = cb.and(
			cb.isFalse(samples.get(Sample.DELETED)),
			cb.or(cb.isNull(samples.get(Sample.SPECIMEN_CONDITION)), cb.equal(samples.get(Sample.SPECIMEN_CONDITION), SpecimenCondition.ADEQUATE)));
		filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter, sampleFilter);

		cq.where(filter);

		final List<CaseTestsDto> queryResult = QueryHelper.getResultList(em, cq, null, null);

		final Map<PathogenTestResultType, Long> result = new HashMap<>();
		final Map<Long, CaseTestsDto> caseTestsMap = new HashMap<>();
		queryResult.forEach(caseTestsDto -> {
			final Long caseId = caseTestsDto.getCaseId();
			if (!caseTestsMap.containsKey(caseId) || caseTestsMap.get(caseId).getSampleDateTime().before(caseTestsDto.getSampleDateTime())) {
				caseTestsMap.put(caseId, caseTestsDto);
			}
		});
		final Collection<CaseTestsDto> caseTestsDtos = caseTestsMap.values();
		for (PathogenTestResultType pathogenTestResultType : PathogenTestResultType.values()) {
			long count = caseTestsDtos.stream().filter(caseTestsDto -> caseTestsDto.getPathogenTestResultType() == pathogenTestResultType).count();
			if (count > 0) {
				result.put(pathogenTestResultType, count);
			}
		}

		return result;
	}

	public static class CaseTestsDto implements Serializable {

		private Long caseId;
		private PathogenTestResultType pathogenTestResultType;
		private Date sampleDateTime;

		public CaseTestsDto(Long caseId, PathogenTestResultType pathogenTestResultType, Date sampleDateTime) {
			this.caseId = caseId;
			this.pathogenTestResultType = pathogenTestResultType;
			this.sampleDateTime = sampleDateTime;
		}

		public Long getCaseId() {
			return caseId;
		}

		public void setCaseId(Long caseId) {
			this.caseId = caseId;
		}

		public PathogenTestResultType getPathogenTestResultType() {
			return pathogenTestResultType;
		}

		public void setPathogenTestResultType(PathogenTestResultType pathogenTestResultType) {
			this.pathogenTestResultType = pathogenTestResultType;
		}

		public Date getSampleDateTime() {
			return sampleDateTime;
		}

		public void setSampleDateTime(Date sampleDateTime) {
			this.sampleDateTime = sampleDateTime;
		}
	}

	public Map<CaseClassification, Integer> getCasesCountByClassification(DashboardCriteria dashboardCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> caze = cq.from(Case.class);

		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);

		Predicate filter = caseService.createUserFilter(caseQueryContext, new CaseUserFilterCriteria().excludeCasesFromContacts(true));
		Predicate criteriaFilter = createCaseCriteriaFilter(dashboardCriteria, caseQueryContext);
		filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);

		Map<CaseClassification, Integer> result;
		if (filter != null) {
			cq.multiselect(caze.get(Case.CASE_CLASSIFICATION), cb.count(caze.get(Case.CASE_CLASSIFICATION)));
			cq.where(filter);
			cq.groupBy(caze.get(Case.CASE_CLASSIFICATION));

			result = em.createQuery(cq)
				.getResultStream()
				.collect(Collectors.toMap(tuple -> (CaseClassification) tuple[0], tuple -> ((Number) tuple[1]).intValue()));
		} else {
			result = Collections.emptyMap();
		}

		return result;
	}

	public Map<Disease, Long> getCaseCountByDisease(DashboardCriteria dashboardCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> caze = cq.from(Case.class);
		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);

		Predicate filter = caseService.createUserFilter(caseQueryContext, new CaseUserFilterCriteria().excludeCasesFromContacts(true));

		filter = CriteriaBuilderHelper.and(cb, filter, createCaseCriteriaFilter(dashboardCriteria, caseQueryContext));

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(caze.get(Case.DISEASE));
		cq.multiselect(caze.get(Case.DISEASE), cb.count(caze));
		List<Object[]> results = em.createQuery(cq).getResultList();

		Map<Disease, Long> resultMap = results.stream().collect(Collectors.toMap(e -> (Disease) e[0], e -> (Long) e[1]));

		return resultMap;
	}

	public String getLastReportedDistrictName(DashboardCriteria dashboardCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> caze = cq.from(Case.class);
		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);
		final CaseJoins joins = caseQueryContext.getJoins();
		Join<Case, District> district = joins.getResponsibleDistrict();

		Predicate filter = caseService.createUserFilter(caseQueryContext, new CaseUserFilterCriteria().excludeCasesFromContacts(true));

		filter = CriteriaBuilderHelper.and(cb, filter, createCaseCriteriaFilter(dashboardCriteria, caseQueryContext));

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(district.get(District.NAME));
		List<Order> order = new ArrayList<>();
		order.add(cb.desc(caze.get(Case.REPORT_DATE)));
		order.add(cb.desc(caze.get(Case.CREATION_DATE)));
		cq.orderBy(order);

		return QueryHelper.getFirstResult(em, cq, t -> t == null ? StringUtils.EMPTY : t);
	}

	public Map<Disease, District> getLastReportedDistrictByDisease(DashboardCriteria dashboardCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> caze = cq.from(Case.class);
		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);
		final CaseJoins joins = caseQueryContext.getJoins();
		Join<Case, District> districtJoin = joins.getResponsibleDistrict();

		Predicate filter = caseService.createUserFilter(caseQueryContext, new CaseUserFilterCriteria().excludeCasesFromContacts(true));

		filter = CriteriaBuilderHelper.and(cb, filter, createCaseCriteriaFilter(dashboardCriteria, caseQueryContext));

		if (filter != null) {
			cq.where(filter);
		}

		Expression<Number> maxReportDate = cb.max(caze.get(Case.REPORT_DATE));
		Expression<Number> maxCreationDate = cb.max(caze.get(Case.CREATION_DATE));
		cq.multiselect(caze.get(Case.DISEASE), districtJoin);
		cq.groupBy(caze.get(Case.DISEASE), districtJoin);

		List<Order> order = new ArrayList<>();
		order.add(cb.desc(maxReportDate));
		order.add(cb.desc(maxCreationDate));
		cq.orderBy(order);

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

	public Map<Disease, Long> getDeathCountByDisease(DashboardCriteria dashboardCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> root = cq.from(Case.class);

		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, root);
		CaseJoins joins = caseQueryContext.getJoins();
		Join<Case, Person> person = joins.getPerson();

		Predicate filter = caseService.createUserFilter(caseQueryContext, new CaseUserFilterCriteria().excludeCasesFromContacts(true));
		filter = CriteriaBuilderHelper.and(cb, filter, createCaseCriteriaFilter(dashboardCriteria, caseQueryContext));
		filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(person.get(Person.CAUSE_OF_DEATH_DISEASE), root.get(Case.DISEASE)));

		if (filter != null) {
			cq.where(filter);
		}

		cq.multiselect(person.get(Person.CAUSE_OF_DEATH_DISEASE), cb.count(person));
		cq.groupBy(person.get(Person.CAUSE_OF_DEATH_DISEASE));

		List<Object[]> results = em.createQuery(cq).getResultList();

		Map<Disease, Long> outbreaks = results.stream().collect(Collectors.toMap(e -> (Disease) e[0], e -> (Long) e[1]));

		return outbreaks;
	}

	public long countCasesConvertedFromContacts(DashboardCriteria dashboardCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Case> caze = cq.from(Case.class);
		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);

		Predicate filter = caseService.createUserFilter(caseQueryContext, new CaseUserFilterCriteria().excludeCasesFromContacts(true));
		Predicate criteriaFilter = createCaseCriteriaFilter(dashboardCriteria, caseQueryContext);
		filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);

		caze.join(Case.CONVERTED_FROM_CONTACT, JoinType.INNER);

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(caze));
		return em.createQuery(cq).getSingleResult();
	}

	public Map<PresentCondition, Integer> getCasesCountPerPersonCondition(DashboardCriteria dashboardCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> caze = cq.from(Case.class);
		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);
		final CaseJoins joins = caseQueryContext.getJoins();

		Join<Case, Person> person = joins.getPerson();

		Predicate filter = caseService.createUserFilter(caseQueryContext, new CaseUserFilterCriteria().excludeCasesFromContacts(true));
		Predicate criteriaFilter = createCaseCriteriaFilter(dashboardCriteria, caseQueryContext);
		filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(person.get(Person.PRESENT_CONDITION));
		cq.multiselect(person.get(Person.PRESENT_CONDITION), cb.count(caze));
		List<Object[]> results = em.createQuery(cq).getResultList();

		Map<PresentCondition, Integer> resultMap = results.stream()
			.collect(Collectors.toMap(e -> e[0] != null ? (PresentCondition) e[0] : PresentCondition.UNKNOWN,
				e -> ((Number) e[1]).intValue(), (v1, v2) -> v1 + v2));
		return resultMap;
	}

	public List<DashboardEventDto> getNewEvents(DashboardCriteria dashboardCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardEventDto> cq = cb.createQuery(DashboardEventDto.class);
		Root<Event> event = cq.from(Event.class);
		EventQueryContext eventQueryContext = new EventQueryContext(cb, cq, event);
		EventJoins eventJoins = eventQueryContext.getJoins();
		Join<Event, Location> eventLocation = eventJoins.getLocation();
		Join<Location, District> eventDistrict = eventJoins.getDistrict();

		Predicate filter = eventService.createDefaultFilter(cb, event);
		filter = CriteriaBuilderHelper.and(cb, filter, buildEventCriteriaFilter(dashboardCriteria, eventQueryContext));
		filter = CriteriaBuilderHelper.and(cb, filter, eventService.createUserFilter(eventQueryContext));

		List<DashboardEventDto> result;

		if (filter != null) {
			cq.where(filter);
			cq.multiselect(
				event.get(Event.UUID),
				event.get(Event.EVENT_STATUS),
				event.get(Event.EVENT_INVESTIGATION_STATUS),
				event.get(Event.DISEASE),
				event.get(Event.DISEASE_DETAILS),
				event.get(Event.START_DATE),
				event.get(Event.REPORT_LAT),
				event.get(Event.REPORT_LON),
				eventLocation.get(Location.LATITUDE),
				eventLocation.get(Location.LONGITUDE),
				eventJoins.getReportingUser().get(User.UUID),
				eventJoins.getResponsibleUser().get(User.UUID),
				eventJoins.getRegion().get(Region.UUID),
				eventDistrict.get(District.NAME),
				eventDistrict.get(District.UUID),
				eventJoins.getCommunity().get(Community.UUID),
				JurisdictionHelper.booleanSelector(cb, eventService.inJurisdictionOrOwned(eventQueryContext)));

			result = em.createQuery(cq).getResultList();

		} else {
			result = Collections.emptyList();
		}

		return result;
	}

	public Map<EventStatus, Long> getEventCountByStatus(DashboardCriteria dashboardCriteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Event> event = cq.from(Event.class);
		EventQueryContext eventQueryContext = new EventQueryContext(cb, cq, event);

		cq.multiselect(event.get(Event.EVENT_STATUS), cb.count(event));
		cq.groupBy(event.get(Event.EVENT_STATUS));

		Predicate filter = eventService.createDefaultFilter(cb, event);
		filter = CriteriaBuilderHelper.and(cb, filter, buildEventCriteriaFilter(dashboardCriteria, eventQueryContext));
		filter = CriteriaBuilderHelper.and(cb, filter, eventService.createUserFilter(eventQueryContext));

		if (filter != null)
			cq.where(filter);

		List<Object[]> results = em.createQuery(cq).getResultList();

		return results.stream().collect(Collectors.toMap(e -> (EventStatus) e[0], e -> (Long) e[1]));
	}

	private <T extends AbstractDomainObject> Predicate createCaseCriteriaFilter(
		DashboardCriteria dashboardCriteria,
		CaseQueryContext caseQueryContext) {

		final From<?, Case> from = caseQueryContext.getRoot();
		final CriteriaBuilder cb = caseQueryContext.getCriteriaBuilder();
		final CriteriaQuery<?> cq = caseQueryContext.getQuery();
		final CaseJoins joins = caseQueryContext.getJoins();

		Join<Case, Region> responsibleRegion = joins.getResponsibleRegion();
		Join<Case, District> responsibleDistrict = joins.getResponsibleDistrict();

		Predicate filter = null;
		if (dashboardCriteria.getDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.DISEASE), dashboardCriteria.getDisease()));
		}
		if (dashboardCriteria.getRegion() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(responsibleRegion.get(Region.UUID), dashboardCriteria.getRegion().getUuid()));
		}
		if (dashboardCriteria.getDistrict() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(responsibleDistrict.get(District.UUID), dashboardCriteria.getDistrict().getUuid()));
		}
		if (dashboardCriteria.getDateFrom() != null && dashboardCriteria.getDateTo() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				caseService.createNewCaseFilter(
					caseQueryContext,
					DateHelper.getStartOfDay(dashboardCriteria.getDateFrom()),
					DateHelper.getEndOfDay(dashboardCriteria.getDateTo()),
					dashboardCriteria.getNewCaseDateType()));
		}
		if (!dashboardCriteria.shouldIncludeNotACaseClassification()) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.notEqual(caseQueryContext.getRoot().get(Case.CASE_CLASSIFICATION), CaseClassification.NO_CASE));
		}

		// Exclude deleted cases. Archived cases should stay included
		filter = CriteriaBuilderHelper.and(cb, filter, cb.isFalse(from.get(Case.DELETED)));

		return filter;
	}

	private Predicate buildEventCriteriaFilter(DashboardCriteria dashboardCriteria, EventQueryContext eventQueryContext) {

		CriteriaBuilder cb = eventQueryContext.getCriteriaBuilder();
		From<?, Event> from = eventQueryContext.getRoot();

		Predicate filter = null;
		if (dashboardCriteria.getDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Event.DISEASE), dashboardCriteria.getDisease()));
		}
		if (dashboardCriteria.getRegion() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(
					from.join(Event.EVENT_LOCATION, JoinType.LEFT).join(Location.REGION, JoinType.LEFT).get(Region.UUID),
					dashboardCriteria.getRegion().getUuid()));
		}
		if (dashboardCriteria.getDistrict() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(
					from.join(Event.EVENT_LOCATION, JoinType.LEFT).join(Location.DISTRICT, JoinType.LEFT).get(District.UUID),
					dashboardCriteria.getDistrict().getUuid()));
		}

		filter = CriteriaBuilderHelper.and(cb, filter, createEventDateFilter(eventQueryContext.getQuery(), cb, from, dashboardCriteria));

		// Exclude deleted events. Archived events should stay included
		filter = CriteriaBuilderHelper.and(cb, filter, cb.isFalse(from.get(Event.DELETED)));

		return filter;
	}

	private Predicate createEventDateFilter(CriteriaQuery<?> cq, CriteriaBuilder cb, From<?, Event> from, DashboardCriteria dashboardCriteria) {
		Predicate filter = null;

		Date eventDateFrom = dashboardCriteria.getDateFrom();
		Date eventDateTo = dashboardCriteria.getDateTo();

		Predicate eventDateFilter = null;

		if (eventDateFrom != null && eventDateTo != null) {
			eventDateFilter = cb.or(
				cb.and(cb.isNull(from.get(Event.END_DATE)), cb.between(from.get(Event.START_DATE), eventDateFrom, eventDateTo)),
				cb.and(cb.isNull(from.get(Event.START_DATE)), cb.between(from.get(Event.END_DATE), eventDateFrom, eventDateTo)),
				cb.and(
					cb.greaterThanOrEqualTo(from.get(Event.END_DATE), eventDateFrom),
					cb.lessThanOrEqualTo(from.get(Event.START_DATE), eventDateTo)));
		} else if (eventDateFrom != null) {
			eventDateFilter = cb.or(
				cb.and(cb.isNull(from.get(Event.END_DATE)), cb.greaterThanOrEqualTo(from.get(Event.START_DATE), eventDateFrom)),
				cb.and(cb.isNull(from.get(Event.START_DATE)), cb.greaterThanOrEqualTo(from.get(Event.END_DATE), eventDateFrom)));
		} else if (eventDateTo != null) {
			eventDateFilter = cb.or(
				cb.and(cb.isNull(from.get(Event.START_DATE)), cb.lessThanOrEqualTo(from.get(Event.END_DATE), eventDateTo)),
				cb.and(cb.isNull(from.get(Event.END_DATE)), cb.lessThanOrEqualTo(from.get(Event.START_DATE), eventDateTo)));
		}

		if (eventDateFrom != null || eventDateTo != null) {
			Predicate reportFilter = cb.and(
				cb.isNull(from.get(Event.START_DATE)),
				cb.isNull(from.get(Event.END_DATE)),
				cb.between(from.get(Event.REPORT_DATE_TIME), eventDateFrom, eventDateTo));
			filter = CriteriaBuilderHelper.and(cb, filter, cb.or(eventDateFilter, reportFilter));
		}

		return filter;
	}
}
