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

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class TaskService extends AbstractAdoService<Task> {

	@EJB
	private CaseService caseService;
	@EJB
	private ContactService contactService;
	@EJB
	private EventService eventService;

	public TaskService() {
		super(Task.class);
	}

	public List<Task> getAllActiveTasksAfter(Date date, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(getElementClass());
		Root<Task> from = cq.from(getElementClass());

		Predicate filter = buildActiveTasksFilter(cb, from);

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = AbstractAdoService.and(cb, filter, userFilter);
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, date);
			filter = AbstractAdoService.and(cb, filter, dateFilter);
		}

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(Task.CHANGE_DATE)));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}

	public List<String> getAllActiveUuids(User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> from = cq.from(getElementClass());

		Predicate filter = buildActiveTasksFilter(cb, from);

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = AbstractAdoService.and(cb, filter, userFilter);
		}

		cq.where(filter);
		cq.select(from.get(Task.UUID));

		return em.createQuery(cq).getResultList();
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Task, Task> taskPath) {

		// National users can access all tasks in the system
		User currentUser = getCurrentUser();
		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		if (currentUser == null
				|| (jurisdictionLevel == JurisdictionLevel.NATION && !currentUser.hasAnyUserRole(UserRole.POE_NATIONAL_USER))
				|| currentUser.hasAnyUserRole(UserRole.REST_USER)) {
			return null;
		}

		// whoever created the task or is assigned to it is allowed to access it
		Predicate filter = cb.equal(taskPath.join(Task.CREATOR_USER, JoinType.LEFT), currentUser);
		filter = cb.or(filter, cb.equal(taskPath.join(Task.ASSIGNEE_USER, JoinType.LEFT), currentUser));

		Predicate caseFilter = caseService.createUserFilter(cb, cq, taskPath.join(Task.CAZE, JoinType.LEFT));
		if (caseFilter != null) {
			filter = cb.or(filter, caseFilter);
		}
		Predicate contactFilter = contactService.createUserFilter(cb, cq, taskPath.join(Task.CONTACT, JoinType.LEFT));
		if (contactFilter != null) {
			filter = cb.or(filter, contactFilter);
		}
		Predicate eventFilter = eventService.createUserFilter(cb, cq, taskPath.join(Task.EVENT, JoinType.LEFT));
		if (eventFilter != null) {
			filter = cb.or(filter, eventFilter);
		}

		return filter;
	}

	public long getCount(TaskCriteria taskCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> from = cq.from(getElementClass());

		Predicate filter = buildCriteriaFilter(taskCriteria, cb, from);
		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(from));

		long count = em.createQuery(cq).getSingleResult();
		return count;
	}

	public List<Task> findBy(TaskCriteria taskCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(getElementClass());
		Root<Task> from = cq.from(getElementClass());

		Predicate filter = buildCriteriaFilter(taskCriteria, cb, from);
		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.asc(from.get(Task.CREATION_DATE)));

		List<Task> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public Predicate buildCriteriaFilter(TaskCriteria taskCriteria, CriteriaBuilder cb, Root<Task> from) {

		Predicate filter = null;
		Join<Task, Case> caze = from.join(Task.CAZE, JoinType.LEFT);
		Join<Task, Contact> contact = from.join(Task.CONTACT, JoinType.LEFT);
		Join<Contact, Case> contactCaze = contact.join(Contact.CAZE, JoinType.LEFT);
		Join<Task, Event> event = from.join(Task.EVENT, JoinType.LEFT);

		if (taskCriteria.getTaskStatus() != null) {
			filter = and(cb, filter, cb.equal(from.get(Task.TASK_STATUS), taskCriteria.getTaskStatus()));
		}
		if (taskCriteria.getTaskType() != null) {
			filter = and(cb, filter, cb.equal(from.get(Task.TASK_TYPE), taskCriteria.getTaskType()));
		}
		if (taskCriteria.getAssigneeUser() != null) {
			filter = and(cb, filter, cb.equal(from.join(Task.ASSIGNEE_USER, JoinType.LEFT).get(User.UUID), taskCriteria.getAssigneeUser().getUuid()));
		}
		if (taskCriteria.getExcludeAssigneeUser() != null) {
			filter = and(
				cb,
				filter,
				cb.notEqual(from.join(Task.ASSIGNEE_USER, JoinType.LEFT).get(User.UUID), taskCriteria.getExcludeAssigneeUser().getUuid()));
		}
		if (taskCriteria.getCaze() != null) {
			filter = and(cb, filter, cb.equal(from.join(Task.CAZE, JoinType.LEFT).get(Case.UUID), taskCriteria.getCaze().getUuid()));
		}
		if (taskCriteria.getContact() != null) {
			filter = and(cb, filter, cb.equal(from.join(Task.CONTACT, JoinType.LEFT).get(User.UUID), taskCriteria.getContact().getUuid()));
		}
		if (taskCriteria.getContactPerson() != null) {
			filter = and(
				cb,
				filter,
				cb.equal(
					from.join(Task.CONTACT, JoinType.LEFT).join(Contact.PERSON, JoinType.LEFT).get(User.UUID),
					taskCriteria.getContactPerson().getUuid()));
		}
		if (taskCriteria.getEvent() != null) {
			filter = and(cb, filter, cb.equal(from.join(Task.EVENT, JoinType.LEFT).get(User.UUID), taskCriteria.getEvent().getUuid()));
		}
		if (taskCriteria.getDueDateFrom() != null && taskCriteria.getDueDateTo() != null) {
			filter = cb.and(filter, cb.greaterThanOrEqualTo(from.get(Task.DUE_DATE), taskCriteria.getDueDateFrom()));
			filter = cb.and(filter, cb.lessThan(from.get(Task.DUE_DATE), taskCriteria.getDueDateTo()));
		}
		if (taskCriteria.getStartDateFrom() != null && taskCriteria.getStartDateTo() != null) {
			filter = cb.and(filter, cb.greaterThanOrEqualTo(from.get(Task.SUGGESTED_START), taskCriteria.getStartDateFrom()));
			filter = cb.and(filter, cb.lessThan(from.get(Task.SUGGESTED_START), taskCriteria.getStartDateTo()));
		}
		if (taskCriteria.getStatusChangeDateFrom() != null && taskCriteria.getStatusChangeDateTo() != null) {
			filter = cb.and(filter, cb.greaterThanOrEqualTo(from.get(Task.STATUS_CHANGE_DATE), taskCriteria.getStatusChangeDateFrom()));
			filter = cb.and(filter, cb.lessThan(from.get(Task.STATUS_CHANGE_DATE), taskCriteria.getStatusChangeDateTo()));
		}
		if (taskCriteria.getRelevanceStatus() != null) {
			if (taskCriteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = and(cb, filter, buildActiveTasksFilter(cb, from));
			} else if (taskCriteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = and(
					cb,
					filter,
					cb.or(
						cb.and(cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.CASE), cb.equal(caze.get(Case.ARCHIVED), true)),
						cb.and(cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.CONTACT), cb.equal(contactCaze.get(Case.ARCHIVED), true)),
						cb.and(cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.EVENT), cb.equal(event.get(Event.ARCHIVED), true))));
			}
		}
		return filter;
	}

	private Predicate buildActiveTasksFilter(CriteriaBuilder cb, Root<Task> from) {

		Join<Task, Case> caze = from.join(Task.CAZE, JoinType.LEFT);
		Join<Task, Contact> contact = from.join(Task.CONTACT, JoinType.LEFT);
		Join<Contact, Case> contactCaze = contact.join(Contact.CAZE, JoinType.LEFT);
		Join<Task, Event> event = from.join(Task.EVENT, JoinType.LEFT);

		Predicate filter = cb.or(
			cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.GENERAL),
			cb.and(
				cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.CASE),
				cb.or(cb.equal(caze.get(Case.ARCHIVED), false), cb.isNull(caze.get(Case.ARCHIVED)))),
			cb.and(
				cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.CONTACT),
				cb.or(cb.equal(contactCaze.get(Case.ARCHIVED), false), cb.isNull(contactCaze.get(Case.ARCHIVED)))),
			cb.and(
				cb.equal(from.get(Task.TASK_CONTEXT), TaskContext.EVENT),
				cb.or(cb.equal(event.get(Event.ARCHIVED), false), cb.isNull(event.get(Event.ARCHIVED)))));

		return filter;
	}

	public Task buildTask(User creatorUser) {

		Task task = new Task();
		task.setCreatorUser(creatorUser);
		task.setPriority(TaskPriority.NORMAL);
		task.setTaskStatus(TaskStatus.PENDING);
		return task;
	}
}
