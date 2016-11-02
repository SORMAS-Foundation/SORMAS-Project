package de.symeda.sormas.backend.task;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
			filter = cb.equal(from.get(Task.ASSIGNEE_USER).get(User.REGION), region);
			filter = cb.or(filter, cb.equal(from.get(Task.CREATOR_USER).get(User.REGION), region));
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

	public long getPendingTaskCount(String userUuid) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> from = cq.from(getElementClass());
		
		Predicate filter = cb.equal(from.get(Task.ASSIGNEE_USER).get(User.UUID), userUuid);
		filter = cb.and(cb.equal(from.get(Task.TASK_STATUS), TaskStatus.PENDING));
		cq.where(filter);
		
		cq.select(cb.countDistinct(from));

		long count = em.createQuery(cq).getSingleResult();
		return count;
	}
}
