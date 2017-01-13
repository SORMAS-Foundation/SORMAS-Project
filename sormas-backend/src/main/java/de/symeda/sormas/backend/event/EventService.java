package de.symeda.sormas.backend.event;

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

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class EventService extends AbstractAdoService<Event> {

	public EventService() {
		super(Event.class);
	}
	
	public List<Event> getAllAfter(Date date, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Event> cq = cb.createQuery(getElementClass());
		Root<Event> from = cq.from(getElementClass());
		
		Predicate filter = cb.equal(from.get(Event.REPORTING_USER), user);
		if (user.getUserRoles().contains(UserRole.SURVEILLANCE_OFFICER)) {
			filter = cb.or(filter, cb.equal(from.get(Case.SURVEILLANCE_OFFICER), user));
		}
		
		Join<Event, Task> tasksJoin = from.join(Event.TASKS, JoinType.LEFT);
		filter = cb.or(filter, cb.equal(tasksJoin.get(Task.ASSIGNEE_USER), user));
		
		// TODO add Filter by Region...
		
		if (user.getUserRoles().contains(UserRole.SURVEILLANCE_SUPERVISOR)) {
			filter = null;//cb.or(filter, cb.equal(from.get(Case.SURVEILLANCE_SUPERVISOR), user));
		}
		
		if(date != null) {
			Predicate dateFilter = cb.greaterThan(from.get(AbstractDomainObject.CHANGE_DATE), date);
			if(filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}
		
		if(filter != null) {
			cq.where(filter);
		}
		
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));
		
		List<Event> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

}
