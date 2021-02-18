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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.event;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.vaccinationinfo.VaccinationInfoService;

@Stateless
@LocalBean
public class EventParticipantService extends AbstractCoreAdoService<EventParticipant> {

	@EJB
	private EventService eventService;
	@EJB
	private SampleService sampleService;
	@EJB
	private VaccinationInfoService vaccinationInfoService;

	public EventParticipantService() {
		super(EventParticipant.class);
	}

	public List<EventParticipant> getAllActiveEventParticipantsAfter(Date date, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventParticipant> cq = cb.createQuery(getElementClass());
		Root<EventParticipant> from = cq.from(getElementClass());
		Join<EventParticipant, Event> event = from.join(EventParticipant.EVENT, JoinType.LEFT);

		Predicate filter = cb.or(cb.equal(event.get(Event.ARCHIVED), false), cb.isNull(event.get(Event.ARCHIVED)));

		filter = cb.and(filter, createDefaultFilter(cb, from));

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, date);
			filter = cb.and(filter, dateFilter);
		}

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(EventParticipant.CHANGE_DATE)));
		cq.distinct(true);

		List<EventParticipant> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public List<String> getAllActiveUuids(User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<EventParticipant> from = cq.from(getElementClass());
		Join<EventParticipant, Event> event = from.join(EventParticipant.EVENT, JoinType.LEFT);

		Predicate filter = cb.or(cb.equal(event.get(Event.ARCHIVED), false), cb.isNull(event.get(Event.ARCHIVED)));

		filter = cb.and(filter, createDefaultFilter(cb, from));

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
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

	public List<EventParticipant> getAllActiveByEvent(Event event) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventParticipant> cq = cb.createQuery(getElementClass());
		Root<EventParticipant> from = cq.from(getElementClass());

		Predicate filter = cb.equal(from.get(EventParticipant.EVENT), event);
		filter = cb.and(filter, cb.equal(from.get(Event.DELETED), false));

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(EventParticipant.CREATION_DATE)));

		List<EventParticipant> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public Predicate buildCriteriaFilter(EventParticipantCriteria criteria, CriteriaBuilder cb, Root<EventParticipant> from) {

		Join<EventParticipant, Event> event = from.join(EventParticipant.EVENT, JoinType.LEFT);
		Join<Case, Person> person = from.join(EventParticipant.PERSON, JoinType.LEFT);
		Predicate filter = null;
		if (criteria.getEvent() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(event.get(Event.UUID), criteria.getEvent().getUuid()));
		}
		if (criteria.getPerson() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(person.get(Person.UUID), criteria.getPerson().getUuid()));
		}

		if (criteria.getFreeText() != null) {
			String[] textFilters = criteria.getFreeText().split("\\s+");
			for (int i = 0; i < textFilters.length; i++) {
				String textFilter = formatForLike(textFilters[i]);
				if (!DataHelper.isNullOrEmpty(textFilter)) {
					Predicate likeFilters = cb.or(
						cb.like(cb.lower(person.get(Person.FIRST_NAME)), textFilter),
						cb.like(cb.lower(person.get(Person.LAST_NAME)), textFilter),
						phoneNumberPredicate(cb, person.get(Person.PHONE), textFilter));
					filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
				}
			}
		}

		if (criteria.getBirthdateYYYY() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(person.get(Person.BIRTHDATE_YYYY), criteria.getBirthdateYYYY()));
		}
		if (criteria.getBirthdateMM() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(person.get(Person.BIRTHDATE_MM), criteria.getBirthdateMM()));
		}
		if (criteria.getBirthdateDD() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(person.get(Person.BIRTHDATE_DD), criteria.getBirthdateDD()));
		}

		filter = CriteriaBuilderHelper.and(cb, filter, createDefaultFilter(cb, from));

		return filter;
	}

	public Predicate createActiveEventParticipantsFilter(CriteriaBuilder cb, Root<EventParticipant> root) {

		Join<EventParticipant, Event> event = root.join(EventParticipant.EVENT, JoinType.LEFT);
		return cb.and(cb.isFalse(event.get(Event.ARCHIVED)), cb.isFalse(event.get(Event.DELETED)));
	}

	public Predicate createActiveEventParticipantsFilter(CriteriaBuilder cb, Join<?, EventParticipant> eventParticipantJoin) {

		Join<EventParticipant, Event> event = eventParticipantJoin.join(EventParticipant.EVENT, JoinType.LEFT);
		return cb.and(cb.isFalse(event.get(Event.ARCHIVED)), cb.isFalse(event.get(Event.DELETED)));
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, EventParticipant> eventParticipantPath) {
		return createUserFilterForJoin(cb, cq, eventParticipantPath);
	}

	public Predicate createUserFilterForJoin(CriteriaBuilder cb, CriteriaQuery cq, From<?, EventParticipant> eventParticipantPath) {
		// can see the participants of all accessible events
		EventUserFilterCriteria eventUserFilterCriteria = new EventUserFilterCriteria();
		eventUserFilterCriteria.includeUserCaseFilter(true);
		eventUserFilterCriteria.forceRegionJurisdiction(true);

		Predicate filter =
			eventService.createUserFilter(cb, cq, eventParticipantPath.join(EventParticipant.EVENT, JoinType.LEFT), eventUserFilterCriteria);

		return filter;
	}

	public List<EventParticipant> getAllByPerson(Person person) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventParticipant> cq = cb.createQuery(getElementClass());
		Root<EventParticipant> from = cq.from(getElementClass());

		Predicate userFilter = eventService.createUserFilter(cb, cq, from.join(EventParticipant.EVENT, JoinType.INNER));

		Predicate filter = CriteriaBuilderHelper.and(cb, cb.equal(from.get(EventParticipant.PERSON), person), userFilter);

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(EventParticipant.CREATION_DATE)));

		List<EventParticipant> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public EventParticipant getByEventAndPerson(String eventUuid, String personUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventParticipant> cq = cb.createQuery(getElementClass());
		Root<EventParticipant> from = cq.from(getElementClass());

		cq.where(
			createDefaultFilter(cb, from),
			cb.equal(from.join(EventParticipant.PERSON).get(Person.UUID), personUuid),
			cb.equal(from.join(EventParticipant.EVENT).get(Event.UUID), eventUuid));

		return em.createQuery(cq).getResultList().stream().findFirst().orElse(null);
	}

	@Override
	public void delete(EventParticipant eventParticipant) {

		eventParticipant.getSamples()
			.stream()
			.filter(sample -> sample.getAssociatedCase() == null && sample.getAssociatedContact() == null)
			.forEach(sample -> sampleService.delete(sample));

		super.delete(eventParticipant);
	}

	public List<String> getDeletedUuidsSince(Date since, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<EventParticipant> eventParticipantRoot = cq.from(EventParticipant.class);
		Join<EventParticipant, Event> event = eventParticipantRoot.join(EventParticipant.EVENT, JoinType.LEFT);

		EventUserFilterCriteria eventUserFilterCriteria = new EventUserFilterCriteria();
		eventUserFilterCriteria.includeUserCaseFilter(true);
		eventUserFilterCriteria.forceRegionJurisdiction(true);

		Predicate filter =
			eventService.createUserFilter(cb, cq, eventParticipantRoot.join(EventParticipant.EVENT, JoinType.LEFT), eventUserFilterCriteria);

		if (since != null) {
			Predicate dateFilter = createChangeDateFilter(cb, eventParticipantRoot, since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		Predicate deletedFilter = cb.equal(eventParticipantRoot.get(EventParticipant.DELETED), true);
		if (filter != null) {
			filter = cb.and(filter, deletedFilter);
		} else {
			filter = deletedFilter;
		}

		cq.where(filter);
		cq.select(eventParticipantRoot.get(EventParticipant.UUID));

		return em.createQuery(cq).getResultList();
	}

	public Predicate createDefaultFilter(CriteriaBuilder cb, From<?, EventParticipant> root) {
		return cb.isFalse(root.get(EventParticipant.DELETED));
	}

	public Optional<EventParticipant> getFirst(EventParticipantCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventParticipant> cq = cb.createQuery(EventParticipant.class);
		Root<EventParticipant> eventParticipant = cq.from(EventParticipant.class);

		Predicate filter = buildCriteriaFilter(criteria, cb, eventParticipant);
		cq.where(filter);
		cq.orderBy(cb.asc(eventParticipant.get(EventParticipant.UUID)));

		try {
			return Optional.of(em.createQuery(cq).setFirstResult(0).setMaxResults(1).getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}

	public List<EventParticipant> getByEventUuids(List<String> eventUuids) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventParticipant> cq = cb.createQuery(EventParticipant.class);
		Root<EventParticipant> epRoot = cq.from(EventParticipant.class);
		Join<EventParticipant, Event> eventJoin = epRoot.join(EventParticipant.EVENT, JoinType.LEFT);

		Predicate filter = cb.and(createDefaultFilter(cb, epRoot), eventJoin.get(AbstractDomainObject.UUID).in(eventUuids));

		cq.where(filter);
		return em.createQuery(cq).getResultList();
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, EventParticipant> from, Timestamp date) {
		Predicate dateFilter = super.createChangeDateFilter(cb, from, date);
		dateFilter =
			cb.or(dateFilter, vaccinationInfoService.createChangeDateFilter(cb, from.join(EventParticipant.VACCINATION_INFO, JoinType.LEFT), date));

		return dateFilter;
	}
}
