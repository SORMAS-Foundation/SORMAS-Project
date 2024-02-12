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
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
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
import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.RequestContextHolder;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventCriteriaDateType;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolRuntimeException;
import de.symeda.sormas.api.share.ExternalShareStatus;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.criteria.CriteriaDateType;
import de.symeda.sormas.api.utils.criteria.ExternalShareDateType;
import de.symeda.sormas.backend.action.Action;
import de.symeda.sormas.backend.action.ActionService;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.ChangeDateBuilder;
import de.symeda.sormas.backend.common.ChangeDateFilterBuilder;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.common.DeletableAdo;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.document.DocumentService;
import de.symeda.sormas.backend.externalsurveillancetool.ExternalSurveillanceToolGatewayFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonQueryContext;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleJoins;
import de.symeda.sormas.backend.sample.SampleJurisdictionPredicateValidator;
import de.symeda.sormas.backend.share.ExternalShareInfo;
import de.symeda.sormas.backend.share.ExternalShareInfoCountAndLatestDate;
import de.symeda.sormas.backend.share.ExternalShareInfoService;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfoService;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserRole;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.ExternalDataUtil;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless
@LocalBean
public class EventService extends AbstractCoreAdoService<Event, EventJoins> {

	@EJB
	private EventParticipantService eventParticipantService;
	@EJB
	private TaskService taskService;
	@EJB
	private ActionService actionService;
	@EJB
	private CaseService caseService;
	@EJB
	private UserService userService;
	@EJB
	private SormasToSormasShareInfoService sormasToSormasShareInfoService;
	@EJB
	private SormasToSormasFacadeEjb.SormasToSormasFacadeEjbLocal sormasToSormasFacade;
	@EJB
	private SormasToSormasShareInfoFacadeEjb.SormasToSormasShareInfoFacadeEjbLocal sormasToSormasShareInfoFacade;
	@EJB
	private ExternalShareInfoService externalShareInfoService;
	@EJB
	private DocumentService documentService;
	@EJB
	private EventFacadeEjb.EventFacadeEjbLocal eventFacade;
	@EJB
	private ExternalSurveillanceToolGatewayFacadeEjb.ExternalSurveillanceToolGatewayFacadeEjbLocal externalSurveillanceToolGatewayFacade;

	public EventService() {
		super(Event.class, DeletableEntityType.EVENT);
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Predicate createRelevantDataFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Event> from) {

		Predicate filter = createActiveEventsFilter(cb, from);

		User user = getCurrentUser();
		if (user != null) {
			EventUserFilterCriteria eventUserFilterCriteria = new EventUserFilterCriteria();
			eventUserFilterCriteria.includeUserCaseAndEventParticipantFilter(true);
			eventUserFilterCriteria.forceRegionJurisdiction(true);

			EventQueryContext eventQueryContext = new EventQueryContext(cb, cq, from);
			filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(eventQueryContext, eventUserFilterCriteria));
		}

		return filter;
	}

	@Override
	protected List<String> referencesToBeFetched() {
		return Arrays.asList(Event.EVENT_LOCATION);
	}

	public List<String> getAllActiveUuids() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Event> from = cq.from(getElementClass());

		EventQueryContext eventQueryContext = new EventQueryContext(cb, cq, from);

		Predicate filter = createActiveEventsFilter(cb, from);

		User user = getCurrentUser();
		if (user != null) {
			EventUserFilterCriteria eventUserFilterCriteria = new EventUserFilterCriteria();
			eventUserFilterCriteria.includeUserCaseAndEventParticipantFilter(true);
			eventUserFilterCriteria.forceRegionJurisdiction(true);

			Predicate userFilter = createUserFilter(eventQueryContext, eventUserFilterCriteria);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		if (RequestContextHolder.isMobileSync()) {
			Predicate predicate = createLimitedChangeDateFilter(cb, from);
			if (predicate != null) {
				filter = CriteriaBuilderHelper.and(cb, filter, predicate);
			}
		}

		cq.where(filter);
		cq.select(from.get(Event.UUID));
		cq.distinct(true);

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

	public Map<String, Optional<User>> getAllEventUuidsWithResponsibleUserByPersonAndDiseaseAfterDateForNotification(
		String personUuid,
		Disease disease,
		Date date) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Event> eventRoot = cq.from(Event.class);
		Join<Event, EventParticipant> eventParticipantJoin = eventRoot.join(Event.EVENT_PARTICIPANTS, JoinType.INNER);
		Join<EventParticipant, Person> personJoin = eventParticipantJoin.join(EventParticipant.PERSON, JoinType.INNER);
		Join<Event, User> responsibleUserJoin = eventRoot.join(Event.RESPONSIBLE_USER, JoinType.LEFT);

		Timestamp timestamp = DateHelper.toTimestampUpper(date);
		Predicate filter = cb.and(
			cb.equal(personJoin.get(Person.UUID), personUuid),
			cb.equal(eventRoot.get(Event.DISEASE), disease),
			createActiveEventsFilter(cb, eventRoot),
			CriteriaBuilderHelper.greaterThanAndNotNull(cb, eventRoot.get(Event.REPORT_DATE_TIME), timestamp),
			eventParticipantService.createActiveEventParticipantsFilter(cb, eventParticipantJoin));
		cq.where(filter);
		cq.multiselect(eventRoot.get(Event.UUID), responsibleUserJoin).distinct(true);
		return em.createQuery(cq)
			.getResultList()
			.stream()
			.collect(Collectors.toMap(row -> (String) row[0], row -> Optional.ofNullable((User) row[1])));
	}

	public Map<Disease, Long> getEventCountByDisease(EventCriteria eventCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Event> event = cq.from(Event.class);
		EventQueryContext eventQueryContext = new EventQueryContext(cb, cq, event);

		cq.multiselect(event.get(Event.DISEASE), cb.count(event));
		cq.groupBy(event.get(Event.DISEASE));

		Predicate filter = createDefaultFilter(cb, event);
		filter = CriteriaBuilderHelper.and(cb, filter, buildCriteriaFilter(eventCriteria, eventQueryContext));
		filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(eventQueryContext));

		if (filter != null)
			cq.where(filter);

		List<Object[]> results = em.createQuery(cq).getResultList();

		return results.stream().collect(Collectors.toMap(e -> (Disease) e[0], e -> (Long) e[1]));
	}

	public Event getEventReferenceByEventParticipant(String eventParticipantUuid) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Event> cq = cb.createQuery(Event.class);
		final Root<Event> event = cq.from(Event.class);
		final EventQueryContext eventQueryContext = new EventQueryContext(cb, cq, event);
		final EventJoins joins = eventQueryContext.getJoins();

		Predicate filter = createDefaultFilter(cb, event);
		filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getEventParticipants().get(EventParticipant.UUID), eventParticipantUuid));
		filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(eventQueryContext));
		cq.where(filter);

		return em.createQuery(cq).getResultList().stream().findFirst().orElse(null);
	}

	@Override
	public ProcessedEntity archive(String entityUuid, Date endOfProcessingDate) {
		return archive(Collections.singletonList(entityUuid)).get(0);
	}

	@Override
	public List<ProcessedEntity> archive(List<String> entityUuids) {

		List<ProcessedEntity> updatedInExternalSurveillanceTool = updateArchiveFlagInExternalSurveillanceTool(entityUuids, true);
		List<String> uuidsWithoutFailure = getEntitiesWithoutFailure(entityUuids, updatedInExternalSurveillanceTool).stream()
			.map(AbstractDomainObject::getUuid)
			.collect(Collectors.toList());

		List<ProcessedEntity> resultList =
			updatedInExternalSurveillanceTool.stream().filter(e -> !uuidsWithoutFailure.contains(e.getEntityUuid())).collect(Collectors.toList());

		if (uuidsWithoutFailure.size() > 0) {
			resultList.addAll(super.archive(uuidsWithoutFailure));
		}

		return resultList;
	}

	@Override
	public List<ProcessedEntity> dearchive(List<String> entityUuids, String dearchiveReason) {

		List<ProcessedEntity> updatedInExternalSurveillanceTool = updateArchiveFlagInExternalSurveillanceTool(entityUuids, false);
		List<String> uuidsWithoutFailure = getEntitiesWithoutFailure(entityUuids, updatedInExternalSurveillanceTool).stream()
			.map(AbstractDomainObject::getUuid)
			.collect(Collectors.toList());

		List<ProcessedEntity> resultList =
			updatedInExternalSurveillanceTool.stream().filter(e -> !uuidsWithoutFailure.contains(e.getEntityUuid())).collect(Collectors.toList());

		if (uuidsWithoutFailure.size() > 0) {
			resultList.addAll(super.dearchive(uuidsWithoutFailure, dearchiveReason));
		}

		return resultList;
	}

	private List<ProcessedEntity> updateArchiveFlagInExternalSurveillanceTool(List<String> entityUuids, boolean archived) {
		List<ProcessedEntity> processedEntities = new ArrayList<>();

		List<String> sharedEventUuids = getSharedEventUuids(entityUuids);
		if (!sharedEventUuids.isEmpty()) {
			processedEntities = externalSurveillanceToolGatewayFacade.sendEventsInternal(sharedEventUuids, archived);
		}

		return processedEntities;
	}

	public List<String> getSharedEventUuids(List<String> entityUuids) {
		List<Long> eventIds = getEventIds(entityUuids);
		List<String> sharedEventUuids = new ArrayList<>();
		List<ExternalShareInfoCountAndLatestDate> eventShareInfos =
			externalShareInfoService.getShareCountAndLatestDate(eventIds, ExternalShareInfo.EVENT);
		eventShareInfos.forEach(shareInfo -> {
			if (shareInfo.getLatestStatus() != ExternalShareStatus.DELETED) {
				sharedEventUuids.add(shareInfo.getAssociatedObjectUuid());
			}
		});

		return sharedEventUuids;
	}

	public List<Long> getEventIds(List<String> entityUuids) {
		List<Long> eventIds = new ArrayList<>();
		entityUuids.forEach(uuid -> eventIds.add(this.getByUuid(uuid).getId()));
		return eventIds;
	}

	public List<String> getArchivedUuidsSince(Date since) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Event> event = cq.from(Event.class);
		final EventQueryContext eventQueryContext = new EventQueryContext(cb, cq, event);

		EventUserFilterCriteria eventUserFilterCriteria = new EventUserFilterCriteria();
		eventUserFilterCriteria.includeUserCaseAndEventParticipantFilter(true);
		eventUserFilterCriteria.forceRegionJurisdiction(true);

		Predicate filter = createUserFilter(eventQueryContext, eventUserFilterCriteria);
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

		List<String> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public List<String> getDeletedUuidsSince(Date since) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<String> cq = cb.createQuery(String.class);
		final Root<Event> event = cq.from(Event.class);
		final EventQueryContext eventQueryContext = new EventQueryContext(cb, cq, event);

		EventUserFilterCriteria eventUserFilterCriteria = new EventUserFilterCriteria();
		eventUserFilterCriteria.includeUserCaseAndEventParticipantFilter(true);
		eventUserFilterCriteria.forceRegionJurisdiction(true);

		Predicate filter = createUserFilter(eventQueryContext, eventUserFilterCriteria);
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

	@Override
	@SuppressWarnings("rawtypes")
	protected Predicate createUserFilterInternal(CriteriaBuilder cb, CriteriaQuery cq, From<?, Event> from) {
		return createUserFilter(new EventQueryContext(cb, cq, from));
	}

	@Override
	protected EventJoins toJoins(From<?, Event> adoPath) {
		return new EventJoins(adoPath);
	}

	public Predicate createUserFilter(EventQueryContext queryContext) {
		return createUserFilter(queryContext, null);
	}

	public Predicate createUserFilter(final EventQueryContext queryContext, final EventUserFilterCriteria eventUserFilterCriteria) {

		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return null;
		}
		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		Predicate filter = null;

		@SuppressWarnings("rawtypes")
		final CriteriaQuery cq = queryContext.getQuery();
		final CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		final EventJoins eventJoins = queryContext.getJoins();
		final From<?, Event> eventJoin = queryContext.getRoot();

		if (isRestrictedToAssignedEntities()) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(eventJoin.get(Event.RESPONSIBLE_USER).get(User.ID), currentUser.getId()));
		} else {
			if (jurisdictionLevel != JurisdictionLevel.NATION) {
				switch (jurisdictionLevel) {
				case REGION:
					if (currentUser.getRegion() != null) {
						filter =
							CriteriaBuilderHelper.or(cb, filter, cb.equal(eventJoins.getLocation().get(Location.REGION), currentUser.getRegion()));
					}
					break;
				case DISTRICT:
					if (currentUser.getDistrict() != null) {
						filter = CriteriaBuilderHelper
							.or(cb, filter, cb.equal(eventJoins.getLocation().get(Location.DISTRICT), currentUser.getDistrict()));
					}
					break;
				case COMMUNITY:
					if (currentUser.getCommunity() != null) {
						filter = CriteriaBuilderHelper
							.or(cb, filter, cb.equal(eventJoins.getLocation().get(Location.COMMUNITY), currentUser.getCommunity()));
					}
					break;
				case HEALTH_FACILITY:
					if (currentUser.getHealthFacility() != null && currentUser.getHealthFacility().getDistrict() != null) {
						filter = CriteriaBuilderHelper
							.or(cb, filter, cb.equal(eventJoins.getLocation().get(Location.DISTRICT), currentUser.getHealthFacility().getDistrict()));
					}
					break;
				case LABORATORY:
					final Subquery<Long> sampleSubQuery = cq.subquery(Long.class);
					final Root<Sample> sampleRoot = sampleSubQuery.from(Sample.class);
					final SampleJoins sampleJoins = new SampleJoins(sampleRoot);
					final Join eventParticipant = sampleJoins.getEventParticipant();
					final From<?, EventParticipant> eventParticipantJoin = eventJoins.getEventParticipants();
					SampleJurisdictionPredicateValidator sampleJurisdictionPredicateValidator =
						SampleJurisdictionPredicateValidator.withoutAssociations(cb, sampleJoins, currentUser);
					sampleSubQuery.where(
						cb.and(cb.equal(eventParticipant, eventParticipantJoin), sampleJurisdictionPredicateValidator.inJurisdictionOrOwned()));
					sampleSubQuery.select(sampleRoot.get(Sample.ID));
					filter = CriteriaBuilderHelper.or(cb, cb.exists(sampleSubQuery));
					break;
				default:
				}

				Predicate filterResponsible = cb.equal(eventJoins.getRoot().get(Event.REPORTING_USER), currentUser);
				filterResponsible = cb.or(filterResponsible, cb.equal(eventJoins.getRoot().get(Event.RESPONSIBLE_USER), currentUser));

				if (eventUserFilterCriteria != null && eventUserFilterCriteria.isIncludeUserCaseAndEventParticipantFilter()) {
					filter = CriteriaBuilderHelper.or(cb, filter, createCaseAndEventParticipantFilter(queryContext));
				}

				if (eventUserFilterCriteria != null && eventUserFilterCriteria.isForceRegionJurisdiction()) {
					filter = CriteriaBuilderHelper.or(cb, filter, cb.equal(eventJoins.getLocation().get(Location.REGION), currentUser.getRegion()));
				}

				if (filter != null) {
					filter = CriteriaBuilderHelper.or(cb, filter, filterResponsible);
				} else {
					filter = filterResponsible;
				}
			}
		}

		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			CriteriaBuilderHelper.limitedDiseasePredicate(cb, currentUser, eventJoin.get(Event.DISEASE), cb.isNull(eventJoin.get(Event.DISEASE))));

		if (RequestContextHolder.isMobileSync()) {
			Predicate limitedChangeDatePredicate = CriteriaBuilderHelper.and(cb, createLimitedChangeDateFilter(cb, eventJoin));
			if (limitedChangeDatePredicate != null) {
				filter = CriteriaBuilderHelper.and(cb, filter, limitedChangeDatePredicate);
			}
		}

		return filter;
	}

	public Predicate createCaseAndEventParticipantFilter(EventQueryContext eventQueryContext) {

		From<?, EventParticipant> eventParticipants = eventQueryContext.getJoins().getEventParticipants();

		CriteriaBuilder cb = eventQueryContext.getCriteriaBuilder();
		Predicate filter = caseService.createUserFilter(
			new CaseQueryContext(cb, eventQueryContext.getQuery(), eventQueryContext.getJoins().getEventParticipantJoins().getCaseJoins()));

		final User currentUser = getCurrentUser();
		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		if (jurisdictionLevel == JurisdictionLevel.REGION || jurisdictionLevel == JurisdictionLevel.DISTRICT) {

			switch (jurisdictionLevel) {
			case REGION:
				if (currentUser.getRegion() != null) {
					filter = CriteriaBuilderHelper
						.or(cb, filter, cb.equal(eventParticipants.get(EventParticipant.REGION).get(Region.ID), currentUser.getRegion().getId()));
				}
				break;
			case DISTRICT:
				if (currentUser.getDistrict() != null) {
					filter = CriteriaBuilderHelper.or(
						cb,
						filter,
						cb.equal(eventParticipants.get(EventParticipant.DISTRICT).get(District.ID), currentUser.getDistrict().getId()));
				}
				break;
			//$CASES-OMITTED$
			default:
				break;
			}
		}

		return filter;
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, Event> eventPath, Timestamp date) {
		return addChangeDates(new ChangeDateFilterBuilder(cb, date), toJoins(eventPath), false).build();
	}

	public Predicate createChangeDateFilter(CriteriaBuilder cb, EventJoins joins, Expression<? extends Date> dateExpression) {
		return addChangeDates(new ChangeDateFilterBuilder(cb, dateExpression), joins, false).build();
	}

	@Override
	protected <T extends ChangeDateBuilder<T>> T addChangeDates(T builder, EventJoins joins, boolean includeExtendedChangeDateFilters) {

		final From<?, Event> eventFrom = joins.getRoot();
		final Join<Event, Action> eventActionJoin = joins.getEventActions();
		final From<?, EventParticipant> eventParticipants = joins.getEventParticipants();
		final Join<EventParticipant, Sample> eventParticipantSampleJoin = joins.getEventParticipantJoins().getSamples();

		builder = super.addChangeDates(builder, joins, includeExtendedChangeDateFilters).add(eventFrom, Event.EVENT_LOCATION);

		if (includeExtendedChangeDateFilters) {
			builder.add(eventFrom, Event.SORMAS_TO_SORMAS_ORIGIN_INFO)
				.add(eventFrom, Event.SORMAS_TO_SORMAS_SHARES)
				.add(eventActionJoin)
				.add(eventParticipants)
				.add(eventParticipantSampleJoin);
		}

		return builder;
	}

	@Override
	public void delete(Event event, DeletionDetails deletionDetails) {

		// Delete all event participants associated with this event
		List<EventParticipant> eventParticipants = eventParticipantService.getAllByEventAfter(null, event);
		for (EventParticipant eventParticipant : eventParticipants) {
			eventParticipantService.delete(eventParticipant, deletionDetails);
		}

		removeFromSubordinateEvents(event);
		deleteEventInExternalSurveillanceTool(event);

		// Mark the event as deleted
		super.delete(event, deletionDetails);
	}

	@Override
	public void restore(Event event) {
		// restore all event participants associated with this event
		List<EventParticipant> eventParticipants = eventParticipantService.getAllByEventAfter(null, event);
		for (EventParticipant eventParticipant : eventParticipants) {
			eventParticipantService.restore(eventParticipant);
		}
		super.restore(event);
	}

	@Override
	public void deletePermanent(Event event) {

		// Delete all tasks associated with this event
		List<Task> tasks = taskService.findBy(new TaskCriteria().event(new EventReferenceDto(event.getUuid())), true);
		for (Task task : tasks) {
			taskService.deletePermanent(task);
		}

		// Delete all event actions associated with this event
		List<Action> actions = actionService.getAllByEvent(event);
		for (Action action : actions) {
			actionService.deletePermanent(action);
		}

		event.getEventParticipants().forEach(eventParticipant -> eventParticipantService.deletePermanent(eventParticipant));

		sormasToSormasShareInfoService.getByAssociatedEntity(SormasToSormasShareInfo.EVENT, event.getUuid()).forEach(s -> {
			s.setEvent(null);
			if (sormasToSormasShareInfoFacade.hasAnyEntityReference(s)) {
				sormasToSormasShareInfoService.ensurePersisted(s);
			} else {
				try {
					sormasToSormasFacade.revokePendingShareRequests(Collections.singletonList(s), false);
				} catch (SormasToSormasException e) {
					logger.warn("Could not revoke share requests of share info {}", s.getUuid(), e);
				}
				sormasToSormasShareInfoService.deletePermanent(s);
			}
		});

		externalShareInfoService.getShareInfoByEvent(event.getUuid()).forEach(e -> {
			externalShareInfoService.deletePermanent(e);
		});

		documentService.getRelatedToEntity(DocumentRelatedEntityType.EVENT, event.getUuid()).forEach(d -> documentService.markAsDeleted(d));

		removeFromSubordinateEvents(event);

		super.deletePermanent(event);
	}

	private void removeFromSubordinateEvents(Event event) {
		event.getSubordinateEvents().forEach(subEvent -> {
			subEvent.setSuperordinateEvent(null);
			ensurePersisted(subEvent);
		});
	}

	private void deleteEventInExternalSurveillanceTool(Event event) {
		try {
			eventFacade.deleteEventInExternalSurveillanceTool(event);
		} catch (ExternalSurveillanceToolException e) {
			throw new ExternalSurveillanceToolRuntimeException(e.getMessage(), e.getErrorCode());
		}
	}

	public List<Event> getByExternalId(String externalId) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Event> cq = cb.createQuery(Event.class);
		Root<Event> eventRoot = cq.from(Event.class);

		cq.where(cb.equal(eventRoot.get(Event.EXTERNAL_ID), externalId), cb.equal(eventRoot.get(Event.DELETED), Boolean.FALSE));

		return em.createQuery(cq).getResultList();
	}

	public Predicate buildCriteriaFilter(EventCriteria eventCriteria, EventQueryContext eventQueryContext) {

		CriteriaBuilder cb = eventQueryContext.getCriteriaBuilder();
		From<?, Event> from = eventQueryContext.getRoot();
		final EventJoins joins = eventQueryContext.getJoins();

		Predicate filter = null;
		if (eventCriteria.getReportingUserRole() != null) {
			Join<Event, User> reportingUser = joins.getReportingUser();
			Join<User, UserRole> rolesJoin = reportingUser.join(User.USER_ROLES, JoinType.LEFT);
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(rolesJoin.get(UserRole.UUID), eventCriteria.getReportingUserRole().getUuid()));
		}
		if (eventCriteria.getDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Event.DISEASE), eventCriteria.getDisease()));
		}
		if (eventCriteria.getDiseaseVariant() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Event.DISEASE_VARIANT), eventCriteria.getDiseaseVariant()));
		}
		if (eventCriteria.getEventStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Event.EVENT_STATUS), eventCriteria.getEventStatus()));
		}
		if (eventCriteria.getRiskLevel() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Event.RISK_LEVEL), eventCriteria.getRiskLevel()));
		}
		if (eventCriteria.getSpecificRisk() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Event.SPECIFIC_RISK), eventCriteria.getSpecificRisk()));
		}
		if (eventCriteria.getEventInvestigationStatus() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(from.get(Event.EVENT_INVESTIGATION_STATUS), eventCriteria.getEventInvestigationStatus()));
		}
		if (eventCriteria.getEventManagementStatus() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Event.EVENT_MANAGEMENT_STATUS), eventCriteria.getEventManagementStatus()));
		}
		if (eventCriteria.getEventIdentificationSource() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(from.get(Event.EVENT_IDENTIFICATION_SOURCE), eventCriteria.getEventIdentificationSource()));
		}
		if (eventCriteria.getTypeOfPlace() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Event.TYPE_OF_PLACE), eventCriteria.getTypeOfPlace()));
		}
		if (eventCriteria.getRelevanceStatus() != null) {
			if (eventCriteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.or(cb.equal(from.get(Event.ARCHIVED), false), cb.isNull(from.get(Event.ARCHIVED))));
			} else if (eventCriteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Event.ARCHIVED), true));
			} else if (eventCriteria.getRelevanceStatus() == EntityRelevanceStatus.DELETED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Event.DELETED), true));
			}
		}
		if (eventCriteria.getRelevanceStatus() != EntityRelevanceStatus.DELETED) {
			filter = CriteriaBuilderHelper.and(cb, filter, createDefaultFilter(cb, from));
		}

		if (eventCriteria.getRegion() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getRegion().get(Region.UUID), eventCriteria.getRegion().getUuid()));
		}
		if (eventCriteria.getDistrict() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getDistrict().get(District.UUID), eventCriteria.getDistrict().getUuid()));
		}
		if (eventCriteria.getCommunity() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getCommunity().get(Community.UUID), eventCriteria.getCommunity().getUuid()));
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
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(joins.getResponsibleUser().get(User.UUID), eventCriteria.getResponsibleUser().getUuid()));
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
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(Event.INTERNAL_TOKEN), textFilter),
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
			From<?, EventParticipant> eventParticipantJoin = joins.getEventParticipants();
			Join<EventParticipant, Person> personJoin = joins.getEventParticipantPersons();

			final PersonQueryContext personQueryContext =
				new PersonQueryContext(cb, eventQueryContext.getQuery(), joins.getEventParticipantJoins().getPersonJoins());

			String[] textFilters = eventCriteria.getFreeTextEventParticipants().split("\\s+");

			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.ilike(cb, eventParticipantJoin.get(EventParticipant.UUID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, personJoin.get(Person.FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, personJoin.get(Person.LAST_NAME), textFilter),
					CriteriaBuilderHelper.ilike(cb, personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_PHONE_SUBQUERY), textFilter),
					CriteriaBuilderHelper.ilike(cb, personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_EMAIL_SUBQUERY), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isFalse(eventParticipantJoin.get(EventParticipant.DELETED)));
		}
		if (StringUtils.isNotEmpty(eventCriteria.getFreeTextEventGroups())) {
			Join<Event, EventGroup> eventGroupJoin = joins.getEventGroup();

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
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getEventParticipantCases().get(Case.UUID), eventCriteria.getCaze().getUuid()));

			filter = CriteriaBuilderHelper.and(cb, filter, cb.isFalse(joins.getEventParticipants().get(EventParticipant.DELETED)));
		}
		if (eventCriteria.getPerson() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.in(joins.getEventParticipantPersons().get(Person.UUID)).value(eventCriteria.getPerson().getUuid()),
				cb.isFalse(joins.getEventParticipants().get(EventParticipant.DELETED)));
		}
		if (eventCriteria.getFacilityType() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getLocation().get(Location.FACILITY_TYPE), eventCriteria.getFacilityType()));
		}
		if (eventCriteria.getFacility() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getFacility().get(Facility.UUID), eventCriteria.getFacility().getUuid()));
		}
		if (eventCriteria.getSuperordinateEvent() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(from.get(Event.SUPERORDINATE_EVENT).get(AbstractDomainObject.UUID), eventCriteria.getSuperordinateEvent().getUuid()));
		}
		if (eventCriteria.getEventGroup() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getEventGroup().get(EventGroup.UUID), eventCriteria.getEventGroup().getUuid()));
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
				(latestShareDate) -> createChangeDateFilter(cb, joins, latestShareDate)));

		return filter;
	}

	private Predicate createEventDateFilter(CriteriaQuery<?> cq, CriteriaBuilder cb, From<?, Event> from, EventCriteria eventCriteria) {
		Predicate filter = null;

		CriteriaDateType eventDateType = eventCriteria.getEventDateType();
		Date eventDateFrom = eventCriteria.getEventDateFrom();
		Date eventDateTo = eventCriteria.getEventDateTo();

		if (eventDateType == null || eventDateType == EventCriteriaDateType.EVENT_DATE) {
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
				filter = CriteriaBuilderHelper.and(cb, filter, eventDateFilter);
			}
		} else if (eventDateType == EventCriteriaDateType.REPORT_DATE) {
			Predicate eventDateFilter = null;

			if (eventDateFrom != null && eventDateTo != null) {
				eventDateFilter = cb.between(from.get(Event.REPORT_DATE_TIME), eventDateFrom, eventDateTo);
			} else if (eventDateFrom != null) {
				eventDateFilter = cb.greaterThanOrEqualTo(from.get(Event.REPORT_DATE_TIME), eventDateFrom);
			} else if (eventDateTo != null) {
				eventDateFilter = cb.lessThanOrEqualTo(from.get(Event.REPORT_DATE_TIME), eventDateTo);
			}

			if (eventDateFrom != null || eventDateTo != null) {
				filter = CriteriaBuilderHelper.and(cb, filter, eventDateFilter);
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
	 * Creates a filter that excludes all events that are either {@link Event#isArchived()} or {@link DeletableAdo#isDeleted()}.
	 */
	public Predicate createActiveEventsFilter(CriteriaBuilder cb, Root<Event> root) {
		return cb.and(cb.isFalse(root.get(Event.ARCHIVED)), cb.isFalse(root.get(Event.DELETED)));
	}

	/**
	 * Creates a filter that excludes all events that are either {@link Event#isArchived()} or {@link DeletableAdo#isDeleted()}.
	 */
	public Predicate createActiveEventsFilter(CriteriaBuilder cb, Path<Event> root) {
		return cb.and(cb.isFalse(root.get(Event.ARCHIVED)), cb.isFalse(root.get(Event.DELETED)));
	}

	/**
	 * Creates a default filter that should be used as the basis of queries that do not use {@link EventCriteria}.
	 * This essentially removes {@link DeletableAdo#isDeleted()} events from the queries.
	 */
	public Predicate createDefaultFilter(CriteriaBuilder cb, From<?, Event> root) {
		return cb.isFalse(root.get(Event.DELETED));
	}

	public String getUuidByCaseUuidOrPersonUuid(String searchTerm) {

		if (StringUtils.isEmpty(searchTerm)) {
			return null;
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Event> root = cq.from(Event.class);
		EventJoins joins = new EventJoins(root);

		Predicate filter = cb.or(
			cb.equal(cb.lower(joins.getEventParticipantCases().get(Case.UUID)), searchTerm.toLowerCase()),
			cb.equal(cb.lower(joins.getEventParticipantPersons().get(Person.UUID)), searchTerm.toLowerCase()));

		cq.where(filter);
		cq.orderBy(cb.desc(root.get(Event.REPORT_DATE_TIME)));
		cq.select(root.get(Event.UUID));

		return QueryHelper.getFirstResult(em, cq);
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

	@Override
	public EditPermissionType getEditPermissionType(Event event) {

		if (!inJurisdictionOrOwned(event)) {
			return EditPermissionType.OUTSIDE_JURISDICTION;
		}

		if (sormasToSormasShareInfoService.isEventOwnershipHandedOver(event)
			|| event.getSormasToSormasOriginInfo() != null && !event.getSormasToSormasOriginInfo().isOwnershipHandedOver()) {
			return EditPermissionType.WITHOUT_OWNERSHIP;
		}

		return super.getEditPermissionType(event);
	}

	public boolean inJurisdiction(Event event) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Boolean> cq = cb.createQuery(Boolean.class);
		Root<Event> root = cq.from(Event.class);
		cq.multiselect(JurisdictionHelper.booleanSelector(cb, inJurisdiction(new EventQueryContext(cb, cq, root), userService.getCurrentUser())));
		cq.where(cb.equal(root.get(Event.UUID), event.getUuid()));
		return em.createQuery(cq).getResultList().stream().anyMatch(aBoolean -> aBoolean);
	}

	public Predicate inJurisdiction(EventQueryContext qc, User user) {
		return EventJurisdictionPredicateValidator.of(qc, user).inJurisdiction();
	}

	@Override
	public Predicate inJurisdictionOrOwned(CriteriaBuilder cb, CriteriaQuery<?> cq, From<?, Event> from) {
		return inJurisdictionOrOwned(new EventQueryContext(cb, cq, from));
	}

	public Predicate inJurisdictionOrOwned(EventQueryContext qc) {
		return inJurisdictionOrOwned(qc, getCurrentUser());
	}

	public Predicate inJurisdictionOrOwned(EventQueryContext qc, User user) {
		return EventJurisdictionPredicateValidator.of(qc, user).inJurisdictionOrOwned();
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateExternalData(List<ExternalDataDto> externalData) throws ExternalDataUpdateException {
		ExternalDataUtil.updateExternalData(externalData, this::getByUuids, this::ensurePersisted);
	}

	public List<Event> getAllByCase(String caseUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Event> cq = cb.createQuery(getElementClass());
		Root<Event> from = cq.from(getElementClass());
		from.fetch(Event.EVENT_LOCATION);

		EventQueryContext eventQueryContext = new EventQueryContext(cb, cq, from);
		EventJoins joins = eventQueryContext.getJoins();

		Predicate filter = createActiveEventsFilter(cb, from);

		User user = getCurrentUser();
		if (user != null) {
			Predicate userFilter = createUserFilter(eventQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getEventParticipantCases().get(Case.UUID), caseUuid));

		cq.where(filter);
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}

	public boolean hasRegionAndDistrict(String eventUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Event> from = cq.from(getElementClass());
		Join<Event, Location> locationJoin = from.join(Event.EVENT_LOCATION, JoinType.LEFT);

		cq.where(
			CriteriaBuilderHelper.and(
				cb,
				cb.equal(from.get(AbstractDomainObject.UUID), eventUuid),
				cb.isNotNull(locationJoin.get(Location.REGION)),
				cb.isNotNull(locationJoin.get(Location.DISTRICT))));
		cq.select(cb.count(from));

		return em.createQuery(cq).getSingleResult() > 0;
	}

	public boolean hasAnyEventParticipantWithoutJurisdiction(String eventUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<EventParticipant> from = cq.from(EventParticipant.class);
		Join<EventParticipant, Event> eventJoin = from.join(EventParticipant.EVENT, JoinType.LEFT);

		cq.where(
			CriteriaBuilderHelper.and(
				cb,
				cb.equal(eventJoin.get(AbstractDomainObject.UUID), eventUuid),
				cb.or(cb.isNull(from.get(EventParticipant.REGION)), cb.isNull(from.get(EventParticipant.DISTRICT))),
				cb.isFalse(from.get(EventParticipant.DELETED))));
		cq.select(cb.count(from));

		return em.createQuery(cq).getSingleResult() > 0;
	}

	@Override
	protected boolean hasLimitedChangeDateFilterImplementation() {
		return true;
	}

	@Override
	protected String getDeleteReferenceField(DeletionReference deletionReference) {
		if (deletionReference == DeletionReference.REPORT) {
			return Event.REPORT_DATE_TIME;
		}

		return super.getDeleteReferenceField(deletionReference);
	}
}
