package de.symeda.sormas.backend.task;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class TaskService extends AbstractAdoService<Task> {
	
	public TaskService() {
		super(Task.class);
	}
	
	public List<Task> getAllAfter(Date date, User user) {

		// TODO get user from session?

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(getElementClass());
		Root<Task> from = cq.from(getElementClass());

		Predicate userFilter = cb.equal(from.get(Task.ASSIGNEE_USER), user);
		userFilter = cb.or(userFilter, cb.equal(from.get(Task.CREATOR_USER), user));
		
		cq.where(cb.and(userFilter, cb.greaterThan(from.get(AbstractDomainObject.CHANGE_DATE), date)));
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));

		List<Task> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
}
