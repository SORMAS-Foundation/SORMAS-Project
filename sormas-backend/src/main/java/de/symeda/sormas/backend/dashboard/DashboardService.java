package de.symeda.sormas.backend.dashboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.contact.DashboardQuarantineDataDto;
import de.symeda.sormas.api.dashboard.DashboardCaseDto;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.dashboard.DashboardEventDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.caze.CaseUserFilterCriteria;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventGroup;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventQueryContext;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonQueryContext;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.utils.CaseJoins;

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
		final CaseJoins<Case> joins = (CaseJoins<Case>) caseQueryContext.getJoins();
		Join<Case, Symptoms> symptoms = joins.getSymptoms();
		Join<Case, Person> person = joins.getPerson();

		Predicate filter = caseService.createUserFilter(cb, cq, caze, new CaseUserFilterCriteria().excludeCasesFromContacts(true));
		Predicate criteriaFilter = createCaseCriteriaFilter(dashboardCriteria, caseQueryContext);
		filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);

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

	public Map<CaseClassification, Integer> getCasesCountByClassification(DashboardCriteria dashboardCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> caze = cq.from(Case.class);

		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);

		Predicate filter = caseService.createUserFilter(cb, cq, caze, new CaseUserFilterCriteria().excludeCasesFromContacts(true));
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

	public List<DashboardQuarantineDataDto> getQuarantineData(DashboardCriteria dashboardCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardQuarantineDataDto> cq = cb.createQuery(DashboardQuarantineDataDto.class);
		Root<Case> caze = cq.from(Case.class);

		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);

		Predicate filter = caseService.createUserFilter(cb, cq, caze, new CaseUserFilterCriteria().excludeCasesFromContacts(false));
		Predicate criteriaFilter = createCaseCriteriaFilter(dashboardCriteria, caseQueryContext);
		filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);

		Predicate dateFilter = buildQuarantineDateFilter(cb, caze, dashboardCriteria.getNewCaseDateFrom(), dashboardCriteria.getNewCaseDateTo());
		if (filter != null) {
			filter = cb.and(filter, dateFilter);
		} else {
			filter = dateFilter;
		}

		if (filter != null) {
			cq.where(filter);
			cq.multiselect(caze.get(AbstractDomainObject.ID), caze.get(Case.QUARANTINE_FROM), caze.get(Case.QUARANTINE_TO));

			return em.createQuery(cq).getResultList();
		}

		return Collections.emptyList();
	}

	public String getLastReportedDistrictName(DashboardCriteria dashboardCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> caze = cq.from(Case.class);
		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);
		final CaseJoins<Case> joins = (CaseJoins<Case>) caseQueryContext.getJoins();
		Join<Case, District> district = joins.getDistrict();

		Predicate filter = caseService.createUserFilter(cb, cq, caze, new CaseUserFilterCriteria().excludeCasesFromContacts(true));

		filter = CriteriaBuilderHelper.and(cb, filter, createCaseCriteriaFilter(dashboardCriteria, caseQueryContext));

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(district.get(District.NAME));
		List<Order> order = new ArrayList<>();
		order.add(cb.desc(caze.get(Case.REPORT_DATE)));
		order.add(cb.desc(caze.get(Case.CREATION_DATE)));
		cq.orderBy(order);

		TypedQuery<String> query = em.createQuery(cq).setMaxResults(1);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return "";
		}
	}

	public long countCasesConvertedFromContacts(DashboardCriteria dashboardCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Case> caze = cq.from(Case.class);
		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);

		Predicate filter = caseService.createUserFilter(cb, cq, caze, new CaseUserFilterCriteria().excludeCasesFromContacts(false));
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
		final CaseJoins<Case> joins = (CaseJoins<Case>) caseQueryContext.getJoins();

		Join<Case, Person> person = joins.getPerson();

		Predicate filter = caseService.createUserFilter(cb, cq, caze, new CaseUserFilterCriteria().excludeCasesFromContacts(true));
		Predicate criteriaFilter = createCaseCriteriaFilter(dashboardCriteria, caseQueryContext);
		filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(person.get(Person.PRESENT_CONDITION));
		cq.multiselect(person.get(Person.PRESENT_CONDITION), cb.count(caze));
		List<Object[]> results = em.createQuery(cq).getResultList();

		Map<PresentCondition, Integer> resultMap = results.stream()
			.collect(Collectors.toMap(e -> e[0] != null ? (PresentCondition) e[0] : PresentCondition.UNKNOWN, e -> ((Number) e[1]).intValue()));
		return resultMap;
	}

	public List<DashboardEventDto> getNewEvents(EventCriteria eventCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardEventDto> cq = cb.createQuery(DashboardEventDto.class);
		Root<Event> event = cq.from(Event.class);
		Join<Event, Location> eventLocation = event.join(Event.EVENT_LOCATION, JoinType.LEFT);
		Join<Location, District> eventDistrict = eventLocation.join(Location.DISTRICT, JoinType.LEFT);

		Predicate filter = eventService.createDefaultFilter(cb, event);
		filter = CriteriaBuilderHelper.and(cb, filter, eventService.buildCriteriaFilter(eventCriteria, new EventQueryContext(cb, cq, event)));
		filter = CriteriaBuilderHelper.and(cb, filter, eventService.createUserFilter(cb, cq, event));

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
				event.join(Event.REPORTING_USER, JoinType.LEFT).get(User.UUID),
				event.join(Event.RESPONSIBLE_USER, JoinType.LEFT).get(User.UUID),
				eventLocation.join(Location.REGION, JoinType.LEFT).get(Region.UUID),
				eventDistrict.get(District.NAME),
				eventDistrict.get(District.UUID),
				eventLocation.join(Location.COMMUNITY, JoinType.LEFT).get(Community.UUID));

			result = em.createQuery(cq).getResultList();

		} else {
			result = Collections.emptyList();
		}

		return result;
	}

	public Map<EventStatus, Long> getEventCountByStatus(EventCriteria eventCriteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Event> event = cq.from(Event.class);
		EventQueryContext eventQueryContext = new EventQueryContext(cb, cq, event);

		cq.multiselect(event.get(Event.EVENT_STATUS), cb.count(event));
		cq.groupBy(event.get(Event.EVENT_STATUS));

		Predicate filter = eventService.createDefaultFilter(cb, event);
		filter = CriteriaBuilderHelper.and(cb, filter, buildEventCriteriaFilter(eventCriteria, eventQueryContext));
		filter = CriteriaBuilderHelper.and(cb, filter, eventService.createUserFilter(cb, cq, event));

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
		final CaseJoins<Case> joins = (CaseJoins<Case>) caseQueryContext.getJoins();

		Join<Case, Region> region = joins.getRegion();
		Join<Case, District> district = joins.getDistrict();

		Predicate filter = null;
		if (dashboardCriteria.getDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.DISEASE), dashboardCriteria.getDisease()));
		}
		if (dashboardCriteria.getRegion() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(region.get(Region.UUID), dashboardCriteria.getRegion().getUuid()));
		}
		if (dashboardCriteria.getDistrict() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(district.get(District.UUID), dashboardCriteria.getDistrict().getUuid()));
		}
		if (dashboardCriteria.getNewCaseDateFrom() != null && dashboardCriteria.getNewCaseDateTo() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				caseService.createNewCaseFilter(
					cq,
					cb,
					from,
					DateHelper.getStartOfDay(dashboardCriteria.getNewCaseDateFrom()),
					DateHelper.getEndOfDay(dashboardCriteria.getNewCaseDateTo()),
					dashboardCriteria.getNewCaseDateType()));
		}
		if (!dashboardCriteria.shouldIncludeNoCases()) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.notEqual(caseQueryContext.getRoot().get(Case.CASE_CLASSIFICATION), CaseClassification.NO_CASE));
		}

		return filter;
	}

	private Predicate buildQuarantineDateFilter(CriteriaBuilder cb, Root<Case> caze, Date fromDate, Date toDate) {
		Predicate filter = null;
		if (fromDate != null && toDate != null) {
			filter = cb.or(
				cb.and(cb.isNull(caze.get(Case.QUARANTINE_TO)), cb.between(caze.get(Case.QUARANTINE_FROM), fromDate, toDate)),
				cb.and(cb.isNull(caze.get(Case.QUARANTINE_FROM)), cb.between(caze.get(Case.QUARANTINE_TO), fromDate, toDate)),
				cb.and(
					cb.greaterThanOrEqualTo(caze.get(Case.QUARANTINE_TO), fromDate),
					cb.lessThanOrEqualTo(caze.get(Case.QUARANTINE_FROM), toDate)));
		} else if (fromDate != null) {
			filter = cb.or(
				cb.and(cb.isNull(caze.get(Case.QUARANTINE_TO)), cb.greaterThanOrEqualTo(caze.get(Case.QUARANTINE_FROM), fromDate)),
				cb.and(cb.isNull(caze.get(Case.QUARANTINE_FROM)), cb.greaterThanOrEqualTo(caze.get(Case.QUARANTINE_TO), fromDate)));
		} else if (toDate != null) {
			filter = cb.or(
				cb.and(cb.isNull(caze.get(Case.QUARANTINE_FROM)), cb.lessThanOrEqualTo(caze.get(Case.QUARANTINE_TO), toDate)),
				cb.and(cb.isNull(caze.get(Case.QUARANTINE_TO)), cb.lessThanOrEqualTo(caze.get(Case.QUARANTINE_FROM), toDate)));
		}

		return filter;
	}

	private Predicate buildEventCriteriaFilter(EventCriteria eventCriteria, EventQueryContext eventQueryContext) {

		CriteriaBuilder cb = eventQueryContext.getCriteriaBuilder();
		From<?, Event> from = eventQueryContext.getRoot();

		Predicate filter = null;
		if (eventCriteria.getReportingUserRole() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.isMember(eventCriteria.getReportingUserRole(), from.join(Event.REPORTING_USER, JoinType.LEFT).get(User.USER_ROLES)));
		}
		if (eventCriteria.getDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Event.DISEASE), eventCriteria.getDisease()));
		}
		if (eventCriteria.getEventStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Event.EVENT_STATUS), eventCriteria.getEventStatus()));
		}
		if (eventCriteria.getRiskLevel() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Event.RISK_LEVEL), eventCriteria.getRiskLevel()));
		}
		if (eventCriteria.getEventInvestigationStatus() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(from.get(Event.EVENT_INVESTIGATION_STATUS), eventCriteria.getEventInvestigationStatus()));
		}
		if (eventCriteria.getEventManagementStatus() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Event.EVENT_MANAGEMENT_STATUS), eventCriteria.getEventManagementStatus()));
		}
		if (eventCriteria.getTypeOfPlace() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Event.TYPE_OF_PLACE), eventCriteria.getTypeOfPlace()));
		}
		if (eventCriteria.getRelevanceStatus() != null) {
			if (eventCriteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.or(cb.equal(from.get(Event.ARCHIVED), false), cb.isNull(from.get(Event.ARCHIVED))));
			} else if (eventCriteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Event.ARCHIVED), true));
			}
		}
		if (eventCriteria.getDeleted() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Event.DELETED), eventCriteria.getDeleted()));
		}
		if (eventCriteria.getRegion() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(
					from.join(Event.EVENT_LOCATION, JoinType.LEFT).join(Location.REGION, JoinType.LEFT).get(Region.UUID),
					eventCriteria.getRegion().getUuid()));
		}
		if (eventCriteria.getDistrict() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(
					from.join(Event.EVENT_LOCATION, JoinType.LEFT).join(Location.DISTRICT, JoinType.LEFT).get(District.UUID),
					eventCriteria.getDistrict().getUuid()));
		}
		if (eventCriteria.getCommunity() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(
					from.join(Event.EVENT_LOCATION, JoinType.LEFT).join(Location.COMMUNITY, JoinType.LEFT).get(Community.UUID),
					eventCriteria.getCommunity().getUuid()));
		}

		if (eventCriteria.getEventEvolutionDateFrom() != null && eventCriteria.getEventEvolutionDateTo() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.between(from.get(Event.EVOLUTION_DATE), eventCriteria.getEventEvolutionDateFrom(), eventCriteria.getEventEvolutionDateTo()));
		} else if (eventCriteria.getEventEvolutionDateFrom() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.greaterThanOrEqualTo(from.get(Event.EVOLUTION_DATE), eventCriteria.getEventEvolutionDateFrom()));
		} else if (eventCriteria.getEventEvolutionDateTo() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.lessThanOrEqualTo(from.get(Event.EVOLUTION_DATE), eventCriteria.getEventEvolutionDateTo()));
		}
		if (eventCriteria.getResponsibleUser() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(from.join(Event.RESPONSIBLE_USER, JoinType.LEFT).get(User.UUID), eventCriteria.getResponsibleUser().getUuid()));
		}
		if (StringUtils.isNotEmpty(eventCriteria.getFreeText())) {
			String[] textFilters = eventCriteria.getFreeText().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.ilike(cb, from.get(Event.UUID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(Event.EXTERNAL_ID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(Event.EXTERNAL_TOKEN), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(Event.EVENT_TITLE), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(Event.EVENT_DESC), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(Event.SRC_FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(Event.SRC_LAST_NAME), textFilter),
					CriteriaBuilderHelper.ilike(cb, from.get(Event.SRC_EMAIL), textFilter),
					CriteriaBuilderHelper.ilike(cb, from.get(Event.SRC_TEL_NO), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}
		if (StringUtils.isNotEmpty(eventCriteria.getFreeTextEventParticipants())) {
			Join<Event, EventParticipant> eventParticipantJoin = from.join(Event.EVENT_PERSONS, JoinType.LEFT);
			Join<EventParticipant, Person> personJoin = eventParticipantJoin.join(EventParticipant.PERSON, JoinType.LEFT);

			final PersonQueryContext personQueryContext = new PersonQueryContext(cb, eventQueryContext.getQuery(), personJoin);

			String[] textFilters = eventCriteria.getFreeTextEventParticipants().split("\\s+");

			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.ilike(cb, eventParticipantJoin.get(EventParticipant.UUID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, personJoin.get(Person.FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, personJoin.get(Person.LAST_NAME), textFilter),
					CriteriaBuilderHelper.ilike(
						cb,
						(Expression<String>) personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_PHONE_SUBQUERY),
						textFilter),
					CriteriaBuilderHelper.ilike(
						cb,
						(Expression<String>) personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_EMAIL_SUBQUERY),
						textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isFalse(eventParticipantJoin.get(EventParticipant.DELETED)));
		}
		if (StringUtils.isNotEmpty(eventCriteria.getFreeTextEventGroups())) {
			Join<Event, EventGroup> eventGroupJoin = from.join(Event.EVENT_GROUPS, JoinType.LEFT);

			String[] textFilters = eventCriteria.getFreeTextEventGroups().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.ilike(cb, eventGroupJoin.get(EventGroup.UUID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, eventGroupJoin.get(EventGroup.NAME), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}

		if (eventCriteria.getSrcType() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Event.SRC_TYPE), eventCriteria.getSrcType()));
		}

		if (eventCriteria.getCaze() != null) {
			Join<Event, EventParticipant> eventParticipantJoin = from.join(Event.EVENT_PERSONS, JoinType.LEFT);
			Join<EventParticipant, Case> caseJoin = eventParticipantJoin.join(EventParticipant.RESULTING_CASE, JoinType.LEFT);

			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(caseJoin.get(Case.UUID), eventCriteria.getCaze().getUuid()));

			filter = CriteriaBuilderHelper.and(cb, filter, cb.isFalse(eventParticipantJoin.get(EventParticipant.DELETED)));
		}
		if (eventCriteria.getPerson() != null) {
			Join<Event, EventParticipant> eventParticipantJoin = from.join(Event.EVENT_PERSONS, JoinType.LEFT);
			Join<EventParticipant, Person> personJoin = eventParticipantJoin.join(EventParticipant.PERSON, JoinType.LEFT);

			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.in(personJoin.get(Person.UUID)).value(eventCriteria.getPerson().getUuid()),
				cb.isFalse(eventParticipantJoin.get(EventParticipant.DELETED)));
		}
		if (eventCriteria.getFacilityType() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(from.join(Event.EVENT_LOCATION).get(Location.FACILITY_TYPE), eventCriteria.getFacilityType()));
		}
		if (eventCriteria.getFacility() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(from.join(Event.EVENT_LOCATION).join(Location.FACILITY).get(Facility.UUID), eventCriteria.getFacility().getUuid()));
		}
		if (eventCriteria.getSuperordinateEvent() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(from.get(Event.SUPERORDINATE_EVENT).get(AbstractDomainObject.UUID), eventCriteria.getSuperordinateEvent().getUuid()));
		}
		if (eventCriteria.getEventGroup() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(from.join(Event.EVENT_GROUPS).get(EventGroup.UUID), eventCriteria.getEventGroup().getUuid()));
		}
		if (CollectionUtils.isNotEmpty(eventCriteria.getExcludedUuids())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.not(from.get(AbstractDomainObject.UUID).in(eventCriteria.getExcludedUuids())));
		}
		if (Boolean.TRUE.equals(eventCriteria.getHasNoSuperordinateEvent())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNull(from.get(Event.SUPERORDINATE_EVENT)));
		}

		filter = CriteriaBuilderHelper.and(cb, filter, createEventDateFilter(eventQueryContext.getQuery(), cb, from, eventCriteria));

		return filter;
	}

	private Predicate createEventDateFilter(CriteriaQuery<?> cq, CriteriaBuilder cb, From<?, Event> from, EventCriteria eventCriteria) {
		Predicate filter = null;

		Date eventDateFrom = eventCriteria.getEventDateFrom();
		Date eventDateTo = eventCriteria.getEventDateTo();

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
