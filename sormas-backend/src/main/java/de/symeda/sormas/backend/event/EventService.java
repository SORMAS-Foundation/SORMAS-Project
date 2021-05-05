/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.event;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.event.DashboardEventDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventCriteriaDateType;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.criteria.CriteriaDateType;
import de.symeda.sormas.api.utils.criteria.ExternalShareDateType;
import de.symeda.sormas.backend.action.Action;
import de.symeda.sormas.backend.action.ActionService;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.ChangeDateFilterBuilder;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonQueryContext;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.share.ExternalShareInfo;
import de.symeda.sormas.backend.share.ExternalShareInfoService;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasShareInfoService;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless
@LocalBean
public class EventService extends AbstractCoreAdoService<Event> {

	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private EventParticipantService eventParticipantService;
	@EJB
	private TaskService taskService;
	@EJB
	private ActionService actionService;
	@EJB
	private CaseService caseService;
	@EJB
	private EventJurisdictionChecker eventJurisdictionChecker;
	@EJB
	private SormasToSormasShareInfoService sormasToSormasShareInfoService;
	@EJB
	private ExternalShareInfoService externalShareInfoService;

	public EventService() {
		super(Event.class);
	}

	public List<Event> getAllActiveEventsAfter(Date date) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Event> cq = cb.createQuery(getElementClass());
		Root<Event> from = cq.from(getElementClass());
		from.fetch(Event.EVENT_LOCATION);

		Predicate filter = createActiveEventsFilter(cb, from);

		User user = getCurrentUser();
		if (user != null) {
			EventUserFilterCriteria eventUserFilterCriteria = new EventUserFilterCriteria();
			eventUserFilterCriteria.includeUserCaseAndEventParticipantFilter(true);
			eventUserFilterCriteria.forceRegionJurisdiction(true);

			Predicate userFilter = createUserFilter(cb, cq, from, eventUserFilterCriteria);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, DateHelper.toTimestampUpper(date));
			filter = cb.and(filter, dateFilter);
		}

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(Event.CHANGE_DATE)));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}

	public List<String> getAllActiveUuids() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Event> from = cq.from(getElementClass());

		Predicate filter = createActiveEventsFilter(cb, from);

		User user = getCurrentUser();
		if (user != null) {
			EventUserFilterCriteria eventUserFilterCriteria = new EventUserFilterCriteria();
			eventUserFilterCriteria.includeUserCaseAndEventParticipantFilter(true);
			eventUserFilterCriteria.forceRegionJurisdiction(true);

			Predicate userFilter = createUserFilter(cb, cq, from, eventUserFilterCriteria);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		cq.where(filter);
		cq.select(from.get(Event.UUID));

		return em.createQuery(cq).getResultList();
	}

	public Map<String, User> getAllEventUuidWithResponsibleUserByCaseAfterDateForNotification(Case caze, Date date) {
		if (caze == null || date == null) {
			return Collections.emptyMap();
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<EventParticipant> from = cq.from(EventParticipant.class);
		Join<EventParticipant, Event> eventJoin = from.join(EventParticipant.EVENT, JoinType.INNER);

		Predicate diseaseFilter = cb.equal(eventJoin.get(Event.DISEASE), caze.getDisease());
		Predicate personFilter = cb.equal(from.get(EventParticipant.PERSON), caze.getPerson());

		Timestamp timestamp = DateHelper.toTimestampUpper(date);
		Predicate dateFilter = cb.or(
			CriteriaBuilderHelper.greaterThanAndNotNull(cb, eventJoin.get(Event.START_DATE), timestamp),
			CriteriaBuilderHelper.greaterThanAndNotNull(cb, eventJoin.get(Event.END_DATE), timestamp));

		Predicate responsibleUserFilter = cb.and(
			cb.isNotNull(eventJoin.get(Event.RESPONSIBLE_USER)),
			cb.not(cb.equal(eventJoin.get(Event.RESPONSIBLE_USER), caze.getReportingUser())));

		Predicate activeEventsFilter = createActiveEventsFilter(cb, eventJoin);

		cq.where(cb.and(diseaseFilter, personFilter, dateFilter, responsibleUserFilter, activeEventsFilter));
		cq.orderBy(cb.desc(from.get(EventParticipant.CREATION_DATE)));
		cq.multiselect(Arrays.asList(eventJoin.get(Event.UUID), eventJoin.get(Event.RESPONSIBLE_USER)));

		return em.createQuery(cq).getResultList().stream().collect(Collectors.toMap(objects -> (String) objects[0], objects -> (User) objects[1]));
	}

	public List<DashboardEventDto> getNewEventsForDashboard(EventCriteria eventCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardEventDto> cq = cb.createQuery(DashboardEventDto.class);
		Root<Event> event = cq.from(getElementClass());
		Join<Event, Location> eventLocation = event.join(Event.EVENT_LOCATION, JoinType.LEFT);
		Join<Location, District> eventDistrict = eventLocation.join(Location.DISTRICT, JoinType.LEFT);

		Predicate filter = createDefaultFilter(cb, event);
		filter = CriteriaBuilderHelper.and(cb, filter, buildCriteriaFilter(eventCriteria, new EventQueryContext(cb, cq, event)));
		filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(cb, cq, event));

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

	public Map<Disease, Long> getEventCountByDisease(EventCriteria eventCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Event> event = cq.from(Event.class);

		cq.multiselect(event.get(Event.DISEASE), cb.count(event));
		cq.groupBy(event.get(Event.DISEASE));

		Predicate filter = createDefaultFilter(cb, event);
		filter = CriteriaBuilderHelper.and(cb, filter, buildCriteriaFilter(eventCriteria, new EventQueryContext(cb, cq, event)));
		filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(cb, cq, event));

		if (filter != null)
			cq.where(filter);

		List<Object[]> results = em.createQuery(cq).getResultList();

		return results.stream().collect(Collectors.toMap(e -> (Disease) e[0], e -> (Long) e[1]));
	}

	public Event getEventReferenceByEventParticipant(String eventParticipantUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Event> cq = cb.createQuery(Event.class);
		Root<Event> event = cq.from(Event.class);

		Predicate filter = createDefaultFilter(cb, event);
		filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(event.join(Event.EVENT_PERSONS).get(EventParticipant.UUID), eventParticipantUuid));
		filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(cb, cq, event));
		cq.where(filter);

		return em.createQuery(cq).getResultList().stream().findFirst().orElse(null);
	}

	public Map<EventStatus, Long> getEventCountByStatus(EventCriteria eventCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Event> event = cq.from(Event.class);
		EventQueryContext eventQueryContext = new EventQueryContext(cb, cq, event);

		cq.multiselect(event.get(Event.EVENT_STATUS), cb.count(event));
		cq.groupBy(event.get(Event.EVENT_STATUS));

		Predicate filter = createDefaultFilter(cb, event);
		filter = CriteriaBuilderHelper.and(cb, filter, buildCriteriaFilter(eventCriteria, eventQueryContext));
		filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(cb, cq, event));

		if (filter != null)
			cq.where(filter);

		List<Object[]> results = em.createQuery(cq).getResultList();

		return results.stream().collect(Collectors.toMap(e -> (EventStatus) e[0], e -> (Long) e[1]));
	}

	public List<String> getArchivedUuidsSince(Date since) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Event> event = cq.from(Event.class);

		EventUserFilterCriteria eventUserFilterCriteria = new EventUserFilterCriteria();
		eventUserFilterCriteria.includeUserCaseAndEventParticipantFilter(true);
		eventUserFilterCriteria.forceRegionJurisdiction(true);

		Predicate filter = createUserFilter(cb, cq, event, eventUserFilterCriteria);
		if (since != null) {
			Predicate dateFilter = cb.greaterThanOrEqualTo(event.get(Event.CHANGE_DATE), since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		Predicate archivedFilter = cb.equal(event.get(Event.ARCHIVED), true);
		if (filter != null) {
			filter = cb.and(filter, archivedFilter);
		} else {
			filter = archivedFilter;
		}

		cq.where(filter);
		cq.select(event.get(Event.UUID));

		return em.createQuery(cq).getResultList();
	}

	public List<String> getDeletedUuidsSince(Date since) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Event> event = cq.from(Event.class);

		EventUserFilterCriteria eventUserFilterCriteria = new EventUserFilterCriteria();
		eventUserFilterCriteria.includeUserCaseAndEventParticipantFilter(true);
		eventUserFilterCriteria.forceRegionJurisdiction(true);

		Predicate filter = createUserFilter(cb, cq, event, eventUserFilterCriteria);
		if (since != null) {
			Predicate dateFilter = cb.greaterThanOrEqualTo(event.get(Event.CHANGE_DATE), since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		Predicate deletedFilter = cb.equal(event.get(Event.DELETED), true);
		if (filter != null) {
			filter = cb.and(filter, deletedFilter);
		} else {
			filter = deletedFilter;
		}

		cq.where(filter);
		cq.select(event.get(Event.UUID));

		return em.createQuery(cq).getResultList();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Event> eventPath) {
		return createUserFilter(cb, cq, eventPath, null);
	}

	@SuppressWarnings("rawtypes")
	public Predicate createUserFilter(
		CriteriaBuilder cb,
		CriteriaQuery cq,
		From<?, Event> eventPath,
		EventUserFilterCriteria eventUserFilterCriteria) {

		final User currentUser = getCurrentUser();
		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		if (jurisdictionLevel == JurisdictionLevel.NATION || currentUser.hasAnyUserRole(UserRole.REST_USER)) {
			return null;
		}

		Predicate filter = null;

		switch (jurisdictionLevel) {
		case REGION:
			if (currentUser.getRegion() != null) {
				filter = CriteriaBuilderHelper
					.or(cb, filter, cb.equal(eventPath.join(Event.EVENT_LOCATION, JoinType.LEFT).get(Location.REGION), currentUser.getRegion()));
			}
			break;
		case DISTRICT:
			if (currentUser.getDistrict() != null) {
				filter = CriteriaBuilderHelper
					.or(cb, filter, cb.equal(eventPath.join(Event.EVENT_LOCATION, JoinType.LEFT).get(Location.DISTRICT), currentUser.getDistrict()));
			}
			break;
		default:
		}

		if (filter != null && currentUser.getLimitedDisease() != null) {
			filter = cb
				.and(filter, cb.or(cb.equal(eventPath.get(Event.DISEASE), currentUser.getLimitedDisease()), cb.isNull(eventPath.get(Event.DISEASE))));
		}

		Predicate filterResponsible = cb.equal(eventPath.join(Event.REPORTING_USER, JoinType.LEFT), currentUser);
		filterResponsible = cb.or(filterResponsible, cb.equal(eventPath.join(Event.RESPONSIBLE_USER, JoinType.LEFT), currentUser));

		if (eventUserFilterCriteria != null && eventUserFilterCriteria.isIncludeUserCaseAndEventParticipantFilter()) {
			filter = CriteriaBuilderHelper.or(cb, filter, createCaseAndEventParticipantFilter(cb, cq, eventPath));
		}

		if (eventUserFilterCriteria != null && eventUserFilterCriteria.isForceRegionJurisdiction()) {
			filter = CriteriaBuilderHelper
				.or(cb, filter, cb.equal(eventPath.join(Event.EVENT_LOCATION, JoinType.LEFT).get(Location.REGION), currentUser.getRegion()));
		}

		if (filter != null) {
			filter = CriteriaBuilderHelper.or(cb, filter, filterResponsible);
		} else {
			filter = filterResponsible;
		}

		return filter;
	}

	public Predicate createCaseAndEventParticipantFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Event> eventPath) {

		Join<Event, EventParticipant> eventParticipantJoin = eventPath.join(Event.EVENT_PERSONS, JoinType.LEFT);
		Join<EventParticipant, Case> caseJoin = eventParticipantJoin.join(EventParticipant.RESULTING_CASE, JoinType.LEFT);

		Subquery<Long> caseSubquery = cq.subquery(Long.class);
		Root<Case> caseRoot = caseSubquery.from(Case.class);
		caseSubquery.where(caseService.createUserFilter(cb, cq, caseRoot));
		caseSubquery.select(caseRoot.get(Case.ID));

		Predicate filter = cb.in(caseJoin.get(Case.ID)).value(caseSubquery);

		final User currentUser = getCurrentUser();
		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		if (jurisdictionLevel == JurisdictionLevel.REGION || jurisdictionLevel == JurisdictionLevel.DISTRICT) {
			Subquery<Long> eventParticipantSubquery = cq.subquery(Long.class);
			Root<EventParticipant> epRoot = eventParticipantSubquery.from(EventParticipant.class);

			switch (jurisdictionLevel) {
			case REGION:
				if (currentUser.getRegion() != null) {
					eventParticipantSubquery.where(
						cb.and(
							cb.equal(epRoot.get(EventParticipant.EVENT).get(Event.ID), eventPath.get(Event.ID)),
							cb.equal(epRoot.get(EventParticipant.REGION).get(Region.ID), currentUser.getRegion().getId())));
				}
				break;
			case DISTRICT:
				if (currentUser.getDistrict() != null) {
					eventParticipantSubquery.where(
						cb.and(
							cb.equal(epRoot.get(EventParticipant.EVENT).get(Event.ID), eventPath.get(Event.ID)),
							cb.equal(epRoot.get(EventParticipant.DISTRICT).get(District.ID), currentUser.getDistrict().getId())));
				}
				break;
			default:
			}

			eventParticipantSubquery.select(epRoot.get(EventParticipant.ID));

			filter = CriteriaBuilderHelper.or(cb, filter, cb.in(eventParticipantJoin.get(EventParticipant.ID)).value(eventParticipantSubquery));
		}

		return filter;

	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, Event> eventPath, Timestamp date) {

		return addChangeDateFilter(new ChangeDateFilterBuilder(cb, date), eventPath).build();
	}

	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, Event> eventPath, Expression<? extends Date> dateExpression) {

		return addChangeDateFilter(new ChangeDateFilterBuilder(cb, dateExpression), eventPath).build();
	}

	private ChangeDateFilterBuilder addChangeDateFilter(ChangeDateFilterBuilder filterBuilder, From<?, Event> eventPath) {

		filterBuilder.add(eventPath).add(eventPath, Event.EVENT_LOCATION).add(eventPath, Contact.SORMAS_TO_SORMAS_SHARES);

		return filterBuilder;
	}

	@Override
	public void delete(Event event) {

		// Delete all event participants associated with this event
		List<EventParticipant> eventParticipants = eventParticipantService.getAllByEventAfter(null, event);
		for (EventParticipant eventParticipant : eventParticipants) {
			eventParticipantService.delete(eventParticipant);
		}

		// Delete all tasks associated with this event
		List<Task> tasks = taskService.findBy(new TaskCriteria().event(new EventReferenceDto(event.getUuid())), true);
		for (Task task : tasks) {
			taskService.delete(task);
		}

		// Delete all event actions associated with this event
		List<Action> actions = actionService.getAllByEvent(event);
		for (Action action : actions) {
			actionService.delete(action);
		}

		// Mark the event as deleted
		super.delete(event);
	}

	public Predicate buildCriteriaFilter(EventCriteria eventCriteria, EventQueryContext eventQueryContext) {

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
		if (eventCriteria.getReportedDateFrom() != null || eventCriteria.getReportedDateTo() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.between(from.get(Event.REPORT_DATE_TIME), eventCriteria.getReportedDateFrom(), eventCriteria.getReportedDateTo()));
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
					CriteriaBuilderHelper.ilike(cb, (Expression<String>) personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_PHONE_SUBQUERY), textFilter),
					CriteriaBuilderHelper.ilike(cb, (Expression<String>) personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_EMAIL_SUBQUERY), textFilter));
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
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(from.join(Event.EVENT_GROUPS).get(EventGroup.UUID), eventCriteria.getEventGroup().getUuid()));
		}
		if (CollectionUtils.isNotEmpty(eventCriteria.getExcludedUuids())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.not(from.get(AbstractDomainObject.UUID).in(eventCriteria.getExcludedUuids())));
		}
		if (Boolean.TRUE.equals(eventCriteria.getHasNoSuperordinateEvent())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNull(from.get(Event.SUPERORDINATE_EVENT)));
		}

		filter = CriteriaBuilderHelper.and(cb, filter, createEventDateFilter(eventQueryContext.getQuery(), cb, from, eventCriteria));
		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			externalShareInfoService.buildShareCriteriaFilter(
				eventCriteria,
				eventQueryContext.getQuery(),
				cb,
				from,
				ExternalShareInfo.EVENT,
				(latestShareDate) -> createChangeDateFilter(cb, from, latestShareDate)));

		return filter;
	}

	private Predicate createEventDateFilter(CriteriaQuery<?> cq, CriteriaBuilder cb, From<?, Event> from, EventCriteria eventCriteria) {
		Predicate filter = null;

		CriteriaDateType eventDateType = eventCriteria.getEventDateType();
		Date eventDateFrom = eventCriteria.getEventDateFrom();
		Date eventDateTo = eventCriteria.getEventDateTo();

		if (eventDateType == null || eventDateType == EventCriteriaDateType.EVENT_DATE) {
			if (eventDateFrom != null && eventDateTo != null) {
				filter = CriteriaBuilderHelper.and(
					cb,
					filter,
					cb.or(
						cb.between(from.get(Event.START_DATE), eventDateFrom, eventDateTo),
						cb.and(
							cb.isNotNull(from.get(Event.END_DATE)),
							cb.lessThan(from.get(Event.START_DATE), eventDateFrom),
							cb.greaterThanOrEqualTo(from.get(Event.END_DATE), eventDateFrom))));
			} else if (eventDateFrom != null) {
				filter = CriteriaBuilderHelper.and(
					cb,
					filter,
					cb.or(
						cb.greaterThanOrEqualTo(from.get(Event.START_DATE), eventDateFrom),
						cb.and(
							cb.isNotNull(from.get(Event.END_DATE)),
							cb.lessThan(from.get(Event.START_DATE), eventDateFrom),
							cb.greaterThanOrEqualTo(from.get(Event.END_DATE), eventDateFrom))));
			} else if (eventDateTo != null) {
				filter = CriteriaBuilderHelper.and(
					cb,
					filter,
					cb.or(
						cb.and(cb.isNull(from.get(Event.END_DATE)), cb.lessThanOrEqualTo(from.get(Event.START_DATE), eventDateTo)),
						cb.lessThanOrEqualTo(from.get(Event.END_DATE), eventDateTo)));
			}
		} else if (eventDateType == ExternalShareDateType.LAST_EXTERNAL_SURVEILLANCE_TOOL_SHARE) {
			filter = externalShareInfoService.buildLatestSurvToolShareDateFilter(cq, cb, from, ExternalShareInfo.EVENT, (latestShareDate) -> {
				if (eventDateFrom != null && eventDateTo != null) {
					return cb.between(latestShareDate, eventDateFrom, eventDateTo);
				} else if (eventDateFrom != null) {
					return cb.greaterThanOrEqualTo(latestShareDate, eventDateFrom);
				} else {
					return cb.lessThanOrEqualTo(latestShareDate, eventDateTo);
				}
			});
		}

		return filter;
	}

	/**
	 * Creates a filter that excludes all events that are either {@link Event#isArchived()} or {@link CoreAdo#isDeleted()}.
	 */
	public Predicate createActiveEventsFilter(CriteriaBuilder cb, Root<Event> root) {
		return cb.and(cb.isFalse(root.get(Event.ARCHIVED)), cb.isFalse(root.get(Event.DELETED)));
	}

	/**
	 * Creates a filter that excludes all events that are either {@link Event#isArchived()} or {@link CoreAdo#isDeleted()}.
	 */
	public Predicate createActiveEventsFilter(CriteriaBuilder cb, Path<Event> root) {
		return cb.and(cb.isFalse(root.get(Event.ARCHIVED)), cb.isFalse(root.get(Event.DELETED)));
	}

	/**
	 * Creates a default filter that should be used as the basis of queries that do not use {@link EventCriteria}.
	 * This essentially removes {@link CoreAdo#isDeleted()} events from the queries.
	 */
	public Predicate createDefaultFilter(CriteriaBuilder cb, Root<Event> root) {
		return cb.isFalse(root.get(Event.DELETED));
	}

	public String getUuidByCaseUuidOrPersonUuid(String searchTerm) {

		if (StringUtils.isEmpty(searchTerm)) {
			return null;
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Event> root = cq.from(Event.class);
		Join<Event, EventParticipant> eventParticipant = root.join(Event.EVENT_PERSONS, JoinType.LEFT);
		Join<EventParticipant, Person> eventParticipantPerson = eventParticipant.join(EventParticipant.PERSON, JoinType.LEFT);
		Join<EventParticipant, Case> eventParticipantCase = eventParticipant.join(EventParticipant.RESULTING_CASE, JoinType.LEFT);

		Predicate filter = cb.or(
			cb.equal(cb.lower(eventParticipantCase.get(Case.UUID)), searchTerm.toLowerCase()),
			cb.equal(cb.lower(eventParticipantPerson.get(Person.UUID)), searchTerm.toLowerCase()));

		cq.where(filter);
		cq.orderBy(cb.desc(root.get(Event.REPORT_DATE_TIME)));
		cq.select(root.get(Event.UUID));

		try {
			return em.createQuery(cq).setMaxResults(1).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<EventSummaryDetails> getEventSummaryDetailsByCases(List<Long> casesId) {
		if (casesId.isEmpty()) {
			return Collections.emptyList();
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventSummaryDetails> eventsCq = cb.createQuery(EventSummaryDetails.class);
		Root<EventParticipant> eventsCqRoot = eventsCq.from(EventParticipant.class);
		Join<EventParticipant, Event> eventJoin = eventsCqRoot.join(EventParticipant.EVENT, JoinType.INNER);
		Join<EventParticipant, Case> cazeJoin = eventsCqRoot.join(EventParticipant.RESULTING_CASE, JoinType.INNER);

		eventsCq.where(
			cb.and(
				cazeJoin.get(AbstractDomainObject.ID).in(casesId),
				cb.isFalse(eventJoin.get(Event.DELETED)),
				cb.isFalse(eventJoin.get(Event.ARCHIVED)),
				cb.isFalse(eventsCqRoot.get(EventParticipant.DELETED))));
		eventsCq.multiselect(
			cazeJoin.get(Case.ID),
			eventJoin.get(Event.UUID),
			eventJoin.get(Event.EVENT_STATUS),
			eventJoin.get(Event.EVENT_TITLE),
			cb.coalesce(cb.coalesce(eventJoin.get(Event.END_DATE), eventJoin.get(Event.START_DATE)), eventJoin.get(Event.REPORT_DATE_TIME)));

		return em.createQuery(eventsCq).getResultList();
	}

	public List<ContactEventSummaryDetails> getEventSummaryDetailsByContacts(List<String> contactUuids) {
		if (contactUuids.isEmpty()) {
			return Collections.emptyList();
		}

		List<ContactEventSummaryDetails> eventSummaryDetailsList = new ArrayList<>();

		IterableHelper.executeBatched(contactUuids, ModelConstants.PARAMETER_LIMIT, batchedContactUuids -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ContactEventSummaryDetails> eventsCq = cb.createQuery(ContactEventSummaryDetails.class);
			Root<EventParticipant> eventsCqRoot = eventsCq.from(EventParticipant.class);
			Join<EventParticipant, Event> eventJoin = eventsCqRoot.join(EventParticipant.EVENT, JoinType.INNER);
			Join<Person, Contact> contactJoin = eventsCqRoot.join(EventParticipant.PERSON, JoinType.INNER).join(Person.CONTACTS, JoinType.INNER);

			eventsCq.where(
				cb.and(
					contactJoin.get(AbstractDomainObject.UUID).in(batchedContactUuids),
					cb.isFalse(eventJoin.get(Event.DELETED)),
					cb.isFalse(eventJoin.get(Event.ARCHIVED)),
					cb.isFalse(eventsCqRoot.get(EventParticipant.DELETED))));
			eventsCq.multiselect(
				contactJoin.get(Contact.UUID),
				eventJoin.get(Event.UUID),
				eventJoin.get(Event.EVENT_TITLE),
				cb.coalesce(cb.coalesce(eventJoin.get(Event.END_DATE), eventJoin.get(Event.START_DATE)), eventJoin.get(Event.REPORT_DATE_TIME)));

			eventSummaryDetailsList.addAll(em.createQuery(eventsCq).getResultList());
		});

		return eventSummaryDetailsList;
	}

	public boolean isEventEditAllowed(Event event) {
		if (event.getSormasToSormasOriginInfo() != null) {
			return event.getSormasToSormasOriginInfo().isOwnershipHandedOver();
		}

		return eventJurisdictionChecker.isInJurisdictionOrOwned(event) && !sormasToSormasShareInfoService.isEventOwnershipHandedOver(event);
	}
}
