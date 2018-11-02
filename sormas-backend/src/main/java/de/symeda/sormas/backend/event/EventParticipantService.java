package de.symeda.sormas.backend.event;

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

import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class EventParticipantService extends AbstractAdoService<EventParticipant> {

	@EJB
	private EventService eventService;
	@EJB
	private CaseService caseService;
	
	public EventParticipantService() {
		super(EventParticipant.class);
	}
	
	public List<EventParticipant> getAllActiveEventParticipantsAfter(Date date, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventParticipant> cq = cb.createQuery(getElementClass());
		Root<EventParticipant> from = cq.from(getElementClass());
		Join<EventParticipant, Event> event = from.join(EventParticipant.EVENT, JoinType.LEFT);

		Predicate filter = cb.or(
				cb.equal(event.get(Event.ARCHIVED), false),
				cb.isNull(event.get(Event.ARCHIVED)));

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from, user);
			filter = cb.and(filter, userFilter);
		}

		if (date != null) {
			Predicate dateFilter = createDateFilter(cb, cq, from, date);
			filter = cb.and(filter, dateFilter);		
		}

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(EventParticipant.CHANGE_DATE)));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}
	
	public List<String> getAllActiveUuids(User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<EventParticipant> from = cq.from(getElementClass());
		Join<EventParticipant, Event> event = from.join(EventParticipant.EVENT, JoinType.LEFT);

		Predicate filter = cb.or(
				cb.equal(event.get(Event.ARCHIVED), false),
				cb.isNull(event.get(Event.ARCHIVED)));
		
		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from, user);
			filter = cb.and(filter, userFilter);
		}
		
		cq.where(filter);
		cq.select(from.get(EventParticipant.UUID));
		
		return em.createQuery(cq).getResultList();
	}
	
	public List<EventParticipant> getAllByEventAfter(Date date, Event event) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventParticipant> cq = cb.createQuery(getElementClass());
		Root<EventParticipant> from = cq.from(getElementClass());
		
		Predicate filter = cb.equal(from.get(EventParticipant.EVENT), event);
		if (date != null) {
			filter = cb.and(filter, createDateFilter(cb, cq, from, date));
		}
		cq.where(filter);		
		cq.orderBy(cb.desc(from.get(EventParticipant.CREATION_DATE)));
		
		List<EventParticipant> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<EventParticipant, EventParticipant> eventParticipantPath, User user) {
		// can see the participants of all accessible events
		Predicate filter = eventService.createUserFilter(cb, cq, eventParticipantPath.join(EventParticipant.EVENT, JoinType.LEFT), user);
	
		return filter;
	}

	public List<EventParticipant> getAllByPerson(Person person) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventParticipant> cq = cb.createQuery(getElementClass());
		Root<EventParticipant> from = cq.from(getElementClass());

		cq.where(cb.equal(from.get(EventParticipant.PERSON), person));
		cq.orderBy(cb.desc(from.get(EventParticipant.CREATION_DATE)));

		List<EventParticipant> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
}
