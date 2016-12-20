package de.symeda.sormas.backend.task;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class TaskService extends AbstractAdoService<Task> {
	
	public TaskService() {
		super(Task.class);
	}
	
	/**
	 * @return ordered by priority, suggested start
	 */
	@Override
	public List<Task> getAll() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(getElementClass());
		Root<Task> from = cq.from(getElementClass());
		cq.orderBy(cb.asc(from.get(Task.PRIORITY)), cb.asc(from.get(Task.SUGGESTED_START)), cb.asc(from.get(AbstractDomainObject.ID)));

		return em.createQuery(cq).getResultList();
	}
	
	/**
	 * @return ordered by priority, suggested start
	 */
	public List<Task> getAllAfter(Date date, User user) {

		// TODO get user from session?

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(getElementClass());
		Root<Task> from = cq.from(getElementClass());

		Predicate filter;
		if (user.isSupervisor()) {
			// supervisor: all tasks in the relevant region
			// TODO only context tasks (surveillance, case management, contact tracing)
			Region region = user.getRegion();
			Join<Task, User> assigneeJoin = from.join(Task.ASSIGNEE_USER, JoinType.LEFT);
			filter = cb.equal(assigneeJoin.get(User.REGION), region);
			Join<Task, User> creatorJoin = from.join(Task.CREATOR_USER, JoinType.LEFT);
			filter = cb.or(filter, cb.equal(creatorJoin.get(User.REGION), region));
		} else {
			// officer: only assigned or created tasks
			filter = cb.equal(from.get(Task.ASSIGNEE_USER), user);
			filter = cb.or(filter, cb.equal(from.get(Task.CREATOR_USER), user));
		}
		
		if (date != null) {
			filter = cb.and(filter, cb.greaterThan(from.get(AbstractDomainObject.CHANGE_DATE), date));
		}
		
		cq.where(filter);
		cq.orderBy(cb.asc(from.get(Task.PRIORITY)), cb.asc(from.get(Task.SUGGESTED_START)), cb.asc(from.get(AbstractDomainObject.ID)));

		List<Task> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
	public long getCount(TaskCriteria taskCriteria) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> from = cq.from(getElementClass());
		
		Predicate filter = buildCriteraFilter(taskCriteria, cb, from);
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

		Predicate filter = buildCriteraFilter(taskCriteria, cb, from);
		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.asc(from.get(Task.CREATION_DATE)));

		List<Task> resultList = em.createQuery(cq).getResultList();
		return resultList;	
	}

	private Predicate buildCriteraFilter(TaskCriteria taskCriteria, CriteriaBuilder cb, Root<Task> from) {
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
}
