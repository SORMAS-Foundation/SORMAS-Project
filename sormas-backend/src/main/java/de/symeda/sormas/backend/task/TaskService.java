package de.symeda.sormas.backend.task;

import java.util.Collections;
import java.util.Date;
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

import de.symeda.sormas.api.task.DashboardTask;
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
		if (user.getUserRoles().contains(UserRole.NATIONAL_USER)) {
			return null;
		}
		
		// whoever created the task or is assigned to it is allowed to access it
		Predicate filter = cb.equal(taskPath.get(Task.CREATOR_USER), user);
		filter = cb.or(filter, cb.equal(taskPath.get(Task.ASSIGNEE_USER), user));
		
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

	public List<DashboardTask> getAllPending(Date from, Date to, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardTask> cq = cb.createQuery(DashboardTask.class);
		Root<Task> task = cq.from(getElementClass());
		
		TaskCriteria taskCriteria = new TaskCriteria().assigneeUserEquals(user).taskStatusEquals(TaskStatus.PENDING);
		Predicate filter = buildCriteriaFilter(taskCriteria, cb, task);
		
		List<DashboardTask> result;
		if (filter != null) {
			cq.where(filter);
			cq.multiselect(
					task.get(Task.PRIORITY),
					task.get(Task.TASK_STATUS)
			);
			
			result = em.createQuery(cq).getResultList();
		} else {
			result = Collections.emptyList();
		}
		
		return result;
	}

	private Predicate buildCriteriaFilter(TaskCriteria taskCriteria, CriteriaBuilder cb, Root<Task> from) {
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
			filter = and(cb, filter, cb.equal(from.get(Task.ASSIGNEE_USER), taskCriteria.getAssigneeUser()));
		}
		if (taskCriteria.getCaze() != null) {
			filter = and(cb, filter, cb.equal(from.get(Task.CAZE), taskCriteria.getCaze()));
		}
		if (taskCriteria.getContact() != null) {
			filter = and(cb, filter, cb.equal(from.get(Task.CONTACT), taskCriteria.getContact()));
		}
		if (taskCriteria.getContactPerson() != null) {
			filter = and(cb, filter, cb.equal(from.join(Task.CONTACT, JoinType.LEFT).get(Contact.PERSON), taskCriteria.getContactPerson()));
		}
		if (taskCriteria.getEvent() != null) {
			filter = and(cb, filter, cb.equal(from.get(Task.EVENT), taskCriteria.getEvent()));
		}
		if (taskCriteria.getDueDateFrom() != null && taskCriteria.getDueDateTo() != null) {
			filter = cb.and(filter, cb.greaterThanOrEqualTo(from.get(Task.DUE_DATE), taskCriteria.getDueDateFrom()));
			filter = cb.and(filter, cb.lessThan(from.get(Task.DUE_DATE), taskCriteria.getDueDateTo()));
		}
		return filter;
	}
	
	/**
	 * TODO move to CriteriaBuilderHelper
	 * @param existing nullable
	 */
	public static Predicate and(CriteriaBuilder cb, Predicate existing, Predicate additional) {
		if (existing == null) {
			return additional;
		}
		return cb.and(existing, additional);
	}
	
	/**
	 * TODO move to CriteriaBuilderHelper
	 * @param existing nullable
	 */
	public static Predicate or(CriteriaBuilder cb, Predicate existing, Predicate additional) {
		if (existing == null) {
			return additional;
		}
		return cb.or(existing, additional);
	}

	public Task buildTask(User creatorUser) {
		Task task = new Task();
		task.setCreatorUser(creatorUser);
		task.setPriority(TaskPriority.NORMAL);
    	task.setTaskStatus(TaskStatus.PENDING);
		return task;
	}
}
