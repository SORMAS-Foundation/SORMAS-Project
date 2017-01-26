package de.symeda.sormas.backend.event;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.location.Location;
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
		
		Predicate filter = createUserFilter(cb, from, user);

		if (date != null) {
			Predicate dateFilter = cb.greaterThan(from.get(AbstractDomainObject.CHANGE_DATE), date);
			if(filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}
		
		if (filter != null) {
			cq.where(filter);
		}
		
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));
		
		List<Event> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public Predicate createUserFilter(CriteriaBuilder cb, From<Event,Event> eventPath, User user) {

		// whoever created the event or is assigned to it is allowed to access it
		Predicate filter = cb.equal(eventPath.get(Event.REPORTING_USER), user);
		filter = cb.or(filter, cb.equal(eventPath.get(Event.SURVEILLANCE_OFFICER), user));
		
		// allow event access based on user role
		for (UserRole userRole : user.getUserRoles()) {
			switch (userRole) {
			case SURVEILLANCE_SUPERVISOR:
			case CONTACT_SUPERVISOR:
			case CASE_SUPERVISOR:
				// supervisors see all events of their region
				if (user.getRegion() != null) {
					filter = cb.or(filter, cb.equal(eventPath.join(Event.EVENT_LOCATION, JoinType.LEFT).get(Location.REGION), user.getRegion()));
				}
				break;
			case SURVEILLANCE_OFFICER:
			case CONTACT_OFFICER:
			case CASE_OFFICER:
				// officers see all events of their district
				if (user.getDistrict() != null) {
					filter = cb.or(filter, cb.equal(eventPath.join(Event.EVENT_LOCATION, JoinType.LEFT).get(Location.DISTRICT), user.getDistrict()));
				}
				break;
			case INFORMANT:
				// informants dont see events
				break;
			default:
				break;
			}
		}
		
//		// events assigned with task
//		Join<Event, Task> tasksJoin = from.join(Event.TASKS, JoinType.LEFT);
//		filter = cb.or(filter, cb.equal(tasksJoin.get(Task.ASSIGNEE_USER), user));
		
		return filter;
	}

}
