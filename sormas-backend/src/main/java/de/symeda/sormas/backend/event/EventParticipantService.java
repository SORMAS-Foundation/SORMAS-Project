package de.symeda.sormas.backend.event;

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
public class EventParticipantService extends AbstractAdoService<EventParticipant> {

	public EventParticipantService() {
		super(EventParticipant.class);
	}
	
	public List<EventParticipant> getAllByEventAfter(Date date, Event event) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventParticipant> cq = cb.createQuery(getElementClass());
		Root<EventParticipant> from = cq.from(getElementClass());
		
		Predicate filter = cb.equal(from.get(EventParticipant.EVENT), event);
		
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
		
		List<EventParticipant> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
	public List<EventParticipant> getAllAfter(Date date, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventParticipant> cq = cb.createQuery(getElementClass());
		Root<EventParticipant> from = cq.from(getElementClass());
		
		// TODO check and implement the user rights 
		
		if(date != null) {
			Predicate dateFilter = cb.greaterThan(from.get(AbstractDomainObject.CHANGE_DATE), date);
			cq.where(dateFilter);
		}
		
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));
		
		List<EventParticipant> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
}
