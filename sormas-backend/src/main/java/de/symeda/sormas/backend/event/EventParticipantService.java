/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
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

import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;
import org.hibernate.criterion.Projections;

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
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = AbstractAdoService.and(cb, filter, userFilter);
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, date);
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
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = AbstractAdoService.and(cb, filter, userFilter);
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
			filter = cb.and(filter, createChangeDateFilter(cb, from, date));
		}
		cq.where(filter);		
		cq.orderBy(cb.desc(from.get(EventParticipant.CREATION_DATE)));
		
		List<EventParticipant> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
	public Predicate buildCriteriaFilter(EventParticipantCriteria criteria, CriteriaBuilder cb, Root<EventParticipant> from) {
		Join<EventParticipant, Event> event = from.join(EventParticipant.EVENT, JoinType.LEFT);
		Predicate filter = null;
		if (criteria.getEvent() != null) {
			filter = and(cb, filter, cb.equal(event.get(Event.UUID), criteria.getEvent().getUuid()));
		}

		return filter;
	}
	
	public Predicate createActiveEventParticipantsFilter(CriteriaBuilder cb, Root<EventParticipant> root) {
		Join<EventParticipant, Event> event = root.join(EventParticipant.EVENT, JoinType.LEFT);
		return cb.and(
				cb.isFalse(event.get(Event.ARCHIVED)),
				cb.isFalse(event.get(Event.DELETED)));
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<EventParticipant, EventParticipant> eventParticipantPath) {
		// can see the participants of all accessible events
		Predicate filter = eventService.createUserFilter(cb, cq, eventParticipantPath.join(EventParticipant.EVENT, JoinType.LEFT));
	
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

	public Long countByPerson(Person person){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<EventParticipant> from = cq.from(getElementClass());

		Predicate filter = cb.equal(from.get(EventParticipant.PERSON), person);
		Predicate userFilter = eventService.createUserFilter(cb, cq, from.join(EventParticipant.EVENT, JoinType.INNER));
		if(userFilter != null){
			filter = cb.and(filter, userFilter);
		}

		cq.where(filter);

		cq.select(cb.count(from));

		return em.createQuery(cq).getSingleResult();
	}
	
}
