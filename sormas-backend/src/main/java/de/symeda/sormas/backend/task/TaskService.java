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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskJurisdictionFlagsDto;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.common.TaskCreationException;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactQueryContext;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventQueryContext;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.travelentry.TravelEntryQueryContext;
import de.symeda.sormas.backend.travelentry.services.TravelEntryService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.JurisdictionHelper;

@Stateless
@LocalBean
public class TaskService extends AdoServiceWithUserFilter<Task> {

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

	public TaskService() {
		super(Task.class);
	}

	public List<Task> getAllActiveTasksAfter(Date date, User user, Integer batchSize, String lastSynchronizedUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(getElementClass());
		Root<Task> from = cq.from(getElementClass());
		final TaskQueryContext taskQueryContext = new TaskQueryContext(cb, cq, from);

		Predicate filter = buildActiveTasksFilter(taskQueryContext);

		if (user != null) {
			Predicate userFilter = createUserFilter(taskQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, date, lastSynchronizedUuid);
			filter = CriteriaBuilderHelper.and(cb, filter, dateFilter);
		}

		cq.where(filter);
		cq.distinct(true);

		return getBatchedQueryResults(cb, cq, from, batchSize);
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

		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return null;
		}

		CriteriaQuery<?> cq = taskQueryContext.getQuery();
		CriteriaBuilder cb = taskQueryContext.getCriteriaBuilder();
		From<?, Task> taskPath = taskQueryContext.getRoot();

		Predicate assigneeFilter = createAssigneeFilter(cb, taskQueryContext.getJoins().getAssignee());

		Predicate contactRightsPredicate = this.createContactFilter(
			cb,
			taskQueryContext.getRoot(),
			(taskQueryContext.getJoins()).getAssignee(),
			(taskQueryContext.getJoins()).getTaskObservers(),
			currentUser);
		if (contactRightsPredicate != null) {
			assigneeFilter = cb.and(assigneeFilter, contactRightsPredicate);
		}

		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		if ((jurisdictionLevel == JurisdictionLevel.NATION && !UserRole.isPortHealthUser(currentUser.getUserRoles()))
			|| currentUser.hasUserRole(UserRole.REST_USER)) {
			return assigneeFilter;
		}

		Predicate filter = cb.equal(taskPath.get(Task.CREATOR_USER), currentUser);
		filter = cb.or(filter, cb.equal(taskPath.get(Task.ASSIGNEE_USER), currentUser));

		Predicate caseFilter = caseService.createUserFilter(new CaseQueryContext(cb, cq, taskQueryContext.getJoins().getCaseJoins()));
		if (caseFilter != null) {
			filter = cb.or(filter, caseFilter);
		}
		Predicate contactFilter = contactService.createUserFilter(new ContactQueryContext(cb, cq, taskQueryContext.getJoins().getContactJoins()));
		if (contactFilter != null) {
			filter = cb.or(filter, contactFilter);
		}
		Predicate eventFilter = eventService.createUserFilter(new EventQueryContext(cb, cq, taskQueryContext.getJoins().getEventJoins()));
		if (eventFilter != null) {
			filter = cb.or(filter, eventFilter);
		}
		Predicate travelEntryFilter =
			travelEntryService.createUserFilter(new TravelEntryQueryContext(cb, cq, taskQueryContext.getJoins().getTravelEntryJoins()));
		if (travelEntryFilter != null) {
			filter = cb.or(filter, travelEntryFilter);
		}

		return CriteriaBuilderHelper.and(cb, filter, assigneeFilter);
	}

	public Predicate createAssigneeFilter(CriteriaBuilder cb, Join<?, User> assigneeUserJoin) {
		return CriteriaBuilderHelper
			.or(cb, cb.isNull(assigneeUserJoin.get(User.UUID)), userService.createCurrentUserJurisdictionFilter(cb, assigneeUserJoin));
	}

	/*
	 * A user that not have CONTACT_VIEW or CONTACT_EDIT rights is allowed to see the tasks assign to it or where it is
	 * set as an observer. This restriction should be applied only for tasks of type CONTACT.
	 */
	private Predicate createContactFilter(
		CriteriaBuilder cb,
		From<?, Task> task,
		Join<?, User> assigneeUserJoin,
		Join<?, User> observersJoin,
		User user) {
		Predicate predicate = null;
		if (!userService.hasRight(UserRight.CONTACT_VIEW) && !userService.hasRight(UserRight.CONTACT_EDIT)) {
			predicate = cb.or(
				cb.notEqual(task.get(Task.TASK_CONTEXT), TaskContext.CONTACT),
				cb.equal(assigneeUserJoin.get(User.UUID), user.getUuid()),
				cb.equal(observersJoin.get(User.UUID), user.getUuid()));
		}

		return predicate;
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
						cb.and(cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.CONTACT), cb.equal(joins.getContactCase().get(Case.ARCHIVED), true)),
						cb.and(cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.EVENT), cb.equal(joins.getEvent().get(Event.ARCHIVED), true)),
						cb.and(
							cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.TRAVEL_ENTRY),
							cb.equal(joins.getTravelEntry().get(TravelEntry.ARCHIVED), true))));
			}
		}
		if (taskCriteria.getTaskContext() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Task.TASK_CONTEXT), taskCriteria.getTaskContext()));
		}
		if (taskCriteria.getRegion() != null) {
			Expression<Object> region = cb.selectCase()
				.when(cb.isNotNull(joins.getCaseRegion()), joins.getCaseRegion().get(Region.UUID))
				.otherwise(
					cb.selectCase()
						.when(cb.isNotNull(joins.getContactRegion()), joins.getContactRegion().get(Region.UUID))
						.otherwise(joins.getEventRegion().get(Region.UUID)));
			String regionUuid = taskCriteria.getRegion().getUuid();
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.or(cb.equal(region, regionUuid), cb.equal(joins.getCaseResponsibleRegion().get(Region.UUID), regionUuid)));
		}
		if (taskCriteria.getDistrict() != null) {
			Expression<Object> district = cb.selectCase()
				.when(cb.isNotNull(joins.getCaseDistrict()), joins.getCaseDistrict().get(District.UUID))
				.otherwise(
					cb.selectCase()
						.when(cb.isNotNull(joins.getContactDistrict()), joins.getContactDistrict().get(District.UUID))
						.otherwise(joins.getEventDistrict().get(District.UUID)));
			String districtUuid = taskCriteria.getDistrict().getUuid();
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.or(cb.equal(district, districtUuid), cb.equal(joins.getCaseResponsibleDistrict().get(District.UUID), districtUuid)));
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

		return filter;
	}

	private Predicate buildActiveTasksFilter(TaskQueryContext taskQueryContext) {

		From<?, Task> from = taskQueryContext.getRoot();
		CriteriaBuilder cb = taskQueryContext.getCriteriaBuilder();
		TaskJoins joins = taskQueryContext.getJoins();

		Join<Task, Case> caze = joins.getCaze();
		Join<Contact, Case> contactCaze = joins.getContactCase();
		Join<Task, Event> event = joins.getEvent();
		Join<Task, TravelEntry> travelEntry = joins.getTravelEntry();

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
						cb.or(cb.isNull(contactCaze.get(Case.ARCHIVED)), cb.isFalse(contactCaze.get(Case.ARCHIVED))),
						cb.or(cb.isNull(contactCaze.get(Case.DELETED)), cb.isFalse(contactCaze.get(Case.DELETED))))),
				cb.and(
					cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.EVENT),
					cb.and(
						cb.or(cb.isNull(event.get(Event.ARCHIVED)), cb.isFalse(event.get(Event.ARCHIVED))),
						cb.or(cb.isNull(event.get(Event.DELETED)), cb.isFalse(event.get(Event.DELETED))))),

				cb.and(
					cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.TRAVEL_ENTRY),
					cb.and(
						cb.or(cb.isNull(travelEntry.get(TravelEntry.ARCHIVED)), cb.isFalse(travelEntry.get(TravelEntry.ARCHIVED))),
						cb.or(cb.isNull(travelEntry.get(TravelEntry.DELETED)), cb.isFalse(travelEntry.get(TravelEntry.DELETED)))))));
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

	public TaskJurisdictionFlagsDto inJurisdictionOrOwned(Task task) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskJurisdictionFlagsDto> cq = cb.createQuery(TaskJurisdictionFlagsDto.class);
		Root<Task> root = cq.from(Task.class);
		cq.multiselect(getJurisdictionSelections(new TaskQueryContext(cb, cq, root)));
		cq.where(cb.equal(root.get(Task.UUID), task.getUuid()));
		return em.createQuery(cq).getSingleResult();
	}

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
					travelEntryService.inJurisdictionOrOwned(new TravelEntryQueryContext(cb, qc.getQuery(), joins.getTravelEntryJoins())))));
	}

	public Predicate inJurisdictionOrOwned(TaskQueryContext qc) {
		final User currentUser = userService.getCurrentUser();
		return TaskJurisdictionPredicateValidator.of(qc, currentUser).inJurisdictionOrOwned();
	}
}
