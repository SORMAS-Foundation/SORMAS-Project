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
package de.symeda.sormas.backend.task;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang3.ArrayUtils;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.RequestContextHolder;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskJurisdictionFlagsDto;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.caze.CaseUserFilterCriteria;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.common.JurisdictionFlagsService;
import de.symeda.sormas.backend.common.TaskCreationException;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactQueryContext;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.environment.EnvironmentQueryContext;
import de.symeda.sormas.backend.environment.EnvironmentService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventQueryContext;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.travelentry.TravelEntryQueryContext;
import de.symeda.sormas.backend.travelentry.services.TravelEntryService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserRole;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.JurisdictionHelper;

@Stateless
@LocalBean
public class TaskService extends AdoServiceWithUserFilterAndJurisdiction<Task>
	implements JurisdictionFlagsService<Task, TaskJurisdictionFlagsDto, TaskJoins, TaskQueryContext> {

	@EJB
	private CaseService caseService;
	@EJB
	private ContactService contactService;
	@EJB
	private EventService eventService;
	@EJB
	private UserService userService;
	@EJB
	private TravelEntryService travelEntryService;
	@EJB
	private EnvironmentService environmentService;
	@EJB
	private FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	public TaskService() {
		super(Task.class);
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Predicate createRelevantDataFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Task> from) {

		final TaskQueryContext taskQueryContext = new TaskQueryContext(cb, cq, from);
		Predicate filter = buildActiveTasksFilter(taskQueryContext);

		if (getCurrentUser() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(taskQueryContext));
		}

		return filter;
	}

	public List<String> getAllActiveUuids(User user) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<String> cq = cb.createQuery(String.class);
		final Root<Task> from = cq.from(getElementClass());
		final TaskQueryContext taskQueryContext = new TaskQueryContext(cb, cq, from);

		Predicate filter = buildActiveTasksFilter(taskQueryContext);

		if (user != null) {
			Predicate userFilter = createUserFilter(taskQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		if (RequestContextHolder.isMobileSync()) {
			Predicate predicate = createLimitedChangeDateFilter(cb, from);
			if (predicate != null) {
				filter = CriteriaBuilderHelper.and(cb, filter, predicate);
			}
		}

		cq.where(filter);
		cq.select(from.get(Task.UUID));

		return em.createQuery(cq).getResultList();
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Task> taskPath) {
		return createUserFilter(new TaskQueryContext(cb, cq, taskPath));
	}

	public Predicate createUserFilter(TaskQueryContext taskQueryContext) {
		return createUserFilter(taskQueryContext, null);
	}

	private boolean hasContextOrNoContext(TaskCriteria taskCriteria, TaskContext taskContext) {

		switch (taskContext) {
		case CASE:
			return taskCriteria == null
				|| !taskCriteria.hasContextCriteria()
				|| (taskCriteria.getTaskContext() == TaskContext.CASE || taskCriteria.getCaze() != null);
		case CONTACT:
			return taskCriteria == null
				|| !taskCriteria.hasContextCriteria()
				|| (taskCriteria.getTaskContext() == TaskContext.CONTACT || taskCriteria.getContact() != null);
		case EVENT:
			return taskCriteria == null
				|| !taskCriteria.hasContextCriteria()
				|| (taskCriteria.getTaskContext() == TaskContext.EVENT || taskCriteria.getEvent() != null);
		case TRAVEL_ENTRY:
			return taskCriteria == null
				|| !taskCriteria.hasContextCriteria()
				|| (taskCriteria.getTaskContext() == TaskContext.TRAVEL_ENTRY || taskCriteria.getTravelEntry() != null);
		case ENVIRONMENT:
			return taskCriteria == null || (taskCriteria.getTaskContext() == TaskContext.ENVIRONMENT || taskCriteria.getEnvironment() != null);
		case GENERAL:
			return taskCriteria == null || !taskCriteria.hasContextCriteria() || taskCriteria.getTaskContext() == TaskContext.GENERAL;
		default:
			throw new IllegalArgumentException(taskContext.toString());
		}
	}

	public Predicate createUserFilter(TaskQueryContext taskQueryContext, TaskCriteria taskCriteria) {

		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return null;
		}

		CriteriaQuery<?> cq = taskQueryContext.getQuery();
		CriteriaBuilder cb = taskQueryContext.getCriteriaBuilder();
		From<?, Task> taskPath = taskQueryContext.getRoot();

		TaskJoins joins = taskQueryContext.getJoins();

		Predicate assigneeFilter = createAssigneeFilter(cb, joins.getAssignee());

		Predicate relatedEntityNotDeletedFilter = cb.or(
			cb.equal(taskPath.get(Task.TASK_CONTEXT), TaskContext.GENERAL),
			caseService.createDefaultFilter(cb, joins.getCaze()),
			contactService.createDefaultFilter(cb, joins.getContact()),
			eventService.createDefaultFilter(cb, joins.getEvent()),
			travelEntryService.createDefaultFilter(cb, joins.getTravelEntry()),
			environmentService.createDefaultFilter(cb, joins.getEnvironment()));

		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		if (jurisdictionLevel == JurisdictionLevel.NATION && currentUser.getUserRoles().stream().noneMatch(UserRole::isPortHealthUser)) {
			return cb.and(assigneeFilter, relatedEntityNotDeletedFilter);
		}

		Predicate filter = cb.equal(taskPath.get(Task.CREATOR_USER), currentUser);
		filter = cb.or(filter, cb.equal(taskPath.get(Task.ASSIGNEE_USER), currentUser));

		Predicate caseFilter = hasContextOrNoContext(taskCriteria, TaskContext.CASE)
			? caseService.createUserFilter(
				new CaseQueryContext(cb, cq, joins.getCaseJoins()),
				taskCriteria != null
					? new CaseUserFilterCriteria().excludeLimitedSyncRestrictions(taskCriteria.isExcludeLimitedSyncRestrictions())
					: null)
			: null;
		if (caseFilter != null) {
			filter = cb.or(filter, caseFilter);
		}
		Predicate contactFilter = hasContextOrNoContext(taskCriteria, TaskContext.CONTACT)
			? contactService.createUserFilter(new ContactQueryContext(cb, cq, joins.getContactJoins()))
			: null;
		if (contactFilter != null) {
			filter = cb.or(
				filter,
				CriteriaBuilderHelper
					.or(cb, contactFilter, createAssigneeOrObserverFilter(cb, joins.getAssignee(), joins.getTaskObservers(), currentUser)));
		}
		Predicate eventFilter = hasContextOrNoContext(taskCriteria, TaskContext.EVENT)
			? eventService.createUserFilter(new EventQueryContext(cb, cq, joins.getEventJoins()))
			: null;
		if (eventFilter != null) {
			filter = cb.or(filter, eventFilter);
		}
		Predicate travelEntryFilter = hasContextOrNoContext(taskCriteria, TaskContext.TRAVEL_ENTRY)
			? travelEntryService.createUserFilter(new TravelEntryQueryContext(cb, cq, joins.getTravelEntryJoins()))
			: null;
		if (travelEntryFilter != null) {
			filter = cb.or(filter, travelEntryFilter);
		}
		Predicate environmantFilter = hasContextOrNoContext(taskCriteria, TaskContext.ENVIRONMENT)
			? environmentService.createUserFilter(new EnvironmentQueryContext(cb, cq, joins.getEnvironmentJoins()))
			: null;
		if (environmantFilter != null) {
			filter = cb.or(filter, environmantFilter);
		}

		filter = cb.or(filter, assigneeFilter);

		if (RequestContextHolder.isMobileSync()) {
			Predicate limitedChangeDatePredicate = CriteriaBuilderHelper.and(cb, createLimitedChangeDateFilter(cb, taskQueryContext.getRoot()));
			if (limitedChangeDatePredicate != null) {
				filter = CriteriaBuilderHelper.and(cb, filter, limitedChangeDatePredicate);
			}
		}

		if ((taskCriteria == null || !taskCriteria.isExcludeLimitedSyncRestrictions())
			&& featureConfigurationFacade
				.isPropertyValueTrue(FeatureType.LIMITED_SYNCHRONIZATION, FeatureTypeProperty.EXCLUDE_NO_CASE_CLASSIFIED_CASES)
			&& RequestContextHolder.isMobileSync()) {

			Predicate limitedCaseSyncPredicate = CriteriaBuilderHelper.and(
				cb,
				caseService.createLimitedSyncCasePredicate(cb, joins.getCaze(), currentUser),
				caseService.createLimitedSyncCasePredicate(cb, joins.getContactCase(), currentUser));

			return CriteriaBuilderHelper.and(cb, filter, relatedEntityNotDeletedFilter, limitedCaseSyncPredicate);
		} else {
			return CriteriaBuilderHelper.and(cb, filter, relatedEntityNotDeletedFilter);
		}
	}

	@Override
	protected List<Predicate> getAdditionalObsoleteUuidsPredicates(Date since, CriteriaBuilder cb, CriteriaQuery<String> cq, Root<Task> from) {

		if (featureConfigurationFacade.isFeatureEnabled(FeatureType.LIMITED_SYNCHRONIZATION)
			&& featureConfigurationFacade
				.isPropertyValueTrue(FeatureType.LIMITED_SYNCHRONIZATION, FeatureTypeProperty.EXCLUDE_NO_CASE_CLASSIFIED_CASES)) {

			List<Predicate> predicates = new ArrayList<>();

			TaskQueryContext taskQueryContext = new TaskQueryContext(cb, cq, from);
			predicates.add(caseService.createObsoleteLimitedSyncCasePredicate(cb, taskQueryContext.getJoins().getCaze(), since, getCurrentUser()));
			predicates
				.add(caseService.createObsoleteLimitedSyncCasePredicate(cb, taskQueryContext.getJoins().getContactCase(), since, getCurrentUser()));
			return predicates;
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	protected Predicate getUserFilterForObsoleteUuids(CriteriaBuilder cb, CriteriaQuery<String> cq, Root<Task> from) {

		return createUserFilter(new TaskQueryContext(cb, cq, from), new TaskCriteria().excludeLimitedSyncRestrictions(true));
	}

	public Predicate createAssigneeFilter(CriteriaBuilder cb, Join<?, User> assigneeUserJoin) {
		return CriteriaBuilderHelper
			.or(cb, cb.isNull(assigneeUserJoin.get(User.UUID)), userService.createCurrentUserJurisdictionFilter(cb, assigneeUserJoin));
	}

	private Predicate createAssigneeOrObserverFilter(CriteriaBuilder cb, Join<?, User> assigneeUserJoin, Join<?, User> observersJoin, User user) {

		return cb.or(cb.equal(assigneeUserJoin.get(User.UUID), user.getUuid()), cb.equal(observersJoin.get(User.UUID), user.getUuid()));
	}

	public long getCount(TaskCriteria taskCriteria) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		final Root<Task> from = cq.from(getElementClass());
		final TaskQueryContext taskQueryContext = new TaskQueryContext(cb, cq, from);

		Predicate filter = buildCriteriaFilter(taskCriteria, taskQueryContext);
		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(from));

		return em.createQuery(cq).getSingleResult();
	}

	public List<Task> findBy(TaskCriteria taskCriteria, boolean ignoreUserFilter) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Task> cq = cb.createQuery(getElementClass());
		final Root<Task> from = cq.from(getElementClass());
		final TaskQueryContext taskQueryContext = new TaskQueryContext(cb, cq, from);

		Predicate filter = buildCriteriaFilter(taskCriteria, taskQueryContext);
		if (!ignoreUserFilter) {
			filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(taskQueryContext));
		}

		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.asc(from.get(Task.CREATION_DATE)));

		return em.createQuery(cq).getResultList();
	}

	public List<String> getArchivedUuidsSince(Date since) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<String> cq = cb.createQuery(String.class);
		final Root<Task> taskRoot = cq.from(Task.class);
		final TaskQueryContext taskQueryContext = new TaskQueryContext(cb, cq, taskRoot);

		Predicate filter = createUserFilter(taskQueryContext);
		if (since != null) {
			Predicate dateFilter = cb.greaterThanOrEqualTo(taskRoot.get(Task.CHANGE_DATE), since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		Predicate archivedFilter = cb.equal(taskRoot.get(Task.ARCHIVED), true);
		if (filter != null) {
			filter = cb.and(filter, archivedFilter);
		} else {
			filter = archivedFilter;
		}

		cq.where(filter);
		cq.select(taskRoot.get(Task.UUID));

		return em.createQuery(cq).getResultList();
	}

	public Predicate buildCriteriaFilter(TaskCriteria taskCriteria, TaskQueryContext taskQueryContext) {

		final CriteriaBuilder cb = taskQueryContext.getCriteriaBuilder();
		final TaskJoins joins = taskQueryContext.getJoins();
		final From<?, Task> from = taskQueryContext.getRoot();

		Predicate filter = null;

		if (taskCriteria.getTaskStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Task.TASK_STATUS), taskCriteria.getTaskStatus()));
		}
		if (taskCriteria.getTaskType() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Task.TASK_TYPE), taskCriteria.getTaskType()));
		}
		if (taskCriteria.getAssigneeUser() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getAssignee().get(User.UUID), taskCriteria.getAssigneeUser().getUuid()));
		}
		if (taskCriteria.getExcludeAssigneeUser() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.notEqual(joins.getAssignee().get(User.UUID), taskCriteria.getExcludeAssigneeUser().getUuid()));
		}
		if (taskCriteria.getCaze() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getCaze().get(Case.UUID), taskCriteria.getCaze().getUuid()));
		}
		if (taskCriteria.getContact() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getContact().get(Contact.UUID), taskCriteria.getContact().getUuid()));
		}
		if (taskCriteria.getContactPerson() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getContactPerson().get(User.UUID), taskCriteria.getContactPerson().getUuid()));
		}
		if (taskCriteria.getEvent() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getEvent().get(Event.UUID), taskCriteria.getEvent().getUuid()));
		}
		if (taskCriteria.getTravelEntry() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(joins.getTravelEntry().get(TravelEntry.UUID), taskCriteria.getTravelEntry().getUuid()));
		}
		if (taskCriteria.getEnvironment() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(joins.getEnvironment().get(Environment.UUID), taskCriteria.getEnvironment().getUuid()));
		}
		if (taskCriteria.getDueDateFrom() != null && taskCriteria.getDueDateTo() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.greaterThanOrEqualTo(from.get(Task.DUE_DATE), taskCriteria.getDueDateFrom()));
			filter = CriteriaBuilderHelper.and(cb, filter, cb.lessThan(from.get(Task.DUE_DATE), taskCriteria.getDueDateTo()));
		}
		if (taskCriteria.getStartDateFrom() != null && taskCriteria.getStartDateTo() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.greaterThanOrEqualTo(from.get(Task.SUGGESTED_START), taskCriteria.getStartDateFrom()));
			filter = CriteriaBuilderHelper.and(cb, filter, cb.lessThan(from.get(Task.SUGGESTED_START), taskCriteria.getStartDateTo()));
		}
		if (taskCriteria.getStatusChangeDateFrom() != null && taskCriteria.getStatusChangeDateTo() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.greaterThanOrEqualTo(from.get(Task.STATUS_CHANGE_DATE), taskCriteria.getStatusChangeDateFrom()));
			filter = CriteriaBuilderHelper.and(cb, filter, cb.lessThan(from.get(Task.STATUS_CHANGE_DATE), taskCriteria.getStatusChangeDateTo()));
		}
		if (taskCriteria.getRelevanceStatus() != null) {
			if (taskCriteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper.and(cb, filter, buildActiveTasksFilter(taskQueryContext));
			} else if (taskCriteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(
					cb,
					filter,
					cb.or(
						cb.isTrue(from.get(Task.ARCHIVED)),
						cb.and(cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.CASE), cb.equal(joins.getCaze().get(Case.ARCHIVED), true)),
						cb.and(cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.CONTACT), cb.equal(joins.getContact().get(Case.ARCHIVED), true)),
						cb.and(cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.EVENT), cb.equal(joins.getEvent().get(Event.ARCHIVED), true)),
						cb.and(
							cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.TRAVEL_ENTRY),
							cb.equal(joins.getTravelEntry().get(TravelEntry.ARCHIVED), true)),
						cb.and(
							cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.ENVIRONMENT),
							cb.equal(joins.getEnvironment().get(Environment.ARCHIVED), true))));
			}
		}
		if (taskCriteria.getTaskContext() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Task.TASK_CONTEXT), taskCriteria.getTaskContext()));
		}
		if (taskCriteria.getRegion() != null) {
			String regionUuid = taskCriteria.getRegion().getUuid();
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				CriteriaBuilderHelper.or(
					cb,
					cb.equal(joins.getCaseRegion().get(Region.UUID), regionUuid),
					cb.equal(joins.getCaseResponsibleRegion().get(Region.UUID), regionUuid),
					cb.equal(joins.getContactRegion().get(Region.UUID), regionUuid),
					cb.equal(joins.getContactJoins().getCaseRegion().get(Region.UUID), regionUuid),
					cb.equal(joins.getContactJoins().getCaseResponsibleRegion().get(Region.UUID), regionUuid),
					cb.equal(joins.getEventRegion().get(Region.UUID), regionUuid),
					cb.equal(joins.getEnvironmentRegion().get(Region.UUID), regionUuid)));
		}
		if (taskCriteria.getDistrict() != null) {
			String districtUuid = taskCriteria.getDistrict().getUuid();
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				CriteriaBuilderHelper.or(
					cb,
					cb.equal(joins.getCaseDistrict().get(District.UUID), districtUuid),
					cb.equal(joins.getCaseResponsibleDistrict().get(District.UUID), districtUuid),
					cb.equal(joins.getContactDistrict().get(District.UUID), districtUuid),
					cb.equal(joins.getContactJoins().getCaseDistrict().get(District.UUID), districtUuid),
					cb.equal(joins.getContactJoins().getCaseResponsibleDistrict().get(District.UUID), districtUuid),
					cb.equal(joins.getEventDistrict().get(District.UUID), districtUuid),
					cb.equal(joins.getEnvironmentDistrict().get(District.UUID), districtUuid)));
		}
		if (taskCriteria.getFreeText() != null) {
			String[] textFilters = taskCriteria.getFreeText().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.ilike(cb, joins.getCaze().get(Case.UUID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getCaze().get(Case.INTERNAL_TOKEN), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getCasePerson().get(Person.LAST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getCasePerson().get(Person.FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getCasePerson().get(Person.INTERNAL_TOKEN), textFilter),
					CriteriaBuilderHelper.ilike(cb, joins.getContact().get(Contact.UUID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getContact().get(Contact.INTERNAL_TOKEN), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getContactPerson().get(Person.LAST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getContactPerson().get(Person.FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getContactPerson().get(Person.INTERNAL_TOKEN), textFilter),
					CriteriaBuilderHelper.ilike(cb, joins.getEvent().get(Event.UUID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getEvent().get(Event.INTERNAL_TOKEN), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getEvent().get(Event.EVENT_TITLE), textFilter),
					CriteriaBuilderHelper.ilike(cb, joins.getEnvironment().get(Environment.UUID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getEnvironment().get(Environment.EXTERNAL_ID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getEnvironment().get(Environment.ENVIRONMENT_NAME), textFilter),
					CriteriaBuilderHelper.ilike(cb, joins.getTravelEntry().get(TravelEntry.UUID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getTravelEntryPerson().get(Person.LAST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getTravelEntryPerson().get(Person.FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getTravelEntryPerson().get(Person.INTERNAL_TOKEN), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}
		if (taskCriteria.getAssigneeUserLike() != null) {
			String[] textFilters = taskCriteria.getAssigneeUserLike().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getAssignee().get(User.LAST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getAssignee().get(User.FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getAssignee().get(User.USER_NAME), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}
		if (taskCriteria.getCreatorUserLike() != null) {
			String[] textFilters = taskCriteria.getCreatorUserLike().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.ilike(cb, joins.getCaze().get(Case.UUID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getCreator().get(User.LAST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getCreator().get(User.FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getCreator().get(User.USER_NAME), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}

		if (taskCriteria.getAssignedByUserLike() != null) {
			String[] textFilters = taskCriteria.getAssignedByUserLike().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.ilike(cb, joins.getCaze().get(Case.UUID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getAssignedBy().get(User.LAST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getAssignedBy().get(User.FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getAssignedBy().get(User.USER_NAME), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}

		if (getCurrentUser() != null) {
			Predicate taskContextFilter = buildTaskContextFilter(taskQueryContext);
			taskContextFilter = CriteriaBuilderHelper
				.or(cb, taskContextFilter, createAssigneeOrObserverFilter(cb, joins.getAssignee(), joins.getTaskObservers(), getCurrentUser()));
			filter = CriteriaBuilderHelper.and(cb, filter, taskContextFilter);
		}

		return filter;
	}

	private Predicate buildTaskContextFilter(TaskQueryContext taskQueryContext) {
		From<?, Task> from = taskQueryContext.getRoot();
		CriteriaBuilder cb = taskQueryContext.getCriteriaBuilder();

		List<TaskContext> allowTaskContext = new ArrayList<>();
		allowTaskContext.add(TaskContext.GENERAL);
		if (hasRight(UserRight.CASE_VIEW)) {
			allowTaskContext.add(TaskContext.CASE);
		}
		if (hasRight(UserRight.CONTACT_VIEW)) {
			allowTaskContext.add(TaskContext.CONTACT);
		}
		if (hasRight(UserRight.EVENT_VIEW)) {
			allowTaskContext.add(TaskContext.EVENT);
		}
		if (hasRight(UserRight.TRAVEL_ENTRY_VIEW)) {
			allowTaskContext.add(TaskContext.TRAVEL_ENTRY);
		}
		if (hasRight(UserRight.ENVIRONMENT_VIEW)) {
			allowTaskContext.add(TaskContext.ENVIRONMENT);
		}
		return cb.in(from.get(Task.TASK_CONTEXT)).value(allowTaskContext);
	}

	private Predicate buildActiveTasksFilter(TaskQueryContext taskQueryContext) {
		From<?, Task> from = taskQueryContext.getRoot();
		CriteriaBuilder cb = taskQueryContext.getCriteriaBuilder();
		TaskJoins joins = taskQueryContext.getJoins();

		Join<Task, Case> caze = joins.getCaze();
		Join<Task, Contact> contact = joins.getContact();
		Join<Task, Event> event = joins.getEvent();
		Join<Task, TravelEntry> travelEntry = joins.getTravelEntry();
		Join<Task, Environment> environment = joins.getEnvironment();

		return cb.and(
			cb.isFalse(from.get(Task.ARCHIVED)),
			cb.or(
				cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.GENERAL),
				cb.and(
					cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.CASE),
					cb.and(
						cb.or(cb.isNull(caze.get(Case.ARCHIVED)), cb.isFalse(caze.get(Case.ARCHIVED))),
						cb.or(cb.isNull(caze.get(Case.DELETED)), cb.isFalse(caze.get(Case.DELETED))))),
				cb.and(
					cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.CONTACT),
					cb.and(
						cb.or(cb.isNull(contact.get(Case.ARCHIVED)), cb.isFalse(contact.get(Case.ARCHIVED))),
						cb.or(cb.isNull(contact.get(Case.DELETED)), cb.isFalse(contact.get(Case.DELETED))))),
				cb.and(
					cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.EVENT),
					cb.and(
						cb.or(cb.isNull(event.get(Event.ARCHIVED)), cb.isFalse(event.get(Event.ARCHIVED))),
						cb.or(cb.isNull(event.get(Event.DELETED)), cb.isFalse(event.get(Event.DELETED))))),
				cb.and(
					cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.TRAVEL_ENTRY),
					cb.and(
						cb.or(cb.isNull(travelEntry.get(TravelEntry.ARCHIVED)), cb.isFalse(travelEntry.get(TravelEntry.ARCHIVED))),
						cb.or(cb.isNull(travelEntry.get(TravelEntry.DELETED)), cb.isFalse(travelEntry.get(TravelEntry.DELETED))))),
				cb.and(
					cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.ENVIRONMENT),
					cb.and(
						cb.or(cb.isNull(environment.get(Environment.ARCHIVED)), cb.isFalse(environment.get(Environment.ARCHIVED))),
						cb.or(cb.isNull(environment.get(Environment.DELETED)), cb.isFalse(environment.get(Environment.DELETED)))))));
	}

	public Task buildTask(User creatorUser) {

		Task task = new Task();
		task.setCreatorUser(creatorUser);
		task.setPriority(TaskPriority.NORMAL);
		task.setTaskStatus(TaskStatus.PENDING);
		return task;
	}

	public User getTaskAssigneeByUuid(String uuid) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<User> cq = cb.createQuery(User.class);
			Root<Task> from = cq.from(getElementClass());
			cq.where(cb.equal(from.get(Task.UUID), uuid));
			cq.select(from.get(Task.ASSIGNEE_USER));
			return em.createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public User getTaskAssignee(Contact contact) throws TaskCreationException {

		User assignee = null;
		if (contact.getContactOfficer() != null) {
			// 1) The contact officer that is responsible for the contact
			assignee = contact.getContactOfficer();
		} else {
			// 2) A random user with user right CONTACT_RESPONSIBLE from the contact's, contact person's or contact case's district
			Function<District, User> lookupByDistrict = district -> userService.getRandomDistrictUser(district, UserRight.CONTACT_RESPONSIBLE);
			if (contact.getDistrict() != null) {
				assignee = lookupByDistrict.apply(contact.getDistrict());
			}
			if (assignee == null && contact.getPerson().getAddress().getDistrict() != null) {
				assignee = lookupByDistrict.apply(contact.getPerson().getAddress().getDistrict());
			}
			Case contactCase = contact.getCaze();
			if (assignee == null && contactCase != null) {
				assignee = lookupByDistrict.apply(contactCase.getResponsibleDistrict());

				if (assignee == null && contactCase.getDistrict() != null) {
					assignee = lookupByDistrict.apply(contactCase.getDistrict());
				}
			}
		}

		if (assignee == null) {
			// 3) Assign a random user with user right CONTACT_RESPONSIBLE from the contact's, contact person's or contact case's region
			Function<Region, User> lookupByRegion = region -> userService.getRandomRegionUser(region, UserRight.CONTACT_RESPONSIBLE);
			if (contact.getRegion() != null) {
				assignee = lookupByRegion.apply(contact.getRegion());
			}
			if (assignee == null && contact.getPerson().getAddress().getRegion() != null) {
				assignee = lookupByRegion.apply(contact.getPerson().getAddress().getRegion());
			}
			Case contactCase = contact.getCaze();
			if (assignee == null && contactCase != null) {
				assignee = lookupByRegion.apply(contactCase.getResponsibleRegion());

				if (assignee == null && contactCase.getRegion() != null) {
					assignee = lookupByRegion.apply(contactCase.getRegion());
				}
			}

			if (assignee == null) {
				throw new TaskCreationException("Contact has not contact officer and no region - can't create follow-up task: " + contact.getUuid());
			}
		}

		return assignee;
	}

	public List<Task> findByAssigneeContactTypeAndStatuses(
		UserReferenceDto assignee,
		ContactReferenceDto contact,
		TaskType type,
		TaskStatus... statuses) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Task> cq = cb.createQuery(getElementClass());
		final Root<Task> from = cq.from(getElementClass());

		final TaskJoins joins = new TaskJoins(from);

		Predicate filter = null;

		if (assignee != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getAssignee().get(User.UUID), assignee.getUuid()));
		}
		if (type != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Task.TASK_TYPE), type));
		}
		if (!ArrayUtils.isEmpty(statuses)) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.in(from.get(Task.TASK_STATUS)).value(Arrays.asList(statuses)));
		}
		if (contact != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getContact().get(Contact.UUID), contact.getUuid()));
		}

		if (filter != null) {
			cq.where(filter);
		}

		return em.createQuery(cq).getResultList();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateArchived(List<String> taskUuids, boolean archived) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Task> cu = cb.createCriteriaUpdate(Task.class);
		Root<Task> root = cu.from(Task.class);

		cu.set(Task.CHANGE_DATE, Timestamp.from(Instant.now()));
		cu.set(root.get(Task.ARCHIVED), archived);

		cu.where(root.get(Task.UUID).in(taskUuids));

		em.createQuery(cu).executeUpdate();
	}

	public boolean isArchived(String taskUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> from = cq.from(getElementClass());

		cq.where(cb.and(cb.equal(from.get(Task.ARCHIVED), true), cb.equal(from.get(AbstractDomainObject.UUID), taskUuid)));
		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	@Override
	public TaskJurisdictionFlagsDto getJurisdictionFlags(Task entity) {

		return getJurisdictionsFlags(Collections.singletonList(entity)).get(entity.getId());
	}

	@Override
	public Map<Long, TaskJurisdictionFlagsDto> getJurisdictionsFlags(List<Task> selectedEntities) {

		return getSelectionAttributes(
			selectedEntities,
			(cb, cq, from) -> getJurisdictionSelections(new TaskQueryContext(cb, cq, from)),
			e -> new TaskJurisdictionFlagsDto(e));
	}

	@Override
	public List<Selection<?>> getJurisdictionSelections(TaskQueryContext qc) {

		final CriteriaBuilder cb = qc.getCriteriaBuilder();
		final TaskJoins joins = qc.getJoins();

		return Arrays.asList(
			JurisdictionHelper.booleanSelector(cb, inJurisdictionOrOwned(qc)),
			JurisdictionHelper.booleanSelector(
				cb,
				cb.and(
					cb.isNotNull(joins.getCaze()),
					caseService.inJurisdictionOrOwned(new CaseQueryContext(cb, qc.getQuery(), joins.getCaseJoins())))),
			JurisdictionHelper.booleanSelector(
				cb,
				cb.and(
					cb.isNotNull(joins.getContact()),
					contactService.inJurisdictionOrOwned(new ContactQueryContext(cb, qc.getQuery(), joins.getContactJoins())))),
			JurisdictionHelper.booleanSelector(
				cb,
				cb.and(
					cb.isNotNull(joins.getContact()),
					cb.isNotNull(joins.getContactJoins().getCaze()),
					caseService.inJurisdictionOrOwned(new CaseQueryContext(cb, qc.getQuery(), joins.getContactJoins().getCaseJoins())))),
			JurisdictionHelper.booleanSelector(
				cb,
				cb.and(
					cb.isNotNull(joins.getEvent()),
					eventService.inJurisdictionOrOwned(new EventQueryContext(cb, qc.getQuery(), joins.getEventJoins())))),
			JurisdictionHelper.booleanSelector(
				cb,
				cb.and(
					cb.isNotNull(joins.getTravelEntry()),
					travelEntryService.inJurisdictionOrOwned(new TravelEntryQueryContext(cb, qc.getQuery(), joins.getTravelEntryJoins())))),
			JurisdictionHelper.booleanSelector(
				cb,
				cb.and(
					cb.isNotNull(joins.getEnvironment()),
					environmentService.inJurisdictionOrOwned(new EnvironmentQueryContext(cb, qc.getQuery(), joins.getEnvironmentJoins())))));
	}

	@Override
	public Predicate inJurisdictionOrOwned(TaskQueryContext qc) {
		final User currentUser = userService.getCurrentUser();
		return TaskJurisdictionPredicateValidator.of(qc, currentUser).inJurisdictionOrOwned();
	}

	public EditPermissionType getEditPermissionType(Task task) {
		if (task.isArchived()) {
			return featureConfigurationFacade.isFeatureEnabled(FeatureType.EDIT_ARCHIVED_ENTITIES)
				? EditPermissionType.ALLOWED
				: EditPermissionType.ARCHIVING_STATUS_ONLY;
		}

		switch (task.getTaskContext()) {
		case CASE:
			EditPermissionType casePermissionType = caseService.getEditPermissionType(task.getCaze());
			return casePermissionType == EditPermissionType.WITHOUT_OWNERSHIP ? EditPermissionType.ALLOWED : casePermissionType;
		case CONTACT:
			EditPermissionType contactPermissionType = contactService.getEditPermissionType(task.getContact());
			return contactPermissionType == EditPermissionType.WITHOUT_OWNERSHIP ? EditPermissionType.ALLOWED : contactPermissionType;
		case EVENT:
			EditPermissionType eventPermissionType = eventService.getEditPermissionType(task.getEvent());
			return eventPermissionType == EditPermissionType.WITHOUT_OWNERSHIP ? EditPermissionType.ALLOWED : eventPermissionType;
		case TRAVEL_ENTRY:
			return travelEntryService.getEditPermissionType(task.getTravelEntry());
		case ENVIRONMENT:
			return environmentService.getEditPermissionType(task.getEnvironment());
		}

		return EditPermissionType.ALLOWED;
	}

	@Override
	protected boolean hasLimitedChangeDateFilterImplementation() {
		return true;
	}
}
