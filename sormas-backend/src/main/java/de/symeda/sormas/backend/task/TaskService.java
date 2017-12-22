package de.symeda.sormas.backend.task;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class TaskService extends AbstractAdoService<Task> {
	
	@EJB
	CaseService caseService;
	@EJB
	ContactService contactService;
	@EJB
	EventService eventService;

	public TaskService() {
		super(Task.class);
	}
	
	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Task,Task> taskPath, User user) {
		// National users can access all tasks in the system
		if (user.getUserRoles().contains(UserRole.NATIONAL_USER)
			|| user.getUserRoles().contains(UserRole.NATIONAL_OBSERVER)) {
			return null;
		}
		
		// whoever created the task or is assigned to it is allowed to access it
		Predicate filter = cb.equal(taskPath.join(Task.CREATOR_USER, JoinType.LEFT), user);
		filter = cb.or(filter, cb.equal(taskPath.join(Task.ASSIGNEE_USER, JoinType.LEFT), user));
		
		Predicate caseFilter = caseService.createUserFilter(cb, cq, taskPath.join(Task.CAZE, JoinType.LEFT), user);
		if (caseFilter != null) {
			filter = cb.or(filter, caseFilter);
		} 
		Predicate contactFilter = contactService.createUserFilter(cb, cq, taskPath.join(Task.CONTACT, JoinType.LEFT), user);
		if (contactFilter != null) {
			filter = cb.or(filter, contactFilter);
		}
		Predicate eventFilter = eventService.createUserFilter(cb, cq, taskPath.join(Task.EVENT, JoinType.LEFT), user);
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
		if (taskCriteria.getTaskStatuses() != null && taskCriteria.getTaskStatuses().length > 0) {
			if (taskCriteria.getTaskStatuses().length == 1) {
				filter = and(cb, filter, cb.equal(from.get(Task.TASK_STATUS), taskCriteria.getTaskStatuses()[0]));
			} else {
				Predicate subFilter = null;
				for (TaskStatus taskStatus : taskCriteria.getTaskStatuses()) {
					subFilter = or(cb, subFilter, cb.equal(from.get(Task.TASK_STATUS), taskStatus));
				}
				filter = and(cb, filter, subFilter);
			}
		}
		if (taskCriteria.getTaskType() != null) {
			filter = and(cb, filter, cb.equal(from.get(Task.TASK_TYPE), taskCriteria.getTaskType()));
		}
		if (taskCriteria.getAssigneeUser() != null) {
			filter = and(cb, filter, cb.equal(from.join(Task.ASSIGNEE_USER, JoinType.LEFT).get(User.UUID), taskCriteria.getAssigneeUser().getUuid()));
		}
		if (taskCriteria.getCaze() != null) {
			filter = and(cb, filter, cb.equal(from.join(Task.CAZE, JoinType.LEFT).get(User.UUID), taskCriteria.getCaze().getUuid()));
		}
		if (taskCriteria.getContact() != null) {
			filter = and(cb, filter, cb.equal(from.join(Task.CONTACT, JoinType.LEFT).get(User.UUID), taskCriteria.getContact().getUuid()));
		}
		if (taskCriteria.getContactPerson() != null) {
			filter = and(cb, filter, cb.equal(
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
		if (taskCriteria.getStatusChangeDateFrom() != null && taskCriteria.getStatusChangeDateTo() != null) {
			filter = cb.and(filter, cb.greaterThanOrEqualTo(from.get(Task.STATUS_CHANGE_DATE), taskCriteria.getStatusChangeDateFrom()));
			filter = cb.and(filter, cb.lessThan(from.get(Task.STATUS_CHANGE_DATE), taskCriteria.getStatusChangeDateTo()));
		}
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
