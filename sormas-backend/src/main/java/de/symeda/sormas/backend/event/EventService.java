package de.symeda.sormas.backend.event;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.user.User;

public class EventService extends AbstractAdoService<Event> {

	public EventService() {
		super(Event.class);
	}
	
	public List<Event> getAllAfter(Date date, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Event> cq = cb.createQuery(getElementClass());
		Root<Event> from = cq.from(getElementClass());
		
		Predicate filter = cb.equal(from.get(Event.REPORTING_USER), user);
		// TODO add event officer?
		
		// TODO add filter by task
		
		// TODO add filter by region
		
		// TODO add event supervisor?
		
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
